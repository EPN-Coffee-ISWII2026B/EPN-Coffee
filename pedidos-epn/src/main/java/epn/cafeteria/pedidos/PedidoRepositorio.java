 package epn.cafeteria.pedidos;


import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Lee y escribe los pedidos en un archivo CSV. */
public class PedidoRepositorio {

    private static final String ENCABEZADO =
            "sucursal,proveedor,producto,cantidad,precioUnitario,fecha,subtotal,iva,total";

    private final Path archivo;

    public PedidoRepositorio(String nombreArchivo) {
        this.archivo = Path.of(nombreArchivo);
    }

    public List<Pedido> cargar() throws PedidoException {
        List<Pedido> pedidos = new ArrayList<>();
        if (!Files.exists(archivo)) {
            return pedidos;
        }
        try {
            List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);
            for (int i = 1; i < lineas.size(); i++) {
                if (!lineas.get(i).isBlank()) {
                    pedidos.add(aPedido(lineas.get(i), i + 1));
                }
            }
        } catch (IOException e) {
            throw new PedidoException("No se pudo leer el archivo CSV: " + e.getMessage());
        }
        return pedidos;
    }

    public void guardar(Pedido p) throws PedidoException {
        boolean nuevo = !Files.exists(archivo);
        try (BufferedWriter w = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            if (nuevo) {
                w.write(ENCABEZADO);
                w.newLine();
            }
            w.write(String.join(",", texto(p.sucursal()), texto(p.proveedor()), texto(p.producto()),
                    String.valueOf(p.cantidad()), p.precioUnitario().toPlainString(), p.fecha().toString(),
                    p.subtotal().toPlainString(), p.iva().toPlainString(), p.total().toPlainString()));
            w.newLine();
        } catch (IOException e) {
            throw new PedidoException("No se pudo guardar en el archivo CSV: " + e.getMessage());
        }
    }

    private static String texto(String s) {
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }

    private Pedido aPedido(String linea, int numeroLinea) throws PedidoException {
        List<String> c = dividir(linea);
        try {
            return new Pedido(c.get(0), c.get(1), c.get(2), Integer.parseInt(c.get(3)),
                    new BigDecimal(c.get(4)), LocalDate.parse(c.get(5)));
        } catch (RuntimeException e) {
            throw new PedidoException("Línea " + numeroLinea + " del CSV con formato inválido.");
        }
    }

    /** Divide una línea CSV respetando comillas. */
    private static List<String> dividir(String linea) {
        List<String> campos = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean entreComillas = false;
        for (int i = 0; i < linea.length(); i++) {
            char ch = linea.charAt(i);
            if (ch == '"') {
                if (entreComillas && i + 1 < linea.length() && linea.charAt(i + 1) == '"') {
                    actual.append('"');
                    i++;
                } else {
                    entreComillas = !entreComillas;
                }
            } else if (ch == ',' && !entreComillas) {
                campos.add(actual.toString());
                actual.setLength(0);
            } else {
                actual.append(ch);
            }
        }
        campos.add(actual.toString());
        return campos;
    }
}