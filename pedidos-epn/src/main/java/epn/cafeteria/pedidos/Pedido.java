 package epn.cafeteria.pedidos;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/** Modelo de un pedido de compra a un proveedor, solicitado por una sucursal. */
public record Pedido(String sucursal, String proveedor, String producto,
                     int cantidad, BigDecimal precioUnitario, LocalDate fecha) {

    /** Tasa de IVA (15 %). Cámbiala aquí si la tarifa varía. */
    public static final BigDecimal TASA_IVA = new BigDecimal("0.15");

    public BigDecimal subtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal iva() {
        return subtotal().multiply(TASA_IVA).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal total() {
        return subtotal().add(iva());
    }
}