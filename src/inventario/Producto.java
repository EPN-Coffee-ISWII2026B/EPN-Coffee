package inventario;

import java.util.LinkedHashSet;
import java.util.Set;

/** Un producto dentro del inventario, con su stock actual, su stock minimo y sus proveedores. */
public class Producto {
    private final String id;
    private final String nombre;
    private int cantidad;
    private final int minimo;
    private final Set<Proveedor> proveedores = new LinkedHashSet<>();

    public Producto(String id, String nombre, int cantidad, int minimo) {
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("el id del producto no puede estar vacio");
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("el nombre del producto no puede estar vacio");
        if (cantidad < 0)
            throw new IllegalArgumentException("la cantidad no puede ser negativa");
        if (minimo < 0)
            throw new IllegalArgumentException("el stock minimo no puede ser negativo");

        this.id = id;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.minimo = minimo;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public int getCantidad() { return cantidad; }
    public int getMinimo() { return minimo; }

    public boolean estaBajoMinimo() {
        return cantidad < minimo;
    }

    void agregarStock(int unidades) {
        if (unidades < 0)
            throw new IllegalArgumentException("no se puede agregar una cantidad negativa");
        cantidad += unidades;
    }

    void agregarProveedor(Proveedor proveedor) {
        proveedores.add(proveedor);
    }

    public Set<Proveedor> getProveedores() {
        return new LinkedHashSet<>(proveedores);
    }
}
