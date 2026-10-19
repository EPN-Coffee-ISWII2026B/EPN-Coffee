package epn.cafeteria.inventario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/** Interfaz Swing del módulo Inventario. Paleta: beige, negro y café. */
public class InventarioFrame extends JFrame {

    static final Color BEIGE = new Color(0xF5F0E1);
    static final Color BEIGE_OSCURO = new Color(0xE6DCC3);
    static final Color CAFE = new Color(0x6F4E37);
    static final Color NEGRO = new Color(0x1A1A1A);
    static final Font FUENTE = new Font("SansSerif", Font.PLAIN, 14);
    static final Font TITULO = new Font("SansSerif", Font.BOLD, 20);

    private final InventarioService service = new InventarioService(Path.of("data", "inventario.csv"));

    // Proveedores
    private final JTextField txtProveedor = campo();
    private final JTextField txtTelefono = campo();
    private final JTextField txtProducto = campo();
    private final JSpinner spnCantidad = new JSpinner(new SpinnerNumberModel(1, 0, 100000, 1));
    private final DefaultListModel<String> modeloPendientes = new DefaultListModel<>();
    private final List<Producto> pendientes = new ArrayList<>();
    private final DefaultTableModel modeloProveedores =
            modelo("Proveedor", "Teléfono", "Productos que suministra");

    public InventarioFrame() {
        super("Cafetería EPN · Inventario");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(860, 620);
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(BEIGE);

        JLabel titulo = new JLabel("  ☕ Inventario · Cafetería EPN");
        titulo.setFont(TITULO);
        titulo.setForeground(BEIGE);
        titulo.setOpaque(true);
        titulo.setBackground(NEGRO);
        titulo.setBorder(new EmptyBorder(14, 10, 14, 10));
        raiz.add(titulo, BorderLayout.NORTH);

        raiz.add(panelProveedores(), BorderLayout.CENTER);
        setContentPane(raiz);
        service.listarProveedores().forEach(this::agregarFila);
    }

    // ---------- Panel Proveedores (HU1) ----------
    private JPanel panelProveedores() {
        JPanel p = fondo(new BorderLayout(10, 10));

        JPanel form = fondo(new GridBagLayout());
        form.setBorder(new LineBorder(CAFE, 2));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; form.add(etiqueta("Proveedor:"), g);
        g.gridx = 1; g.weightx = 1; form.add(txtProveedor, g);
        g.gridx = 2; g.weightx = 0; form.add(etiqueta("Teléfono:"), g);
        g.gridx = 3; g.weightx = 1; form.add(txtTelefono, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0; form.add(etiqueta("Producto:"), g);
        g.gridx = 1; g.weightx = 1; form.add(txtProducto, g);
        g.gridx = 2; g.weightx = 0; form.add(etiqueta("Cantidad:"), g);
        spnCantidad.setFont(FUENTE);
        g.gridx = 3; g.weightx = 1; form.add(spnCantidad, g);

        JButton btnAgregar = boton("+ Agregar producto");
        btnAgregar.addActionListener(e -> agregarPendiente());
        JList<String> lista = new JList<>(modeloPendientes);
        lista.setFont(FUENTE);
        lista.setBackground(BEIGE_OSCURO);
        lista.setForeground(NEGRO);
        JScrollPane sp = new JScrollPane(lista);
        sp.setPreferredSize(new Dimension(100, 70));
        sp.setBorder(new LineBorder(CAFE));

        g.gridx = 0; g.gridy = 2; g.gridwidth = 1; form.add(btnAgregar, g);
        g.gridx = 1; g.gridwidth = 3; form.add(sp, g);

        JButton btnRegistrar = boton("Registrar proveedor");
        btnRegistrar.addActionListener(e -> registrarProveedor());
        g.gridx = 0; g.gridy = 3; g.gridwidth = 4; form.add(btnRegistrar, g);

        JButton btnActualizar = boton("Agregar productos a proveedor existente");
        btnActualizar.addActionListener(e -> agregarProductosExistente());
        g.gridy = 4; form.add(btnActualizar, g);

        JScrollPane scrollProveedores = scrollTabla(modeloProveedores);
        JTable tablaProveedores = (JTable) scrollProveedores.getViewport().getView();
        tablaProveedores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProveedores.setToolTipText("Seleccione un proveedor para cargar su nombre y teléfono");
        tablaProveedores.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int fila = tablaProveedores.getSelectedRow();
            if (fila < 0) return;
            int indice = tablaProveedores.convertRowIndexToModel(fila);
            txtProveedor.setText(modeloProveedores.getValueAt(indice, 0).toString());
            txtTelefono.setText(modeloProveedores.getValueAt(indice, 1).toString());
        });

