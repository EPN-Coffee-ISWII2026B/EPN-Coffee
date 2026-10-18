package epn.coffe;

import java.time.LocalDate;
import java.util.Locale;

public class Pedido {

    public static final double PRECIO_CAFE = 1.50;
    public static final double PRECIO_SANDWICH = 2.50;
    public static final double LIMITE_DESCUENTO = 20.0;
    public static final double PORCENTAJE_DESCUENTO = 0.10;
    public static final int MINIMO_PRODUCTOS_DESCUENTO = 5;

    private int cantidadCafes;
    private int cantidadSandwiches;

    private LocalDate fecha = LocalDate.now();
    private String nombreCliente = "Cliente anónimo";
    private String local = "Sin asignar";

    public void agregarCafe()     { cantidadCafes++; }
    public void agregarSandwich() { cantidadSandwiches++; }

    public int getCantidadCafes()     { return cantidadCafes; }
    public int getCantidadSandwiches() { return cantidadSandwiches; }
    public int getCantidadTotalProductos() { return cantidadCafes + cantidadSandwiches; }

    public LocalDate getFecha()            { return fecha; }
    public void setFecha(LocalDate fecha)  { this.fecha = fecha; }

    public String getNombreCliente()          { return nombreCliente; }
    public void setNombreCliente(String n)    { this.nombreCliente = n; }

    public String getLocal()                  { return local; }
    public void setLocal(String l)            { this.local = l; }

    public double calcularSubtotal() {
        return (cantidadCafes * PRECIO_CAFE) + (cantidadSandwiches * PRECIO_SANDWICH);
    }

    public boolean aplicaDescuento() {
        return calcularSubtotal() > LIMITE_DESCUENTO
            || getCantidadTotalProductos() >= MINIMO_PRODUCTOS_DESCUENTO;
    }

    public double calcularTotal() {
        double subtotal = calcularSubtotal();
        return aplicaDescuento() ? subtotal - (subtotal * PORCENTAJE_DESCUENTO) : subtotal;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%s | %s | %s | %d café(s), %d sandwich(es) | $%.2f",
                fecha, nombreCliente, local,
                cantidadCafes, cantidadSandwiches, calcularTotal());
    }
}
