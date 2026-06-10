package sistema;

import tda.ColaConPrioridad;
import tda.ColaConPrioridadImpl;
import tda.Diccionario;
import tda.DiccionarioImpl;
import modelo.Lote;

public class StockSucursal
{
    private String                                    idSucursal;
    private Diccionario<String, ColaConPrioridad<Lote>> stock;
    private Diccionario<String, Integer>              stockMinimo;

    public StockSucursal(String idSucursal)
    {
        this.idSucursal  = idSucursal;
        this.stock       = new DiccionarioImpl<>();
        this.stockMinimo = new DiccionarioImpl<>();
        this.stock.initialize();
        this.stockMinimo.initialize();
    }

    // O(n) — dominado por enqueuePriority que es O(n) sobre los lotes del medicamento
    public void agregarLote(String codigoMed, Lote lote)
    {
        ColaConPrioridad<Lote> cola;
        if (!stock.containsKey(codigoMed))
        {
            cola = new ColaConPrioridadImpl<>();
            stock.put(codigoMed, cola);
        }
        else
        {
            cola = stock.get(codigoMed);
        }
        // Prioridad negativa: fecha mas cercana -> numero mas negativo -> mayor prioridad
        int prioridad = (int) -lote.getFechaVencimiento().toEpochDay();
        cola.enqueuePriority(lote, prioridad);
    }

    // O(n²) — while O(n) con enqueuePriority O(n) adentro
    public int consultarStockTotal(String codigoMed)
    {
        if (!stock.containsKey(codigoMed)) return 0;

        ColaConPrioridad<Lote> cola    = stock.get(codigoMed);
        ColaConPrioridad<Lote> temporal = new ColaConPrioridadImpl<>();
        int total = 0;

        while (!cola.isEmpty())
        {
            Lote lote      = cola.front();
            int  prioridad = cola.priority();
            cola.dequeue();
            total += lote.getCantidad();
            temporal.enqueuePriority(lote, prioridad);
        }

        // Restaurar la cola original
        while (!temporal.isEmpty())
        {
            int  prioridad = temporal.priority();
            Lote lote      = temporal.front();
            temporal.dequeue();
            cola.enqueuePriority(lote, prioridad);
        }

        return total;
    }

    // O(1) — Pre: existe al menos un lote
    public Lote obtenerLotePrioritario(String codigoMed)
    {
        if (!stock.containsKey(codigoMed)) return null;
        ColaConPrioridad<Lote> cola = stock.get(codigoMed);
        if (cola.isEmpty()) return null;
        return cola.front();
    }

    // O(k) donde k = cantidad de lotes consumidos para cubrir el despacho
    public void registrarDespacho(String codigoMed, int cantidad)
    {
        if (!stock.containsKey(codigoMed)) return;
        ColaConPrioridad<Lote> cola = stock.get(codigoMed);
        int restante = cantidad;

        while (restante > 0 && !cola.isEmpty())
        {
            Lote lote = cola.front();
            if (lote.getCantidad() <= restante)
            {
                restante -= lote.getCantidad();
                cola.dequeue();
                System.out.println("[FEFO] Lote utilizado: " + lote.getIdLote());
            }
            else
            {
                lote.descontarUnidades(restante);
                restante = 0;
                System.out.println("[FEFO] Lote utilizado: " + lote.getIdLote());
            }
        }
    }

    // O(1)
    public void configurarStockMinimo(String codigoMed, int minimo)
    {
        stockMinimo.put(codigoMed, minimo);
    }

    // O(n²) — llama a consultarStockTotal que es O(n²)
    public boolean verificarStockMinimo(String codigoMed)
    {
        if (!stockMinimo.containsKey(codigoMed)) return false;
        int actual  = consultarStockTotal(codigoMed);
        int minimo  = stockMinimo.get(codigoMed);
        return actual <= minimo;
    }

    public String getIdSucursal()
    {
        return idSucursal;
    }
}
