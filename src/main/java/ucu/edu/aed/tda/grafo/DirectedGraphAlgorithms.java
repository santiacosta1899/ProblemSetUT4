package ucu.edu.aed.tda.grafo;

import ucu.edu.aed.tda.grafo.model.IGraph;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;
import ucu.edu.aed.tda.grafo.model.result.DijkstraResult;
import ucu.edu.aed.tda.grafo.model.result.FloydWarshallResult;
import ucu.edu.aed.tda.grafo.model.result.IDijkstraResult;
import ucu.edu.aed.tda.grafo.model.result.IFloydWarshallResult;
import ucu.edu.aed.tda.grafo.model.result.Path;
import ucu.edu.aed.tda.grafo.model.result.ResultadoCaminoCritico;
import ucu.edu.aed.tda.grafo.model.result.SecuenciaConHolgura;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Consumer;

public class DirectedGraphAlgorithms implements IDirectedGraphAlgorithms {

    /**
     * Clasificación topológica: BPF siguiendo los predecesores (equivale a invertir los arcos)
     * y agregando cada vértice a la lista al salir de la recursión.
     * Precondición: el grafo es acíclico.
     */
    @Override
    public <V, D> List<V> calcularClasificacionTopologica(IDirectedIGraph<V, D> grafo) {
        if (grafo.tieneCiclos()) {
            throw new IllegalArgumentException("El grafo tiene ciclos: no admite clasificación topológica");
        }

        List<V> resultado = new ArrayList<>();
        Set<V> visitados = new HashSet<>();

        for (V v : grafo.vertices()) {
            if (!visitados.contains(v)) {
                bpfTopologico(grafo, v, visitados, resultado);
            }
        }
        return resultado;
    }

    private <V, D> void bpfTopologico(IDirectedIGraph<V, D> grafo, V actual, Set<V> visitados, List<V> resultado) {
        visitados.add(actual);

        for (V predecesor : grafo.predecessors(grafo.construirComparable(actual))) {
            if (!visitados.contains(predecesor)) {
                bpfTopologico(grafo, predecesor, visitados, resultado);
            }
        }

        resultado.add(actual);   // salida de la recursión: todo lo que lo precede ya está en la lista
    }

    /**
     * Camino crítico: las tareas son los arcos (peso = duración). Procesando los vértices en orden
     * topológico se obtiene el camino más largo hasta cada vértice; después se listan todas las
     * secuencias inicio-fin con su holgura respecto del camino crítico.
     * Precondición: el grafo es acíclico y no vacío.
     */
    public <V, D extends WeightedEdge> ResultadoCaminoCritico<V> calcularCaminoCritico(IDirectedIGraph<V, D> grafo) {
        if (grafo.cantidadDeVertices() == 0) {
            throw new IllegalArgumentException("El grafo está vacío");
        }
        List<V> orden = calcularClasificacionTopologica(grafo);   // lanza excepción si hay ciclos

        // camino más largo hasta cada vértice
        Map<V, Double> costoMasLargo = new HashMap<>();
        Map<V, V> predecesorCritico = new HashMap<>();
        for (V v : orden) costoMasLargo.put(v, 0.0);

        for (V v : orden) {
            for (Edge<V, D> arco : grafo.adyacencias(grafo.construirComparable(v))) {
                double nuevoCosto = costoMasLargo.get(v) + arco.dato().getWeight();
                if (nuevoCosto > costoMasLargo.get(arco.target())) {
                    costoMasLargo.put(arco.target(), nuevoCosto);
                    predecesorCritico.put(arco.target(), v);
                }
            }
        }

        // el vértice final de mayor costo cierra el camino crítico
        double costoCritico = -1;
        V verticeFinal = null;
        for (V v : grafo.vertices()) {
            if (grafo.gradoDeSalida(grafo.construirComparable(v)) == 0 && costoMasLargo.get(v) > costoCritico) {
                costoCritico = costoMasLargo.get(v);
                verticeFinal = v;
            }
        }

        // reconstruir el camino hacia atrás
        LinkedList<V> camino = new LinkedList<>();
        for (V actual = verticeFinal; actual != null; actual = predecesorCritico.get(actual)) {
            camino.addFirst(actual);
        }
        SecuenciaConHolgura<V> critico = new SecuenciaConHolgura<>(camino, costoCritico, 0);

        // todas las secuencias inicio-fin
        List<SecuenciaConHolgura<V>> secuencias = new ArrayList<>();
        for (V inicio : grafo.vertices()) {
            if (grafo.gradoDeEntrada(grafo.construirComparable(inicio)) == 0) {
                List<V> caminoActual = new ArrayList<>();
                caminoActual.add(inicio);
                listarSecuencias(grafo, inicio, caminoActual, 0, costoCritico, secuencias);
            }
        }
        secuencias.sort((a, b) -> Double.compare(b.getCost(), a.getCost()));

        return new ResultadoCaminoCritico<>(critico, secuencias);
    }

    private <V, D extends WeightedEdge> void listarSecuencias(IDirectedIGraph<V, D> grafo, V actual, List<V> caminoActual,
                                                              double costoActual, double costoCritico,
                                                              List<SecuenciaConHolgura<V>> secuencias) {
        List<Edge<V, D>> salientes = grafo.adyacencias(grafo.construirComparable(actual));
        if (salientes.isEmpty()) {
            secuencias.add(new SecuenciaConHolgura<>(new ArrayList<>(caminoActual), costoActual, costoCritico - costoActual));
            return;
        }
        for (Edge<V, D> arco : salientes) {
            caminoActual.add(arco.target());
            listarSecuencias(grafo, arco.target(), caminoActual, costoActual + arco.dato().getWeight(), costoCritico, secuencias);
            caminoActual.remove(caminoActual.size() - 1);   // backtracking
        }
    }

