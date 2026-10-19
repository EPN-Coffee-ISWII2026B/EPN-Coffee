package inventario;

import java.util.NoSuchElementException;

import static inventario.Prueba.*;

public class InventarioTest {
    public static void ejecutar() {
        System.out.println("== InventarioTest ==");

        correr("registrar un producto permite consultar su stock", () -> {
            Inventario inv = new Inventario();
            inv.registrarProducto("P001", "Cafe en grano 1kg", 20, 5);
            assertEquals(20, inv.consultarStock("P001"));
        });

        correr("no se permite registrar dos veces el mismo id", () -> {
            Inventario inv = new Inventario();
            inv.registrarProducto("P001", "Cafe en grano 1kg", 20, 5);
            assertThrows(IllegalArgumentException.class,
                    () -> inv.registrarProducto("P001", "Otro", 1, 0));
        });

        correr("consultar un producto no registrado lanza excepcion", () -> {
            Inventario inv = new Inventario();
            assertThrows(NoSuchElementException.class, () -> inv.consultarStock("P999"));
        });

        correr("un producto bajo minimo se reporta correctamente", () -> {
            Inventario inv = new Inventario();
            inv.registrarProducto("P002", "Leche entera 1L", 3, 10);
            assertTrue(inv.estaBajoMinimo("P002"), "debia estar bajo minimo");
        });

        correr("listar devuelve todos los productos registrados", () -> {
            Inventario inv = new Inventario();
            inv.registrarProducto("P001", "Cafe en grano 1kg", 20, 5);
            inv.registrarProducto("P002", "Leche entera 1L", 3, 10);
            assertEquals(2, inv.listar().size());
        });

        correr("recibir una entrega de un producto nuevo lo crea con el minimo por defecto", () -> {
            Inventario inv = new Inventario();
            inv.recibirEntrega("Cafe Andino", "0991234567", "Cafe en grano", 40, 10);
            assertEquals(40, inv.consultarStock("Cafe en grano"));
        });

        correr("recibir una segunda entrega del mismo producto suma el stock", () -> {
            Inventario inv = new Inventario();
            inv.recibirEntrega("Dulces del Valle", "0998765432", "Azucar", 50, 10);
            inv.recibirEntrega("Te Verde Distribuciones", "0961122334", "Azucar", 15, 10);
            assertEquals(65, inv.consultarStock("Azucar"));
        });

        correr("el producto queda con todos los proveedores que lo entregaron", () -> {
            Inventario inv = new Inventario();
            inv.recibirEntrega("Dulces del Valle", "0998765432", "Azucar", 50, 10);
            Producto azucar = inv.recibirEntrega("Te Verde Distribuciones", "0961122334", "Azucar", 15, 10);
            assertEquals(2, azucar.getProveedores().size());
        });

        correr("una entrega con cantidad negativa es rechazada", () -> {
            Inventario inv = new Inventario();
            assertThrows(IllegalArgumentException.class,
                    () -> inv.recibirEntrega("Cafe Andino", "0991234567", "Cafe en grano", -5, 10));
        });
    }
}
