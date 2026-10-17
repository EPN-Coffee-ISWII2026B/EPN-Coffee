package transferencias;

import java.util.*;

/** Un punto de inventario: la matriz o una sucursal. */
public class Sucursal {
    private final String nombre;
    private final boolean matriz;
    private final Map<String, Integer> stock = new LinkedHashMap<>();
    private final Map<String, Integer> minimo = new HashMap<>();

    public Sucursal(String nombre, boolean matriz) {
        this.nombre = nombre;
        this.matriz = matriz;
    }

    public String getNombre() { return nombre; }
    public boolean isMatriz() { return matriz; }
    public Set<String> productos() { return stock.keySet(); }

    public void agregarProducto(String producto, int cantidad, int stockMinimo) {
        if (cantidad < 0) throw new IllegalArgumentException("El stock no puede ser negativo");
        if (stockMinimo < 0) throw new IllegalArgumentException("El stock minimo no puede ser negativo");
        stock.put(producto, cantidad);
        minimo.put(producto, stockMinimo);
    }

    public int getStock(String producto) { return stock.getOrDefault(producto, 0); }
    public int getMinimo(String producto) { return minimo.getOrDefault(producto, 0); }
    public boolean estaVacio(String producto) { return getStock(producto) == 0; }

    /** Cuanto puede ceder: la matriz todo; una sucursal solo lo que pase de su minimo. */
    public int excedente(String producto) {
        return matriz ? getStock(producto) : Math.max(0, getStock(producto) - getMinimo(producto));
    }

    public void agregar(String producto, int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        stock.merge(producto, cantidad, Integer::sum);
        minimo.putIfAbsent(producto, 0);
    }

    public void retirar(String producto, int cantidad) {
        if (cantidad <= 0 || cantidad > getStock(producto))
            throw new IllegalArgumentException("Cantidad invalida para retirar de " + nombre);
        stock.put(producto, getStock(producto) - cantidad);
    }
}
