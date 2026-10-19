# EPN-Café — Módulo de Inventario (HU1)

Tarea: Módulo de Inventario 
Integrantes:

- Marco  Marcillo 
- Camila Caicedo
- Erick Cuichan
- Abigail Tenelema


Proyecto Java 17, estructura estándar src/main/ src/test/
módulo Inventario 

 Registra proveedores y productos que suministran; permite agregar productos a proveedores existentes y verlos en una tabla


## Historias de usuario

COMO cliente, QUIERO registrar proveedores con los productos que suministran, PARA mantener organizada la información y facilitar la identificación de los productos entregados por cada proveedor.

COMO cliente, QUIERO consultar la cantidad disponible de cada producto, PARA conocer su existencia antes de realizar una compra.

Por el momento se requiere implementar la primer historia de usuario

## Estructura
- `src/main/java/.../InventarioFrame.java`: interfaz y punto de entrada.
- `InventarioService.java`: lógica de proveedores.
- `Producto.java`, `Proveedor.java`: modelos.
- `CsvInventario.java`: persistencia.
- `src/test/java/.../InventarioServiceTest.java`: pruebas.
- `docs/`: informe XP y roles por completar.