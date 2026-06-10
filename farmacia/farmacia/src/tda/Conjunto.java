package tda;

public interface Conjunto<T> {
    void    add(T x);
    void    remove(T x);
    boolean contains(T x);
    boolean isEmpty();
    boolean isFull();
    int     size();
    void    clear();
    T[]     toArray();
}
