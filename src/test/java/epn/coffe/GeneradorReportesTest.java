package epn.coffe;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

public class GeneradorReportesTest {

    private RepositorioPedidos repo;
    private GeneradorReportes generador;

    @BeforeEach
    public void setUp() {
        repo = new RepositorioPedidos();
        generador = new GeneradorReportes(repo);
    }

    private Pedido crearPedido(String cliente, String local, LocalDate fecha,
                               int cafes, int sandwiches) {
        Pedido p = new Pedido();
        p.setNombreCliente(cliente);
        p.setLocal(local);
        p.setFecha(fecha);
        for (int i = 0; i < cafes; i++) p.agregarCafe();
        for (int i = 0; i < sandwiches; i++) p.agregarSandwich();
        return p;
    }

    private void cargarDatosDeEjemplo() {
        LocalDate hoy = LocalDate.now();
        repo.guardar(crearPedido("Juan Pérez",  "Local 1", hoy, 2, 0)); // $3.00
        repo.guardar(crearPedido("María López", "Local 2", hoy, 0, 2)); // $5.00
        repo.guardar(crearPedido("Juan Pérez",  "Local 1", hoy, 3, 0)); // $4.50
        repo.guardar(crearPedido("Ana Torres",  "Local 2", hoy, 0, 3)); // $7.50
    }

    // --- Reporte general ---

    @Test
    public void sinPedidosElReporteEstaVacio() {
        assertEquals("No hay ventas registradas.", generador.reporteGeneral());
    }

    @Test
    public void reporteGeneralMuestraTotalDePedidos() {
        cargarDatosDeEjemplo();
        String r = generador.reporteGeneral();
        assertTrue(r.contains("4"));         // 4 pedidos
        assertTrue(r.contains("20.00"));     // 3+5+4.5+7.5 = 20.00
    }

    // --- Reporte por fecha ---

    @Test
    public void reportePorFechaSoloMuestraEseDia() {
        LocalDate hoy = LocalDate.now();
        LocalDate ayer = hoy.minusDays(1);
        repo.guardar(crearPedido("Juan", "Local 1", hoy,  2, 0));
        repo.guardar(crearPedido("Ana",  "Local 2", ayer, 5, 0));

        String r = generador.reportePorFecha(hoy);
        assertTrue(r.contains("1"));        // 1 pedido ese día
        assertTrue(r.contains("3.00"));     // solo el de hoy
    }

    // --- Reporte por cliente ---

    @Test
    public void reportePorClienteMuestraSusCompras() {
        cargarDatosDeEjemplo();
        String r = generador.reportePorCliente("Juan Pérez");
        assertTrue(r.contains("Juan Pérez"));
        assertTrue(r.contains("5"));        // 2+3 = 5 cafés
        assertTrue(r.contains("7.50"));     // 3.00 + 4.50 = 7.50
    }

    // --- Reporte por local ---

    @Test
    public void reportePorLocalMuestraTotalVendido() {
        cargarDatosDeEjemplo();
        String r = generador.reportePorLocal("Local 2");
        assertTrue(r.contains("Local 2"));
        assertTrue(r.contains("12.50"));    // 5.00 + 7.50
    }

    // --- Producto más vendido ---

    @Test
    public void debeIdentificarElProductoMasVendido() {
        cargarDatosDeEjemplo();
        // 5 cafés vs 5 sandwiches → empate, gana Café (regla arbitraria)
        assertEquals("Café", generador.productoMasVendido());
    }

    @Test
    public void debeCambiarElProductoTopSiCambianLosDatos() {
        LocalDate hoy = LocalDate.now();
        repo.guardar(crearPedido("Juan", "Local 1", hoy, 0, 9));
        assertEquals("Sandwich", generador.productoMasVendido());
    }

    // --- Local donde más se vendió un producto ---

    @Test
    public void debeIdentificarElLocalDondeMasSeVendioUnProducto() {
        LocalDate hoy = LocalDate.now();
        repo.guardar(crearPedido("Juan",  "Local 1", hoy, 1, 0));
        repo.guardar(crearPedido("María", "Local 2", hoy, 5, 0));
        repo.guardar(crearPedido("Pedro", "Local 2", hoy, 3, 0));
        assertEquals("Local 2", generador.localDondeMasSeVendio("Café"));
    }

    // --- Dashboard ---

    @Test
    public void elDashboardDebeMostrarBarrasPorLocal() {
        cargarDatosDeEjemplo();
        String dash = generador.dashboard();
        assertTrue(dash.contains("Local 1"));
        assertTrue(dash.contains("Local 2"));
        assertTrue(dash.contains("#"));
    }

    @Test
    public void elDashboardDebeIncluirElProductoTop() {
        cargarDatosDeEjemplo();
        assertTrue(generador.dashboard().contains("Producto top"));
    }
}
