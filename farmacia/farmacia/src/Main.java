import java.time.LocalDate;
import modelo.Lote;
import modelo.Medicamento;
import sistema.SistemaFarmacia;

public class Main
{
    public static void main(String[] args)
    {
        System.out.println("======================================");
        System.out.println("[INICIO DE PRUEBA]");
        System.out.println("Sucursal: S001");
        System.out.println("======================================");
        System.out.println();

        SistemaFarmacia sistema = new SistemaFarmacia();

        // --- Registro de medicamentos ---
        sistema.registrarMedicamento(new Medicamento("IBU001", "Ibuprofeno",
                "Antiinflamatorio no esteroide", "500mg",
                Medicamento.Categoria.ANTIINFLAMATORIO));
        sistema.registrarMedicamento(new Medicamento("DIC001", "Diclofenac",
                "Antiinflamatorio AINE", "50mg",
                Medicamento.Categoria.ANTIINFLAMATORIO));
        sistema.registrarMedicamento(new Medicamento("AMO001", "Amoxicilina",
                "Antibiotico betalactamico", "500mg",
                Medicamento.Categoria.ANTIBIOTICO));
        sistema.registrarMedicamento(new Medicamento("PAR001", "Paracetamol",
                "Analgesico y antipiretico", "500mg",
                Medicamento.Categoria.ANALGESICO));

        // --- Relaciones de sustitucion en el grafo ---
        sistema.agregarRelacionSustituto("IBU001", "DIC001");
        sistema.agregarRelacionSustituto("PAR001", "IBU001");

        // --- Configuracion de stock minimo ---
        sistema.configurarStockMinimo("S001", "IBU001", 5);
        sistema.configurarStockMinimo("S001", "DIC001", 3);
        sistema.configurarStockMinimo("S001", "PAR001", 5);

        System.out.println();
        System.out.println("--- PASO 1: Ingreso de lotes ---");

        sistema.ingresarLote("S001", "IBU001", new Lote(
                "L001", "IBU001",
                LocalDate.of(2025, 1, 10),
                LocalDate.of(2025, 6, 1),
                10, "FarmaDistrib"));

        sistema.ingresarLote("S001", "IBU001", new Lote(
                "L002", "IBU001",
                LocalDate.of(2025, 1, 10),
                LocalDate.of(2025, 8, 15),
                20, "FarmaDistrib"));

        sistema.ingresarLote("S001", "DIC001", new Lote(
                "L003", "DIC001",
                LocalDate.of(2025, 1, 10),
                LocalDate.of(2025, 7, 10),
                15, "MediSupply"));

        sistema.ingresarLote("S001", "PAR001", new Lote(
                "L004", "PAR001",
                LocalDate.of(2025, 1, 10),
                LocalDate.of(2025, 9, 20),
                8, "FarmaDistrib"));

        System.out.println();
        System.out.println("--- PASO 2: Despacho 10 uds IBU001 (FEFO) ---");
        sistema.despacharMedicamento("S001", "IBU001", 10);

        System.out.println();
        System.out.println("--- PASO 3: Despacho 18 uds IBU001 -> stock minimo ---");
        sistema.despacharMedicamento("S001", "IBU001", 18);

        System.out.println();
        System.out.println("--- PASO 4: Despacho 5 uds IBU001 sin stock -> sustituto BFS ---");
        sistema.despacharMedicamento("S001", "IBU001", 5);

        System.out.println();
        System.out.println("--- PASO 5: Despacho 20 uds AMO001 sin stock ni sustitutos ---");
        sistema.despacharMedicamento("S001", "AMO001", 20);

        System.out.println();
        System.out.println("--- PASO 6: Recepcion del primer pedido (FIFO) ---");
        System.out.println("[PROCESAR PEDIDOS]");
        sistema.recibirPedido();

        System.out.println();
        System.out.println("--- Arbol de categorias ---");
        sistema.getCategorias().agregarSubcategoria("Catalogo", "Analgesicos");
        sistema.getCategorias().agregarSubcategoria("Analgesicos", "Antiinflamatorios");
        sistema.getCategorias().agregarMedicamentoACategoria("Antiinflamatorios", "IBU001");
        sistema.getCategorias().agregarMedicamentoACategoria("Antiinflamatorios", "DIC001");
        sistema.getCategorias().agregarMedicamentoACategoria("Analgesicos", "PAR001");
        sistema.getCategorias().imprimirArbol();

        System.out.println();
        System.out.println("[FIN DE PRUEBA]");
        System.out.println("======================================");
    }
}
