package epn.coffe;

import java.time.LocalDate;
import java.util.*;

public class GeneradorReportes {

    private final RepositorioPedidos repo;

    public GeneradorReportes(RepositorioPedidos repo) {
        this.repo = repo;
    }

    private double totalDe(List<Pedido> lista) {
        return lista.stream().mapToDouble(Pedido::calcularTotal).sum();
    }

    // ---------- Reporte general ----------

    public String reporteGeneral() {
        if (repo.getCantidad() == 0) return "No hay ventas registradas.";
        return "=== REPORTE GENERAL ===\n"
             + "Pedidos: " + repo.getCantidad() + "\n"
             + "Ingreso total: $" + String.format(Locale.US, "%.2f", totalDe(repo.obtenerTodos()));
    }

    // ---------- Reporte por fecha ----------

    public String reportePorFecha(LocalDate fecha) {
        List<Pedido> lista = repo.obtenerPorFecha(fecha);
        return "=== VENTAS DEL " + fecha + " ===\n"
             + "Pedidos: " + lista.size() + "\n"
             + "Total: $" + String.format(Locale.US, "%.2f", totalDe(lista));
    }

    // ---------- Reporte por cliente ----------

    public String reportePorCliente(String cliente) {
        List<Pedido> lista = repo.obtenerPorCliente(cliente);
        int cafes = lista.stream().mapToInt(Pedido::getCantidadCafes).sum();
        int sandwiches = lista.stream().mapToInt(Pedido::getCantidadSandwiches).sum();
        return "=== CLIENTE: " + cliente + " ===\n"
             + cafes + " Café(s)\n"
             + sandwiches + " Sandwich(es)\n"
             + "Total: $" + String.format(Locale.US, "%.2f", totalDe(lista));
    }

    // ---------- Reporte por local ----------

    public String reportePorLocal(String local) {
        List<Pedido> lista = repo.obtenerPorLocal(local);
        return "=== LOCAL: " + local + " ===\n"
             + "Pedidos: " + lista.size() + "\n"
             + "Total: $" + String.format(Locale.US, "%.2f", totalDe(lista));
    }

    // ---------- BI: producto top ----------

    public String productoMasVendido() {
        int cafes = 0, sandwiches = 0;
        for (Pedido p : repo.obtenerTodos()) {
            cafes += p.getCantidadCafes();
            sandwiches += p.getCantidadSandwiches();
        }
        if (cafes == 0 && sandwiches == 0) return "Ninguno";
        return cafes >= sandwiches ? "Café" : "Sandwich";
    }

    public String localDondeMasSeVendio(String producto) {
        Map<String, Integer> porLocal = new HashMap<>();
        for (Pedido p : repo.obtenerTodos()) {
            int cantidad = producto.equalsIgnoreCase("Café")
                    ? p.getCantidadCafes()
                    : p.getCantidadSandwiches();
            porLocal.merge(p.getLocal(), cantidad, Integer::sum);
        }
        return porLocal.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Ninguno");
    }

    // ---------- Dashboard ----------

    public String dashboard() {
        Map<String, Double> totales = new TreeMap<>();
        for (Pedido p : repo.obtenerTodos()) {
            totales.merge(p.getLocal(), p.calcularTotal(), Double::sum);
        }

        if (totales.isEmpty()) return "No hay datos para el dashboard.";

        double max = totales.values().stream()
                .mapToDouble(Double::doubleValue).max().orElse(1);

        StringBuilder sb = new StringBuilder();
        sb.append("=== DASHBOARD DE VENTAS POR LOCAL ===\n");

        for (Map.Entry<String, Double> e : totales.entrySet()) {
            int barra = (int) Math.round((e.getValue() / max) * 30);
            sb.append(String.format(Locale.US, "%-10s | %s $%.2f%n",
                    e.getKey(), "#".repeat(Math.max(barra, 1)), e.getValue()));
        }

        sb.append("\nProducto top: ").append(productoMasVendido()).append("\n");
        sb.append("Local con más ").append(productoMasVendido()).append(": ")
          .append(localDondeMasSeVendio(productoMasVendido()));
        return sb.toString();
    }
}
