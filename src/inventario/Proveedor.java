package inventario;

import java.util.Objects;

/** Un proveedor que entrega productos al inventario (dato compartido con el modulo POS). */
public class Proveedor {
    private final String nombre;
    private final String telefono;

    public Proveedor(String nombre, String telefono) {
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("el nombre del proveedor no puede estar vacio");
        if (telefono == null || telefono.isBlank())
            throw new IllegalArgumentException("el telefono del proveedor no puede estar vacio");

        this.nombre = nombre;
        this.telefono = telefono;
    }

    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Proveedor)) return false;
        Proveedor p = (Proveedor) o;
        return nombre.equals(p.nombre) && telefono.equals(p.telefono);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, telefono);
    }

    @Override
    public String toString() {
        return nombre + " (" + telefono + ")";
    }
}
