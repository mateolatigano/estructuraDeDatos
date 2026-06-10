# Documentación técnica — Sistema de Farmacia

## Índice
1. [Diagrama de relaciones](#1-diagrama-de-relaciones)
2. [Clases del modelo](#2-clases-del-modelo)
3. [Clases del sistema](#3-clases-del-sistema)
4. [Clase Main](#4-clase-main)
5. [Justificación de TDAs](#5-justificación-de-tdas)
6. [Resumen de costos](#6-resumen-de-costos)

---

## 1. Diagrama de relaciones

```
Main
 └── SistemaFarmacia
       ├── Diccionario<String, Medicamento>     (catálogo global)
       ├── Diccionario<String, StockSucursal>   (una entrada por sucursal)
       │     └── StockSucursal
       │           ├── Diccionario<String, ColaConPrioridad<Lote>>
       │           └── Diccionario<String, Integer>   (stock mínimo)
       ├── ColaPedidos
       │     └── Cola<Pedido>
       ├── RedSustitutos
       │     └── Grafo<String>
       └── CategoriaMedicamentos
             └── ArbolGeneral<Categoria>
```

**Modelo (entidades puras, sin lógica de sistema):**
- `Medicamento` ← referenciado por código `String` en todo el sistema
- `Lote` ← almacenado dentro de `ColaConPrioridad<Lote>` en `StockSucursal`
- `Pedido` ← almacenado dentro de `Cola<Pedido>` en `ColaPedidos`

---

## 2. Clases del modelo

### `Medicamento`

**Rol:** Representa un medicamento del catálogo. Es la entidad base del sistema.  
**Atributos clave:** `codigo` (único), `nombre`, `descripcion`, `dosis`, `categoria` (enum).  
**Relaciones:** es almacenado en el `Diccionario` de `SistemaFarmacia`. Su código es la clave de búsqueda en todo el sistema.

| Operación | Costo | Descripción |
|-----------|-------|-------------|
| `equals(obj)` | O(1) | Compara por código |
| `hashCode()` | O(1) | Hash del código (longitud fija en práctica) |
| Getters | O(1) | Acceso directo a atributos |

**Por qué es importante:** actúa como contrato de identidad del medicamento. Al implementar `equals` y `hashCode` por código, permite que el `Diccionario` (basado en hash) funcione correctamente.

---

### `Lote`

**Rol:** Representa una unidad física de stock: una carga concreta de un medicamento con fecha de vencimiento y cantidad disponible.  
**Atributos clave:** `idLote`, `codigoMedicamento`, `fechaVencimiento`, `cantidad`.  
**Relaciones:** es almacenado dentro de la `ColaConPrioridad<Lote>` en `StockSucursal`. Implementa `Comparable<Lote>` para poder ser ordenado por fecha de vencimiento.

| Operación | Costo | Descripción |
|-----------|-------|-------------|
| `compareTo(otro)` | O(1) | Compara fechas de vencimiento |
| `descontarUnidades(n)` | O(1) | Resta unidades al lote |
| `verificarVencimiento(hoy)` | O(1) | Compara fecha con la actual |
| `estaAgotado()` | O(1) | Verifica si cantidad == 0 |
| Getters | O(1) | Acceso directo a atributos |

**Por qué es importante:** al implementar `Comparable`, permite que la `ColaConPrioridad` ordene los lotes por fecha de vencimiento de manera natural, habilitando la política **FEFO** (First Expired, First Out).

---

### `Pedido`

**Rol:** Representa una orden de reposición de stock generada automáticamente cuando el stock cae por debajo del mínimo o cuando no hay stock ni sustitutos disponibles.  
**Atributos clave:** `idPedido` (autogenerado), `codigoMedicamento`, `cantidad`, `estado` (enum `EstadoPedido`), `idSucursal`.  
**Relaciones:** es almacenado en la `Cola<Pedido>` de `ColaPedidos`. Es creado por `SistemaFarmacia` y procesado por `recibirPedido()`.

| Operación | Costo | Descripción |
|-----------|-------|-------------|
| Constructor | O(1) | Genera ID y fecha automáticamente |
| `cambiarEstado(nuevoEstado)` | O(1) | Solo permite transiciones válidas (no cambia si está CANCELADO) |
| `marcarRecibido()` | O(1) | Delegación a `cambiarEstado` |
| `estaRecibido()` | O(1) | Comparación de enum |
| Getters | O(1) | Acceso directo a atributos |

**Por qué es importante:** encapsula la lógica de estado del ciclo de vida de una orden, garantizando que un pedido cancelado no pueda ser reactivado.

---

## 3. Clases del sistema

### `StockSucursal`

**Rol:** Gestiona el inventario de una sucursal específica. Mantiene los lotes de cada medicamento ordenados por fecha de vencimiento (política FEFO) y controla los umbrales mínimos de stock.

**TDAs usados:**
- `Diccionario<String, ColaConPrioridad<Lote>>` → clave: código de medicamento, valor: cola de lotes ordenada por vencimiento.
- `Diccionario<String, Integer>` → stock mínimo configurado por medicamento.

**Relaciones:** es creado y gestionado por `SistemaFarmacia`. Contiene instancias de `Lote`. Es consultado por `RedSustitutos` al buscar alternativas disponibles.

| Operación | Costo | Descripción |
|-----------|-------|-------------|
| `agregarLote(codigoMed, lote)` | O(n) | Inserta en la cola con prioridad; n = lotes existentes del medicamento |
| `consultarStockTotal(codigoMed)` | O(n²) | Vacía y restaura la cola sumando cantidades; n = cantidad de lotes |
| `obtenerLotePrioritario(codigoMed)` | O(1) | Consulta el frente de la cola sin extraerlo |
| `registrarDespacho(codigoMed, cantidad)` | O(k) | k = cantidad de lotes consumidos para cubrir el pedido |
| `configurarStockMinimo(codigoMed, minimo)` | O(1) | Inserción en diccionario |
| `verificarStockMinimo(codigoMed)` | O(n²) | Llama a `consultarStockTotal` internamente |

**Por qué es importante:** es el núcleo del control de inventario. La combinación `Diccionario + ColaConPrioridad` permite acceso O(1) al medicamento y despacho FEFO automático sin necesidad de ordenar manualmente.

---

### `ColaPedidos`

**Rol:** Gestiona la cola de pedidos de reposición pendientes de procesamiento, respetando el orden de llegada (FIFO).

**TDA usado:** `Cola<Pedido>` — garantiza que los pedidos se procesen en el orden en que fueron generados, sin posibilidad de saltear ninguno.

**Relaciones:** es creada y utilizada exclusivamente por `SistemaFarmacia`. Recibe `Pedido` generados en `despacharMedicamento()` y los consume en `recibirPedido()`.

| Operación | Costo | Descripción |
|-----------|-------|-------------|
| `encolarPedido(pedido)` | O(1) | Agrega al final de la cola |
| `procesarSiguiente()` | O(1) | Extrae el primero, lo marca como recibido |
| `obtenerPrimerPedido()` | O(1) | Consulta sin extraer |
| `estaVacia()` | O(1) | Verifica si hay pedidos pendientes |

**Por qué es importante:** la política FIFO garantiza equidad en el procesamiento: el primer medicamento que quedó sin stock es el primero en reponerse.

---

### `RedSustitutos`

**Rol:** Modela las relaciones de sustitución entre medicamentos como un grafo no dirigido. Cuando no hay stock de un medicamento, permite encontrar automáticamente un sustituto disponible mediante BFS.

**TDA usado:** `Grafo<String>` — cada vértice es un código de medicamento y cada arista indica que dos medicamentos son intercambiables terapéuticamente.

**Relaciones:** es creada por `SistemaFarmacia`. Usa los códigos de `Medicamento`. Consulta `StockSucursal` para verificar disponibilidad del sustituto encontrado.

| Operación | Costo | Descripción |
|-----------|-------|-------------|
| `agregarMedicamento(cod)` | O(1) amortizado | Agrega vértice si no existe |
| `agregarRelacion(cod1, cod2)` | O(1) | Agrega arista entre dos medicamentos |
| `existeRelacion(cod1, cod2)` | O(1) | Consulta si hay arista |
| `obtenerSustitutos(cod)` | O(1) | Devuelve los vecinos directos del vértice |
| `buscarAlternativaDisponible(origen, stock)` | O(V+E) + O(n²·C) | BFS sobre el grafo; por cada candidato consulta stock (O(n²)); C = candidatos |
| `recorrerRed(origen)` | O(V+E) | BFS completo desde un medicamento |

**Por qué es importante:** el grafo refleja la realidad del dominio: las sustituciones son simétricas (si A sustituye a B, B sustituye a A) y pueden existir cadenas de sustitución. BFS garantiza encontrar el sustituto más cercano (menor grado de separación) primero.

---

### `CategoriaMedicamentos`

**Rol:** Organiza los medicamentos en una jerarquía de categorías terapéuticas (ej: Catálogo → Analgésicos → Antiinflamatorios). Permite listar todos los medicamentos de una categoría incluyendo sus subcategorías.

**TDA usado:** `ArbolGeneral<Categoria>` — la jerarquía de categorías es naturalmente un árbol: una raíz (Catálogo), nodos internos (grupos) y hojas (categorías terminales con medicamentos).

**Relaciones:** es creada y expuesta por `SistemaFarmacia` a través de `getCategorias()`. Cada nodo del árbol es una instancia de `Categoria` que contiene una lista de códigos de `Medicamento`.

| Operación | Costo | Descripción |
|-----------|-------|-------------|
| `buscarCategoria(nombre)` | O(n) | DFS sobre el árbol completo; n = cantidad de nodos |
| `agregarSubcategoria(padre, nueva)` | O(n) | Busca el nodo padre + agrega hijo |
| `agregarMedicamentoACategoria(cat, cod)` | O(n) | Busca el nodo + agrega el código |
| `listarMedicamentosCategoria(cat)` | O(n) | Recorre el subárbol completo de la categoría |
| `imprimirArbol()` | O(n) | Recorrido preorden completo |

**Por qué es importante:** el árbol general (a diferencia del binario) permite que cada categoría tenga un número arbitrario de subcategorías, reflejando fielmente la clasificación farmacológica real. `listarMedicamentosCategoria` recupera no solo los medicamentos del nodo sino también los de todas las subcategorías descendientes.

---

### `SistemaFarmacia`

**Rol:** Fachada principal del sistema. Integra todos los subsistemas y orquesta el flujo completo de despacho: stock FEFO → sustituto BFS → pedido automático FIFO.

**TDAs propios:**
- `Diccionario<String, Medicamento>` → catálogo global de medicamentos.
- `Diccionario<String, StockSucursal>` → una entrada por cada sucursal activa.

**Relaciones:** contiene y coordina a `StockSucursal`, `ColaPedidos`, `RedSustitutos` y `CategoriaMedicamentos`. Crea instancias de `Pedido` y `Lote` internamente.

| Operación | Costo | Descripción |
|-----------|-------|-------------|
| `registrarMedicamento(m)` | O(1) amortizado | Inserta en catálogo y en el grafo de sustitutos |
| `ingresarLote(sucursal, cod, lote)` | O(n) | Delegado a `StockSucursal.agregarLote` |
| `despacharMedicamento(sucursal, cod, cant)` — caso 1: hay stock | O(k) + O(n²) | Despacho FEFO + verificación de mínimo |
| `despacharMedicamento(sucursal, cod, cant)` — caso 2: sin stock, hay sustituto | O(V+E) + O(n²·C) | BFS + consulta de stock por candidato |
| `despacharMedicamento(sucursal, cod, cant)` — caso 3: sin stock ni sustituto | O(V+E) + O(1) | BFS sin resultado + generar pedido |
| `recibirPedido()` | O(n) | Procesa el primer pedido de la cola e ingresa el lote |
| `agregarRelacionSustituto(cod1, cod2)` | O(1) | Delegado a `RedSustitutos` |
| `configurarStockMinimo(sucursal, cod, min)` | O(1) | Delegado a `StockSucursal` |

**Por qué es importante:** al centralizar toda la lógica de coordinación en una sola clase, el resto del sistema permanece desacoplado. `Main` solo necesita conocer a `SistemaFarmacia`; no interactúa directamente con ningún TDA ni con las clases de sistema internas.

---

## 4. Clase `Main`

**Rol:** Punto de entrada del programa. Instancia `SistemaFarmacia` y ejecuta un escenario de prueba completo que cubre todos los flujos del sistema.

**Flujo demostrado:**
1. Registro de medicamentos (IBU001, DIC001, AMO001, PAR001).
2. Definición de relaciones de sustitución en el grafo.
3. Configuración de stocks mínimos.
4. Ingreso de lotes con distintas fechas de vencimiento.
5. Despacho con stock disponible → aplica FEFO.
6. Despacho que agota el stock y activa pedido automático por stock mínimo.
7. Despacho sin stock → BFS encuentra sustituto (DIC001 para IBU001).
8. Despacho sin stock ni sustitutos → genera pedido (AMO001).
9. Recepción del primer pedido en cola → ingresa nuevo lote.
10. Construcción e impresión del árbol de categorías.

**Relaciones:** usa exclusivamente `SistemaFarmacia` como punto de entrada al sistema.

---

## 5. Justificación de TDAs

### Diccionario (HashMap)
**Usado en:** `SistemaFarmacia` (catálogo, sucursales), `StockSucursal` (stock por medicamento, stock mínimo).  
**Justificación:** el acceso por clave (código de medicamento, ID de sucursal) es la operación más frecuente del sistema. El `Diccionario` garantiza búsqueda, inserción y eliminación en O(1) amortizado, lo que es imposible con listas o árboles de búsqueda que son O(log n) o O(n).

### Cola con Prioridad (Min-Heap por vencimiento)
**Usado en:** `StockSucursal` — una cola por cada medicamento.  
**Justificación:** implementa la política **FEFO** (First Expired, First Out), obligatoria en el manejo de medicamentos para minimizar pérdidas por vencimiento. La prioridad se asigna como el negativo de la fecha de vencimiento en días epoch, de modo que el lote que vence más pronto siempre queda al frente. Alternativa descartada: lista ordenada requeriría O(n) por inserción y O(1) por extracción; la cola con prioridad equilibra ambas en O(log n) / O(n) según la implementación.

### Cola FIFO
**Usado en:** `ColaPedidos`.  
**Justificación:** los pedidos deben procesarse en orden de generación (equidad). Una cola FIFO garantiza O(1) para encolar y desencolar, y no permite reordenamiento ni acceso aleatorio, lo cual es correcto: no tiene sentido saltear un pedido pendiente.

### Grafo no dirigido
**Usado en:** `RedSustitutos`.  
**Justificación:** las relaciones de sustitución son simétricas (si A sustituye a B, B sustituye a A) y no tienen peso. El grafo modela exactamente esto. BFS sobre el grafo garantiza encontrar el sustituto más cercano (menor número de "saltos" de sustitución) antes que uno más lejano, lo que es clínicamente preferible.

### Árbol General
**Usado en:** `CategoriaMedicamentos`.  
**Justificación:** la clasificación farmacológica es jerárquica por naturaleza y cada nodo puede tener un número arbitrario de subcategorías. El árbol general (a diferencia del binario) soporta esto directamente. El recorrido por subárbol en `listarMedicamentosCategoria` permite consultar "todos los antiinflamatorios" incluyendo subcategorías sin conocer la estructura interna.

### Conjunto
**Usado en:** como tipo de retorno en `RedSustitutos` y `CategoriaMedicamentos`.  
**Justificación:** garantiza que no haya duplicados en los resultados de listados y recorridos de grafos/árboles, sin necesidad de verificar manualmente.

---

## 6. Resumen de costos

| Clase | Operación | Costo |
|-------|-----------|-------|
| `Medicamento` | `equals`, `hashCode`, getters | O(1) |
| `Lote` | `compareTo`, `descontarUnidades`, `verificarVencimiento`, `estaAgotado` | O(1) |
| `Pedido` | Constructor, `cambiarEstado`, `marcarRecibido`, `estaRecibido` | O(1) |
| `StockSucursal` | `agregarLote` | O(n) — n = lotes del medicamento |
| `StockSucursal` | `consultarStockTotal`, `verificarStockMinimo` | O(n²) |
| `StockSucursal` | `obtenerLotePrioritario`, `configurarStockMinimo` | O(1) |
| `StockSucursal` | `registrarDespacho` | O(k) — k = lotes consumidos |
| `ColaPedidos` | Todas las operaciones | O(1) |
| `RedSustitutos` | `agregarMedicamento`, `agregarRelacion`, `existeRelacion`, `obtenerSustitutos` | O(1) |
| `RedSustitutos` | `recorrerRed` | O(V+E) |
| `RedSustitutos` | `buscarAlternativaDisponible` | O(V+E) + O(n²·C) |
| `CategoriaMedicamentos` | Todas las operaciones | O(n) — n = nodos del árbol |
| `SistemaFarmacia` | `registrarMedicamento`, `agregarRelacionSustituto`, `configurarStockMinimo` | O(1) |
| `SistemaFarmacia` | `ingresarLote`, `recibirPedido` | O(n) |
| `SistemaFarmacia` | `despacharMedicamento` (caso 1: hay stock) | O(k) + O(n²) |
| `SistemaFarmacia` | `despacharMedicamento` (caso 2/3: sin stock) | O(V+E) + O(n²·C) |

**Referencias:** n = cantidad de lotes de un medicamento · k = lotes consumidos en un despacho · V = vértices del grafo · E = aristas del grafo · C = candidatos sustitutos evaluados.
