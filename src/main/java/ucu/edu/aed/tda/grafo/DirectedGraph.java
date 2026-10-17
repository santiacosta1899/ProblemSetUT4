package ucu.edu.aed.tda.grafo;

import ucu.edu.aed.tda.grafo.model.edge.DirectedEdge;
import ucu.edu.aed.tda.grafo.model.edge.Edge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementación mínima de grafo dirigido con lista de adyacencias:
 * cada vértice apunta a la lista de sus aristas salientes.
 */
public class DirectedGraph<V, D> implements IDirectedIGraph<V, D> {

    private final Map<V, List<Edge<V, D>>> adyacencias = new LinkedHashMap<>();

    @Override
    public boolean agregarVertice(V vertex) {
        if (adyacencias.containsKey(vertex)) return false;
        adyacencias.put(vertex, new ArrayList<>());
        return true;
    }

    @Override
    public V buscarVertice(Comparable<V> criterio) {
        for (V v : adyacencias.keySet()) {
            if (criterio.compareTo(v) == 0) return v;
        }
        return null;
    }

    @Override
    public boolean agregarArista(V source, V target, D dato) {
        if (!adyacencias.containsKey(source) || !adyacencias.containsKey(target)) return false;
        if (existeArista(construirComparable(source), construirComparable(target))) return false;
        adyacencias.get(source).add(new DirectedEdge<>(source, target, dato));
        return true;
    }

    @Override
    public boolean eliminarArista(Comparable<V> source, Comparable<V> target) {
        V s = buscarVertice(source);
        if (s == null) return false;
        return adyacencias.get(s).removeIf(e -> target.compareTo(e.target()) == 0);
    }

    @Override
    public boolean removerVertice(Comparable<V> criteria) {
        V v = buscarVertice(criteria);
        if (v == null) return false;
        adyacencias.remove(v);
        for (List<Edge<V, D>> salientes : adyacencias.values()) {
            salientes.removeIf(e -> e.target().equals(v));
        }
        return true;
    }

    @Override
    public Set<V> vertices() {
        return Collections.unmodifiableSet(adyacencias.keySet());
    }

    @Override
    public Set<Edge<V, D>> aristas() {
        Set<Edge<V, D>> todas = new LinkedHashSet<>();
        adyacencias.values().forEach(todas::addAll);
        return Collections.unmodifiableSet(todas);
    }

    @Override
    public boolean existeArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) {
        return obtenerArista(sourceCriteria, targetCriteria) != null;
    }

    @Override
    public Edge<V, D> obtenerArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) {
        V s = buscarVertice(sourceCriteria);
        if (s == null) return null;
        for (Edge<V, D> e : adyacencias.get(s)) {
            if (targetCriteria.compareTo(e.target()) == 0) return e;
        }
        return null;
    }

    @Override
    public List<Edge<V, D>> adyacencias(Comparable<V> verticeCriteria) {
        V v = buscarVertice(verticeCriteria);
        if (v == null) return Collections.emptyList();
        return Collections.unmodifiableList(adyacencias.get(v));
    }

    @Override
    public Set<V> successors(Comparable<V> criteria) {
        Set<V> res = new LinkedHashSet<>();
        for (Edge<V, D> e : adyacencias(criteria)) res.add(e.target());
        return res;
    }

    @Override
    public Set<V> predecessors(Comparable<V> criteria) {
        Set<V> res = new LinkedHashSet<>();
        V v = buscarVertice(criteria);
        if (v == null) return res;
        for (Map.Entry<V, List<Edge<V, D>>> entry : adyacencias.entrySet()) {
            for (Edge<V, D> e : entry.getValue()) {
                if (e.target().equals(v)) res.add(entry.getKey());
            }
        }
        return res;
    }

    @Override
    public void vaciar() {
        adyacencias.clear();
    }

    @Override
    public boolean esConexo() {
        throw new UnsupportedOperationException("pendiente");
    }

    /**
     * Búsqueda en profundidad: si desde un vértice se llega a otro que sigue "en el camino"
     * (pendiente en la recursión), es un arco de retroceso y por lo tanto hay un ciclo.
     */
    @Override
    public boolean tieneCiclos() {
        Set<V> visitados = new java.util.HashSet<>();
        Set<V> enElCamino = new java.util.HashSet<>();

        for (V v : adyacencias.keySet()) {
            if (!visitados.contains(v) && bpfTieneCiclo(v, visitados, enElCamino)) return true;
        }
        return false;
    }

    private boolean bpfTieneCiclo(V actual, Set<V> visitados, Set<V> enElCamino) {
        visitados.add(actual);
        enElCamino.add(actual);

        for (Edge<V, D> arco : adyacencias.get(actual)) {
            V sucesor = arco.target();
            if (enElCamino.contains(sucesor)) return true;   // arco de retroceso
            if (!visitados.contains(sucesor) && bpfTieneCiclo(sucesor, visitados, enElCamino)) return true;
        }

        enElCamino.remove(actual);
        return false;
    }
}
