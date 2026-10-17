package transferencias;

import java.util.Objects;

/** Mini framework de pruebas (sin librerias externas) para ver ROJO / VERDE. */
public class Prueba {
    static int verdes = 0, rojos = 0;

    static void correr(String nombre, Runnable prueba) {
        try {
            prueba.run();
            verdes++;
            System.out.println("  [VERDE] " + nombre);
        } catch (Throwable e) {
            rojos++;
            System.out.println("  [ROJO]  " + nombre + "  ->  " + e);
        }
    }

    static void assertEquals(Object esperado, Object real) {
        if (!Objects.equals(esperado, real))
            throw new AssertionError("esperado <" + esperado + "> pero fue <" + real + ">");
    }

    static void assertTrue(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
    }

    static void assertThrows(Class<? extends Throwable> tipo, Runnable codigo) {
        try {
            codigo.run();
        } catch (Throwable e) {
            if (tipo.isInstance(e)) return;
            throw new AssertionError("se esperaba " + tipo.getSimpleName() + " pero fue " + e);
        }
        throw new AssertionError("se esperaba " + tipo.getSimpleName() + " pero no hubo excepcion");
    }

    static void resumen() {
        System.out.println("\nResultado: " + verdes + " verdes, " + rojos + " rojos");
        System.out.println(rojos == 0 ? ">>> GREEN: todo en verde" : ">>> RED: hay pruebas fallando");
        if (rojos > 0) System.exit(1);
    }
}
