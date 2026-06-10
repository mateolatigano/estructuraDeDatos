package sistema;

import java.time.LocalDate;
import tda.Diccionario;
import tda.DiccionarioImpl;
import modelo.Lote;
import modelo.Medicamento;
import modelo.Pedido;

public class SistemaFarmacia
{
    private Diccionario<String, Medicamento>   catalogoGlobal;
    private Diccionario<String, StockSucursal> sucursales;
    private ColaPedidos                         colaPedidos;
    private RedSustitutos                       redSustitutos;
    private CategoriaMedicamentos               categorias;

    public SistemaFarmacia()
    {
        this.catalogoGlobal = new DiccionarioImpl<>();
        this.sucursales     = new DiccionarioImpl<>();
        this.catalogoGlobal.initialize();
        this.sucursales.initialize();
        this.colaPedidos   = new ColaPedidos();
        this.redSustitutos = new RedSustitutos();
        this.categorias    = new CategoriaMedicamentos();
    }

    // O(1) amortizado
    public void registrarMedicamento(Medicamento m)
    {
        if (catalogoGlobal.containsKey(m.getCodigo()))
        {
            System.out.println("[ERROR] El medicamento ya existe: " + m.getCodigo());
            return;
        }
        catalogoGlobal.put(m.getCodigo(), m);
        redSustitutos.agregarMedicamento(m.getCodigo());
        System.out.println("[OK] Medicamento registrado: " + m.getCodigo());
    }

    // O(n) — dominado por agregarLote en StockSucursal
    public void ingresarLote(String idSucursal, String codigoMed, Lote lote)
    {
        if (!catalogoGlobal.containsKey(codigoMed))
        {
            System.out.println("[ERROR] Medicamento inexistente: " + codigoMed);
            return;
        }
        StockSucursal stock;
        if (!sucursales.containsKey(idSucursal))
        {
            stock = new StockSucursal(idSucursal);
            sucursales.put(idSucursal, stock);
        }
        else
        {
            stock = sucursales.get(idSucursal);
        }
        stock.agregarLote(codigoMed, lote);
        System.out.println("[LOTE INGRESADO] " + codigoMed + " - "
                + lote.getIdLote()
                + " - vence " + lote.getFechaVencimiento()
                + " - cantidad " + lote.getCantidad());
    }

    // Flujo completo: FEFO -> sustituto -> pedido
    public void despacharMedicamento(String idSucursal, String codigoMed, int cantidad)
    {
        if (!sucursales.containsKey(idSucursal))
        {
            System.out.println("[ERROR] Sucursal inexistente: " + idSucursal);
            return;
        }
        if (!catalogoGlobal.containsKey(codigoMed))
        {
            System.out.println("[ERROR] Medicamento inexistente: " + codigoMed);
            return;
        }

        StockSucursal stock = sucursales.get(idSucursal);

        System.out.println("[DESPACHO] Medicamento solicitado: " + codigoMed
                + " - cantidad " + cantidad);

        // CASO 1: hay stock suficiente
        if (stock.consultarStockTotal(codigoMed) >= cantidad)
        {
            stock.registrarDespacho(codigoMed, cantidad);
            System.out.println("[STOCK RESTANTE] " + codigoMed + " = "
                    + stock.consultarStockTotal(codigoMed));

            if (stock.verificarStockMinimo(codigoMed))
            {
                Pedido pedido = new Pedido(codigoMed, 50, idSucursal);
                colaPedidos.encolarPedido(pedido);
                System.out.println("[PEDIDO AUTO] Stock minimo alcanzado para " + codigoMed);
            }
        }
        else
        {
            // CASO 2: sin stock -> buscar sustituto
            System.out.println("[SIN STOCK] Se busca sustituto en la red");
            String sustituto = redSustitutos.buscarAlternativaDisponible(codigoMed, stock);

            if (sustituto != null)
            {
                System.out.println("[SUSTITUTO ENCONTRADO] " + sustituto);
                stock.registrarDespacho(sustituto, cantidad);
                System.out.println("[DESPACHO SUSTITUTO] " + sustituto
                        + " - cantidad " + cantidad);
                System.out.println("[STOCK RESTANTE] " + sustituto + " = "
                        + stock.consultarStockTotal(sustituto));
            }
            else
            {
                // CASO 3: sin sustituto -> generar pedido
                System.out.println("[SIN STOCK] No hay sustitutos disponibles");
                Pedido pedido = new Pedido(codigoMed, cantidad, idSucursal);
                colaPedidos.encolarPedido(pedido);
            }
        }
    }

    // O(n) — dominado por ingresarLote
    public void recibirPedido()
    {
        Pedido p = colaPedidos.procesarSiguiente();
        if (p != null)
        {
            Lote lote = new Lote(
                "L-" + System.currentTimeMillis(),
                p.getCodigoMedicamento(),
                LocalDate.now(),
                p.getFechaVencimiento(),
                p.getCantidad(),
                p.getProveedor()
            );
            ingresarLote(p.getIdSucursal(), p.getCodigoMedicamento(), lote);
            System.out.println("[RECIBIDO] Pedido de " + p.getCodigoMedicamento()
                    + " procesado e ingresado al stock.");
        }
    }

    public void agregarRelacionSustituto(String cod1, String cod2)
    {
        redSustitutos.agregarRelacion(cod1, cod2);
    }

    public void configurarStockMinimo(String idSucursal, String codigoMed, int minimo)
    {
        if (!sucursales.containsKey(idSucursal)) return;
        sucursales.get(idSucursal).configurarStockMinimo(codigoMed, minimo);
    }

    public CategoriaMedicamentos getCategorias()
    {
        return categorias;
    }
}
