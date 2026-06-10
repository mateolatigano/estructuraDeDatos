package tda;

public interface Diccionario<K, V> {
    void    initialize();
    void    put(K key, V value);
    void    remove(K key);
    V       get(K key);
    boolean containsKey(K key);
    K[]     keys();
    boolean isEmpty();
}