        JLabel ayuda = etiqueta("Seleccione un proveedor de la tabla para cargar sus datos.");
        JPanel listado = fondo(new BorderLayout(0, 6));
        listado.setBorder(new EmptyBorder(0, 0, 0, 0));
        listado.add(ayuda, BorderLayout.NORTH);
        listado.add(scrollProveedores, BorderLayout.CENTER);

        p.add(form, BorderLayout.NORTH);
        p.add(listado, BorderLayout.CENTER);
        return p;
    }

    private void agregarPendiente() {
        try {
            Producto prod = new Producto(txtProducto.getText(), (Integer) spnCantidad.getValue());
            pendientes.add(prod);
            modeloPendientes.addElement(prod.nombre() + "  ×" + prod.cantidad());
            txtProducto.setText("");
            spnCantidad.setValue(1);
            txtProducto.requestFocus();
        } catch (IllegalArgumentException ex) {
            aviso(ex.getMessage());
        }
    }

    private void registrarProveedor() {
        try {
            Proveedor pr = service.registrarProveedor(
                    txtProveedor.getText(), txtTelefono.getText(), pendientes);
            agregarFila(pr);
            txtProveedor.setText("");
            txtTelefono.setText("");
            pendientes.clear();
            modeloPendientes.clear();
            JOptionPane.showMessageDialog(this, "Proveedor registrado correctamente");
        } catch (IllegalArgumentException | IllegalStateException | java.io.UncheckedIOException ex) {
            aviso(ex.getMessage());
        }
    }

    private void agregarProductosExistente() {
        try {
            service.agregarProductos(txtProveedor.getText(), pendientes);
            modeloProveedores.setRowCount(0);
            service.listarProveedores().forEach(this::agregarFila);
            txtProveedor.setText("");
            txtTelefono.setText("");
            pendientes.clear();
            modeloPendientes.clear();
            JOptionPane.showMessageDialog(this, "Productos agregados correctamente");
        } catch (IllegalArgumentException | java.io.UncheckedIOException ex) {
            aviso(ex.getMessage());
        } catch (ArithmeticException ex) {
            aviso("La cantidad total del producto es demasiado grande");
        }
    }

    private void agregarFila(Proveedor pr) {
        StringJoiner sj = new StringJoiner(", ");
        pr.productos().forEach(x -> sj.add(x.nombre() + " (" + x.cantidad() + ")"));
        modeloProveedores.addRow(new Object[]{pr.nombre(), pr.telefono(), sj.toString()});
    }

    // ---------- Utilidades de estilo ----------
    private static JPanel fondo(LayoutManager lm) {
        JPanel p = new JPanel(lm);
        p.setBackground(BEIGE);
        p.setBorder(new EmptyBorder(12, 12, 12, 12));
        return p;
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
        b.setBorder(new EmptyBorder(8, 16, 8, 16));
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
        t.setRowHeight(28);
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
        sp.setBorder(new LineBorder(CAFE, 2));
        sp.getViewport().setBackground(BEIGE);
        return sp;
    }

    private void aviso(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        UIManager.put("OptionPane.background", BEIGE);
        UIManager.put("Panel.background", BEIGE);
        UIManager.put("OptionPane.messageForeground", NEGRO);
        SwingUtilities.invokeLater(() -> new InventarioFrame().setVisible(true));
    }
}
