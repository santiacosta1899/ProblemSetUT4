package ucu.edu.aed.tda.grafo.model.result;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class FloydWarshallResult<V> implements IFloydWarshallResult<V> {
    private final Map<V, Integer> indice;
    private final double[][] costos;
    private final int[][] pred;

    public FloydWarshallResult(Map<V, Integer> indice, double[][] costos, int[][] pred) {
        this.indice = indice;
        this.costos = costos;
        this.pred = pred;
    }

    @Override
    public List<V> getPath(V source, V target) {
        LinkedList<V> camino = new LinkedList<>();
        if (!connected(source, target)) return camino;

        List<V> porIndice = verticesPorIndice();
        int i = indice.get(source);
        for (int actual = indice.get(target); ; actual = pred[i][actual]) {
            camino.addFirst(porIndice.get(actual));
            if (actual == i) break;
        }
        return camino;
    }

    @Override
    public double getCost(V source, V target) {
        return costos[indice.get(source)][indice.get(target)];
    }

    @Override
    public boolean connected(V source, V target) {
        return getCost(source, target) != Double.POSITIVE_INFINITY;
    }

    private List<V> verticesPorIndice() {
        List<V> res = new java.util.ArrayList<>(java.util.Collections.nCopies(indice.size(), null));
        indice.forEach((v, i) -> res.set(i, v));
        return res;
    }
}
