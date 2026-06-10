package modelo;

import java.time.LocalDate;

public class Lote implements Comparable<Lote>
{
    private String    idLote;
    private String    codigoMedicamento;
    private LocalDate fechaIngreso;
    private LocalDate fechaVencimiento;
    private int       cantidad;
    private String    proveedor;

    // Pre: cantidad >= 0 y fechaVencimiento posterior a fechaIngreso
    public Lote(String idLote, String codigoMedicamento, LocalDate fechaIngreso,
                LocalDate fechaVencimiento, int cantidad, String proveedor)
    {
        this.idLote            = idLote;
        this.codigoMedicamento = codigoMedicamento;
        this.fechaIngreso      = fechaIngreso;
        this.fechaVencimiento  = fechaVencimiento;
        this.cantidad          = cantidad;
        this.proveedor         = proveedor;
    }

    // O(1)
    @Override
    public int compareTo(Lote otro)
    {
        return this.fechaVencimiento.compareTo(otro.fechaVencimiento);
    }

    // O(1) — Pre: n > 0 y n <= cantidad actual
    public void descontarUnidades(int n)
    {
        this.cantidad -= n;
    }

    // O(1) — recibe fecha como parametro para mayor testeabilidad
    public boolean verificarVencimiento(LocalDate hoy)
    {
        return !fechaVencimiento.isAfter(hoy);
    }

    // O(1)
    public boolean estaAgotado()
    {
        return cantidad == 0;
    }

    public String    getIdLote()            { return idLote; }
    public String    getCodigoMedicamento() { return codigoMedicamento; }
    public LocalDate getFechaIngreso()      { return fechaIngreso; }
    public LocalDate getFechaVencimiento()  { return fechaVencimiento; }
    public int       getCantidad()          { return cantidad; }
    public String    getProveedor()         { return proveedor; }
}
