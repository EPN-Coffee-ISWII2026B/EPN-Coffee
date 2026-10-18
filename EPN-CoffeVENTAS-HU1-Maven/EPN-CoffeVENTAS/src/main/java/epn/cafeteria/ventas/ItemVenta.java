package epn.cafeteria.ventas;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Ítem de la venta: producto seleccionado, cantidad y precio unitario.
 * Requisito: "Se debe seleccionar el producto".
 */
public record ItemVenta(String nombreProducto, int cantidad, BigDecimal precioUnitario) {

    public ItemVenta {
        if (nombreProducto == null || nombreProducto.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo");
        }
        nombreProducto = nombreProducto.trim();
        precioUnitario = precioUnitario.setScale(2, RoundingMode.HALF_UP);
    }

    /** Subtotal del ítem (cantidad * precioUnitario). */
    public BigDecimal subtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
