package epn.coffe;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class LectorPedidosTest {

    private LectorPedidos lector;
    private static final String RUTA = "datos/pedidos.txt";

    @BeforeEach
    public void setUp() {
        lector = new LectorPedidos();
    }

    @Test
    public void debeLeerTodosLosPedidosDelArchivo() throws IOException {
        List<Pedido> pedidos = lector.leer(RUTA);
        assertEquals(5, pedidos.size());
    }

    @Test
    public void debeLeerElPrimerPedidoCorrectamente() throws IOException {
        List<Pedido> pedidos = lector.leer(RUTA);
        Pedido primero = pedidos.get(0);

        assertEquals("Juan Pérez", primero.getNombreCliente());
        assertEquals("Local 1",    primero.getLocal());
        assertEquals(LocalDate.of(2026, 10, 14), primero.getFecha());
        assertEquals(2, primero.getCantidadCafes());
        assertEquals(0, primero.getCantidadSandwiches());
    }

    @Test
    public void debeLeerElSegundoPedidoCorrectamente() throws IOException {
        Pedido segundo = lector.leer(RUTA).get(1);

        assertEquals("María López", segundo.getNombreCliente());
        assertEquals("Local 2",     segundo.getLocal());
        assertEquals(0, segundo.getCantidadCafes());
        assertEquals(1, segundo.getCantidadSandwiches());
    }

    @Test
    public void debeCalcularElTotalDeCadaPedidoAlLeer() throws IOException {
        List<Pedido> pedidos = lector.leer(RUTA);
        // 2 cafés = 3.00
        assertEquals(3.00, pedidos.get(0).calcularTotal(), 0.001);
        // 3 sandwiches = 7.50
        assertEquals(7.50, pedidos.get(4).calcularTotal(), 0.001);
    }

    @Test
    public void debeIgnorarLineasVaciasYComentarios() throws IOException {
        // Escribimos un archivo temporal con comentarios y vacíos
        java.nio.file.Path tmp = java.nio.file.Files.createTempFile("pedidos", ".txt");
        java.nio.file.Files.writeString(tmp,
                "# comentario\n" +
                "\n" +
                "Juan Pérez;Local 1;2026-10-14;1;0\n" +
                "   \n" +
                "# otro comentario\n" +
                "Ana Torres;Local 3;2026-10-14;0;2\n");

        List<Pedido> pedidos = lector.leer(tmp.toString());
        assertEquals(2, pedidos.size());
        assertEquals("Juan Pérez", pedidos.get(0).getNombreCliente());
        assertEquals("Ana Torres", pedidos.get(1).getNombreCliente());
    }

    @Test
    public void debeLanzarErrorSiLaLineaTieneFormatoInvalido() throws IOException {
        java.nio.file.Path tmp = java.nio.file.Files.createTempFile("malo", ".txt");
        java.nio.file.Files.writeString(tmp, "SoloDosCampos;Local 1\n");

        assertThrows(IllegalArgumentException.class, () -> lector.leer(tmp.toString()));
    }
}
