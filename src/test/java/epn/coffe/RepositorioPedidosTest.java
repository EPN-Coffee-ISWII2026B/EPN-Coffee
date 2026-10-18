package epn.coffe;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

public class RepositorioPedidosTest {

    private RepositorioPedidos repo;

    @BeforeEach
    public void setUp() {
        repo = new RepositorioPedidos();
    }

    // Método auxiliar para no repetir la creación de pedidos
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

    @Test
    public void repositorioVacioNoTienePedidos() {
        assertEquals(0, repo.getCantidad());
    }

    @Test
    public void debeGuardarUnPedido() {
        repo.guardar(crearPedido("Juan", "Local 1", LocalDate.now(), 2, 0));
        assertEquals(1, repo.getCantidad());
    }

    @Test
    public void debeRecuperarTodosLosPedidos() {
        repo.guardar(crearPedido("Juan",  "Local 1", LocalDate.now(), 2, 0));
        repo.guardar(crearPedido("María", "Local 2", LocalDate.now(), 0, 1));
        assertEquals(2, repo.obtenerTodos().size());
    }

    @Test
    public void debeRecuperarPedidosPorFecha() {
        LocalDate hoy = LocalDate.now();
        LocalDate ayer = hoy.minusDays(1);
        repo.guardar(crearPedido("Juan",  "Local 1", hoy,  2, 0));
        repo.guardar(crearPedido("María", "Local 2", ayer, 1, 0));
        assertEquals(1, repo.obtenerPorFecha(hoy).size());
    }

    @Test
    public void debeRecuperarPedidosPorCliente() {
        LocalDate hoy = LocalDate.now();
        repo.guardar(crearPedido("Juan",  "Local 1", hoy, 2, 0));
        repo.guardar(crearPedido("Juan",  "Local 2", hoy, 1, 0));
        repo.guardar(crearPedido("María", "Local 1", hoy, 0, 1));
        assertEquals(2, repo.obtenerPorCliente("Juan").size());
    }

    @Test
    public void debeRecuperarPedidosPorLocal() {
        LocalDate hoy = LocalDate.now();
        repo.guardar(crearPedido("Juan",  "Local 1", hoy, 2, 0));
        repo.guardar(crearPedido("María", "Local 2", hoy, 0, 1));
        repo.guardar(crearPedido("Pedro", "Local 1", hoy, 1, 1));
        assertEquals(2, repo.obtenerPorLocal("Local 1").size());
    }
}
