package transferencias;

import java.util.*;

/** Logica de negocio: matriz + 5 sucursales, con transferencia confirmada por el cliente. */
public class SistemaTransferencias {
    private final Sucursal matriz;
    private final List<Sucursal> sucursales;
    private final List<Transferencia> historial = new ArrayList<>();
    private int siguienteId = 1;
    private final Map<String, String> idsProducto = new HashMap<>(); // nombre -> id

    public SistemaTransferencias(Sucursal matriz, List<Sucursal> sucursales) {
        if (sucursales.size() != 5)
            throw new IllegalArgumentException("Se requieren exactamente 5 sucursales");
        this.matriz = matriz;
        this.sucursales = new ArrayList<>(sucursales);
    }

    public Sucursal getMatriz() { return matriz; }
    public List<Sucursal> getSucursales() { return Collections.unmodifiableList(sucursales); }

    /** Matriz primero, luego las 5 sucursales. */
    public List<Sucursal> todas() {
        List<Sucursal> lista = new ArrayList<>();
        lista.add(matriz);
        lista.addAll(sucursales);
        return lista;
    }

    public Sucursal buscar(String nombre) {
        for (Sucursal s : todas()) if (s.getNombre().equals(nombre)) return s;
        throw new IllegalArgumentException("No existe la sucursal: " + nombre);
    }

    public Set<String> productos() {
        Set<String> todos = new LinkedHashSet<>();
        for (Sucursal s : todas()) todos.addAll(s.productos());
        return todos;
    }

    /** Id con el que se registro un producto (null si no existe). */
    public String getIdProducto(String producto) { return idsProducto.get(producto); }

    private boolean idExiste(String id) {
        for (String existente : idsProducto.values()) if (existente.equalsIgnoreCase(id)) return true;
        return false;
    }

    /** Da de alta un producto generando un id unico (P1, P2, ...). */
    public void registrarProducto(String producto, int minimoSucursales) {
        int n = productos().size() + 1;
        String id;
        do { id = "P" + n++; } while (idExiste(id));
        registrarProducto(id, producto, minimoSucursales);
    }

    /** Da de alta un producto en la matriz y las 5 sucursales (stock 0). */
    public void registrarProducto(String id, String producto, int minimoSucursales) {
        if (id == null || id.trim().isEmpty())
            throw new IllegalArgumentException("El id del producto no puede estar vacio");
        if (idExiste(id.trim()))
            throw new IllegalArgumentException("Ya existe un producto con el id: " + id.trim());
        if (producto == null || producto.trim().isEmpty())
            throw new IllegalArgumentException("El nombre del producto no puede estar vacio");
        if (minimoSucursales < 0)
            throw new IllegalArgumentException("El stock minimo no puede ser negativo");
        String nombre = producto.trim();
        for (String existente : productos())
            if (existente.equalsIgnoreCase(nombre))
                throw new IllegalArgumentException("Ya existe el producto: " + existente);
        idsProducto.put(nombre, id.trim());
        matriz.agregarProducto(nombre, 0, 0);
        for (Sucursal s : sucursales) s.agregarProducto(nombre, 0, minimoSucursales);
    }

    /** Suma stock a una ubicacion (ej. la matriz recibe mercancia o se carga el inventario inicial). */
    public void agregarStock(String ubicacion, String producto, int cantidad) {
        Sucursal s = buscar(ubicacion);
        if (!productos().contains(producto))
            throw new IllegalArgumentException("Producto no registrado: " + producto);
        s.agregar(producto, cantidad);
    }

    /** Sucursales (no matriz) que tienen 0 de un producto: hay que enviarles. */
    public List<Sucursal> sucursalesVacias(String producto) {
        List<Sucursal> vacias = new ArrayList<>();
        for (Sucursal s : sucursales) if (s.estaVacio(producto)) vacias.add(s);
        return vacias;
    }

    /** Matriz si le alcanza; si no, la sucursal con mayor excedente. null si nadie puede. */
    public Sucursal sugerirOrigen(String destino, String producto, int cantidad) {
        if (matriz.excedente(producto) >= cantidad) return matriz;
        Sucursal mejor = null;
        for (Sucursal s : sucursales) {
            if (s.getNombre().equals(destino) || s.excedente(producto) < cantidad) continue;
            if (mejor == null || s.excedente(producto) > mejor.excedente(producto)) mejor = s;
        }
        return mejor;
    }

    /** Crea la solicitud en estado PENDIENTE. NO mueve stock hasta que el cliente acepte. */
    public Transferencia solicitar(String origen, String destino, String producto, int cantidad) {
        if (origen.equals(destino))
            throw new IllegalArgumentException("Origen y destino no pueden ser iguales");
        if (cantidad <= 0)
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        Sucursal o = buscar(origen);
        Sucursal d = buscar(destino);
        if (d.isMatriz())
            throw new IllegalArgumentException("La matriz no recibe transferencias");
        if (o.excedente(producto) < cantidad)
            throw new IllegalStateException(origen + " solo puede ceder " + o.excedente(producto)
                    + " de " + producto);
        Transferencia t = new Transferencia(siguienteId++, origen, destino, producto, cantidad);
        historial.add(t);
        return t;
    }

    /** El cliente acepta: aqui recien se ejecuta el movimiento de stock. */
    public void aceptar(int id) {
        Transferencia t = pendiente(id);
        Sucursal o = buscar(t.getOrigen());
        if (o.excedente(t.getProducto()) < t.getCantidad())
            throw new IllegalStateException("El stock de " + t.getOrigen() + " cambio; ya no alcanza");
        o.retirar(t.getProducto(), t.getCantidad());
        buscar(t.getDestino()).agregar(t.getProducto(), t.getCantidad());
        t.setEstado(Transferencia.Estado.ACEPTADA);
    }

    /** El cliente deniega: no se mueve nada. */
    public void rechazar(int id) {
        pendiente(id).setEstado(Transferencia.Estado.RECHAZADA);
    }

    public List<Transferencia> getHistorial() { return Collections.unmodifiableList(historial); }

    private Transferencia pendiente(int id) {
        for (Transferencia t : historial) {
            if (t.getId() == id) {
                if (t.getEstado() != Transferencia.Estado.PENDIENTE)
                    throw new IllegalStateException("La transferencia #" + id + " ya fue " + t.getEstado());
                return t;
            }
        }
        throw new IllegalArgumentException("No existe la transferencia #" + id);
    }
}
