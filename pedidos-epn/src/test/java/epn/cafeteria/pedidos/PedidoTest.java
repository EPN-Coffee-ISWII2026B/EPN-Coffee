package epn.cafeteria.pedidos;

import org.junit.jupiter.api.Test;

import epn.cafeteria.pedidos.Pedido;

import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PedidoTest {

    @Test
    void calculaSubtotalIvaYTotal() {
        Pedido p = new Pedido("Quito Norte", "Café Andino", "Café en grano",
                10, new BigDecimal("2.00"), LocalDate.of(2026, 10, 17));

        assertEquals(new BigDecimal("20.00"), p.subtotal());
        assertEquals(new BigDecimal("3.00"), p.iva());
        assertEquals(new BigDecimal("23.00"), p.total());
    }
}