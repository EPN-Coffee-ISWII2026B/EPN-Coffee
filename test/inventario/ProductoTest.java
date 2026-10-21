package inventario;

import static inventario.Prueba.*;

public class ProductoTest {
    public static void ejecutar() {
        System.out.println("== ProductoTest ==");

        correr("un producto nuevo guarda su stock inicial", () -> {
            Producto p = new Producto("P001", "Cafe en grano 1kg", 20, 5);
            assertEquals(20, p.getCantidad());
            assertEquals(5, p.getMinimo());
        });

        correr("un producto con cantidad mayor al minimo no esta bajo minimo", () -> {
            Producto p = new Producto("P001", "Cafe en grano 1kg", 20, 5);
            assertTrue(!p.estaBajoMinimo(), "no debia estar bajo minimo");
        });

        correr("un producto con cantidad menor al minimo esta bajo minimo", () -> {
            Producto p = new Producto("P002", "Leche entera 1L", 3, 10);
            assertTrue(p.estaBajoMinimo(), "debia estar bajo minimo");
        });

        correr("no se permite crear un producto con cantidad negativa", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Producto("P003", "Vasos", -1, 0));
        });

        correr("no se permite crear un producto con minimo negativo", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Producto("P003", "Vasos", 5, -1));
        });

        correr("no se permite crear un producto sin id", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Producto(" ", "Vasos", 5, 0));
        });
    }
}
