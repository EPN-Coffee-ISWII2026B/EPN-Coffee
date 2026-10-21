package inventario;

/** Error al leer o parsear el CSV de entregas de proveedores. */
public class ErrorCsvException extends Exception {
    public ErrorCsvException(String mensaje) {
        super(mensaje);
    }

    public ErrorCsvException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
