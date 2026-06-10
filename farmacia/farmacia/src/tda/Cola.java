package tda;

public interface Cola<T> {
    void    enqueue(T x);
    T       dequeue();
    T       front();
    boolean isEmpty();
    boolean isFull();
}
