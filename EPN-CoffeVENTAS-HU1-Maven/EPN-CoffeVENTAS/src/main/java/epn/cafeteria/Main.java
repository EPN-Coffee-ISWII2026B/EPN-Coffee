package epn.cafeteria;

import epn.cafeteria.ventas.VentasFrame;
import javax.swing.SwingUtilities;

/** Punto de entrada del modulo de Ventas: solamente HU1. */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentasFrame().setVisible(true));
    }
}
