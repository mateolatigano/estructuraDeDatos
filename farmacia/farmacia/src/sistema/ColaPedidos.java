package sistema;

import tda.Cola;
import tda.ColaImpl;
import modelo.Pedido;

public class ColaPedidos
{
    private Cola<Pedido> cola;

    public ColaPedidos()
    {
        this.cola = new ColaImpl<>();
    }

    // O(1)
    public void encolarPedido(Pedido pedido)
    {
        cola.enqueue(pedido);
        System.out.println("[PEDIDO GENERADO] " + pedido.getCodigoMedicamento()
                + " - cantidad " + pedido.getCantidad());
    }

    // O(1)
    public Pedido procesarSiguiente()
    {
        if (cola.isEmpty())
        {
            System.out.println("[INFO] No hay pedidos pendientes.");
            return null;
        }
        Pedido p = cola.dequeue();
        p.marcarRecibido();
        return p;
    }

    // O(1)
    public Pedido obtenerPrimerPedido()
    {
        if (cola.isEmpty()) return null;
        return cola.front();
    }

    // O(1)
    public boolean estaVacia()
    {
        return cola.isEmpty();
    }
}
