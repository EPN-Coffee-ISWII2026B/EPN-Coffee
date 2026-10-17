# Sistema de transferencias Matriz -> 5 sucursales (XP + TDD)

### Tarea: TX (Sucursales)
### Integrantes:
- Kevin Alvear
- Mauricio Carrión
- Kevin García
- Karolina Salazar

Estructura:
  src/    codigo de produccion (logica + CSV + interfaz Swing)
  test/   pruebas (se escriben PRIMERO)
  datos/  inventario_ejemplo.csv

## 1) RED (solo las pruebas, sin src -> NO compila / rojo)
    mkdir -p out
    javac -encoding UTF-8 -d out test/transferencias/*.java

## 2) GREEN (con src + test -> todo verde)
    javac -encoding UTF-8 -d out src/transferencias/*.java test/transferencias/*.java
    java -cp out transferencias.TodasLasPruebas

## 3) Ejecutar la aplicacion con interfaz
    java -cp out transferencias.App                              (vacio; usa el boton "Cargar CSV")
    java -cp out transferencias.App datos/inventario_ejemplo.csv (carga el CSV al abrir)
    java -cp out transferencias.App --demo                       (datos de ejemplo en codigo)

## Formato del CSV (UTF-8, una fila por producto y ubicacion)
    id_producto,nombre_producto,sucursal,cantidad[,minimo]
    P001,Laptop,Matriz,50,0
    P001,Laptop,Norte,0,3
    ...
- Debe haber una ubicacion llamada "Matriz" y exactamente 5 sucursales mas.
- cantidad: entero >= 0 (negativos -> error). minimo (opcional): entero >= 0, por defecto 1.
- Separador coma o punto y coma (Excel en espanol). Sin comillas ni comas dentro de los nombres.
- Si un producto no aparece en alguna sucursal, queda en 0 (vacio).
- Un id = un nombre de producto; no puede haber filas repetidas (mismo id y sucursal).
