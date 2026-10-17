package ucu.edu.aed.tda.grafo;

import ucu.edu.aed.tda.grafo.model.IGraph;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;
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

    // ---- Pendientes (otros ejercicios) ----

    @Override
    public <V, D extends WeightedEdge> IDijkstraResult<V> dijkstra(Comparable<V> source, IDirectedIGraph<V, D> grafo) {
        throw new UnsupportedOperationException("pendiente");
    }

    @Override
    public <V, D extends WeightedEdge> IFloydWarshallResult<V> floyd(IDirectedIGraph<V, D> grafo) {
        throw new UnsupportedOperationException("pendiente");
    }

    @Override
    public <V, D extends WeightedEdge> IFloydWarshallResult<V> warshall(IDirectedIGraph<V, D> grafo) {
        throw new UnsupportedOperationException("pendiente");
    }

    @Override
    public <V, D extends WeightedEdge> V obtenerCentroGrafo(IDirectedIGraph<V, D> grafo) {
        throw new UnsupportedOperationException("pendiente");
    }

    @Override
    public <V, D extends WeightedEdge> double obtenerExcentricidad(IDirectedIGraph<V, D> grafo, Comparable<V> vertexCriteria) {
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
