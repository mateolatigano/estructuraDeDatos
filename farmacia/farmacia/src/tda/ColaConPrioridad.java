package tda;

public interface ColaConPrioridad<T> {
    void    enqueuePriority(T x, int p);
    T       dequeue();
    T       front();
    int     priority();
    boolean isEmpty();
    boolean isFull();
}
