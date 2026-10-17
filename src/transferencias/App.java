package transferencias;

import javax.swing.SwingUtilities;
import java.nio.file.Paths;
import java.util.*;

public class App {
    /**
     * Sin argumentos: arranca vacio (usa el boton "Cargar CSV").
     * java ... App inventario.csv  -> carga ese CSV al abrir.
     * java ... App --demo          -> productos de ejemplo.
     */
    public static void main(String[] args) {
        SistemaTransferencias sistema = vacio();
        if (args.length > 0) {
            if (args[0].equals("--demo")) {
                cargarDemo(sistema);
            } else {
                try {
                    sistema = CargadorCsv.cargar(Paths.get(args[0]));
                } catch (ErrorCsvException e) {
                    System.err.println("No se pudo cargar el CSV: " + e.getMessage());
                }
            }
        }
        final SistemaTransferencias inicial = sistema;
        SwingUtilities.invokeLater(() -> new VentanaPrincipal(inicial).setVisible(true));
    }

    private static SistemaTransferencias vacio() {
        List<Sucursal> sucursales = new ArrayList<>();
        for (String nombre : new String[]{"Norte", "Sur", "Este", "Oeste", "Centro"})
            sucursales.add(new Sucursal(nombre, false));
        return new SistemaTransferencias(new Sucursal("Matriz", true), sucursales);
    }

    private static void cargarDemo(SistemaTransferencias s) {
        String[] productos = {"Laptop", "Mouse", "Teclado", "Monitor"};
        int[] minimos = {3, 5, 4, 2};
        int[] matriz = {50, 120, 80, 30};
        for (int i = 0; i < productos.length; i++) {
            s.registrarProducto("P00" + (i + 1), productos[i], minimos[i]);
            s.agregarStock("Matriz", productos[i], matriz[i]);
        }
        s.agregarStock("Norte", "Mouse", 15);   s.agregarStock("Norte", "Teclado", 4);
        s.agregarStock("Sur", "Laptop", 6);     s.agregarStock("Sur", "Teclado", 10); s.agregarStock("Sur", "Monitor", 3);
        s.agregarStock("Este", "Laptop", 2);    s.agregarStock("Este", "Mouse", 8);   s.agregarStock("Este", "Monitor", 5);
        s.agregarStock("Oeste", "Laptop", 9);   s.agregarStock("Oeste", "Mouse", 12); s.agregarStock("Oeste", "Teclado", 6);
        s.agregarStock("Centro", "Laptop", 4);  s.agregarStock("Centro", "Mouse", 3);
        s.agregarStock("Centro", "Teclado", 7); s.agregarStock("Centro", "Monitor", 2);
    }
}
