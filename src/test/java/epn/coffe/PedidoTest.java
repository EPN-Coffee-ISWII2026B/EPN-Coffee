package epn.coffe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PedidoTest {

    private Pedido pedido;

    @BeforeEach
    public void setUp() {
        pedido = new Pedido();
    }

    @Test
    public void pedidoVacioDebeDarCero() {
        assertEquals(0.0, pedido.calcularTotal(), 0.001);
    }

    @Test
    public void unCafeDebeCostarUnoCincuenta() {
        pedido.agregarCafe();
        assertEquals(1.50, pedido.calcularTotal(), 0.001);
    }

    @Test
    public void unSandwichDebeCostarDosCincuenta() {
        pedido.agregarSandwich();
        assertEquals(2.50, pedido.calcularTotal(), 0.001);
    }

    @Test
    public void dosCafesYUnSandwichDebenDarCincoCincuenta() {
        pedido.agregarCafe();
        pedido.agregarCafe();
        pedido.agregarSandwich();
        assertEquals(5.50, pedido.calcularTotal(), 0.001);
    }

    @Test
    public void cincoProductosTienenDescuentoDel10() {
        for (int i = 0; i < 5; i++) pedido.agregarCafe();
        assertEquals(6.75, pedido.calcularTotal(), 0.001);
    }

    @Test
    public void pedidoGrandeTieneDescuento() {
        for (int i = 0; i < 9; i++) pedido.agregarSandwich();
        assertEquals(20.25, pedido.calcularTotal(), 0.001);
    }

    @Test
    public void cuatroProductosBaratosNoTienenDescuento() {
        for (int i = 0; i < 4; i++) pedido.agregarSandwich();
        assertEquals(10.00, pedido.calcularTotal(), 0.001);
    }
}
