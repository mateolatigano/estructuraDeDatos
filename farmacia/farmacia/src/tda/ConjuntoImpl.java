package tda;

public class ConjuntoImpl<T> implements Conjunto<T>
{
    private static final int MAX = 200;
    private Object[]         datos;
    private int              size;

    public ConjuntoImpl()
    {
        datos = new Object[MAX];
        size  = 0;
    }

    // O(n) — verifica unicidad antes de insertar
    @Override
    public void add(T x)
    {
        // Pre: el conjunto no esta lleno
        if (!contains(x))
        {
            datos[size] = x;
            size++;
        }
    }

    // O(n)
    @Override
    public void remove(T x)
    {
        for (int i = 0; i < size; i++)
        {
            if (datos[i].equals(x))
            {
                datos[i] = datos[size - 1];
                size--;
                return;
            }
        }
    }

    // O(n)
    @Override
    public boolean contains(T x)
    {
        for (int i = 0; i < size; i++)
        {
            if (datos[i].equals(x)) return true;
        }
        return false;
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

    @Override
    public int size()
    {
        return size;
    }

    @Override
    public void clear()
    {
        size = 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T[] toArray()
    {
        // Retorna Object[] — el caller debe iterar con for(int i...) y castear individualmente
        Object[] resultado = new Object[size];
        for (int i = 0; i < size; i++)
        {
            resultado[i] = datos[i];
        }
        return (T[]) resultado;
    }
}
