package epn.cafeteria.inventario;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CsvInventario {

    private static final String CABECERA = "proveedor,telefono,producto,cantidad";

    private CsvInventario() {}

    public static List<Proveedor> leer(Path archivo) {
        if (!Files.exists(archivo)) return new ArrayList<>();
        Map<String, String> telefonos = new LinkedHashMap<>();
        Map<String, List<Producto>> productos = new LinkedHashMap<>();
        try {
            List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);
            for (int i = 1; i < lineas.size(); i++) {           
                if (lineas.get(i).isBlank()) continue;
                List<String> c = partir(lineas.get(i));
                if (c.size() < 4) continue;
                String prov = c.get(0);
                telefonos.putIfAbsent(prov, c.get(1));
                productos.computeIfAbsent(prov, k -> new ArrayList<>())
                         .add(new Producto(c.get(2), Integer.parseInt(c.get(3).trim())));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer " + archivo, e);
        }
        List<Proveedor> res = new ArrayList<>();
        telefonos.forEach((n, t) -> res.add(new Proveedor(n, t, productos.get(n))));
        return res;
    }

    public static void escribir(Path archivo, List<Proveedor> proveedores) {
        List<String> lineas = new ArrayList<>();
        lineas.add(CABECERA);
        for (Proveedor pr : proveedores)
            for (Producto p : pr.productos())
                lineas.add(String.join(",", esc(pr.nombre()), esc(pr.telefono()),
                        esc(p.nombre()), String.valueOf(p.cantidad())));
        try {
            Path padre = archivo.toAbsolutePath().getParent();
            if (padre != null) Files.createDirectories(padre);
            Path temporal = Files.createTempFile(padre, "inventario-", ".tmp");
            try {
                Files.write(temporal, lineas, StandardCharsets.UTF_8);
                try {
                    Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.ATOMIC_MOVE);
                } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                    Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
                }
            } finally {
                Files.deleteIfExists(temporal);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar " + archivo, e);
        }
    }

    private static String esc(String s) {
        return (s.contains(",") || s.contains("\"")) ? "\"" + s.replace("\"", "\"\"") + "\"" : s;
    }

    private static List<String> partir(String linea) {
        List<String> res = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean comillas = false;
        for (int i = 0; i < linea.length(); i++) {
            char ch = linea.charAt(i);
            if (ch == '"') {
                if (comillas && i + 1 < linea.length() && linea.charAt(i + 1) == '"') { sb.append('"'); i++; }
                else comillas = !comillas;
            } else if (ch == ',' && !comillas) { res.add(sb.toString()); sb.setLength(0); }
            else sb.append(ch);
        }
        res.add(sb.toString());
        return res;
    }
}
