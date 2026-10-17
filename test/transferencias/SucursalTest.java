package transferencias;

import static transferencias.Prueba.*;

public class SucursalTest {
    public static void ejecutar() {
        System.out.println("== SucursalTest ==");

        correr("una sucursal con stock 0 esta vacia", () -> {
            Sucursal s = new Sucursal("Norte", false);
            s.agregarProducto("Mouse", 0, 3);
            assertTrue(s.estaVacio("Mouse"), "debia estar vacia");
        });

        correr("retirar descuenta el stock", () -> {
            Sucursal s = new Sucursal("Norte", false);
            s.agregarProducto("Mouse", 10, 3);
            s.retirar("Mouse", 4);
            assertEquals(6, s.getStock("Mouse"));
        });

        correr("retirar mas de lo que hay lanza excepcion", () -> {
            Sucursal s = new Sucursal("Norte", false);
            s.agregarProducto("Mouse", 2, 0);
            assertThrows(IllegalArgumentException.class, () -> s.retirar("Mouse", 3));
        });

        correr("el excedente de una sucursal es stock menos minimo", () -> {
            Sucursal s = new Sucursal("Norte", false);
            s.agregarProducto("Mouse", 10, 3);
            assertEquals(7, s.excedente("Mouse"));
        });

        correr("la matriz puede ceder todo su stock", () -> {
            Sucursal m = new Sucursal("Matriz", true);
            m.agregarProducto("Mouse", 10, 3);
            assertEquals(10, m.excedente("Mouse"));
        });
        correr("no se permite crear un producto con stock negativo", () -> {
            Sucursal s = new Sucursal("Norte", false);
            assertThrows(IllegalArgumentException.class, () -> s.agregarProducto("Mouse", -1, 0));
        });

        correr("no se permite un stock minimo negativo", () -> {
            Sucursal s = new Sucursal("Norte", false);
            assertThrows(IllegalArgumentException.class, () -> s.agregarProducto("Mouse", 5, -1));
        });
    }
}
