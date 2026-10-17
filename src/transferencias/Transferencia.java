package transferencias;

public class Transferencia {
    public enum Estado { PENDIENTE, ACEPTADA, RECHAZADA }

    private final int id;
    private final String origen, destino, producto;
    private final int cantidad;
    private Estado estado = Estado.PENDIENTE;

    public Transferencia(int id, String origen, String destino, String producto, int cantidad) {
        this.id = id; this.origen = origen; this.destino = destino;
        this.producto = producto; this.cantidad = cantidad;
    }

    public int getId() { return id; }
    public String getOrigen() { return origen; }
    public String getDestino() { return destino; }
    public String getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public Estado getEstado() { return estado; }
    void setEstado(Estado estado) { this.estado = estado; }

    public String descripcion() {
        return "#" + id + ": " + cantidad + " x " + producto + "  de " + origen + "  ->  " + destino;
    }

    @Override public String toString() { return descripcion() + "  [" + estado + "]"; }
}
