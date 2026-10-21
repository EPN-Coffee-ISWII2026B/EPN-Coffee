package inventario;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.nio.file.Files;
import java.nio.file.Path;

import static inventario.Prueba.*;

public class CargadorCsvTest {
    public static void ejecutar() {
        System.out.println("== CargadorCsvTest ==");

        correr("carga un CSV valido y suma entregas repetidas del mismo producto", () -> {
            try {
                Path csv = escribirCsvTemporal(
                        "proveedor,telefono,producto,cantidad\n" +
                        "Cafe Andino,0991234567,Cafe en grano,40\n" +
                        "Dulces del Valle,0998765432,Azucar,50\n" +
                        "Te Verde Distribuciones,0961122334,Azucar,15\n");

                Inventario inv = CargadorCsv.cargar(csv, 10);

                assertEquals(40, inv.consultarStock("Cafe en grano"));
                assertEquals(65, inv.consultarStock("Azucar"));
                assertEquals(2, inv.listar().size());
            } catch (ErrorCsvException e) {
                throw new AssertionError(e);
            }
        });

        correr("una fila con columnas de mas o de menos lanza ErrorCsvException", () -> {
            assertThrows(ErrorCsvException.class, () -> {
                Path csv = escribirCsvTemporal(
                        "proveedor,telefono,producto,cantidad\n" +
                        "Cafe Andino,0991234567,Cafe en grano\n");
                CargadorCsv.cargar(csv, 10);
            });
        });

        correr("una cantidad no numerica lanza ErrorCsvException", () -> {
            assertThrows(ErrorCsvException.class, () -> {
                Path csv = escribirCsvTemporal(
                        "proveedor,telefono,producto,cantidad\n" +
                        "Cafe Andino,0991234567,Cafe en grano,abc\n");
                CargadorCsv.cargar(csv, 10);
            });
        });

        correr("una cantidad negativa lanza ErrorCsvException", () -> {
            assertThrows(ErrorCsvException.class, () -> {
                Path csv = escribirCsvTemporal(
                        "proveedor,telefono,producto,cantidad\n" +
                        "Cafe Andino,0991234567,Cafe en grano,-5\n");
                CargadorCsv.cargar(csv, 10);
            });
        });
    }

    private static Path escribirCsvTemporal(String contenido) {
        try {
            Path tmp = Files.createTempFile("inventario_test_", ".csv");
            tmp.toFile().deleteOnExit();
            Files.writeString(tmp, contenido, StandardCharsets.UTF_8);
            return tmp;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
