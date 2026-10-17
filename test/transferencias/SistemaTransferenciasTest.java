package transferencias;

import static transferencias.Prueba.*;
import java.util.*;

public class SistemaTransferenciasTest {

    /** Matriz con 10 laptops y 5 sucursales vacias (minimo 2 cada una). */
    static SistemaTransferencias crear() {
        Sucursal matriz = new Sucursal("Matriz", true);
        matriz.agregarProducto("Laptop", 10, 0);
        List<Sucursal> sucursales = new ArrayList<>();
        for (String n : new String[]{"Norte", "Sur", "Este", "Oeste", "Centro"}) {
            Sucursal s = new Sucursal(n, false);
            s.agregarProducto("Laptop", 0, 2);
            sucursales.add(s);
        }
        return new SistemaTransferencias(matriz, sucursales);
    }

    /** Sistema sin productos: asi arranca mientras no entreguen el inventario. */
    static SistemaTransferencias crearVacio() {
        List<Sucursal> sucursales = new ArrayList<>();
        for (String n : new String[]{"Norte", "Sur", "Este", "Oeste", "Centro"})
            sucursales.add(new Sucursal(n, false));
        return new SistemaTransferencias(new Sucursal("Matriz", true), sucursales);
    }

    public static void ejecutar() {
        System.out.println("== SistemaTransferenciasTest ==");

        correr("exige exactamente 5 sucursales", () ->
            assertThrows(IllegalArgumentException.class,
                () -> new SistemaTransferencias(new Sucursal("Matriz", true), new ArrayList<>())));

        correr("detecta las sucursales vacias de un producto", () -> {
            SistemaTransferencias s = crear();
            s.buscar("Sur").agregar("Laptop", 5);
            assertEquals(4, s.sucursalesVacias("Laptop").size());
        });

        correr("solicitar NO mueve stock (queda PENDIENTE)", () -> {
            SistemaTransferencias s = crear();
            Transferencia t = s.solicitar("Matriz", "Norte", "Laptop", 4);
            assertEquals(Transferencia.Estado.PENDIENTE, t.getEstado());
            assertEquals(10, s.buscar("Matriz").getStock("Laptop"));
            assertEquals(0, s.buscar("Norte").getStock("Laptop"));
        });

        correr("aceptar mueve el stock de origen a destino", () -> {
            SistemaTransferencias s = crear();
            Transferencia t = s.solicitar("Matriz", "Norte", "Laptop", 4);
            s.aceptar(t.getId());
            assertEquals(Transferencia.Estado.ACEPTADA, t.getEstado());
            assertEquals(6, s.buscar("Matriz").getStock("Laptop"));
            assertEquals(4, s.buscar("Norte").getStock("Laptop"));
        });

        correr("rechazar deja el stock intacto", () -> {
            SistemaTransferencias s = crear();
            Transferencia t = s.solicitar("Matriz", "Norte", "Laptop", 4);
            s.rechazar(t.getId());
            assertEquals(Transferencia.Estado.RECHAZADA, t.getEstado());
            assertEquals(10, s.buscar("Matriz").getStock("Laptop"));
            assertEquals(0, s.buscar("Norte").getStock("Laptop"));
        });

        correr("no se puede aceptar dos veces la misma transferencia", () -> {
            SistemaTransferencias s = crear();
            Transferencia t = s.solicitar("Matriz", "Norte", "Laptop", 4);
            s.aceptar(t.getId());
            assertThrows(IllegalStateException.class, () -> s.aceptar(t.getId()));
        });

        correr("no se puede pedir mas de lo que hay en la matriz", () ->
            assertThrows(IllegalStateException.class,
                () -> crear().solicitar("Matriz", "Norte", "Laptop", 11)));

        correr("una sucursal no puede ceder por debajo de su minimo", () -> {
            SistemaTransferencias s = crear();
            s.buscar("Sur").agregar("Laptop", 3); // minimo 2 -> solo 1 de excedente
            assertThrows(IllegalStateException.class,
                () -> s.solicitar("Sur", "Norte", "Laptop", 2));
        });

        correr("una sucursal puede abastecer a otra con su excedente", () -> {
            SistemaTransferencias s = crear();
            s.buscar("Sur").agregar("Laptop", 8); // excedente 6
            Transferencia t = s.solicitar("Sur", "Norte", "Laptop", 6);
            s.aceptar(t.getId());
            assertEquals(2, s.buscar("Sur").getStock("Laptop"));
            assertEquals(6, s.buscar("Norte").getStock("Laptop"));
        });

        correr("origen y destino no pueden ser el mismo", () ->
            assertThrows(IllegalArgumentException.class,
                () -> crear().solicitar("Norte", "Norte", "Laptop", 1)));

        correr("sugerirOrigen prefiere la matriz si le alcanza", () -> {
            SistemaTransferencias s = crear();
            assertEquals("Matriz", s.sugerirOrigen("Norte", "Laptop", 5).getNombre());
        });

        correr("sugerirOrigen usa otra sucursal si la matriz no alcanza", () -> {
            SistemaTransferencias s = crear();
            s.buscar("Matriz").retirar("Laptop", 10);
            s.buscar("Sur").agregar("Laptop", 8);
            assertEquals("Sur", s.sugerirOrigen("Norte", "Laptop", 5).getNombre());
        });

        correr("sugerirOrigen devuelve null si nadie puede abastecer", () -> {
            SistemaTransferencias s = crear();
            s.buscar("Matriz").retirar("Laptop", 10);
            assertTrue(s.sugerirOrigen("Norte", "Laptop", 1) == null, "debia ser null");
        });

        correr("el historial registra todas las transferencias", () -> {
            SistemaTransferencias s = crear();
            s.rechazar(s.solicitar("Matriz", "Norte", "Laptop", 1).getId());
            s.aceptar(s.solicitar("Matriz", "Sur", "Laptop", 1).getId());
            assertEquals(2, s.getHistorial().size());
        });
        System.out.println("-- Inventario que se va agregando --");

        correr("un sistema sin productos funciona (inventario aun no entregado)", () ->
            assertEquals(0, crearVacio().productos().size()));

        correr("registrarProducto lo crea en la matriz y las 5 sucursales con stock 0", () -> {
            SistemaTransferencias s = crearVacio();
            s.registrarProducto("Mouse", 3);
            for (Sucursal x : s.todas()) assertEquals(0, x.getStock("Mouse"));
            assertEquals(3, s.buscar("Norte").getMinimo("Mouse"));
            assertEquals(1, s.productos().size());
        });

        correr("registrarProducto rechaza duplicados sin importar mayusculas", () -> {
            SistemaTransferencias s = crearVacio();
            s.registrarProducto("Mouse", 1);
            assertThrows(IllegalArgumentException.class, () -> s.registrarProducto("mouse", 1));
        });

        correr("registrarProducto rechaza nombre vacio o minimo negativo", () -> {
            SistemaTransferencias s = crearVacio();
            assertThrows(IllegalArgumentException.class, () -> s.registrarProducto("   ", 1));
            assertThrows(IllegalArgumentException.class, () -> s.registrarProducto("Mouse", -1));
        });

        correr("agregarStock suma stock a la ubicacion indicada", () -> {
            SistemaTransferencias s = crearVacio();
            s.registrarProducto("Mouse", 2);
            s.agregarStock("Matriz", "Mouse", 10);
            s.agregarStock("Matriz", "Mouse", 5);
            assertEquals(15, s.buscar("Matriz").getStock("Mouse"));
        });

        correr("agregarStock en un producto no registrado lanza excepcion", () ->
            assertThrows(IllegalArgumentException.class,
                () -> crearVacio().agregarStock("Matriz", "Mouse", 5)));

        correr("agregarStock rechaza cantidades de 0 o negativas", () -> {
            SistemaTransferencias s = crearVacio();
            s.registrarProducto("Mouse", 2);
            assertThrows(IllegalArgumentException.class, () -> s.agregarStock("Matriz", "Mouse", 0));
        });

        correr("un producto recien registrado aparece vacio en las 5 sucursales", () -> {
            SistemaTransferencias s = crearVacio();
            s.registrarProducto("Mouse", 2);
            assertEquals(5, s.sucursalesVacias("Mouse").size());
        });

        correr("producto nuevo + stock en matriz permite transferir", () -> {
            SistemaTransferencias s = crearVacio();
            s.registrarProducto("Mouse", 2);
            s.agregarStock("Matriz", "Mouse", 10);
            s.aceptar(s.solicitar("Matriz", "Norte", "Mouse", 4).getId());
            assertEquals(4, s.buscar("Norte").getStock("Mouse"));
            assertEquals(4, s.sucursalesVacias("Mouse").size());
        });
        correr("registrarProducto guarda el id del producto", () -> {
            SistemaTransferencias s = crearVacio();
            s.registrarProducto("P-01", "Mouse", 2);
            assertEquals("P-01", s.getIdProducto("Mouse"));
        });

        correr("registrarProducto rechaza un id repetido", () -> {
            SistemaTransferencias s = crearVacio();
            s.registrarProducto("P-01", "Mouse", 1);
            assertThrows(IllegalArgumentException.class, () -> s.registrarProducto("p-01", "Teclado", 1));
        });

        correr("registrarProducto rechaza un id vacio", () ->
            assertThrows(IllegalArgumentException.class, () -> crearVacio().registrarProducto("  ", "Mouse", 1)));

        correr("registrarProducto sin id genera uno unico", () -> {
            SistemaTransferencias s = crearVacio();
            s.registrarProducto("Mouse", 1);
            s.registrarProducto("Teclado", 1);
            assertTrue(!s.getIdProducto("Mouse").equals(s.getIdProducto("Teclado")), "ids repetidos");
        });
    }
}
