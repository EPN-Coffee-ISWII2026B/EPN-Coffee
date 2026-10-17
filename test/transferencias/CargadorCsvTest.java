package transferencias;

import static transferencias.Prueba.*;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Pruebas de la lectura del inventario desde CSV (solo logica, sin interfaz). */
public class CargadorCsvTest {

    static final String[] UBICACIONES = {"Matriz", "Norte", "Sur", "Este", "Oeste", "Centro"};

    /** CSV valido: 2 productos (Laptop, Mouse) en las 6 ubicaciones. Norte tiene 15 Mouse. */
    static List<String> filas(String... ubicaciones) {
        List<String> l = new ArrayList<>();
        l.add("id_producto,nombre_producto,sucursal,cantidad");
        for (String u : ubicaciones) {
            l.add("P1,Laptop," + u + "," + (u.equals("Matriz") ? 50 : 5));
            l.add("P2,Mouse," + u + "," + (u.equals("Norte") ? 15 : 20));
        }
        return l;
    }

    static List<String> base() { return filas(UBICACIONES); }

    static Path archivo(List<String> lineas) {
        try {
            Path p = Files.createTempFile("inventario", ".csv");
            Files.write(p, lineas, StandardCharsets.UTF_8);
            p.toFile().deleteOnExit();
            return p;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void ejecutar() {
        System.out.println("== CargadorCsvTest ==");

        // ---- Test 1: existe o no existe el archivo ----
        correr("existe() es true para un archivo real", () ->
            assertTrue(CargadorCsv.existe(archivo(base())), "el archivo deberia existir"));

        correr("existe() es false si el archivo no existe", () ->
            assertTrue(!CargadorCsv.existe(Paths.get("no_existe_inventario.csv")), "no deberia existir"));

        correr("cargar un archivo inexistente lanza ErrorCsvException", () ->
            assertThrows(ErrorCsvException.class,
                () -> CargadorCsv.cargar(Paths.get("no_existe_inventario.csv"))));

        // ---- Test 2: existen sucursales ----
        correr("el CSV crea la matriz y exactamente 5 sucursales", () -> {
            SistemaTransferencias s = CargadorCsv.cargar(archivo(base()));
            assertEquals(6, s.todas().size());
            assertEquals(5, s.getSucursales().size());
            assertEquals("Matriz", s.getMatriz().getNombre());
        });

        correr("falla si el CSV trae menos de 5 sucursales", () ->
            assertThrows(ErrorCsvException.class,
                () -> CargadorCsv.cargar(archivo(filas("Matriz", "Norte", "Sur")))));

        correr("falla si el CSV no trae la matriz", () ->
            assertThrows(ErrorCsvException.class,
                () -> CargadorCsv.cargar(archivo(filas("Norte", "Sur", "Este", "Oeste", "Centro")))));

        // ---- Test 3: en cada sucursal hay productos ----
        correr("cada ubicacion (matriz y sucursales) tiene productos", () -> {
            SistemaTransferencias s = CargadorCsv.cargar(archivo(base()));
            for (Sucursal u : s.todas()) {
                assertTrue(!u.productos().isEmpty(), u.getNombre() + " no tiene productos");
                assertEquals(2, u.productos().size());
            }
        });

        correr("falla si el archivo solo trae el encabezado (sin productos)", () ->
            assertThrows(ErrorCsvException.class,
                () -> CargadorCsv.cargar(archivo(new ArrayList<>(base().subList(0, 1))))));

        correr("un producto que falta en una sucursal queda en 0 (vacio)", () -> {
            List<String> l = base();
            l.remove(6); // Mouse de Sur
            SistemaTransferencias s = CargadorCsv.cargar(archivo(l));
            assertTrue(s.buscar("Sur").productos().contains("Mouse"), "Sur deberia tener Mouse");
            assertEquals(1, s.sucursalesVacias("Mouse").size());
        });

        // ---- Test 4: no existen productos negativos ----
        correr("falla con cantidades negativas en el CSV", () -> {
            List<String> l = base();
            l.set(3, "P1,Laptop,Norte,-3");
            assertThrows(ErrorCsvException.class, () -> CargadorCsv.cargar(archivo(l)));
        });

        correr("ninguna cantidad cargada es negativa", () -> {
            SistemaTransferencias s = CargadorCsv.cargar(archivo(base()));
            for (Sucursal u : s.todas())
                for (String p : u.productos())
                    assertTrue(u.getStock(p) >= 0, "stock negativo en " + u.getNombre());
        });

        // ---- Lectura correcta de datos ----
        correr("lee nombres, ids y cantidades por sucursal", () -> {
            SistemaTransferencias s = CargadorCsv.cargar(archivo(base()));
            assertEquals(50, s.buscar("Matriz").getStock("Laptop"));
            assertEquals(15, s.buscar("Norte").getStock("Mouse"));
            assertEquals("P2", s.getIdProducto("Mouse"));
        });

        correr("acepta punto y coma como separador (Excel en espanol)", () -> {
            List<String> l = new ArrayList<>();
            for (String linea : base()) l.add(linea.replace(",", ";"));
            assertEquals(15, CargadorCsv.cargar(archivo(l)).buscar("Norte").getStock("Mouse"));
        });

        correr("ignora lineas en blanco", () -> {
            List<String> l = base();
            l.add(2, "");
            assertEquals(6, CargadorCsv.cargar(archivo(l)).todas().size());
        });

        correr("lee la columna opcional minimo", () -> {
            List<String> l = base();
            l.set(0, "id_producto,nombre_producto,sucursal,cantidad,minimo");
            l.set(3, "P1,Laptop,Norte,5,4");
            assertEquals(4, CargadorCsv.cargar(archivo(l)).buscar("Norte").getMinimo("Laptop"));
        });

        // ---- Archivos mal formados ----
        correr("falla si el encabezado no es el esperado", () -> {
            List<String> l = base();
            l.set(0, "a,b,c,d");
            assertThrows(ErrorCsvException.class, () -> CargadorCsv.cargar(archivo(l)));
        });

        correr("falla si la cantidad no es un numero", () -> {
            List<String> l = base();
            l.set(3, "P1,Laptop,Norte,abc");
            assertThrows(ErrorCsvException.class, () -> CargadorCsv.cargar(archivo(l)));
        });

        correr("falla si un id esta asignado a dos nombres distintos", () -> {
            List<String> l = base();
            l.set(4, "P2,Teclado,Norte,3");
            assertThrows(ErrorCsvException.class, () -> CargadorCsv.cargar(archivo(l)));
        });

        correr("falla si un producto esta repetido en la misma sucursal", () -> {
            List<String> l = base();
            l.add("P1,Laptop,Norte,2");
            assertThrows(ErrorCsvException.class, () -> CargadorCsv.cargar(archivo(l)));
        });
    }
}
