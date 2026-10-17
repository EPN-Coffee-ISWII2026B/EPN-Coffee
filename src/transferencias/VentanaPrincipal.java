package transferencias;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Interfaz grafica (Swing). Toda la logica vive en SistemaTransferencias y CargadorCsv. */
public class VentanaPrincipal extends JFrame {
    private SistemaTransferencias sistema;
    private final DefaultTableModel modelo = new DefaultTableModel() {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTextArea alertas = new JTextArea(6, 50);
    private final JTextArea historial = new JTextArea(6, 50);
    private final JComboBox<String> cbOrigen = new JComboBox<>();
    private final JComboBox<String> cbDestino = new JComboBox<>();
    private final JComboBox<String> cbProducto = new JComboBox<>();
    private final JSpinner spCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));

    public VentanaPrincipal(SistemaTransferencias sistema) {
        super("Transferencias Matriz - Sucursales");
        this.sistema = sistema;

        JTable tabla = new JTable(modelo) {
            @Override public Component prepareRenderer(TableCellRenderer r, int fila, int col) {
                Component c = super.prepareRenderer(r, fila, col);
                c.setBackground(Color.WHITE);
                c.setForeground(Color.BLACK);
                if (col > 1) { // columnas 0 y 1 = ID y Producto
                    Sucursal s = VentanaPrincipal.this.sistema.todas().get(col - 2);
                    String p = (String) getValueAt(fila, 1);
                    if (s.estaVacio(p)) c.setBackground(new Color(255, 150, 150));            // VACIO
                    else if (!s.isMatriz() && s.getStock(p) < s.getMinimo(p))
                        c.setBackground(new Color(255, 220, 150));                              // bajo minimo
                }
                return c;
            }
        };
        tabla.setRowHeight(24);

        JButton btnCsv = new JButton("Cargar CSV");
        JButton btnProducto = new JButton("Agregar producto");
        JButton btnStock = new JButton("Agregar stock");
        btnCsv.addActionListener(e -> cargarCsv());
        btnProducto.addActionListener(e -> agregarProducto());
        btnStock.addActionListener(e -> agregarStock());
        JButton btnSugerir = new JButton("Sugerir origen");
        JButton btnSolicitar = new JButton("Solicitar transferencia");
        btnSugerir.addActionListener(e -> sugerirOrigen());
        btnSolicitar.addActionListener(e -> solicitar());

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Origen:"));  form.add(cbOrigen);
        form.add(new JLabel("Destino:")); form.add(cbDestino);
        form.add(new JLabel("Producto:")); form.add(cbProducto);
        form.add(new JLabel("Cantidad:")); form.add(spCantidad);
        form.add(btnSugerir); form.add(btnSolicitar);

        JPanel inventario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inventario.add(new JLabel("Inventario:"));
        inventario.add(btnCsv);
        inventario.add(btnProducto);
        inventario.add(btnStock);

        alertas.setEditable(false);
        alertas.setForeground(new Color(170, 0, 0));
        historial.setEditable(false);
        JPanel abajo = new JPanel(new GridLayout(1, 2));
        abajo.add(titulado("Sucursales VACIAS - debes enviar", new JScrollPane(alertas)));
        abajo.add(titulado("Historial de transferencias", new JScrollPane(historial)));

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(titulado("Inventario (rojo = vacio, naranja = bajo minimo)", new JScrollPane(tabla)),
                BorderLayout.CENTER);
        JPanel controles = new JPanel(new GridLayout(2, 1));
        controles.add(inventario);
        controles.add(form);
        centro.add(controles, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(centro, BorderLayout.CENTER);
        add(abajo, BorderLayout.SOUTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 620);
        setLocationRelativeTo(null);
        refrescar();
    }

    private JPanel titulado(String titulo, JComponent c) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(titulo));
        p.add(c);
        return p;
    }

    private void cargarCsv() {
        JFileChooser selector = new JFileChooser(".");
        selector.setDialogTitle("Selecciona el CSV de inventario");
        selector.setFileFilter(new FileNameExtensionFilter("Archivos CSV", "csv"));
        if (selector.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            SistemaTransferencias nuevo = CargadorCsv.cargar(selector.getSelectedFile().toPath());
            sistema = nuevo;
        } catch (ErrorCsvException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage() + "\n\nFormato esperado:\n"
                    + CargadorCsv.ENCABEZADO, "No se pudo cargar el CSV", JOptionPane.ERROR_MESSAGE);
            return;
        }
        refrescar();
    }

    private void agregarProducto() {
        JTextField id = new JTextField(15);
        JTextField nombre = new JTextField(15);
        JSpinner minimo = new JSpinner(new SpinnerNumberModel(1, 0, 100000, 1));
        Object[] campos = {"ID del producto:", id, "Nombre del producto:", nombre,
                "Stock minimo por sucursal:", minimo};
        if (JOptionPane.showConfirmDialog(this, campos, "Agregar producto",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        try {
            sistema.registrarProducto(id.getText(), nombre.getText(), (Integer) minimo.getValue());
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo agregar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        refrescar();
    }

    private void agregarStock() {
        if (sistema.productos().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero carga un CSV o agrega un producto.", "Sin productos",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JComboBox<String> ubicacion = new JComboBox<>();
        for (Sucursal x : sistema.todas()) ubicacion.addItem(x.getNombre());
        JComboBox<String> producto = new JComboBox<>(sistema.productos().toArray(new String[0]));
        JSpinner cantidad = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
        Object[] campos = {"Ubicacion:", ubicacion, "Producto:", producto, "Cantidad a sumar:", cantidad};
        if (JOptionPane.showConfirmDialog(this, campos, "Agregar stock",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        try {
            sistema.agregarStock((String) ubicacion.getSelectedItem(), (String) producto.getSelectedItem(),
                    (Integer) cantidad.getValue());
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo agregar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        refrescar();
    }

    private void sugerirOrigen() {
        String destino = (String) cbDestino.getSelectedItem();
        String producto = (String) cbProducto.getSelectedItem();
        if (destino == null || producto == null) return;
        int cantidad = (Integer) spCantidad.getValue();
        Sucursal o = sistema.sugerirOrigen(destino, producto, cantidad);
        if (o == null)
            JOptionPane.showMessageDialog(this, "Nadie puede abastecer " + cantidad + " de " + producto,
                    "Sin origen disponible", JOptionPane.WARNING_MESSAGE);
        else cbOrigen.setSelectedItem(o.getNombre());
    }

    private void solicitar() {
        if (cbProducto.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "No hay productos cargados.", "Sin productos",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Transferencia t;
        try {
            t = sistema.solicitar((String) cbOrigen.getSelectedItem(), (String) cbDestino.getSelectedItem(),
                    (String) cbProducto.getSelectedItem(), (Integer) spCantidad.getValue());
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se puede solicitar",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        // CONFIRMACION DEL CLIENTE: Aceptar / Denegar
        Object[] opciones = {"Aceptar", "Denegar"};
        int r = JOptionPane.showOptionDialog(this,
                "El cliente debe confirmar esta transferencia:\n\n" + t.descripcion(),
                "Confirmar transferencia", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, opciones, opciones[1]);
        try {
            if (r == 0) sistema.aceptar(t.getId()); else sistema.rechazar(t.getId());
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        refrescar();
    }

    private void llenarCombo(JComboBox<String> combo, Collection<String> items) {
        Object seleccionado = combo.getSelectedItem();
        combo.removeAllItems();
        for (String i : items) combo.addItem(i);
        if (seleccionado != null) combo.setSelectedItem(seleccionado);
    }

    private void refrescar() {
        List<String> todas = new ArrayList<>();
        List<String> soloSucursales = new ArrayList<>();
        for (Sucursal s : sistema.todas()) {
            todas.add(s.getNombre());
            if (!s.isMatriz()) soloSucursales.add(s.getNombre());
        }
        llenarCombo(cbOrigen, todas);
        llenarCombo(cbDestino, soloSucursales);
        llenarCombo(cbProducto, sistema.productos());

        modelo.setRowCount(0);
        modelo.setColumnCount(0);
        modelo.addColumn("ID");
        modelo.addColumn("Producto");
        for (Sucursal s : sistema.todas()) modelo.addColumn(s.getNombre());
        StringBuilder sbAlertas = new StringBuilder();
        for (String p : sistema.productos()) {
            Object[] fila = new Object[sistema.todas().size() + 2];
            fila[0] = sistema.getIdProducto(p);
            fila[1] = p;
            int i = 2;
            for (Sucursal s : sistema.todas()) fila[i++] = s.getStock(p);
            modelo.addRow(fila);
            for (Sucursal vacia : sistema.sucursalesVacias(p))
                sbAlertas.append("VACIO: ").append(vacia.getNombre()).append(" sin ").append(p)
                        .append("  ->  ENVIAR (matriz tiene ").append(sistema.getMatriz().getStock(p))
                        .append(")\n");
        }
        alertas.setText(sistema.productos().isEmpty() ? "Aun no hay productos. Usa 'Cargar CSV' o 'Agregar producto'."
                : sbAlertas.length() == 0 ? "Ninguna sucursal vacia." : sbAlertas.toString());
        StringBuilder sbHist = new StringBuilder();
        for (Transferencia t : sistema.getHistorial()) sbHist.append(t).append('\n');
        historial.setText(sbHist.toString());
    }
}
