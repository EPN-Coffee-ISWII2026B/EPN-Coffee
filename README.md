# EPN-Coffee — Módulo de Ventas (HU1)

### Tarea: Módulo de Ventas (POS)
### Integrantes:
- Dayana Martínez
- Jarvin Rios
- Mateo Molina 
---

Proyecto Java 17, Swing y Maven. Se conserva el `pom.xml` original y la estructura estándar `src/main/java` y `src/test/java`.

## Historia de usuario implementada

**HU1:** Como barista, quiero emitir una factura con el total de la venta, nombre del cliente y número de factura.

- Selección de productos y cantidades.
- Carrito con subtotales y total.
- Nombre del cliente u opción de Consumidor Final.
- Emisión de factura numerada, con detalle, fecha y total de venta.
- Historial de facturas durante la ejecución.

**HU2 no está implementada.** No hay selección ni registro de métodos de pago (crédito, débito ni transferencias).

## Estructura Maven

```text
EPN-CoffeVENTAS/
├── pom.xml
├── README.md
├── data/
│   └── inventario.csv
├── datos/
│   └── inventario_ejemplo.csv
└── src/
    ├── main/java/epn/cafeteria/
    │   ├── Main.java
    │   └── ventas/
    │       ├── Factura.java
    │       ├── ItemVenta.java
    │       ├── VentasFrame.java
    │       └── VentasService.java
    └── test/java/epn/cafeteria/ventas/
        └── VentasServiceTest.java
```

No se incluye ningún archivo `.bat`. El directorio `target/`, si lo genera Maven, es de compilación y no contiene código fuente.

## Ejecutar desde VS Code (Windows)

1. Descomprime el archivo ZIP.
2. En VS Code, selecciona **Archivo > Abrir carpeta** y elige `EPN-CoffeVENTAS` (la que contiene `pom.xml`).
3. Con el JDK 17 o superior y la extensión **Extension Pack for Java** instalada, abre `src/main/java/epn/cafeteria/Main.java`.
4. Haz clic en **Run Java** encima de `public static void main` para abrir el módulo de ventas.

## Compilar y probar con Maven

Si tienes Maven instalado, abre la terminal de VS Code en la carpeta que contiene `pom.xml` y ejecuta:

```powershell
mvn clean test
mvn package
java -jar target/EPN-Coffee-1.0.jar
```

El comando `mvn clean test` ejecuta las pruebas unitarias JUnit de HU1. La aplicación es de escritorio (Swing).

> Si Windows muestra «mvn no se reconoce», falta instalar/configurar Maven o su ruta PATH; el problema no es del código del proyecto. Como alternativa, puedes usar **Run Java** en VS Code con el JDK y las extensiones indicadas.

## Aclaraciones

- Los precios del catálogo actualmente están definidos en `VentasFrame.java`.
- Los CSV que venían con el proyecto se conservan, pero no alimentan el catálogo de ventas.
- El historial y la numeración de facturas se mantienen en memoria mientras la aplicación está abierta; no se guardan al cerrar.
- El ZIP original contenía archivos `.class` ya compilados de inventario y transferencias, pero no incluía sus archivos `.java`. Se omiten esos archivos compilados; `Main.java` abre únicamente Ventas (HU1).
