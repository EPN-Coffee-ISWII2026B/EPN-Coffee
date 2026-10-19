package inventario;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Carga entregas de proveedores al inventario desde un CSV con el
 * formato que comparte el modulo POS:
 *
 *   proveedor,telefono,producto,cantidad
 *
 * Cada fila es una entrega: si el producto no existe en el inventario
 * se crea con el minimo por defecto; si ya existe, se suma el stock.
 */
public class CargadorCsv {
    private static final int COLUMNAS = 4;

    public static Inventario cargar(Path csv, int minimoPorDefecto) throws ErrorCsvException {
        Inventario inventario = new Inventario();

        try (BufferedReader lector = Files.newBufferedReader(csv, StandardCharsets.UTF_8)) {
            String linea = lector.readLine(); // encabezado
            if (linea == null)
                throw new ErrorCsvException("el CSV esta vacio");

            int numeroFila = 1;
            while ((linea = lector.readLine()) != null) {
                numeroFila++;
                if (linea.isBlank()) continue;

                String[] campos = linea.split(",", -1);
                if (campos.length != COLUMNAS)
                    throw new ErrorCsvException("fila " + numeroFila + ": se esperaban "
                            + COLUMNAS + " columnas (proveedor,telefono,producto,cantidad)");

                String proveedor = campos[0].trim();
                String telefono = campos[1].trim();
                String producto = campos[2].trim();
                int cantidad;
                try {
                    cantidad = Integer.parseInt(campos[3].trim());
                } catch (NumberFormatException e) {
                    throw new ErrorCsvException("fila " + numeroFila + ": cantidad invalida '" + campos[3] + "'");
                }
                if (cantidad < 0)
                    throw new ErrorCsvException("fila " + numeroFila + ": la cantidad no puede ser negativa");

                try {
                    inventario.recibirEntrega(proveedor, telefono, producto, cantidad, minimoPorDefecto);
                } catch (IllegalArgumentException e) {
                    throw new ErrorCsvException("fila " + numeroFila + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new ErrorCsvException("no se pudo leer el archivo: " + csv, e);
        }

        return inventario;
    }
}
