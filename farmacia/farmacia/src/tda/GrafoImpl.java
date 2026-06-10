package tda;

public class GrafoImpl<T> implements Grafo<T>
{
    // Clave: vertice — Valor: conjunto de vecinos
    private Diccionario<T, Conjunto<T>> adyacencia;

    public GrafoImpl()
    {
        adyacencia = new DiccionarioImpl<>();
        adyacencia.initialize();
    }

    // O(1)
    @Override
    public void inicializarGrafo()
    {
        adyacencia = new DiccionarioImpl<>();
        adyacencia.initialize();
    }

    // O(1) amortizado — Pre: el vertice no existe
    @Override
    public void agregarVertice(T v)
    {
        if (!adyacencia.containsKey(v))
        {
            Conjunto<T> vecinos = new ConjuntoImpl<>();
            adyacencia.put(v, vecinos);
        }
    }

    // O(1) — Pre: ambos vertices existen
    @Override
    public void agregarArista(T v1, T v2)
    {
        adyacencia.get(v1).add(v2);
        adyacencia.get(v2).add(v1);
    }

    // O(V) — elimina el vertice y todas sus aristas
    @Override
    public void eliminarVertice(T v)
    {
        if (!adyacencia.containsKey(v)) return;
        Object[] vecinosArr = adyacencia.get(v).toArray();
        for (int i = 0; i < vecinosArr.length; i++)
        {
            adyacencia.get((T) vecinosArr[i]).remove(v);
        }
        adyacencia.remove(v);
    }

    // O(1)
    @Override
    public void eliminarArista(T v1, T v2)
    {
        if (adyacencia.containsKey(v1)) adyacencia.get(v1).remove(v2);
        if (adyacencia.containsKey(v2)) adyacencia.get(v2).remove(v1);
    }

    // O(1) amortizado
    @Override
    public boolean existeVertice(T v)
    {
        return adyacencia.containsKey(v);
    }

    // O(1)
    @Override
    public boolean existeArista(T v1, T v2)
    {
        if (!adyacencia.containsKey(v1)) return false;
        return adyacencia.get(v1).contains(v2);
    }

    // O(1) — Pre: el vertice existe
    @Override
    public Conjunto<T> vecinos(T v)
    {
        if (!adyacencia.containsKey(v)) return new ConjuntoImpl<>();
        return adyacencia.get(v);
    }

    // O(V)
    @Override
    public Conjunto<T> vertices()
    {
        Conjunto<T> resultado = new ConjuntoImpl<>();
        T[] claves = adyacencia.keys();
        for (int i = 0; i < claves.length; i++)
        {
            resultado.add(claves[i]);
        }
        return resultado;
    }

    // O(V + E) — recorrido BFS desde origen
    @Override
    public Conjunto<T> recorrerBFS(T origen)
    {
        Conjunto<T> visitados = new ConjuntoImpl<>();
        if (!adyacencia.containsKey(origen)) return visitados;

        Cola<T> cola = new ColaImpl<>();
        cola.enqueue(origen);
        visitados.add(origen);

        while (!cola.isEmpty())
        {
            T actual = cola.dequeue();
            Object[] vecinos = adyacencia.get(actual).toArray();
            for (int i = 0; i < vecinos.length; i++)
            {
                T vecino = (T) vecinos[i];
                if (!visitados.contains(vecino))
                {
                    visitados.add(vecino);
                    cola.enqueue(vecino);
                }
            }
        }
        return visitados;
    }

    // O(V + E) — recorrido DFS desde origen
    @Override
    public Conjunto<T> recorrerDFS(T origen)
    {
        Conjunto<T> visitados = new ConjuntoImpl<>();
        if (!adyacencia.containsKey(origen)) return visitados;
        dfsRecursivo(origen, visitados);
        return visitados;
    }

    private void dfsRecursivo(T v, Conjunto<T> visitados)
    {
        visitados.add(v);
        Object[] vecinos = adyacencia.get(v).toArray();
        for (int i = 0; i < vecinos.length; i++)
        {
            T vecino = (T) vecinos[i];
            if (!visitados.contains(vecino))
            {
                dfsRecursivo(vecino, visitados);
            }
        }
    }

    @Override
    public boolean isEmpty()
    {
        return adyacencia.isEmpty();
    }
}
