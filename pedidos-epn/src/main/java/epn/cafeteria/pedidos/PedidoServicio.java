 package epn.cafeteria.pedidos;



import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/** Validación y lógica de negocio de los pedidos. */
public class PedidoServicio {

    private final PedidoRepositorio repositorio;
    private final List<Pedido> pedidos;

    public PedidoServicio(PedidoRepositorio repositorio) throws PedidoException {
        this.repositorio = repositorio;
        this.pedidos = repositorio.cargar();
    }

    public Pedido registrar(String sucursal, String proveedor, String producto,
                            String cantidadTxt, String precioTxt, String fechaTxt) throws PedidoException {
        exigir(sucursal, "la sucursal");
        exigir(proveedor, "el proveedor");
        exigir(producto, "el nombre del producto");

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadTxt.trim());
        } catch (NumberFormatException e) {
            throw new PedidoException("La cantidad debe ser un número entero.");
        }
        if (cantidad <= 0) {
            throw new PedidoException("La cantidad debe ser mayor que cero.");
        }

        BigDecimal precio;
        try {
            precio = new BigDecimal(precioTxt.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new PedidoException("El precio unitario debe ser un número (ej. 12.50).");
        }
        if (precio.signum() <= 0) {
            throw new PedidoException("El precio unitario debe ser mayor que cero.");
        }

        LocalDate fecha;
        try {
            fecha = LocalDate.parse(fechaTxt.trim());
        } catch (DateTimeParseException e) {
            throw new PedidoException("La fecha debe tener el formato AAAA-MM-DD (ej. 2026-10-17).");
        }

        Pedido pedido = new Pedido(sucursal.trim(), proveedor.trim(), producto.trim(), cantidad, precio, fecha);
        repositorio.guardar(pedido);
        pedidos.add(pedido);
        return pedido;
    }

    /** Filtra por sucursal (null o vacío = todas) y por texto en producto o proveedor. */
    public List<Pedido> filtrar(String sucursal, String texto) {
        String t = texto == null ? "" : texto.trim().toLowerCase();
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido p : pedidos) {
            boolean okSucursal = sucursal == null || sucursal.isEmpty() || p.sucursal().equalsIgnoreCase(sucursal);
            boolean okTexto = t.isEmpty() || p.producto().toLowerCase().contains(t)
                    || p.proveedor().toLowerCase().contains(t);
            if (okSucursal && okTexto) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    private static void exigir(String valor, String campo) throws PedidoException {
        if (valor == null || valor.isBlank()) {
            throw new PedidoException("Debes ingresar " + campo + ".");
        }
    }
}