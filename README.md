
# Inventario - EPN Coffee (XP + TDD)

### Tarea: Inventario
### Roles (ultima iteracion):
- Developer (Programmer): Anthony Contreras
- Tracker: Anthony Contreras
- Coach: David Guachamin
- On-site Customer: David Guachamin

Estructura:
  src/    codigo de produccion
  test/   pruebas 
  datos/  inventario_ejemplo.csv 

## Historia de Usuario 1
HU-01 (MVP): "Como barista, quiero registrar un nuevo lote de café ingresando el tipo de grano, su origen, el nivel de tueste y el peso en kilogramos, para controlar con exactitud la materia prima disponible para la preparación de bebidas".
## Historia de Usuario 2
HU-02 (Para futuras iteraciones): "Como administrador, quiero descontar automáticamente el peso en gramos del inventario cada vez que se prepare una bebida, para tener un control del stock en tiempo real".

# Criterios de Aceptación (HU: Gestión y Recepción de Inventario)
## Criterio 1: Creación de un producto nuevo tras una entrega

Dado que se registra la entrega de un producto que no existe previamente en el sistema,

Cuando se procesa la transacción de entrada,

Entonces el producto se crea automáticamente en el inventario con el stock igual a la cantidad entregada y se le asigna un valor de stock mínimo por defecto.

## Criterio 2: Actualización de stock para un producto existente

Dado que se registra la entrega de un producto que ya se encuentra catalogado en el inventario,

Cuando se procesa la transacción de entrada,

Entonces la cantidad recibida en la entrega se suma matemáticamente al stock actual del producto sin sobrescribir sus configuraciones base.

## Criterio 3: Trazabilidad de proveedores por producto

Dado que se recibe una entrega de un producto (sea nuevo o existente) por parte de un proveedor específico,

Cuando la entrega se consolida en el inventario,

Entonces el sistema añade y conserva a dicho proveedor en la lista histórica de proveedores asociados a ese producto.

## Criterio 4: Restricción de valores negativos (Regla de Negocio)

Dado que el usuario intenta registrar una entrega de inventario o modificar la configuración de un producto,

Cuando ingresa un valor negativo en el campo de "cantidad entregada" o "stock mínimo",

Entonces el sistema rechaza la operación, no realiza cambios en la base de datos y arroja una excepción o mensaje indicando que no se admiten valores negativos.

## Criterio 5: Consulta de disponibilidad y alerta de déficit

Dado que el usuario requiere conocer el estado de un producto,

Cuando consulta la información de dicho producto en el sistema,

Entonces el sistema retorna el valor del stock actual y un indicador booleano o alerta que especifica si dicho stock se encuentra por debajo de su nivel mínimo configurado.


## 1) RED (solo las pruebas, sin src -> NO compila / rojo)
    mkdir -p out
    javac -encoding UTF-8 -d out test/inventario/*.java

## 2) GREEN (con src + test -> todo verde)
    javac -encoding UTF-8 -d out src/inventario/*.java test/inventario/*.java
    java -cp out inventario.TodasLasPruebas

## 3) Ejecutar la aplicacion
    java -cp out inventario.App                              (carga datos/inventario_ejemplo.csv)
    java -cp out inventario.App ruta/a/otro_csv.csv           (carga otro CSV con el mismo formato)
