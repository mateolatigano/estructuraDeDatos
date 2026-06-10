package tda;

public class ArbolGeneralImpl<T> implements ArbolGeneral<T>
{
    private T                         dato;
    private Conjunto<ArbolGeneral<T>> hijos;
    private boolean                   vacio;

    public ArbolGeneralImpl()
    {
        vacio = true;
        hijos = new ConjuntoImpl<>();
    }

    // O(1)
    @Override
    public void inicializarArbol(T dato)
    {
        this.dato  = dato;
        this.hijos = new ConjuntoImpl<>();
        this.vacio = false;
    }

    @Override
    public boolean arbolVacio()
    {
        return vacio;
    }

    // O(1) — Pre: arbol no vacio
    @Override
    public T raiz()
    {
        return dato;
    }

    // O(1)
    @Override
    public Conjunto<ArbolGeneral<T>> hijos()
    {
        return hijos;
    }

    // O(1) — Pre: arbol no vacio
    @Override
    public void agregarHijo(ArbolGeneral<T> subarbol)
    {
        hijos.add(subarbol);
    }

    // O(1)
    @Override
    public boolean esHoja()
    {
        return hijos.isEmpty();
    }

    // O(n) — DFS sobre el arbol completo
    @Override
    @SuppressWarnings("unchecked")
    public ArbolGeneral<T> buscar(T objetivo)
    {
        if (vacio) return null;
        if (dato.equals(objetivo)) return this;

        Object[] hijosArr = hijos.toArray();
        for (int i = 0; i < hijosArr.length; i++)
        {
            ArbolGeneral<T> resultado = ((ArbolGeneral<T>) hijosArr[i]).buscar(objetivo);
            if (resultado != null) return resultado;
        }
        return null;
    }

    // O(n)
    @Override
    @SuppressWarnings("unchecked")
    public int altura()
    {
        if (vacio || esHoja()) return 0;
        int maxAltura = 0;
        Object[] hijosArr = hijos.toArray();
        for (int i = 0; i < hijosArr.length; i++)
        {
            int h = ((ArbolGeneral<T>) hijosArr[i]).altura();
            if (h > maxAltura) maxAltura = h;
        }
        return maxAltura + 1;
    }
}
