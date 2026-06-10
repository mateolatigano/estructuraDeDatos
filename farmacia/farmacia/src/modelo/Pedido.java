package modelo;

import java.time.LocalDate;

public class Pedido
{
    public enum EstadoPedido
    {
        PENDIENTE, EN_TRANSITO, RECIBIDO, CANCELADO
    }

    private static int contador = 1;

    private String       idPedido;
    private String       codigoMedicamento;
    private int          cantidad;
    private EstadoPedido estado;
    private LocalDate    fechaPedido;
    private String       idSucursal;
    private String       proveedor;
    private LocalDate    fechaVencimiento;

    // Pre: codigoMedicamento existe en el sistema, cantidad > 0
    public Pedido(String codigoMedicamento, int cantidad, String idSucursal)
    {
        this.idPedido          = "PED-" + String.format("%03d", contador++);
        this.codigoMedicamento = codigoMedicamento;
        this.cantidad          = cantidad;
        this.idSucursal        = idSucursal;
        this.estado            = EstadoPedido.PENDIENTE;
        this.fechaPedido       = LocalDate.now();
        this.proveedor         = "ProveedorDefault";
        this.fechaVencimiento  = LocalDate.now().plusMonths(12);
    }

    // O(1) — solo permite transiciones validas
    public void cambiarEstado(EstadoPedido nuevoEstado)
    {
        if (estado != EstadoPedido.CANCELADO)
        {
            estado = nuevoEstado;
        }
    }

    // O(1)
    public void marcarRecibido()
    {
        cambiarEstado(EstadoPedido.RECIBIDO);
    }

    // O(1)
    public boolean estaRecibido()
    {
        return estado == EstadoPedido.RECIBIDO;
    }

    public String       getIdPedido()          { return idPedido; }
    public String       getCodigoMedicamento()  { return codigoMedicamento; }
    public int          getCantidad()           { return cantidad; }
    public EstadoPedido getEstado()             { return estado; }
    public LocalDate    getFechaPedido()        { return fechaPedido; }
    public String       getIdSucursal()         { return idSucursal; }
    public String       getProveedor()          { return proveedor; }
    public LocalDate    getFechaVencimiento()   { return fechaVencimiento; }
}
