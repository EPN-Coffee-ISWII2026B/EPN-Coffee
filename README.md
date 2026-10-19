
# Inventario - EPN Coffee (XP + TDD)

### Tarea: Inventario
### Roles (ultima iteracion):
- Developer (Programmer): Anthony
- Coach: David Guachamin
- On-site Customer: David Guachamin

Estructura:
  src/    codigo de produccion
  test/   pruebas (se escriben PRIMERO)
  datos/  inventario_ejemplo.csv (formato compartido con el modulo POS)

## Historia de Usuario 1
> Como encargado de bodega, quiero registrar el stock de un producto
> a partir de las entregas de los proveedores (mismo CSV que usa POS)
> para saber cuanto tengo disponible y cuando debo reabastecer.

Criterios de aceptacion:
- Una entrega de un producto nuevo lo crea en el inventario con un stock minimo por defecto.
- Una entrega de un producto ya existente suma la cantidad al stock actual.
- El producto guarda todos los proveedores que lo han entregado.
- No se permite una cantidad ni un stock minimo negativos.
- Se puede consultar el stock actual de un producto y saber si esta bajo su minimo.

## Formato del CSV (compartido con el modulo POS)
    proveedor,telefono,producto,cantidad
    Café Andino,0991234567,Café en grano,40
    ...
Cada fila es una entrega de un proveedor. Si el producto ya existe (por nombre), se suma
el stock entregado y se agrega el proveedor a su lista; si no existe, se crea con el
stock minimo por defecto (10 unidades, configurable en `App.MINIMO_POR_DEFECTO`).

## 1) RED (solo las pruebas, sin src -> NO compila / rojo)
    mkdir -p out
    javac -encoding UTF-8 -d out test/inventario/*.java

## 2) GREEN (con src + test -> todo verde)
    javac -encoding UTF-8 -d out src/inventario/*.java test/inventario/*.java
    java -cp out inventario.TodasLasPruebas

## 3) Ejecutar la aplicacion
    java -cp out inventario.App                              (carga datos/inventario_ejemplo.csv)
    java -cp out inventario.App ruta/a/otro_csv.csv           (carga otro CSV con el mismo formato)
