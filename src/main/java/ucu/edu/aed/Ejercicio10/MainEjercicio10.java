package ucu.edu.aed.Ejercicio10;

import ucu.edu.aed.tda.grafo.UndirectedGraph;
import ucu.edu.aed.tda.grafo.UndirectedGraphAlgorithms;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;

import java.util.ArrayList;
import java.util.List;

/**
 * Ejercicio 10: búsqueda en amplitud sobre el grafo no dirigido del enunciado, empezando por E.
 * Las respuestas escritas están en Ejercicio10.txt.
 */
public class MainEjercicio10 {

    /**
     * Grafo de la figura. E se agrega primero para que el recorrido empiece por ahí, y las aristas se
     * agregan en orden alfabético, así los adyacentes de cada vértice quedan en orden alfabético.
     */
    public static UndirectedGraph<String, WeightedEdge> grafoFigura() {
        UndirectedGraph<String, WeightedEdge> g = new UndirectedGraph<>();
        for (String v : new String[]{"E", "A", "B", "C", "D", "F", "G", "H"}) g.agregarVertice(v);
        arista(g, "A", "B", 2);
        arista(g, "A", "C", 3);
        arista(g, "B", "D", 2);
        arista(g, "C", "D", 1);
        arista(g, "D", "E", 2);
        arista(g, "D", "F", 4);
        arista(g, "E", "F", 1);
        arista(g, "E", "G", 2);
        arista(g, "F", "G", 2);
        arista(g, "F", "H", 1);
        arista(g, "G", "H", 3);
        return g;
    }

    private static void arista(UndirectedGraph<String, WeightedEdge> g, String a, String b, double peso) {
        g.agregarArista(a, b, new WeightedEdge(peso));
    }

    /**
     * Aristas del árbol abarcador de una búsqueda en amplitud: el padre de cada vértice (salvo el primero)
     * es el primer vértice del recorrido que es adyacente a él, porque fue el que lo encoló.
     * Cada arista se devuelve como "padre-hijo".
     */
    public static List<String> aristasDelArbol(UndirectedGraph<String, WeightedEdge> g, List<String> orden) {
        List<String> aristas = new ArrayList<>();
        for (int i = 1; i < orden.size(); i++) {
            String v = orden.get(i);
            for (int j = 0; j < i; j++) {
                String candidato = orden.get(j);
                if (g.existeArista(g.construirComparable(v), g.construirComparable(candidato))) {
                    aristas.add(candidato + "-" + v);
                    break;
                }
            }
        }
        return aristas;
    }

    public static void main(String[] args) {
        UndirectedGraph<String, WeightedEdge> grafo = grafoFigura();

        System.out.println("Lista de adyacencias:");
        for (String v : grafo.vertices()) {
            System.out.print(v + " -> ");
            for (Edge<String, WeightedEdge> e : grafo.adyacencias(grafo.construirComparable(v))) {
                System.out.print(e.target() + "(" + e.dato() + ")  ");
            }
            System.out.println();
        }

        List<String> orden = new ArrayList<>();
        new UndirectedGraphAlgorithms().bea(grafo, orden::add);
        System.out.println("\nRecorrido en amplitud desde E: " + String.join(", ", orden));
        System.out.println("Aristas del árbol abarcador: " + String.join(", ", aristasDelArbol(grafo, orden)));
    }
}
