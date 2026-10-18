package epn.coffe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LectorPedidos {

    /**
     * Lee un archivo .txt con formato:
     * cliente;local;fecha;cafes;sandwiches
     * Ignora líneas vacías y comentarios (que empiecen con #).
     */
    public List<Pedido> leer(String rutaArchivo) throws IOException {
        List<Pedido> pedidos = new ArrayList<>();
        List<String> lineas = Files.readAllLines(Path.of(rutaArchivo));

        for (String linea : lineas) {
            String limpia = linea.trim();
            if (limpia.isEmpty() || limpia.startsWith("#")) continue;
            pedidos.add(parsear(limpia));
        }
        return pedidos;
    }

    private Pedido parsear(String linea) {
        String[] partes = linea.split(";");
        if (partes.length != 5) {
            throw new IllegalArgumentException("Línea inválida: " + linea);
        }

        Pedido p = new Pedido();
        p.setNombreCliente(partes[0].trim());
        p.setLocal(partes[1].trim());
        p.setFecha(LocalDate.parse(partes[2].trim()));

        int cafes = Integer.parseInt(partes[3].trim());
        int sandwiches = Integer.parseInt(partes[4].trim());
        for (int i = 0; i < cafes; i++) p.agregarCafe();
        for (int i = 0; i < sandwiches; i++) p.agregarSandwich();

        return p;
    }
}
