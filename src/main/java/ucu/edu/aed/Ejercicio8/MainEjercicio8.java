package ucu.edu.aed.Ejercicio8;

import ucu.edu.aed.tda.grafo.IUndirectedGraph;
import ucu.edu.aed.tda.grafo.UndirectedGraph;
import ucu.edu.aed.tda.grafo.UndirectedGraphAlgorithms;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Ejercicio 8: árbol abarcador de costo mínimo de las seis zonas con Prim.
 * Muestra cada paso (U, la arista elegida y el costo acumulado) y lo compara con el prim de la biblioteca.
 * Las respuestas escritas están en Ejercicio8.txt.
 */
public class MainEjercicio8 {

    /** Distancias en km de la tabla del Ejercicio 8 (la matriz es simétrica; "x" es la diagonal). */
    private static final double[][] DISTANCIAS = {
            {0, 2, 5, 1, 7, 2},
            {2, 0, 1, 3, 5, 6},
            {5, 1, 0, 2, 3, 5},
            {1, 3, 2, 0, 7, 1},
            {7, 5, 3, 7, 0, 4},
            {2, 6, 5, 1, 4, 0}
    };

    /** Grafo no dirigido de las zonas. Las aristas se agregan fila por fila: (1,2), (1,3), ..., (5,6). */
    public static UndirectedGraph<String, WeightedEdge> grafoZonas(double[][] distancias) {
        UndirectedGraph<String, WeightedEdge> g = new UndirectedGraph<>();
        for (int i = 1; i <= distancias.length; i++) g.agregarVertice("Zona " + i);
        for (int i = 0; i < distancias.length; i++) {
            for (int j = i + 1; j < distancias.length; j++) {
                g.agregarArista("Zona " + (i + 1), "Zona " + (j + 1), new WeightedEdge(distancias[i][j]));
            }
        }
        return g;
    }

    public static UndirectedGraph<String, WeightedEdge> grafoEjercicio8() {
        return grafoZonas(DISTANCIAS);
    }

    /** Escenario reducido de la parte 2: solo las zonas 1 a 4. */
    public static UndirectedGraph<String, WeightedEdge> grafoCuatroZonas() {
        double[][] cuatro = new double[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) cuatro[i][j] = DISTANCIAS[i][j];
        }
        return grafoZonas(cuatro);
    }

    /** Suma de los pesos de las aristas de un grafo (cada arista se cuenta una vez). */
    public static double costo(IUndirectedGraph<String, WeightedEdge> g) {
        double total = 0;
        for (Edge<String, WeightedEdge> e : g.aristas()) total += e.dato().getWeight();
        return total;
    }

    /** Prim paso a paso: hace lo mismo que el prim de la biblioteca, pero imprime cada paso. */
    private static void primPasoAPaso(UndirectedGraph<String, WeightedEdge> g, String origen) {
        UndirectedGraphAlgorithms algoritmos = new UndirectedGraphAlgorithms();
        Set<String> u = new LinkedHashSet<>();
        Set<String> resto = new LinkedHashSet<>(g.vertices());
        u.add(origen);
        resto.remove(origen);
        double total = 0;
        int paso = 1;
        System.out.println("Inicio: U = " + u);
        while (!resto.isEmpty()) {
            Edge<String, WeightedEdge> e = algoritmos.searchMinEdge(g, u, resto);
            if (e == null) break;
            total += e.dato().getWeight();
            u.add(e.target());
            resto.remove(e.target());
            System.out.println("Paso " + paso++ + ": (" + e.source() + ", " + e.target() + ") costo " + e.dato()
                    + " -> U = " + u + ", acumulado " + total);
        }
    }

    public static void main(String[] args) {
        UndirectedGraphAlgorithms algoritmos = new UndirectedGraphAlgorithms();

        System.out.println("=== Prim con las 6 zonas, desde Zona 1 ===");
        UndirectedGraph<String, WeightedEdge> zonas = grafoEjercicio8();
        primPasoAPaso(zonas, "Zona 1");
        IUndirectedGraph<String, WeightedEdge> arbol = algoritmos.prim(zonas, zonas.construirComparable("Zona 1"));
        System.out.println("Prim de la biblioteca: " + arbol.aristas() + ", costo total " + costo(arbol) + " km");

        System.out.println("\n=== Escenario reducido: zonas 1 a 4, desde Zona 1 ===");
        UndirectedGraph<String, WeightedEdge> cuatro = grafoCuatroZonas();
        primPasoAPaso(cuatro, "Zona 1");
        IUndirectedGraph<String, WeightedEdge> arbolCuatro = algoritmos.prim(cuatro, cuatro.construirComparable("Zona 1"));
        System.out.println("Prim de la biblioteca: " + arbolCuatro.aristas() + ", costo total " + costo(arbolCuatro) + " km");
    }
}
