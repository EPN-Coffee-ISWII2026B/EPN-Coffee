package epn.cafeteria.ventas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias y de aceptación para el Módulo de Ventas.
 *
 * <p>En concordancia estricta con la metodología <b>Extreme Programming (XP)</b>:</p>
 * <ul>
 *   <li><b>Test-Driven Development (TDD)</b>: Se definen primero las pruebas automatizadas
 *       para especificar y validar el comportamiento esperado antes de refactorizar.</li>
 *   <li><b>Automatización de Pruebas</b>: Aseguran no regresión y retroalimentación inmediata.</li>
 *   <li><b>Criterios de Aceptación</b>:
 *     <ul>
 *       <li><b>HU1 (Barista)</b>: Emitir factura con total de la venta, nombre del cliente y número de factura.</li>
 *       <li><b>Requisitos Adicionales</b>: Opción consumidor final, selección de productos y cálculo de subtotales.</li>
 *     </ul>
 *   </li>
 * </ul>
 */
@DisplayName("Módulo de Ventas (TDD - Extreme Programming)")
class VentasServiceTest {

    private VentasService service;
    private ItemVenta cafeEspresso;
    private ItemVenta panDeQueso;
    private ItemVenta capuchino;

    @BeforeEach
    void setUp() {
        service = new VentasService();
        cafeEspresso = new ItemVenta("Café Espresso", 2, new BigDecimal("1.50"));
        panDeQueso = new ItemVenta("Pan de Queso", 3, new BigDecimal("0.75"));
        capuchino = new ItemVenta("Capuchino", 1, new BigDecimal("2.00"));
    }

    // =========================================================================
    // HU1: Emitir factura con total, nombre cliente y número correlativo
    // =========================================================================
    @Nested
    @DisplayName("HU1: Emisión de factura por el Barista")
    class HistoriaUsuario1Tests {

        @Test
        @DisplayName("HU1 - La factura incluye el número correlativo autoincremental")
        void emiteFacturaConNumeroCorrelativo() {
            Factura f1 = service.emitirFactura("Juan Pérez", List.of(cafeEspresso));
            Factura f2 = service.emitirFactura("Ana Gómez", List.of(panDeQueso));

            assertEquals(1, f1.getNumero(), "La primera factura debe tener el número 1");
            assertEquals(2, f2.getNumero(), "La segunda factura debe tener el número 2");
        }

        @Test
        @DisplayName("HU1 - La factura incluye el nombre del cliente indicado")
        void emiteFacturaConNombreDelCliente() {
            Factura f = service.emitirFactura("Carlos Mendoza", List.of(capuchino));

            assertEquals("Carlos Mendoza", f.getNombreCliente());
            assertFalse(f.esConsumidorFinal());
        }

        @Test
        @DisplayName("HU1 - Opción de Consumidor Final cuando no se ingresa nombre o es nulo")
        void emiteFacturaComoConsumidorFinal() {
            Factura fNull = service.emitirFactura(null, List.of(cafeEspresso));
            Factura fVacio = service.emitirFactura("   ", List.of(cafeEspresso));
            Factura fExplicito = service.emitirFactura(Factura.CONSUMIDOR_FINAL, List.of(cafeEspresso));

            assertEquals(Factura.CONSUMIDOR_FINAL, fNull.getNombreCliente());
            assertEquals(Factura.CONSUMIDOR_FINAL, fVacio.getNombreCliente());
            assertEquals(Factura.CONSUMIDOR_FINAL, fExplicito.getNombreCliente());
            assertTrue(fNull.esConsumidorFinal());
            assertTrue(fVacio.esConsumidorFinal());
            assertTrue(fExplicito.esConsumidorFinal());
        }

        @Test
        @DisplayName("HU1 - La factura calcula exactamente el total de la venta")
        void emiteFacturaConTotalCorrecto() {
            // Subtotales:
            // Café Espresso: 2 * 1.50 = 3.00
            // Pan de Queso:  3 * 0.75 = 2.25
            // Capuchino:     1 * 2.00 = 2.00
            // Total esperado: 3.00 + 2.25 + 2.00 = 7.25
            List<ItemVenta> items = List.of(cafeEspresso, panDeQueso, capuchino);
            Factura f = service.emitirFactura("Diana Flores", items);

            assertEquals(new BigDecimal("7.25"), f.getTotal());
        }

        @Test
        @DisplayName("HU1 - La factura preserva todos los productos seleccionados")
        void facturaContieneTodosLosProductos() {
            List<ItemVenta> items = List.of(cafeEspresso, panDeQueso);
            Factura f = service.emitirFactura("Pedro", items);

            assertEquals(2, f.getItems().size());
            assertEquals("Café Espresso", f.getItems().get(0).nombreProducto());
            assertEquals("Pan de Queso", f.getItems().get(1).nombreProducto());
        }

