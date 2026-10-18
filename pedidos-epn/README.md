#  Módulo de Pedidos — Cafetería EPN

Este módulo forma parte del sistema de gestión para la **Cafetería EPN**. Se encarga de la administración, registro y control de pedidos de productos realizados por sucursal a los diferentes proveedores, basándose en el módulo de inventario.

---

##  Integrantes

- **Lenin Amangandi**
- **Alegria Farinango**
- **Erik Guerrero**
- **Rommel Rivera**

---

##  Caso Entendido (Descripción del Problema)

El **Módulo de Pedidos** se encarga de gestionar las solicitudes de productos provenientes de las diferentes sucursales, tomando como referencia los productos registrados en el módulo de inventario 

Cada pedido debe identificar:
- La **sucursal** solicitante
- El **proveedor** correspondiente
- La **fecha** en la que se realiza la compra
- La **cantidad** y el **precio unitario** de los productos solicitados

Asimismo, el módulo calcula automáticamente el **subtotal** de la compra, el valor del **IVA** y el **total** del pedido, permitiendo conocer el costo final de cada adquisición para un correcto control financiero

---

##  Historias de Usuario (Product Backlog)

| Código | Historia de Usuario | Story Points |
| :--- | :--- | :---: |
| **UH1** | **Como cliente** quiero un módulo de pedidos que tenga la información del proveedor, producto, cliente y fecha para realizar el pedido del producto. | **11 PTS** |
| **UH2** | **Como cliente** quiero que el pedido calcule el subtotal, el valor del IVA y el total para tener un control financiero. | **9 PTS** |

---

##  Interfaz del Módulo de Pedidos

El módulo cuenta con una interfaz gráfica (Java Swing) centralizada que permite registrar y consultar pedidos:

1. **Formulario de Registro (Nuevo Pedido):**
   - Selección de Sucursal
   - Selección de Proveedor
   - Selección de Producto (proveniente de inventario)
   - Ingreso de Cantidad y Precio Unitario

2. **Tabla de Registro e Historial:**
   - Filtros por sucursal, producto o proveedor
   - Cálculo automático de `Subtotal`, `IVA` y `Total` por cada ítem


---

##  Estructura del Proyecto

```text
pedidos-epn/
├── .github/
├── imagenes/
├── src/
│   ├── main/java/epn/cafeteria/pedidos/
│   │   ├── CatalogoProveedores.java
│   │   ├── Pedido.java
│   │   ├── PedidoException.java
│   │   ├── PedidoRepositorio.java
│   │   ├── PedidoServicio.java
│   │   └── PedidosFrame.java
│   └── test/
├── .gitignore
├── pedidos.csv
├── pom.xml
└── README.md