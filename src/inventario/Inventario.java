package inventario;

import java.util.*;

/**
 * Historia de Usuario 1:
 * "Como encargado de bodega, quiero registrar el stock de un producto
 *  (id, nombre, cantidad y stock minimo) para saber cuanto tengo
 *  disponible y cuando debo reabastecer."
 *
 * El inventario usa el nombre del producto como id, para ser compatible
 * con el formato de entregas que maneja el modulo POS
 * (proveedor,telefono,producto,cantidad), que no trae un id propio.
 */
public class Inventario {
    private final Map<String, Producto> productos = new LinkedHashMap<>();

    public Producto registrarProducto(String id, String nombre, int cantidad, int minimo) {
        if (productos.containsKey(id))
            throw new IllegalArgumentException("ya existe un producto con id " + id);

        Producto producto = new Producto(id, nombre, cantidad, minimo);
        productos.put(id, producto);
        return producto;
    }

    /**
     * Registra la entrega de un proveedor (formato compartido con POS).
     * Si el producto no existia, lo crea con el minimo por defecto y
     * el stock de la entrega; si ya existia, suma la cantidad entregada
     * y agrega al proveedor a la lista de proveedores del producto.
     */
    public Producto recibirEntrega(String proveedor, String telefono, String nombreProducto,
                                    int cantidad, int minimoPorDefecto) {
        Proveedor p = new Proveedor(proveedor, telefono);
        Producto producto = productos.get(nombreProducto);

        if (producto == null) {
            producto = new Producto(nombreProducto, nombreProducto, 0, minimoPorDefecto);
            productos.put(nombreProducto, producto);
        }

        producto.agregarStock(cantidad);
        producto.agregarProveedor(p);
        return producto;
    }

    public int consultarStock(String id) {
        return obtener(id).getCantidad();
    }

    public boolean estaBajoMinimo(String id) {
        return obtener(id).estaBajoMinimo();
    }

    public List<Producto> listar() {
        return new ArrayList<>(productos.values());
    }

    private Producto obtener(String id) {
        Producto producto = productos.get(id);
        if (producto == null)
            throw new NoSuchElementException("no existe un producto con id " + id);
        return producto;
    }
}