    /**
     * Dijkstra: costo mínimo desde source a todos los demás vértices, con vector de predecesores.
     * Precondiciones: source existe en el grafo y los pesos son >= 0.
     */
    @Override
    public <V, D extends WeightedEdge> IDijkstraResult<V> dijkstra(Comparable<V> source, IDirectedIGraph<V, D> grafo) {
        V origen = grafo.buscarVertice(source);
        if (origen == null) {
            throw new IllegalArgumentException("El vértice origen no existe en el grafo");
        }

        Map<V, Double> costos = new HashMap<>();
        Map<V, V> predecesores = new HashMap<>();
        Set<V> definitivos = new HashSet<>();
        for (V v : grafo.vertices()) costos.put(v, Double.POSITIVE_INFINITY);
        costos.put(origen, 0.0);

        // cola de (vértice, costo); las entradas desactualizadas se descartan al sacarlas
        PriorityQueue<Map.Entry<V, Double>> cola = new PriorityQueue<>(Map.Entry.comparingByValue());
        cola.add(Map.entry(origen, 0.0));

        while (!cola.isEmpty()) {
            V actual = cola.poll().getKey();
            if (!definitivos.add(actual)) continue;

            for (Edge<V, D> arco : grafo.adyacencias(grafo.construirComparable(actual))) {
                double nuevoCosto = costos.get(actual) + arco.dato().getWeight();
                if (nuevoCosto < costos.get(arco.target())) {
                    costos.put(arco.target(), nuevoCosto);
                    predecesores.put(arco.target(), actual);
                    cola.add(Map.entry(arco.target(), nuevoCosto));
                }
            }
        }
        return new DijkstraResult<>(origen, costos, predecesores);
    }

    /**
     * Floyd: costos mínimos entre todos los pares de vértices, con matriz de predecesores.
     * Orden O(V^3). Precondición: no hay ciclos de costo negativo.
     */
    @Override
    public <V, D extends WeightedEdge> IFloydWarshallResult<V> floyd(IDirectedIGraph<V, D> grafo) {
        int n = grafo.cantidadDeVertices();
        Map<V, Integer> indice = new HashMap<>();
        List<V> vertices = new ArrayList<>(grafo.vertices());
        for (int i = 0; i < n; i++) indice.put(vertices.get(i), i);

        double[][] costos = new double[n][n];
        int[][] pred = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                costos[i][j] = (i == j) ? 0 : Double.POSITIVE_INFINITY;
                pred[i][j] = -1;
            }
        }
        for (Edge<V, D> arco : grafo.aristas()) {
            int i = indice.get(arco.source());
            int j = indice.get(arco.target());
            if (i != j) {
                costos[i][j] = arco.dato().getWeight();
                pred[i][j] = i;
            }
        }

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (costos[i][k] + costos[k][j] < costos[i][j]) {
                        costos[i][j] = costos[i][k] + costos[k][j];
                        pred[i][j] = pred[k][j];
                    }
                }
            }
        }
        return new FloydWarshallResult<>(indice, costos, pred);
    }

    /**
     * Excentricidad de un vértice: el mayor de los costos mínimos para llegar a él desde cualquier
     * otro vértice. Infinita si algún vértice no puede llegar a él.
     */
    @Override
    public <V, D extends WeightedEdge> double obtenerExcentricidad(IDirectedIGraph<V, D> grafo, Comparable<V> vertexCriteria) {
        V vertice = grafo.buscarVertice(vertexCriteria);
        if (vertice == null) {
            throw new IllegalArgumentException("El vértice no existe en el grafo");
        }
        return excentricidad(grafo, floyd(grafo), vertice);
    }

    /**
     * Centro del grafo: el vértice de menor excentricidad (si hay empate, el primero en orden de inserción).
     * Precondición: el grafo no es vacío.
     */
    @Override
    public <V, D extends WeightedEdge> V obtenerCentroGrafo(IDirectedIGraph<V, D> grafo) {
        if (grafo.cantidadDeVertices() == 0) {
            throw new IllegalArgumentException("El grafo está vacío");
        }
        IFloydWarshallResult<V> floyd = floyd(grafo);   // se calcula una sola vez para todos los vértices

        V centro = null;
        double menor = Double.POSITIVE_INFINITY;
        for (V v : grafo.vertices()) {
            double exc = excentricidad(grafo, floyd, v);
            if (centro == null || exc < menor) {
                centro = v;
                menor = exc;
            }
        }
        return centro;
    }

    private <V, D extends WeightedEdge> double excentricidad(IDirectedIGraph<V, D> grafo, IFloydWarshallResult<V> floyd, V vertice) {
        double max = 0;
        for (V otro : grafo.vertices()) {
            max = Math.max(max, floyd.getCost(otro, vertice));
        }
        return max;
    }

    // ---- Pendientes (otros ejercicios) ----

    @Override
    public <V, D extends WeightedEdge> IFloydWarshallResult<V> warshall(IDirectedIGraph<V, D> grafo) {
        throw new UnsupportedOperationException("pendiente");
    }

    @Override
    public <V, D extends WeightedEdge> List<Path<V>> obtenerTodosLosCaminos(Comparable<V> source, Comparable<V> target, IGraph<V, D> grafo) {
        throw new UnsupportedOperationException("pendiente");
    }

    @Override
    public <V, D> void recorridoEnProfundidad(IGraph<V, D> grafo, Comparable<V> sourceCriteria, Consumer<V> consumer) {
        throw new UnsupportedOperationException("pendiente");
    }

    @Override
    public <V, D> void recorridoEnAmplitud(IGraph<V, D> grafo, Comparable<V> sourceCriteria, Consumer<V> consumer) {
        throw new UnsupportedOperationException("pendiente");
    }
}
