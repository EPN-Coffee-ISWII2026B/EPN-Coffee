package transferencias;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Lee el inventario desde un CSV (una fila por producto y ubicacion):
 *   id_producto,nombre_producto,sucursal,cantidad[,minimo]
 * Debe haber una ubicacion llamada "Matriz" y exactamente 5 sucursales mas.
 * Acepta coma o punto y coma como separador.
 */
public class CargadorCsv {
    public static final int MINIMO_POR_DEFECTO = 1;
    public static final String ENCABEZADO = "id_producto,nombre_producto,sucursal,cantidad[,minimo]";
    private static final int SUCURSALES_ESPERADAS = 5;

    private static class Fila {
        final String id, nombre, sucursal;
        final int cantidad;
        final Integer minimo;
        Fila(String id, String nombre, String sucursal, int cantidad, Integer minimo) {
            this.id = id; this.nombre = nombre; this.sucursal = sucursal;
            this.cantidad = cantidad; this.minimo = minimo;
        }
    }

    public static boolean existe(Path ruta) {
        return ruta != null && Files.isRegularFile(ruta);
    }

    public static SistemaTransferencias cargar(Path ruta) {
        if (!existe(ruta)) throw new ErrorCsvException("No existe el archivo: " + ruta);

        List<String> lineas;
        try {
            lineas = Files.readAllLines(ruta, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ErrorCsvException("No se pudo leer el archivo (guardalo como CSV UTF-8): " + e.getMessage());
        }

        int cab = 0;
        while (cab < lineas.size() && lineas.get(cab).replace("\uFEFF", "").trim().isEmpty()) cab++;
        if (cab == lineas.size()) throw new ErrorCsvException("El archivo esta vacio");

        String encabezado = lineas.get(cab).replace("\uFEFF", "");
        String separador = encabezado.contains(";") ? ";" : ",";
        validarEncabezado(dividir(encabezado, separador));

        List<Fila> filas = new ArrayList<>();
        for (int n = cab + 1; n < lineas.size(); n++) {
            if (lineas.get(n).trim().isEmpty()) continue;
            filas.add(leerFila(dividir(lineas.get(n), separador), n + 1));
        }
        if (filas.isEmpty()) throw new ErrorCsvException("El archivo no tiene productos");

        try {
            return construir(filas);
        } catch (IllegalArgumentException e) {
            throw new ErrorCsvException(e.getMessage());
        }
    }

    private static void validarEncabezado(String[] cols) {
        String[] esperado = {"id_producto", "nombre_producto", "sucursal", "cantidad"};
        boolean ok = cols.length == 4 || (cols.length == 5 && cols[4].equalsIgnoreCase("minimo"));
        for (int i = 0; ok && i < 4; i++) ok = cols[i].equalsIgnoreCase(esperado[i]);
        if (!ok) throw new ErrorCsvException("Encabezado invalido. Debe ser: " + ENCABEZADO);
    }

    private static Fila leerFila(String[] c, int linea) {
        if (c.length < 4 || c.length > 5)
            throw error(linea, "se esperaban 4 o 5 columnas y hay " + c.length);
        if (c[0].isEmpty() || c[1].isEmpty() || c[2].isEmpty())
            throw error(linea, "id, nombre y sucursal no pueden estar vacios");
        int cantidad = entero(c[3], linea, "cantidad");
        if (cantidad < 0)
            throw error(linea, "cantidad negativa (" + cantidad + "); no se permiten productos negativos");
        Integer minimo = null;
        if (c.length == 5 && !c[4].isEmpty()) {
            minimo = entero(c[4], linea, "minimo");
            if (minimo < 0) throw error(linea, "el minimo no puede ser negativo (" + minimo + ")");
        }
        return new Fila(c[0], c[1], c[2], cantidad, minimo);
    }

    private static SistemaTransferencias construir(List<Fila> filas) {
        // Ubicaciones (sin distinguir mayusculas): la matriz y exactamente 5 sucursales
        Map<String, String> ubicaciones = new LinkedHashMap<>();
        for (Fila f : filas) ubicaciones.putIfAbsent(f.sucursal.toLowerCase(Locale.ROOT), f.sucursal);
        Map<String, String> canon = new LinkedHashMap<>(ubicaciones);
        String nombreMatriz = canon.remove("matriz");
        if (nombreMatriz == null)
            throw new ErrorCsvException("Falta la matriz: debe haber filas con sucursal 'Matriz'");
        if (canon.size() != SUCURSALES_ESPERADAS)
            throw new ErrorCsvException("Se requieren exactamente " + SUCURSALES_ESPERADAS
                    + " sucursales ademas de la Matriz y el archivo trae " + canon.size());

        // Productos: un id <-> un nombre, sin filas repetidas
        Map<String, String> idANombre = new LinkedHashMap<>();
        Map<String, String> nombreAId = new HashMap<>();
        Map<String, Integer> minimos = new HashMap<>();
        Set<String> vistos = new HashSet<>();
        for (Fila f : filas) {
            String previo = idANombre.putIfAbsent(f.id, f.nombre);
            if (previo != null && !previo.equalsIgnoreCase(f.nombre))
                throw new ErrorCsvException("El id '" + f.id + "' esta asignado a dos productos: '"
                        + previo + "' y '" + f.nombre + "'");
            String otroId = nombreAId.putIfAbsent(f.nombre.toLowerCase(Locale.ROOT), f.id);
            if (otroId != null && !otroId.equals(f.id))
                throw new ErrorCsvException("El producto '" + f.nombre + "' tiene dos ids: '"
                        + otroId + "' y '" + f.id + "'");
            if (f.minimo != null) minimos.putIfAbsent(f.id, f.minimo);
            if (!vistos.add(f.id + "|" + f.sucursal.toLowerCase(Locale.ROOT)))
                throw new ErrorCsvException("Fila repetida: producto " + f.id + " en " + f.sucursal);
        }

        List<Sucursal> sucursales = new ArrayList<>();
        for (String nombre : canon.values()) sucursales.add(new Sucursal(nombre, false));
        SistemaTransferencias sistema = new SistemaTransferencias(new Sucursal(nombreMatriz, true), sucursales);

        // Todo producto existe en las 6 ubicaciones (si el CSV no lo trae en una, queda en 0)
        for (Map.Entry<String, String> p : idANombre.entrySet())
            sistema.registrarProducto(p.getKey(), p.getValue(),
                    minimos.getOrDefault(p.getKey(), MINIMO_POR_DEFECTO));
        for (Fila f : filas)
            if (f.cantidad > 0)
                sistema.agregarStock(ubicaciones.get(f.sucursal.toLowerCase(Locale.ROOT)),
                        idANombre.get(f.id), f.cantidad);
        return sistema;
    }

    private static String[] dividir(String linea, String separador) {
        String[] partes = linea.split(Pattern.quote(separador), -1);
        for (int i = 0; i < partes.length; i++) partes[i] = partes[i].trim();
        return partes;
    }

    private static int entero(String texto, int linea, String campo) {
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw error(linea, campo + " no es un numero entero: '" + texto + "'");
        }
    }

    private static ErrorCsvException error(int linea, String mensaje) {
        return new ErrorCsvException("Linea " + linea + ": " + mensaje);
    }
}
