 package epn.cafeteria.pedidos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/** Interfaz gráfica del módulo de pedidos. Contiene el main. */
public class PedidosFrame extends JFrame {

    // Paleta tomada de la interfaz de Inventario (espresso, café y crema)
    private static final Color ESPRESSO = new Color(0x1E1A1A);
    private static final Color PANEL_OSCURO = new Color(0xE8D5B7);
    // private static final Color CAFE = new Color(0x6F4A35);
    private static final Color CAFE = new Color(0xE8D5B7);

    
    private static final Color ACENTO = new Color(0x8B5E3C);
    private static final Color CREMA = new Color(0xF6EEDF);
    private static final Color Negro = new Color(0x000000);
    private static final Color ARENA = new Color(0xE7DAC0);
    // private static final Color TEXTO = new Color(0x2B2220);
    private static final Color TEXTO = new Color(0x00000);

    private static final Font FUENTE = new Font("SansSerif", Font.PLAIN, 16);
    private static final Font NEGRITA = FUENTE.deriveFont(Font.BOLD);
    private static final Font FUENTE_TABLA = new Font("SansSerif", Font.PLAIN, 15);

    private static final String[] SUCURSALES = {"Quito Norte", "Quito Sur", "Quito Centro", "Quito Valle"};
    private static final String[] COLUMNAS = {"Sucursal", "Proveedor", "Producto", "Cantidad",
            "P. Unitario", "Fecha", "Subtotal", "IVA", "Total"};
    private static final int[] ANCHOS = {130, 190, 170, 90, 110, 110, 100, 90, 100};

    private final PedidoServicio servicio;

    // Panel izquierdo (formulario)
    private final JComboBox<String> cbSucursal = new JComboBox<>(SUCURSALES);
    private final JComboBox<String> cbProveedor = new JComboBox<>(CatalogoProveedores.nombres());
    private final JLabel lblTelefono = new JLabel(" ");
    private final JComboBox<String> cbProducto = new JComboBox<>();
    private final JSpinner spCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
    private final JTextField txtPrecio = new JTextField();
    private final JTextField txtFecha = new JTextField(LocalDate.now().toString());

    // Centro (filtros y tabla)
    private final JComboBox<String> cbFiltroSucursal = new JComboBox<>();
    private final JTextField txtFiltro = new JTextField();
    private final DefaultTableModel modelo = new DefaultTableModel(COLUMNAS, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JLabel lblResumen = new JLabel(" ");

    public PedidosFrame() {
        super("Cafetería EPN · Pedidos");
        servicio = iniciarServicio();
        configurarVentana();
        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearPanelFormulario(), BorderLayout.WEST);
        add(crearPanelCentral(), BorderLayout.CENTER);
        cargarProductos();
        aplicarFiltro();
    }

    private PedidoServicio iniciarServicio() {
        try {
            return new PedidoServicio(new PedidoRepositorio("pedidos.csv"));
        } catch (PedidoException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error al iniciar", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return null;
        }
    }

