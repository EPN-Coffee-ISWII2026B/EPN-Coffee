package epn.coffe;

import java.util.List;
import java.util.Locale;

public class Main {

    public static void main(String[] args) {
        LectorPedidos lector = new LectorPedidos();
        String ruta = "datos/pedidos.txt";

        try {
            List<Pedido> pedidos = lector.leer(ruta);

            System.out.println("=== PEDIDOS RECUPERADOS DESDE " + ruta + " ===");
            System.out.println("Total: " + pedidos.size() + " pedidos\n");

            double ingresoTotal = 0;
            for (Pedido p : pedidos) {
                System.out.println("  " + p);
                ingresoTotal += p.calcularTotal();
            }

            System.out.println("\nIngreso total: $" + String.format(Locale.US, "%.2f", ingresoTotal));

        } catch (Exception e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }
}
