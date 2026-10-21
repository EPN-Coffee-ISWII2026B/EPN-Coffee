
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

Criterios de aceptacion:
- Una entrega de un producto nuevo lo crea en el inventario con un stock minimo por defecto.
- Una entrega de un producto ya existente suma la cantidad al stock actual.
- El producto guarda todos los proveedores que lo han entregado.
- No se permite una cantidad ni un stock minimo negativos.
- Se puede consultar el stock actual de un producto y saber si esta bajo su minimo.


## 1) RED (solo las pruebas, sin src -> NO compila / rojo)
    mkdir -p out
    javac -encoding UTF-8 -d out test/inventario/*.java

## 2) GREEN (con src + test -> todo verde)
    javac -encoding UTF-8 -d out src/inventario/*.java test/inventario/*.java
    java -cp out inventario.TodasLasPruebas

## 3) Ejecutar la aplicacion
    java -cp out inventario.App                              (carga datos/inventario_ejemplo.csv)
    java -cp out inventario.App ruta/a/otro_csv.csv           (carga otro CSV con el mismo formato)
