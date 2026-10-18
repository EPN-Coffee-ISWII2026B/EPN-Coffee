package epn.cafeteria.ventas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Lógica de emisión de facturas para la historia de usuario HU1. */
public class VentasService {
    private final List<Factura> facturas = new ArrayList<>();
    private int secuenciaFactura = 1;

    /** Emite una factura a partir de los productos seleccionados, sin solicitar pago. */
    public Factura emitirFactura(String nombreCliente, List<ItemVenta> items) {
        Factura factura = new Factura(secuenciaFactura, nombreCliente, items);
        facturas.add(factura);
        secuenciaFactura++; // Solo se incrementa cuando la factura es válida.
        return factura;
    }

    public List<Factura> listarFacturas() {
        return Collections.unmodifiableList(facturas);
    }

    public Factura buscarFacturaPorNumero(int numero) {
        return facturas.stream().filter(f -> f.getNumero() == numero)
                .findFirst().orElse(null);
    }

    public int getTotalFacturas() { return facturas.size(); }

    public BigDecimal getMontoTotalVendido() {
        return facturas.stream().map(Factura::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
