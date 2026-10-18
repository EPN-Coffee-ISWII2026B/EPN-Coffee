package epn.coffe;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LectorPedidos {

    private static final char SEPARADOR = ',';
    private static final char COMILLA = '"';
    private static final int CAMPOS_POR_LINEA = 5;

    /**
     * Lee un archivo .csv con encabezado y formato:
     * cliente,local,fecha,cafes,sandwiches
     * La primera fila (encabezados) se salta y las líneas vacías se ignoran.
     * Los campos que contienen comas van entre comillas dobles ("Pérez, Juan").
     */
    public List<Pedido> leer(String rutaArchivo) throws IOException {
        List<Pedido> pedidos = new ArrayList<>();
        List<String> lineas = Files.readAllLines(Path.of(rutaArchivo), StandardCharsets.UTF_8);

        for (int i = 1; i < lineas.size(); i++) {   // i = 0 es el encabezado
            String limpia = lineas.get(i).trim();
            if (limpia.isEmpty()) continue;
            pedidos.add(parsear(limpia));
        }
        return pedidos;
    }

    private Pedido parsear(String linea) {
        List<String> partes = separarCampos(linea);
        if (partes.size() != CAMPOS_POR_LINEA) {
            throw new IllegalArgumentException("Línea inválida: " + linea);
        }

        Pedido p = new Pedido();
        p.setNombreCliente(partes.get(0));
        p.setLocal(partes.get(1));
        p.setFecha(LocalDate.parse(partes.get(2)));

        int cafes = Integer.parseInt(partes.get(3));
        int sandwiches = Integer.parseInt(partes.get(4));
        for (int i = 0; i < cafes; i++) p.agregarCafe();
        for (int i = 0; i < sandwiches; i++) p.agregarSandwich();

        return p;
    }

    /**
     * Separa una línea CSV respetando las comillas:
     * dentro de comillas la coma no separa y "" representa una comilla.
     */
    private List<String> separarCampos(String linea) {
        List<String> campos = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean entreComillas = false;

        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (entreComillas) {
                if (c == COMILLA && i + 1 < linea.length() && linea.charAt(i + 1) == COMILLA) {
                    actual.append(COMILLA);
                    i++;
                } else if (c == COMILLA) {
                    entreComillas = false;
                } else {
                    actual.append(c);
                }
            } else if (c == COMILLA) {
                entreComillas = true;
            } else if (c == SEPARADOR) {
                campos.add(actual.toString().trim());
                actual.setLength(0);
            } else {
                actual.append(c);
            }
        }
        campos.add(actual.toString().trim());
        return campos;
    }
}
