package epn.cafeteria.ventas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Interfaz gráfica Swing para el Módulo de Ventas de EPN-Coffee.
 *
 * HU1 (Barista): seleccionar productos y emitir factura con número, cliente y total.
 * Permite facturar a Consumidor Final. No implementa métodos de pago.
 */
public class VentasFrame extends JFrame {

    static final Color BEIGE = new Color(0xF5F0E1);
    static final Color BEIGE_OSCURO = new Color(0xE6DCC3);
    static final Color CAFE = new Color(0x6F4E37);
    static final Color NEGRO = new Color(0x1A1A1A);
    static final Font FUENTE = new Font("SansSerif", Font.PLAIN, 14);
    static final Font TITULO = new Font("SansSerif", Font.BOLD, 20);

    private final VentasService ventasService = new VentasService();
    private final List<ItemVenta> carrito = new ArrayList<>();

    // Componentes para selección de productos
    private final JComboBox<String> cbProductos = new JComboBox<>(new String[]{
            "Café Espresso ($1.50)",
            "Café Americano ($1.25)",
            "Capuchino ($2.00)",
            "Latte ($2.25)",
            "Mocaccino ($2.50)",
            "Té de Manzanilla ($1.00)",
            "Chocolate Caliente ($1.75)",
            "Pan de Queso ($0.75)",
            "Croissant ($1.50)",
            "Tarta de Manzana ($2.50)"
    });
    private final JSpinner spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    private final DefaultTableModel modeloCarrito = modelo("Producto", "Cant.", "P. Unitario ($)", "Subtotal ($)");

    // Datos del cliente
    private final JTextField txtCliente = campo();
    private final JCheckBox chkConsumidorFinal = new JCheckBox("Consumidor Final");

    // Visualización
    private final JLabel lblTotal = new JLabel("Total: $0.00");
    private final JTextArea areaFactura = new JTextArea(14, 42);
    private final DefaultTableModel modeloHistorial = modelo("Nº", "Cliente", "Total ($)");

    public VentasFrame() {
        super("Cafetería EPN · Módulo de Ventas");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1050, 700);
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBackground(BEIGE);

        // Encabezado
        JLabel titulo = new JLabel("  ☕ EPN-Coffee · Módulo de Ventas");
        titulo.setFont(TITULO);
        titulo.setForeground(BEIGE);
        titulo.setOpaque(true);
        titulo.setBackground(NEGRO);
        titulo.setBorder(new EmptyBorder(14, 10, 14, 10));
        raiz.add(titulo, BorderLayout.NORTH);

        // Panel Central dividido: Izquierda (Selección y Carrito) - Derecha (Factura e Historial)
        JPanel centro = new JPanel(new GridLayout(1, 2, 12, 12));
        centro.setBackground(BEIGE);
        centro.setBorder(new EmptyBorder(10, 12, 10, 12));

        centro.add(construirPanelVenta());
        centro.add(construirPanelFacturacion());

