package epn.cafeteria.ventas;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * HU1: factura con numero, cliente, productos seleccionados y total de venta.
 * No registra ni solicita formas de pago.
 */
public class Factura {

    public static final String CONSUMIDOR_FINAL = "Consumidor Final";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final int numero;
    private final String nombreCliente;
    private final List<ItemVenta> items;
    private final LocalDateTime fechaEmision;

    public Factura(int numero, String nombreCliente, List<ItemVenta> items) {
        if (numero <= 0) {
            throw new IllegalArgumentException("El número de factura debe ser mayor a cero");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("La factura debe tener al menos un producto");
        }
        if (items.stream().anyMatch(i -> i == null)) {
            throw new IllegalArgumentException("La factura no puede contener productos nulos");
        }

        this.numero = numero;
        this.nombreCliente = (nombreCliente == null || nombreCliente.isBlank())
                ? CONSUMIDOR_FINAL : nombreCliente.trim();
        this.items = List.copyOf(items);
        this.fechaEmision = LocalDateTime.now();
    }

    public int getNumero() { return numero; }
    public String getNombreCliente() { return nombreCliente; }
    public List<ItemVenta> getItems() { return items; }
    public LocalDateTime getFechaEmision() { return fechaEmision; }

    public boolean esConsumidorFinal() {
        return CONSUMIDOR_FINAL.equalsIgnoreCase(nombreCliente);
    }

    /** Total de la venta: suma de los subtotales de cada producto. */
    public BigDecimal getTotal() {
        return items.stream().map(ItemVenta::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /** Vista previa de la factura en la interfaz Swing. */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("                   EPN - COFFEE                     \n");
        sb.append("                 FACTURA DE VENTA                   \n");
        sb.append("====================================================\n");
        sb.append(String.format(" Factura Nº      : %06d%n", numero));
        sb.append(String.format(" Fecha y Hora    : %s%n", fechaEmision.format(FORMATO_FECHA)));
        sb.append(String.format(" Cliente         : %s%n", nombreCliente));
        sb.append("----------------------------------------------------\n");
        sb.append(String.format(" %-22s %6s %10s %10s%n", "Producto", "Cant.", "P. Unit.", "Subtotal"));
        sb.append("----------------------------------------------------\n");
        for (ItemVenta item : items) {
            sb.append(String.format(" %-22s %6d %10.2f %10.2f%n",
                    item.nombreProducto(), item.cantidad(), item.precioUnitario(), item.subtotal()));
        }
        sb.append("====================================================\n");
        sb.append(String.format(" TOTAL DE LA VENTA:                      $%9.2f%n", getTotal()));
        sb.append("====================================================\n");
        return sb.toString();
    }
}
