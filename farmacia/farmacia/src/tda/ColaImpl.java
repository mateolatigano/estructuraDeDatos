package tda;

public class ColaImpl<T> implements Cola<T>
{
    private static final int MAX = 100;
    private Object[]         datos;
    private int              frente;
    private int              fin;
    private int              size;

    public ColaImpl()
    {
        datos  = new Object[MAX];
        frente = 0;
        fin    = 0;
        size   = 0;
    }

    @Override
    public void enqueue(T x)
    {
        // Pre: la cola no esta llena
        datos[fin] = x;
        fin        = (fin + 1) % MAX;
        size++;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T dequeue()
    {
        // Pre: la cola no esta vacia
        T elem = (T) datos[frente];
        frente = (frente + 1) % MAX;
        size--;
        return elem;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T front()
    {
        // Pre: la cola no esta vacia
        return (T) datos[frente];
    }

    @Override
    public boolean isEmpty()
    {
        return size == 0;
    }

    @Override
    public boolean isFull()
    {
        return size == MAX;
    }
}
