package epn.cafeteria.inventario;

/** Producto suministrado por un proveedor, con la cantidad entregada. */
public record Producto(String nombre, int cantidad) {
    public Producto {
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        if (cantidad < 0)
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        nombre = nombre.trim();
    }
}
