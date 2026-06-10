package sistema;

import tda.ArbolGeneral;
import tda.ArbolGeneralImpl;
import tda.Conjunto;
import tda.ConjuntoImpl;
import modelo.Categoria;

public class CategoriaMedicamentos
{
    private ArbolGeneral<Categoria> arbol;

    public CategoriaMedicamentos()
    {
        this.arbol = new ArbolGeneralImpl<>();
        this.arbol.inicializarArbol(new Categoria("Catalogo"));
    }

    // O(n) — DFS sobre el arbol completo
    public ArbolGeneral<Categoria> buscarCategoria(String nombre)
    {
        return arbol.buscar(new Categoria(nombre));
    }

    // O(n) — dominado por buscarCategoria
    public void agregarSubcategoria(String nombrePadre, String nombreNueva)
    {
        ArbolGeneral<Categoria> padre = buscarCategoria(nombrePadre);
        if (padre == null || padre.arbolVacio()) return;

        ArbolGeneral<Categoria> nueva = new ArbolGeneralImpl<>();
        nueva.inicializarArbol(new Categoria(nombreNueva));
        padre.agregarHijo(nueva);
    }

    // O(n) — dominado por buscarCategoria
    public void agregarMedicamentoACategoria(String nombreCategoria,
                                              String codigoMedicamento)
    {
        ArbolGeneral<Categoria> nodo = buscarCategoria(nombreCategoria);
        if (nodo == null || nodo.arbolVacio()) return;
        nodo.raiz().agregarMedicamento(codigoMedicamento);
    }

    // O(n) — recorre el subarbol completo de la categoria
    public Conjunto<String> listarMedicamentosCategoria(String nombreCategoria)
    {
        Conjunto<String> resultado = new ConjuntoImpl<>();
        ArbolGeneral<Categoria> nodo = buscarCategoria(nombreCategoria);
        if (nodo == null || nodo.arbolVacio()) return resultado;
        recolectarMedicamentos(nodo, resultado);
        return resultado;
    }

    // O(n) — visita cada nodo del subarbol exactamente una vez
    @SuppressWarnings("unchecked")
    private void recolectarMedicamentos(ArbolGeneral<Categoria> nodo,
                                         Conjunto<String> acumulador)
    {
        Object[] meds = nodo.raiz().getMedicamentos().toArray();
        for (int i = 0; i < meds.length; i++)
        {
            acumulador.add((String) meds[i]);
        }
        if (nodo.esHoja()) return;

        Object[] hijos = nodo.hijos().toArray();
        for (int i = 0; i < hijos.length; i++)
        {
            recolectarMedicamentos((ArbolGeneral<Categoria>) hijos[i], acumulador);
        }
    }

    // O(n) — recorre todos los nodos del arbol en preorden
    public void imprimirArbol()
    {
        imprimirNodo(arbol, 0);
    }

    @SuppressWarnings("unchecked")
    private void imprimirNodo(ArbolGeneral<Categoria> nodo, int nivel)
    {
        if (nodo.arbolVacio()) return;
        Categoria cat = nodo.raiz();
        System.out.println("  ".repeat(nivel) + cat.getNombre());

        Object[] meds = cat.getMedicamentos().toArray();
        for (int i = 0; i < meds.length; i++)
        {
            System.out.println("  ".repeat(nivel + 1) + "- " + meds[i]);
        }
        if (nodo.esHoja()) return;

        Object[] hijos = nodo.hijos().toArray();
        for (int i = 0; i < hijos.length; i++)
        {
            imprimirNodo((ArbolGeneral<Categoria>) hijos[i], nivel + 1);
        }
    }

}

