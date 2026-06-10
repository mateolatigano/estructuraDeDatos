package modelo;

import tda.Conjunto;
import tda.ConjuntoImpl;

public class Categoria
{
    private String        nombre;
    private Conjunto<String> medicamentos;

    public Categoria(String nombre)
    {
        this.nombre       = nombre;
        this.medicamentos = new ConjuntoImpl<>();
    }

    // O(1)
    public String getNombre()
    {
        return nombre;
    }

    // O(1)
    public Conjunto<String> getMedicamentos()
    {
        return medicamentos;
    }

    // O(1)
    public void agregarMedicamento(String codigoMedicamento)
    {
        medicamentos.add(codigoMedicamento);
    }

    // O(1)
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!(obj instanceof Categoria)) return false;
        return nombre.equals(((Categoria) obj).nombre);
    }

    // O(n) donde n = longitud del nombre — en practica O(1)
    @Override
    public int hashCode()
    {
        return nombre.hashCode();
    }
}
