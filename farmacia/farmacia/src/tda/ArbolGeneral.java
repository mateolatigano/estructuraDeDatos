package tda;

public interface ArbolGeneral<T> {
    void                    inicializarArbol(T dato);
    boolean                 arbolVacio();
    T                       raiz();
    Conjunto<ArbolGeneral<T>> hijos();
    void                    agregarHijo(ArbolGeneral<T> subarbol);
    boolean                 esHoja();
    ArbolGeneral<T>         buscar(T dato);
    int                     altura();
}
