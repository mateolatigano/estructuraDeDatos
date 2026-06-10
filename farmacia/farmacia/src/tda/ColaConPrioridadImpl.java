package tda;

public class ColaConPrioridadImpl<T> implements ColaConPrioridad<T>
{
    private static final int MAX = 200;

    private Object[] datos;
    private int[]    prioridades;
    private int      size;

    public ColaConPrioridadImpl()
    {
        datos       = new Object[MAX + 1];
        prioridades = new int[MAX + 1];
        size        = 0;
    }

    // O(n) — recorre la cola buscando la posicion de insercion por prioridad
    @Override
    public void enqueuePriority(T x, int p)
    {
        // Pre: la cola no esta llena
        // Insercion ordenada: mayor prioridad queda al INICIO (indice 0)
        // dequeue/front acceden al indice 0
        int pos = size;
        while (pos > 0 && prioridades[pos - 1] < p)
        {
            datos[pos]       = dados(pos - 1);
            prioridades[pos] = prioridades[pos - 1];
            pos--;
        }
        datos[pos]       = x;
        prioridades[pos] = p;
        size++;
    }

    // O(1) — extrae el elemento de mayor prioridad (indice 0), desplaza el resto
    @Override
    @SuppressWarnings("unchecked")
    public T dequeue()
    {
        // Pre: la cola no esta vacia
        T elem = (T) datos[0];
        for (int i = 0; i < size - 1; i++)
        {
            datos[i]       = dados(i + 1);
            prioridades[i] = prioridades[i + 1];
        }
        size--;
        return elem;
    }

    // O(1)
    @Override
    @SuppressWarnings("unchecked")
    public T front()
    {
        // Pre: la cola no esta vacia
        return (T) datos[0];
    }

    // O(1)
    @Override
    public int priority()
    {
        // Pre: la cola no esta vacia
        return prioridades[0];
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

    @SuppressWarnings("unchecked")
    private T dados(int i)
    {
        return (T) datos[i];
    }
}
