package tda;

public interface Grafo<T> {
    void        inicializarGrafo();
    void        agregarVertice(T v);
    void        agregarArista(T v1, T v2);
    void        eliminarVertice(T v);
    void        eliminarArista(T v1, T v2);
    boolean     existeVertice(T v);
    boolean     existeArista(T v1, T v2);
    Conjunto<T> vecinos(T v);
    Conjunto<T> vertices();
    Conjunto<T> recorrerBFS(T origen);
    Conjunto<T> recorrerDFS(T origen);
    boolean     isEmpty();
}