        @Test
        @DisplayName("HU1 - Rechaza emisión de factura sin ítems (lista vacía o nula)")
        void rechazaFacturaSinItems() {
            assertThrows(IllegalArgumentException.class, () ->
                    service.emitirFactura("Cliente", List.of()));
            assertThrows(IllegalArgumentException.class, () ->
                    service.emitirFactura("Cliente", null));
        }

        @Test
        @DisplayName("HU1 - Factura inválida no consume un número de factura")
        void noSaltaNumeroCuandoFacturaEsInvalida() {
            assertThrows(IllegalArgumentException.class, () ->
                    service.emitirFactura("Cliente", List.of()));
            Factura primera = service.emitirFactura("Cliente", List.of(cafeEspresso));
            assertEquals(1, primera.getNumero());
        }

        @Test
        @DisplayName("HU1 - Representación en texto de la factura contiene número, cliente y total")
        void formatoTextoContieneDatosEsenciales() {
            Factura f = service.emitirFactura("Karina", List.of(cafeEspresso));
            String texto = f.toString();

            assertTrue(texto.contains("000001"));
            assertTrue(texto.contains("Karina"));
            assertTrue(texto.contains("TOTAL DE LA VENTA"));
            assertTrue(texto.contains("3.00"));
            assertFalse(texto.contains("Método de Pago"));
        }
    }

    // =========================================================================
    // Validaciones de dominio del ItemVenta
    // =========================================================================
    @Nested
    @DisplayName("Validaciones de ItemVenta (Selección de Producto)")
    class ItemVentaTests {

        @Test
        @DisplayName("Calcula correctamente el subtotal")
        void calculaSubtotalCorrectamente() {
            ItemVenta item = new ItemVenta("Mocaccino", 4, new BigDecimal("2.50"));
            assertEquals(new BigDecimal("10.00"), item.subtotal());
        }

        @Test
        @DisplayName("Rechaza nombre de producto vacío o nulo")
        void rechazaNombreProductoInvalido() {
            assertThrows(IllegalArgumentException.class, () ->
                    new ItemVenta(null, 1, new BigDecimal("1.00")));
            assertThrows(IllegalArgumentException.class, () ->
                    new ItemVenta("   ", 1, new BigDecimal("1.00")));
        }

        @Test
        @DisplayName("Rechaza cantidad menor o igual a cero")
        void rechazaCantidadInvalida() {
            assertThrows(IllegalArgumentException.class, () ->
                    new ItemVenta("Café", 0, new BigDecimal("1.00")));
            assertThrows(IllegalArgumentException.class, () ->
                    new ItemVenta("Café", -2, new BigDecimal("1.00")));
        }

        @Test
        @DisplayName("Rechaza precio unitario negativo")
        void rechazaPrecioNegativo() {
            assertThrows(IllegalArgumentException.class, () ->
                    new ItemVenta("Café", 1, new BigDecimal("-0.50")));
            assertThrows(IllegalArgumentException.class, () ->
                    new ItemVenta("Café", 1, null));
        }
    }

    // =========================================================================
    // Métodos del Servicio (Listado y Acumulados)
    // =========================================================================
    @Nested
    @DisplayName("Gestión del Servicio de Ventas")
    class VentasServiceGestionTests {

        @Test
        @DisplayName("Registra y lista historial de facturas emitidas")
        void acumulaHistorialDeFacturas() {
            assertEquals(0, service.getTotalFacturas());

            service.emitirFactura("Cliente 1", List.of(cafeEspresso));
            service.emitirFactura("Cliente 2", List.of(panDeQueso));

            assertEquals(2, service.getTotalFacturas());
            assertEquals(2, service.listarFacturas().size());
        }

        @Test
        @DisplayName("Permite buscar factura por número correlativo")
        void buscaFacturaPorNumero() {
            service.emitirFactura("Cliente 1", List.of(cafeEspresso));
            Factura f2 = service.emitirFactura("Cliente 2", List.of(panDeQueso));

            Factura encontrada = service.buscarFacturaPorNumero(2);
            assertNotNull(encontrada);
            assertEquals("Cliente 2", encontrada.getNombreCliente());

            assertNull(service.buscarFacturaPorNumero(999));
        }

        @Test
        @DisplayName("Calcula monto acumulado total de ventas")
        void calculaMontoTotalVendido() {
            service.emitirFactura("Cliente 1", List.of(cafeEspresso)); // 3.00
            service.emitirFactura("Cliente 2", List.of(panDeQueso));  // 2.25

            assertEquals(new BigDecimal("5.25"), service.getMontoTotalVendido());
        }
    }
}