        raiz.add(centro, BorderLayout.CENTER);
        setContentPane(raiz);
    }

    private JPanel construirPanelVenta() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(BEIGE);

        // 1. Selector de productos
        JPanel panelSeleccion = new JPanel(new GridBagLayout());
        panelSeleccion.setBackground(BEIGE);
        panelSeleccion.setBorder(crearBordeTitulado("1. Seleccionar Producto"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 6, 5, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; panelSeleccion.add(etiqueta("Producto:"), g);
        cbProductos.setFont(FUENTE);
        g.gridx = 1; g.weightx = 1; panelSeleccion.add(cbProductos, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0; panelSeleccion.add(etiqueta("Cantidad:"), g);
        spnCantidad.setFont(FUENTE);
        g.gridx = 1; panelSeleccion.add(spnCantidad, g);

        JButton btnAgregar = boton("+ Agregar al Carrito");
        btnAgregar.addActionListener(e -> agregarItem());
        g.gridx = 0; g.gridy = 2; g.gridwidth = 2; panelSeleccion.add(btnAgregar, g);

        // 2. Tabla de Carrito
        JPanel panelCarrito = new JPanel(new BorderLayout(4, 4));
        panelCarrito.setBackground(BEIGE);
        panelCarrito.setBorder(crearBordeTitulado("2. Carrito de la Venta Actual"));
        panelCarrito.add(scrollTabla(modeloCarrito), BorderLayout.CENTER);

        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTotal.setBackground(BEIGE);
        lblTotal.setFont(FUENTE.deriveFont(Font.BOLD, 18f));
        lblTotal.setForeground(CAFE);
        JButton btnVaciar = boton("Vaciar Carrito");
        btnVaciar.setBackground(new Color(0x9E, 0x2A, 0x2B));
        btnVaciar.addActionListener(e -> vaciarCarrito());
        panelTotal.add(btnVaciar);
        panelTotal.add(lblTotal);
        panelCarrito.add(panelTotal, BorderLayout.SOUTH);

        panel.add(panelSeleccion, BorderLayout.NORTH);
        panel.add(panelCarrito, BorderLayout.CENTER);
        return panel;
    }

    private JPanel construirPanelFacturacion() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(BEIGE);

        // Datos del Cliente (HU1)
        JPanel panelDatos = new JPanel(new GridBagLayout());
        panelDatos.setBackground(BEIGE);
        panelDatos.setBorder(crearBordeTitulado("3. Datos de Facturación (HU1)"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        // Cliente
        g.gridx = 0; g.gridy = 0; panelDatos.add(etiqueta("Nombre Cliente:"), g);
        g.gridx = 1; g.weightx = 1; panelDatos.add(txtCliente, g);

        // Consumidor Final
        chkConsumidorFinal.setFont(FUENTE);
        chkConsumidorFinal.setBackground(BEIGE);
        chkConsumidorFinal.addActionListener(e -> {
            boolean selec = chkConsumidorFinal.isSelected();
            txtCliente.setEnabled(!selec);
            if (selec) {
                txtCliente.setText("Consumidor Final");
            } else {
                txtCliente.setText("");
            }
        });
        g.gridx = 1; g.gridy = 1; panelDatos.add(chkConsumidorFinal, g);

        JButton btnEmitir = boton("✔ Emitir Factura (Barista)");
        btnEmitir.setFont(FUENTE.deriveFont(Font.BOLD, 15f));
        btnEmitir.addActionListener(e -> emitirFactura());
        g.gridx = 0; g.gridy = 2; g.gridwidth = 2; panelDatos.add(btnEmitir, g);

        // Vista previa de factura y pestaña de historial
        JTabbedPane tabsFacturas = new JTabbedPane();
        tabsFacturas.setFont(FUENTE.deriveFont(Font.BOLD));
        tabsFacturas.setBackground(CAFE);
        tabsFacturas.setForeground(NEGRO);

        areaFactura.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaFactura.setEditable(false);
        areaFactura.setBackground(Color.WHITE);
        areaFactura.setForeground(NEGRO);
        areaFactura.setText("\n  La factura emitida se mostrará aquí.\n");
        tabsFacturas.addTab("Factura Actual", new JScrollPane(areaFactura));
        tabsFacturas.addTab("Historial de Facturas", scrollTabla(modeloHistorial));

        panel.add(panelDatos, BorderLayout.NORTH);
        panel.add(tabsFacturas, BorderLayout.CENTER);
        return panel;
    }

    private void agregarItem() {
        String seleccion = (String) cbProductos.getSelectedItem();
        if (seleccion == null) return;

        // Parsear nombre y precio desde el combo: ej "Capuchino ($2.00)"
        int inicioPrecio = seleccion.indexOf("($");
        int finPrecio = seleccion.indexOf(")");
        String nombre = seleccion.substring(0, inicioPrecio).trim();
        String precioStr = seleccion.substring(inicioPrecio + 2, finPrecio).trim();
        BigDecimal precio = new BigDecimal(precioStr);
        int cantidad = (Integer) spnCantidad.getValue();

        try {
            ItemVenta nuevoItem = new ItemVenta(nombre, cantidad, precio);
            carrito.add(nuevoItem);
            modeloCarrito.addRow(new Object[]{
                    nuevoItem.nombreProducto(),
                    nuevoItem.cantidad(),
                    String.format("%.2f", nuevoItem.precioUnitario()),
                    String.format("%.2f", nuevoItem.subtotal())
            });
            actualizarTotal();
            spnCantidad.setValue(1);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al agregar", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void vaciarCarrito() {
        carrito.clear();
        modeloCarrito.setRowCount(0);
        actualizarTotal();
    }

    private void actualizarTotal() {
        BigDecimal total = carrito.stream()
                .map(ItemVenta::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        lblTotal.setText(String.format("Total: $%.2f", total));
    }

    private void emitirFactura() {
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe agregar al menos un producto al carrito.", "Carrito vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cliente = txtCliente.getText();
        if (chkConsumidorFinal.isSelected() || cliente == null || cliente.isBlank()) {
            cliente = Factura.CONSUMIDOR_FINAL;
        }

        try {
            Factura factura = ventasService.emitirFactura(cliente, new ArrayList<>(carrito));
            areaFactura.setText(factura.toString());

            // Agregar a historial
            modeloHistorial.addRow(new Object[]{
                    String.format("%06d", factura.getNumero()),
                    factura.getNombreCliente(),
                    String.format("%.2f", factura.getTotal())
            });

            JOptionPane.showMessageDialog(this,
                    String.format("Factura Nº %06d emitida con éxito.\nTotal: $%.2f\nCliente: %s",
                            factura.getNumero(), factura.getTotal(), factura.getNombreCliente()),
                    "Factura Emitida", JOptionPane.INFORMATION_MESSAGE);

            // Limpiar venta actual
            vaciarCarrito();
            if (!chkConsumidorFinal.isSelected()) {
                txtCliente.setText("");
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al emitir factura", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Utilidades de diseño ----------
    private static TitledBorder crearBordeTitulado(String titulo) {
        return BorderFactory.createTitledBorder(
                new LineBorder(CAFE, 2),
                titulo,
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                FUENTE.deriveFont(Font.BOLD),
                CAFE
        );
    }

    private static JTextField campo() {
        JTextField t = new JTextField();
        t.setFont(FUENTE);
        t.setBackground(Color.WHITE);
        t.setForeground(NEGRO);
        t.setCaretColor(NEGRO);
        t.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CAFE), new EmptyBorder(4, 6, 4, 6)));
        return t;
    }

    private static JLabel etiqueta(String s) {
        JLabel l = new JLabel(s);
        l.setFont(FUENTE.deriveFont(Font.BOLD));
        l.setForeground(NEGRO);
        return l;
    }

    private static JButton boton(String s) {
        JButton b = new JButton(s);
        b.setFont(FUENTE.deriveFont(Font.BOLD));
        b.setBackground(CAFE);
        b.setForeground(BEIGE);
        b.setOpaque(true);
        b.setBorder(new EmptyBorder(8, 14, 8, 14));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private static DefaultTableModel modelo(String... cols) {
        return new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    private static JScrollPane scrollTabla(DefaultTableModel m) {
        JTable t = new JTable(m);
        t.setFont(FUENTE);
        t.setRowHeight(26);
        t.setBackground(BEIGE);
        t.setForeground(NEGRO);
        t.setGridColor(BEIGE_OSCURO);
        t.setSelectionBackground(CAFE);
        t.setSelectionForeground(BEIGE);
        DefaultTableCellRenderer zebra = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable tb, Object v,
                                                                      boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(tb, v, sel, foc, r, c);
                if (!sel) {
                    setBackground(r % 2 == 0 ? BEIGE : BEIGE_OSCURO);
                    setForeground(NEGRO);
                }
                return this;
            }
        };
        t.setDefaultRenderer(Object.class, zebra);
        JTableHeader h = t.getTableHeader();
        h.setFont(FUENTE.deriveFont(Font.BOLD));
        h.setBackground(NEGRO);
        h.setForeground(BEIGE);
        h.setReorderingAllowed(false);
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(new LineBorder(CAFE, 1));
        sp.getViewport().setBackground(BEIGE);
        return sp;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentasFrame().setVisible(true));
    }
}
