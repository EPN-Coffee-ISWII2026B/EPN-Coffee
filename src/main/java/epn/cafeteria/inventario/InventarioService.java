package epn.cafeteria.inventario;

import java.nio.file.Path;
import java.util.*;

/** Lógica del registro de proveedores y sus productos (HU1). */
public class InventarioService {

    private final List<Proveedor> proveedores = new ArrayList<>();
    private final Path archivo;   // null = solo en memoria

    /** Servicio en memoria (usado por los tests). */
    public InventarioService() { this.archivo = null; }

    /** Servicio persistente: carga el CSV al iniciar y lo guarda en cada registro. */
    public InventarioService(Path archivo) {
        this.archivo = archivo;
        proveedores.addAll(CsvInventario.leer(archivo));
    }

    /** HU1: registra un proveedor junto con los productos que suministra. */
    public Proveedor registrarProveedor(String nombre, String telefono, List<Producto> productos) {
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre del proveedor es obligatorio");
        if (productos == null || productos.isEmpty())
            throw new IllegalArgumentException("El proveedor debe suministrar al menos un producto");
        String limpio = nombre.trim();
        boolean existe = proveedores.stream().anyMatch(p -> p.nombre().equalsIgnoreCase(limpio));
        if (existe)
            throw new IllegalStateException("El proveedor ya está registrado");

        Proveedor nuevo = new Proveedor(limpio, telefono == null ? "" : telefono.trim(), productos);
        List<Proveedor> nuevos = new ArrayList<>(proveedores);
        nuevos.add(nuevo);
        if (archivo != null) CsvInventario.escribir(archivo, nuevos);
        proveedores.add(nuevo);
        return nuevo;
    }

    /** Agrega productos sin volver a registrar al proveedor ni cambiar su teléfono. */
    public Proveedor agregarProductos(String nombre, List<Producto> productos) {
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre del proveedor es obligatorio");
        if (productos == null || productos.isEmpty())
            throw new IllegalArgumentException("Agregue al menos un producto");
        for (int i = 0; i < proveedores.size(); i++) {
            Proveedor actual = proveedores.get(i);
            if (!actual.nombre().equalsIgnoreCase(nombre.trim())) continue;
            Map<String, Producto> unidos = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            List<Producto> todos = new ArrayList<>(actual.productos());
            todos.addAll(productos);
            for (Producto producto : todos) {
                unidos.merge(producto.nombre(), producto, (a, b) ->
                        new Producto(a.nombre(), Math.addExact(a.cantidad(), b.cantidad())));
            }
            Proveedor actualizado = new Proveedor(actual.nombre(), actual.telefono(),
                    new ArrayList<>(unidos.values()));
            List<Proveedor> nuevos = new ArrayList<>(proveedores);
            nuevos.set(i, actualizado);
            if (archivo != null) CsvInventario.escribir(archivo, nuevos);
            proveedores.set(i, actualizado);
            return actualizado;
        }
        throw new IllegalArgumentException("El proveedor no está registrado");
    }

    public List<Proveedor> listarProveedores() {
        return Collections.unmodifiableList(proveedores);
    }

}
