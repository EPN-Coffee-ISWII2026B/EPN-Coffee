package epn.coffe;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RepositorioPedidos {

    private final List<Pedido> pedidos = new ArrayList<>();

    public void guardar(Pedido pedido) {
        pedidos.add(pedido);
    }

    public int getCantidad() {
        return pedidos.size();
    }

    public List<Pedido> obtenerTodos() {
        return new ArrayList<>(pedidos);
    }

    public List<Pedido> obtenerPorFecha(LocalDate fecha) {
        return pedidos.stream()
                .filter(p -> fecha.equals(p.getFecha()))
                .collect(Collectors.toList());
    }

    public List<Pedido> obtenerPorCliente(String cliente) {
        return pedidos.stream()
                .filter(p -> cliente.equalsIgnoreCase(p.getNombreCliente()))
                .collect(Collectors.toList());
    }

    public List<Pedido> obtenerPorLocal(String local) {
        return pedidos.stream()
                .filter(p -> local.equalsIgnoreCase(p.getLocal()))
                .collect(Collectors.toList());
    }
}
