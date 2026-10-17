package transferencias;

/** Error al leer o validar el archivo CSV de inventario. */
public class ErrorCsvException extends RuntimeException {
    public ErrorCsvException(String mensaje) { super(mensaje); }
}
