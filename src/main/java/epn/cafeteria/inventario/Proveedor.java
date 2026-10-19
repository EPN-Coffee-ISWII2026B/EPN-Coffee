package epn.cafeteria.inventario;

import java.util.List;

public record Proveedor(String nombre, String telefono, List<Producto> productos) {
    public Proveedor {
        productos = List.copyOf(productos);
    }
}
