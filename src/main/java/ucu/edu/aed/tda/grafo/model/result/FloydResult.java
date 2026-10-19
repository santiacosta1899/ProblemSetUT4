package ucu.edu.aed.tda.grafo.model.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resultado de Floyd: matriz A de costos mínimos y matriz P de intermedios.
 * P[i][j] = k es el vértice intermedio que mejoró A[i][j]; -1 significa que el camino es el arco directo.
 */
public class FloydResult<V> implements IFloydWarshallResult<V> {

    private final List<V> vertices;
    private final Map<V, Integer> indice = new HashMap<>();
    private final double[][] costos;
    private final int[][] intermedios;

    public FloydResult(List<V> vertices, double[][] costos, int[][] intermedios) {
        this.vertices = vertices;
        this.costos = costos;
        this.intermedios = intermedios;
        for (int i = 0; i < vertices.size(); i++) indice.put(vertices.get(i), i);
    }

    @Override
    public double getCost(V source, V target) {
        return costos[indice.get(source)][indice.get(target)];
    }

    @Override
    public boolean connected(V source, V target) {
        return getCost(source, target) != Double.POSITIVE_INFINITY;
    }

    /** Camino completo (origen ... destino). Lista vacía si no hay camino. */
    @Override
    public List<V> getPath(V source, V target) {
        if (!connected(source, target)) return Collections.emptyList();
        List<V> camino = new ArrayList<>();
        camino.add(source);
        if (!source.equals(target)) {
            agregarIntermedios(indice.get(source), indice.get(target), camino);
            camino.add(target);
        }
        return camino;
    }

    private void agregarIntermedios(int i, int j, List<V> camino) {
        int k = intermedios[i][j];
        if (k == -1) return;                 // arco directo, no hay nada en el medio
        agregarIntermedios(i, k, camino);
        camino.add(vertices.get(k));
        agregarIntermedios(k, j, camino);
    }

    public List<V> getVertices() {
        return vertices;
    }

    public double[][] getCostos() {
        return costos;
    }

    public int[][] getIntermedios() {
        return intermedios;
    }
}
