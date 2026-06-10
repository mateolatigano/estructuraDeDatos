package sistema;

import tda.Conjunto;
import tda.ConjuntoImpl;
import tda.Grafo;
import tda.GrafoImpl;

public class RedSustitutos
{
    private Grafo<String> grafo;

    public RedSustitutos()
    {
        this.grafo = new GrafoImpl<>();
        this.grafo.inicializarGrafo();
    }

    // O(1) amortizado
    public void agregarMedicamento(String cod)
    {
        if (!grafo.existeVertice(cod))
        {
            grafo.agregarVertice(cod);
        }
    }

    // O(1)
    public void agregarRelacion(String cod1, String cod2)
    {
        agregarMedicamento(cod1);
        agregarMedicamento(cod2);
        grafo.agregarArista(cod1, cod2);
    }

    // O(1)
    public boolean existeRelacion(String cod1, String cod2)
    {
        if (!grafo.existeVertice(cod1) || !grafo.existeVertice(cod2)) return false;
        return grafo.existeArista(cod1, cod2);
    }

    // O(1)
    public Conjunto<String> obtenerSustitutos(String cod)
    {
        if (!grafo.existeVertice(cod)) return new ConjuntoImpl<>();
        return grafo.vecinos(cod);
    }

    // O(V + E) por BFS + O(n²) por consultarStockTotal por cada candidato
    public String buscarAlternativaDisponible(String origen, StockSucursal stock)
    {
        if (!grafo.existeVertice(origen)) return null;

        Conjunto<String> alcanzables = grafo.recorrerBFS(origen);
        Object[] arr = alcanzables.toArray();

        for (int i = 0; i < arr.length; i++)
        {
            String candidato = (String) arr[i];
            if (!candidato.equals(origen) && stock.consultarStockTotal(candidato) > 0)
            {
                return candidato;
            }
        }
        return null;
    }

    // O(V + E)
    public Conjunto<String> recorrerRed(String origen)
    {
        Conjunto<String> resultado = new ConjuntoImpl<>();
        if (!grafo.existeVertice(origen)) return resultado;

        Conjunto<String> alcanzables = grafo.recorrerBFS(origen);
        Object[] arr = alcanzables.toArray();

        for (int i = 0; i < arr.length; i++)
        {
            if (!arr[i].equals(origen)) resultado.add((String) arr[i]);
        }
        return resultado;
    }
}
