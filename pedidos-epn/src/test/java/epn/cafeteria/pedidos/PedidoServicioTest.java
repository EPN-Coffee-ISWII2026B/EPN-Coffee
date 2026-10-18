package epn.cafeteria.pedidos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PedidoServicioTest {

    @TempDir
    Path carpeta;

    private Path csv;
    private PedidoServicio servicio;

    @BeforeEach
    void preparar() throws PedidoException {
        csv = carpeta.resolve("pedidos.csv");
        servicio = new PedidoServicio(new PedidoRepositorio(csv.toString()));
    }

    @Test
    void rechazaCantidadCero() {
        PedidoException e = assertThrows(PedidoException.class, () ->
                servicio.registrar("Quito Norte", "Café Andino", "Café en grano", "0", "2.50", "2026-10-17"));
        assertEquals("La cantidad debe ser mayor que cero.", e.getMessage());
    }

    @Test
    void rechazaFechaConFormatoIncorrecto() {
        assertThrows(PedidoException.class, () ->
                servicio.registrar("Quito Norte", "Café Andino", "Café en grano", "1", "2.50", "17/10/2026"));
    }

    @Test
    void aceptaPrecioConComa() throws PedidoException {
        Pedido p = servicio.registrar("Quito Sur", "Lácteos El Ordeño", "Leche entera", "2", "1,50", "2026-10-17");
        assertEquals(new BigDecimal("1.50"), p.precioUnitario());
    }

    @Test
    void persisteYRecargaDesdeCsv() throws PedidoException {
        servicio.registrar("Quito Sur", "Café Andino", "Café en grano", "3", "4.00", "2026-10-17");

        PedidoServicio nuevo = new PedidoServicio(new PedidoRepositorio(csv.toString()));
        assertEquals(1, nuevo.filtrar("", "").size());
    }

    @Test
    void filtraPorSucursalYTexto() throws PedidoException {
        servicio.registrar("Quito Sur", "Café Andino", "Café en grano", "3", "4.00", "2026-10-17");
        servicio.registrar("Quito Norte", "Dulces del Valle", "Azúcar", "5", "1.00", "2026-10-17");

        assertEquals(1, servicio.filtrar("Quito Norte", "").size());
        assertEquals(1, servicio.filtrar("", "azúcar").size());
    }
}