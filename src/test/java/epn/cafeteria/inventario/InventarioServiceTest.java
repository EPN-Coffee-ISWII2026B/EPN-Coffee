package epn.cafeteria.inventario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class InventarioServiceTest {

    private InventarioService service;

    @BeforeEach
    void setUp() { service = new InventarioService(); }

    // ---------- HU1: registrar proveedores con sus productos ----------
    @Test
    void registraProveedorConSusProductos() {
        Proveedor p = service.registrarProveedor("Café Andino", "0991234567",
                List.of(new Producto("Café en grano", 20), new Producto("Azúcar", 10)));
        assertEquals("Café Andino", p.nombre());
        assertEquals(2, p.productos().size());
        assertEquals(1, service.listarProveedores().size());
    }

    @Test
    void rechazaProveedorSinNombre() {
        assertThrows(IllegalArgumentException.class, () ->
                service.registrarProveedor("  ", "099", List.of(new Producto("Leche", 5))));
    }

    @Test
    void rechazaProveedorSinProductos() {
        assertThrows(IllegalArgumentException.class, () ->
                service.registrarProveedor("Lácteos S.A.", "099", List.of()));
    }

    @Test
    void rechazaProveedorDuplicado() {
        service.registrarProveedor("Lácteos S.A.", "099", List.of(new Producto("Leche", 5)));
        assertThrows(IllegalStateException.class, () ->
                service.registrarProveedor("lácteos s.a.", "099", List.of(new Producto("Leche", 5))));
    }

    @Test
    void rechazaProductoConCantidadNegativa() {
        assertThrows(IllegalArgumentException.class, () -> new Producto("Leche", -1));
    }

    @Test
    void agregaProductosEnMemoria() {
        service.registrarProveedor("Arca", "099", List.of(new Producto("Agua", 3)));
        service.agregarProductos("Arca", List.of(new Producto("agua", 2)));
        assertEquals(5, service.listarProveedores().get(0).productos().get(0).cantidad());
    }

    @Test
    void rechazaProductoSinNombre() {
        assertThrows(IllegalArgumentException.class, () -> new Producto(" ", 1));
    }

    // ---------- Persistencia CSV ----------
    @Test
    void guardaYRecuperaProveedoresDesdeCsv(@TempDir Path dir) {
        Path csv = dir.resolve("inventario.csv");
        new InventarioService(csv).registrarProveedor("Café, Andino", "099",
                List.of(new Producto("Café en grano", 20), new Producto("Azúcar", 10)));

        InventarioService recargado = new InventarioService(csv);
        assertEquals(1, recargado.listarProveedores().size());
        assertEquals("Café, Andino", recargado.listarProveedores().get(0).nombre());
        assertEquals(20, recargado.listarProveedores().get(0).productos().get(0).cantidad());
    }

    @Test
    void archivoInexistenteIniciaVacio(@TempDir Path dir) {
        assertTrue(new InventarioService(dir.resolve("nada.csv")).listarProveedores().isEmpty());
    }

    @Test
    void agregaProductosAProveedorExistenteYPersiste(@TempDir Path dir) {
        Path csv = dir.resolve("inventario.csv");
        InventarioService persistente = new InventarioService(csv);
        persistente.registrarProveedor("Arca Continental", "099",
                List.of(new Producto("Agua Dasani", 30)));
        persistente.agregarProductos(" arca continental ", List.of(
                new Producto("Agua Cielo", 7), new Producto("agua dasani", 2)));
        InventarioService recargado = new InventarioService(csv);
        assertEquals(1, recargado.listarProveedores().size());
        assertEquals("099", recargado.listarProveedores().get(0).telefono());
        assertEquals(2, recargado.listarProveedores().get(0).productos().size());
        assertTrue(recargado.listarProveedores().get(0).productos().stream()
                .anyMatch(x -> x.nombre().equalsIgnoreCase("Agua Dasani") && x.cantidad() == 32));
        assertTrue(recargado.listarProveedores().get(0).productos().stream()
                .anyMatch(x -> x.nombre().equalsIgnoreCase("Agua Cielo") && x.cantidad() == 7));
    }

    @Test
    void rechazaAgregarProductosSinProveedorOLista() {
        service.registrarProveedor("Arca", "099", List.of(new Producto("Agua", 3)));
        assertThrows(IllegalArgumentException.class,
                () -> service.agregarProductos("Otro", List.of(new Producto("Agua", 1))));
        assertThrows(IllegalArgumentException.class,
                () -> service.agregarProductos("Arca", List.of()));
        assertEquals(3, service.listarProveedores().get(0).productos().get(0).cantidad());
    }
}
