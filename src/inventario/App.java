package inventario;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * MVP de consola: demuestra la Historia de Usuario 1
 * (recibir entregas de proveedores y consultar stock / minimo).
 *
 * java ... App                               -> carga datos/inventario_ejemplo.csv
 * java ... App ruta/al/entregas.csv          -> carga ese CSV (formato POS)
 */
public class App {
    private static final int MINIMO_POR_DEFECTO = 10;

    public static void main(String[] args) {
        Path csv = args.length > 0 ? Paths.get(args[0]) : Paths.get("datos/inventario_ejemplo.csv");

        Inventario inventario;
        try {
            inventario = CargadorCsv.cargar(csv, MINIMO_POR_DEFECTO);
        } catch (ErrorCsvException e) {
            System.err.println("No se pudo cargar el CSV: " + e.getMessage());
            return;
        }

        System.out.println("=== Inventario EPN Coffee (desde " + csv + ") ===");
        for (Producto p : inventario.listar()) {
            String alerta = p.estaBajoMinimo() ? "  <-- BAJO MINIMO" : "";
            List<String> proveedores = p.getProveedores().stream().map(Proveedor::getNombre).toList();
            System.out.printf("%-24s stock=%-4d minimo=%-4d proveedores=%s%s%n",
                    p.getNombre(), p.getCantidad(), p.getMinimo(), proveedores, alerta);
        }
    }
}
