package epn.coffe;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class LectorPedidosTest {

    private LectorPedidos lector;
    private static final String RUTA = "datos/pedidos.csv";
    private static final String ENCABEZADO = "cliente,local,fecha,cafes,sandwiches\n";

    @BeforeEach
    public void setUp() {
        lector = new LectorPedidos();
    }

    // Método auxiliar para crear archivos CSV temporales en las pruebas
    private String crearCsv(String contenido) throws IOException {
        Path tmp = Files.createTempFile("pedidos", ".csv");
        Files.writeString(tmp, contenido);
        return tmp.toString();
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
    public void debeSaltarseLaFilaDeEncabezados() throws IOException {
        String ruta = crearCsv(ENCABEZADO + "Juan Pérez,Local 1,2026-10-14,1,0\n");

        List<Pedido> pedidos = lector.leer(ruta);
        assertEquals(1, pedidos.size());
        assertEquals("Juan Pérez", pedidos.get(0).getNombreCliente());
    }

    @Test
    public void debeIgnorarLineasVacias() throws IOException {
        String ruta = crearCsv(ENCABEZADO +
                "\n" +
                "Juan Pérez,Local 1,2026-10-14,1,0\n" +
                "   \n" +
                "Ana Torres,Local 3,2026-10-14,0,2\n");

        List<Pedido> pedidos = lector.leer(ruta);
        assertEquals(2, pedidos.size());
        assertEquals("Juan Pérez", pedidos.get(0).getNombreCliente());
        assertEquals("Ana Torres", pedidos.get(1).getNombreCliente());
    }

    @Test
    public void archivoSoloConEncabezadoNoTienePedidos() throws IOException {
        String ruta = crearCsv(ENCABEZADO);
        assertTrue(lector.leer(ruta).isEmpty());
    }

    @Test
    public void debeLeerCamposEntreComillasConComas() throws IOException {
        // Estándar CSV: un campo con coma va entre comillas dobles
        String ruta = crearCsv(ENCABEZADO + "\"Pérez, Juan\",\"Local 1\",2026-10-14,2,1\n");

        Pedido p = lector.leer(ruta).get(0);
        assertEquals("Pérez, Juan", p.getNombreCliente());
        assertEquals("Local 1", p.getLocal());
        assertEquals(2, p.getCantidadCafes());
        assertEquals(1, p.getCantidadSandwiches());
    }

    @Test
    public void debeLeerComillasEscapadasDentroDeUnCampo() throws IOException {
        // Estándar CSV: "" dentro de un campo entre comillas representa una comilla
        String ruta = crearCsv(ENCABEZADO + "\"Café \"\"El Poli\"\"\",Local 2,2026-10-14,1,0\n");

        assertEquals("Café \"El Poli\"", lector.leer(ruta).get(0).getNombreCliente());
    }

    @Test
    public void debeLanzarErrorSiLaLineaTieneFormatoInvalido() throws IOException {
        String ruta = crearCsv(ENCABEZADO + "SoloDosCampos,Local 1\n");
        assertThrows(IllegalArgumentException.class, () -> lector.leer(ruta));
    }

    @Test
    public void debeLanzarErrorSiElArchivoUsaElFormatoAntiguoConPuntoYComa() throws IOException {
        String ruta = crearCsv(ENCABEZADO + "Juan Pérez;Local 1;2026-10-14;1;0\n");
        assertThrows(IllegalArgumentException.class, () -> lector.leer(ruta));
    }
}
