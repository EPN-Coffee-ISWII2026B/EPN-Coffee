package epn.cafeteria.pedidos;


import java.util.LinkedHashMap;
import java.util.Map;

/** Proveedores ya registrados en inventario y los productos que cada uno suministra. */
public class CatalogoProveedores {

    private record Proveedor(String telefono, String[] productos) { }

    private static final Map<String, Proveedor> PROVEEDORES = new LinkedHashMap<>();

    static {
        PROVEEDORES.put("Café Andino", new Proveedor("0991234567", new String[]{"Café en grano"}));
        PROVEEDORES.put("Lácteos El Ordeño", new Proveedor("0987654321", new String[]{"Leche entera"}));
        PROVEEDORES.put("Dulces del Valle", new Proveedor("0998765432", new String[]{"Azúcar", "Chocolate en polvo"}));
        PROVEEDORES.put("Panadería La Espiga", new Proveedor("022345678", new String[]{"Pan de chocolate", "Pan de queso"}));
        PROVEEDORES.put("Té Verde Distribuciones", new Proveedor("0961122334", new String[]{"Té de manzanilla", "Azúcar"}));
        PROVEEDORES.put("Arca Continental", new Proveedor("0962582374", new String[]{"Agua Cielo", "Agua Dasani"}));
    }

    private CatalogoProveedores() { }

    public static String[] nombres() {
        return PROVEEDORES.keySet().toArray(new String[0]);
    }

    public static String[] productosDe(String proveedor) {
        Proveedor p = PROVEEDORES.get(proveedor);
        return p == null ? new String[0] : p.productos().clone();
    }

    public static String telefonoDe(String proveedor) {
        Proveedor p = PROVEEDORES.get(proveedor);
        return p == null ? "" : p.telefono();
    }
}