    private void configurarVentana() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1320, 740);
        setMinimumSize(new Dimension(1100, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(CREMA);
        setLayout(new BorderLayout());
    }

    private JPanel crearEncabezado() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(ESPRESSO);
        barra.setBorder(new EmptyBorder(14, 24, 14, 24));
        JLabel titulo = new JLabel("Pedidos · Cafetería EPN");
        titulo.setFont(FUENTE.deriveFont(Font.BOLD, 26f));
        titulo.setForeground(Color.WHITE);
        barra.add(titulo, BorderLayout.WEST);
        return barra;
    }

    // ---------------------------------------------------------------- Panel izquierdo

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_OSCURO);
        panel.setBorder(new EmptyBorder(24, 24, 24, 24));
        panel.setPreferredSize(new Dimension(380, 0));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Nuevo pedido");
        titulo.setFont(FUENTE.deriveFont(Font.BOLD, 24f));
        titulo.setForeground(Negro);
        titulo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, ACENTO), new EmptyBorder(0, 0, 10, 0)));
        g.gridy = 0;
        panel.add(titulo, g);

        int fila = 1;
        fila = agregarCampo(panel, g, fila, "Sucursal", cbSucursal);
        fila = agregarCampo(panel, g, fila, "Proveedor", cbProveedor);

        lblTelefono.setFont(FUENTE.deriveFont(14f));
        lblTelefono.setForeground(ARENA);
        g.gridy = fila++;
        g.insets = new Insets(4, 2, 0, 0);
        panel.add(lblTelefono, g);

        fila = agregarCampo(panel, g, fila, "Nombre del producto", cbProducto);
        fila = agregarCampo(panel, g, fila, "Cantidad", spCantidad);
        fila = agregarCampo(panel, g, fila, "Precio unitario ($)", txtPrecio);
        fila = agregarCampo(panel, g, fila, "Fecha de pedido (AAAA-MM-DD)", txtFecha);

        cbProveedor.addActionListener(e -> cargarProductos());

        JButton btnGuardar = crearBoton("Guardar pedido", ACENTO, Color.WHITE);
        btnGuardar.addActionListener(e -> guardar());
        JButton btnLimpiar = crearBoton("Limpiar", ARENA, TEXTO);
        btnLimpiar.addActionListener(e -> limpiar());
        g.gridy = fila++;
        g.insets = new Insets(26, 0, 0, 0);
        panel.add(btnGuardar, g);
        g.gridy = fila++;
        g.insets = new Insets(12, 0, 0, 0);
        panel.add(btnLimpiar, g);

        // Empuja todo hacia arriba
        g.gridy = fila;
        g.weighty = 1;
        panel.add(Box.createGlue(), g);
        return panel;
    }

    private int agregarCampo(JPanel panel, GridBagConstraints g, int fila, String etiqueta, JComponent campo) {
        JLabel l = new JLabel(etiqueta);
        l.setFont(NEGRITA);
        l.setForeground(Color.BLACK);
        // l.setForeground(CREMA);

        g.gridy = fila++;
        g.insets = new Insets(14, 0, 5, 0);
        panel.add(l, g);

        estilizarCampo(campo);
        g.gridy = fila++;
        g.insets = new Insets(0, 0, 0, 0);
        panel.add(campo, g);
        return fila;
    }

    private void estilizarCampo(JComponent c) {
        c.setFont(FUENTE);
        c.setPreferredSize(new Dimension(280, 42));
        if (c instanceof JTextField) {
            c.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(CAFE, 1), new EmptyBorder(0, 10, 0, 10)));
        } else if (c instanceof JComboBox) {
            c.setBackground(Color.WHITE);
        } else if (c instanceof JSpinner spinner) {
            ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setFont(FUENTE);
        }
    }

    private JButton crearBoton(String texto, Color fondo, Color letra) {
        JButton b = new JButton(texto);
        b.setFont(NEGRITA);
        b.setBackground(fondo);
        b.setForeground(letra);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(280, 46));
        return b;
    }

    // ---------------------------------------------------------------- Centro

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(CREMA);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.add(crearBarraFiltros(), BorderLayout.NORTH);
        panel.add(crearTabla(), BorderLayout.CENTER);

        lblResumen.setOpaque(true);
        lblResumen.setBackground(ESPRESSO);
        lblResumen.setForeground(Color.WHITE);
        lblResumen.setFont(NEGRITA);
        lblResumen.setBorder(new EmptyBorder(11, 16, 11, 16));
        panel.add(lblResumen, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearBarraFiltros() {
        cbFiltroSucursal.addItem("Todas");
        for (String s : SUCURSALES) {
            cbFiltroSucursal.addItem(s);
        }
        cbFiltroSucursal.setFont(FUENTE);
        cbFiltroSucursal.setBackground(Color.WHITE);
        cbFiltroSucursal.setPreferredSize(new Dimension(170, 40));
        estilizarCampo(txtFiltro);
        txtFiltro.setPreferredSize(new Dimension(240, 40));

        JButton btnFiltrar = crearBoton("Filtrar", CAFE, Color.WHITE);
        btnFiltrar.setPreferredSize(new Dimension(130, 40));
        btnFiltrar.addActionListener(e -> aplicarFiltro());

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 12));
        barra.setBackground(CREMA);
        barra.setBorder(BorderFactory.createLineBorder(CAFE, 2));
        barra.add(etiquetaFiltro("Sucursal:"));
        barra.add(cbFiltroSucursal);
        barra.add(etiquetaFiltro("Producto / Proveedor:"));
        barra.add(txtFiltro);
        barra.add(btnFiltrar);
        return barra;
    }

    private JLabel etiquetaFiltro(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(NEGRITA);
        l.setForeground(TEXTO);
        return l;
    }

    private JScrollPane crearTabla() {
        JTable tabla = new JTable(modelo);
        tabla.setFont(FUENTE_TABLA);
        tabla.setRowHeight(36);
        tabla.setGridColor(new Color(0xD6C6A5));
        tabla.setShowHorizontalLines(false);
        tabla.setDefaultRenderer(Object.class, new FilasRenderer());
        for (int i = 0; i < ANCHOS.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(ANCHOS[i]);
        }

        var encabezado = tabla.getTableHeader();
        encabezado.setReorderingAllowed(false);
        encabezado.setPreferredSize(new Dimension(encabezado.getPreferredSize().width, 42));
        encabezado.setDefaultRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(CENTER);
                setOpaque(true);
            }

            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(t, v, s, f, r, c);
                setBackground(ESPRESSO);
                setForeground(Color.WHITE);
                setFont(NEGRITA);
                setBorder(BorderFactory.createMatteBorder(0, 0, 3, 1, CAFE));
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(CREMA);
        scroll.setBorder(BorderFactory.createLineBorder(CAFE, 2));
        return scroll;
    }

    /** Filas alternadas crema/arena, selección café y números alineados a la derecha. */
    private static class FilasRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foco, int fila, int col) {
            super.getTableCellRendererComponent(t, v, sel, false, fila, col);
            setBorder(new EmptyBorder(0, 10, 0, 10));
            if (sel) {
                setBackground(CAFE);
                setForeground(Color.WHITE);
            } else {
                setBackground(fila % 2 == 0 ? CREMA : ARENA);
                setForeground(TEXTO);
            }
            boolean numerica = col == 3 || col == 4 || col >= 6;
            setHorizontalAlignment(numerica ? RIGHT : LEFT);
            return this;
        }
    }

    // ---------------------------------------------------------------- Acciones

    private void cargarProductos() {
        String proveedor = (String) cbProveedor.getSelectedItem();
        cbProducto.setModel(new DefaultComboBoxModel<>(CatalogoProveedores.productosDe(proveedor)));
        lblTelefono.setText("Teléfono: " + CatalogoProveedores.telefonoDe(proveedor));
    }

    private void guardar() {
        try {
            servicio.registrar(String.valueOf(cbSucursal.getSelectedItem()),
                    String.valueOf(cbProveedor.getSelectedItem()),
                    String.valueOf(cbProducto.getSelectedItem()),
                    String.valueOf(spCantidad.getValue()), txtPrecio.getText(), txtFecha.getText());
            limpiarFormulario();
            aplicarFiltro();
        } catch (PedidoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Datos inválidos", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void aplicarFiltro() {
        String sucursal = cbFiltroSucursal.getSelectedIndex() <= 0 ? "" : (String) cbFiltroSucursal.getSelectedItem();
        List<Pedido> lista = servicio.filtrar(sucursal, txtFiltro.getText());
        modelo.setRowCount(0);
        BigDecimal totalIva = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        for (Pedido p : lista) {
            modelo.addRow(new Object[]{p.sucursal(), p.proveedor(), p.producto(), p.cantidad(),
                    p.precioUnitario().setScale(2, RoundingMode.HALF_UP), p.fecha(),
                    p.subtotal(), p.iva(), p.total()});
            totalIva = totalIva.add(p.iva());
            total = total.add(p.total());
        }
        lblResumen.setText("Pedidos: " + lista.size() + "     |     IVA total: $" + totalIva
                + "     |     Total a pagar: $" + total);
    }

    private void limpiarFormulario() {
        cbSucursal.setSelectedIndex(0);
        cbProveedor.setSelectedIndex(0);
        cargarProductos();
        spCantidad.setValue(1);
        txtPrecio.setText("");
        txtFecha.setText(LocalDate.now().toString());
    }

    private void limpiar() {
        limpiarFormulario();
        cbFiltroSucursal.setSelectedIndex(0);
        txtFiltro.setText("");
        aplicarFiltro();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PedidosFrame().setVisible(true));
    }
}