package ucu.edu.aed.tda.grafo.model.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resultado de Warshall: cerradura transitiva (A[i][j] = true si hay camino de i a j).
 * Igual que en Floyd, P[i][j] guarda el intermedio que habilitó el camino (-1 = arco directo).
 * Warshall no trabaja con costos, solo con existencia de caminos.
 */
public class WarshallResult<V> implements IFloydWarshallResult<V> {

  private final List<V> vertices;
  private final Map<V, Integer> indice = new HashMap<>();
  private final boolean[][] alcanzable;
  private final int[][] intermedios;

  public WarshallResult(List<V> vertices, boolean[][] alcanzable, int[][] intermedios) {
    this.vertices = vertices;
    this.alcanzable = alcanzable;
    this.intermedios = intermedios;
    for (int i = 0; i < vertices.size(); i++) indice.put(vertices.get(i), i);
  }

  @Override
  public boolean connected(V source, V target) {
    return alcanzable[indice.get(source)][indice.get(target)];
  }

  @Override
  public double getCost(V source, V target) {
    throw new UnsupportedOperationException("Warshall solo calcula si existe camino; para costos usar Floyd");
  }

  /** Un camino (no necesariamente el de menor costo) de origen a destino. Lista vacía si no hay. */
  @Override
  public List<V> getPath(V source, V target) {
    if (!connected(source, target)) return Collections.emptyList();
    List<V> camino = new ArrayList<>();
    camino.add(source);
    agregarIntermedios(indice.get(source), indice.get(target), camino);
    camino.add(target);
    return camino;
  }

  private void agregarIntermedios(int i, int j, List<V> camino) {
    int k = intermedios[i][j];
    if (k == -1) return;
    agregarIntermedios(i, k, camino);
    camino.add(vertices.get(k));
    agregarIntermedios(k, j, camino);
  }

  public List<V> getVertices() {
    return vertices;
  }

  public boolean[][] getAlcanzable() {
    return alcanzable;
  }
}
