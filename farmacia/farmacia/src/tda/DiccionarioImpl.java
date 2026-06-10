package tda;

import java.util.HashMap;

public class DiccionarioImpl<K, V> implements Diccionario<K, V>
{
    private HashMap<K, V> mapa;

    public DiccionarioImpl()
    {
        mapa = new HashMap<>();
    }

    // O(1) amortizado
    @Override
    public void initialize()
    {
        mapa = new HashMap<>();
    }

    // O(1) amortizado
    @Override
    public void put(K key, V value)
    {
        mapa.put(key, value);
    }

    // O(1) amortizado
    @Override
    public void remove(K key)
    {
        mapa.remove(key);
    }

    // O(1) amortizado — Pre: la clave existe
    @Override
    public V get(K key)
    {
        return mapa.get(key);
    }

    // O(1) amortizado
    @Override
    public boolean containsKey(K key)
    {
        return mapa.containsKey(key);
    }

    // O(n)
    @Override
    @SuppressWarnings("unchecked")
    public K[] keys()
    {
        return (K[]) mapa.keySet().toArray();
    }

    @Override
    public boolean isEmpty()
    {
        return mapa.isEmpty();
    }
}
