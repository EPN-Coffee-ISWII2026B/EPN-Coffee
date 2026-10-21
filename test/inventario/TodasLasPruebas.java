package inventario;

public class TodasLasPruebas {
    public static void main(String[] args) {
        ProductoTest.ejecutar();
        InventarioTest.ejecutar();
        CargadorCsvTest.ejecutar();
        Prueba.resumen();
    }
}
