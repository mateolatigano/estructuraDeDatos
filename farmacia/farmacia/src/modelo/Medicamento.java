package modelo;

public class Medicamento
{
    public enum Categoria
    {
        ANALGESICO, ANTIBIOTICO, ANTIINFLAMATORIO, ANTIHISTAMINICO, OTRO
    }

    private String    codigo;
    private String    nombre;
    private String    descripcion;
    private String    dosis;
    private Categoria categoria;

    // Pre: codigo unico, no nulo ni vacio
    public Medicamento(String codigo, String nombre, String descripcion,
                       String dosis, Categoria categoria)
    {
        this.codigo      = codigo;
        this.nombre      = nombre;
        this.descripcion = descripcion;
        this.dosis       = dosis;
        this.categoria   = categoria;
    }

    public String    getCodigo()      { return codigo; }
    public String    getNombre()      { return nombre; }
    public String    getDescripcion() { return descripcion; }
    public String    getDosis()       { return dosis; }
    public Categoria getCategoria()   { return categoria; }

    // O(1)
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!(obj instanceof Medicamento)) return false;
        return codigo.equals(((Medicamento) obj).codigo);
    }

    // O(n) donde n = longitud del codigo — en practica O(1)
    @Override
    public int hashCode()
    {
        return codigo.hashCode();
    }

    @Override
    public String toString()
    {
        return codigo + " (" + nombre + ")";
    }
}
