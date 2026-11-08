package ucu.edu.aed.Ejercicio9;

import ucu.edu.aed.tda.grafo.IUndirectedGraph;
import ucu.edu.aed.tda.grafo.UndirectedGraph;
import ucu.edu.aed.tda.grafo.UndirectedGraphAlgorithms;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ejercicio 9: árbol abarcador de costo mínimo de las seis zonas con Kruskal.
 * Muestra cada arista que se mira (si se acepta o se descarta y cómo quedan las componentes)
 * y lo compara con el kruskal de la biblioteca. Las respuestas escritas están en Ejercicio9.txt.
 */
public class MainEjercicio9 {

    /** Distancias en km de la tabla del Ejercicio 9 (la matriz es simétrica; "x" es la diagonal). */
    private static final double[][] DISTANCIAS = {
            {0, 3, 5, 1, 7, 2},
            {3, 0, 1, 3, 5, 6},
            {5, 1, 0, 2, 3, 5},
            {1, 3, 2, 0, 7, 1},
            {7, 5, 3, 7, 0, 4},
            {2, 6, 5, 1, 4, 0}
    };

    /** Grafo no dirigido de las zonas. Las aristas se agregan fila por fila: (1,2), (1,3), ..., (5,6). */
    public static UndirectedGraph<String, WeightedEdge> grafoEjercicio9() {
        UndirectedGraph<String, WeightedEdge> g = new UndirectedGraph<>();
        for (int i = 1; i <= DISTANCIAS.length; i++) g.agregarVertice("Zona " + i);
        for (int i = 0; i < DISTANCIAS.length; i++) {
            for (int j = i + 1; j < DISTANCIAS.length; j++) {
                g.agregarArista("Zona " + (i + 1), "Zona " + (j + 1), new WeightedEdge(DISTANCIAS[i][j]));
            }
        }
        return g;
    }

    /** Suma de los pesos de las aristas de un grafo (cada arista se cuenta una vez). */
    public static double costo(IUndirectedGraph<String, WeightedEdge> g) {
        double total = 0;
        for (Edge<String, WeightedEdge> e : g.aristas()) total += e.dato().getWeight();
        return total;
    }

    /** Kruskal paso a paso: mismo criterio que la biblioteca (componentes numeradas, aristas por peso). */
    private static void kruskalPasoAPaso(UndirectedGraph<String, WeightedEdge> g) {
        Map<String, Integer> componente = new LinkedHashMap<>();
        int numero = 1;
        for (String v : g.vertices()) componente.put(v, numero++);

        List<Edge<String, WeightedEdge>> aristas = new ArrayList<>(g.aristas());
        aristas.sort(Comparator.comparingDouble(e -> e.dato().getWeight()));
        System.out.println("Aristas ordenadas por costo:");
        for (Edge<String, WeightedEdge> e : aristas) System.out.println("  " + e);

        int agregadas = 0;
        double total = 0;
        int paso = 1;
        for (Edge<String, WeightedEdge> e : aristas) {
            if (agregadas == g.cantidadDeVertices() - 1) break;
            int origen = componente.get(e.source());
            int destino = componente.get(e.target());
            String linea = "Paso " + paso++ + ": (" + e.source() + ", " + e.target() + ") costo " + e.dato();
            if (origen == destino) {
                System.out.println(linea + " -> DESCARTADA, cerraría un ciclo");
                continue;
            }
            agregadas++;
            total += e.dato().getWeight();
            for (Map.Entry<String, Integer> entry : componente.entrySet()) {
                if (entry.getValue() == destino) entry.setValue(origen);
            }
            System.out.println(linea + " -> ACEPTADA, acumulado " + total + ", componentes " + componente);
        }
    }

    public static void main(String[] args) {
        UndirectedGraph<String, WeightedEdge> zonas = grafoEjercicio9();
        kruskalPasoAPaso(zonas);

        IUndirectedGraph<String, WeightedEdge> arbol = new UndirectedGraphAlgorithms().kruskal(zonas);
        System.out.println("Kruskal de la biblioteca: " + arbol.aristas() + ", costo total "
                + costo(arbol) + " km");
    }
}
