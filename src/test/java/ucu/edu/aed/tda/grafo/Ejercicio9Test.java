package ucu.edu.aed.tda.grafo;

import junit.framework.TestCase;
import ucu.edu.aed.Ejercicio9.MainEjercicio9;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;

public class Ejercicio9Test extends TestCase {

    private final UndirectedGraphAlgorithms algoritmos = new UndirectedGraphAlgorithms();

    private boolean tiene(IUndirectedGraph<String, WeightedEdge> g, int a, int b) {
        return g.existeArista(g.construirComparable("Zona " + a), g.construirComparable("Zona " + b));
    }

    /** Kruskal sobre la tabla del Ejercicio 9: las 5 aristas del paso a paso, 8 km. */
    public void testKruskalGrafoDelEjercicio() {
        IUndirectedGraph<String, WeightedEdge> arbol = algoritmos.kruskal(MainEjercicio9.grafoEjercicio9());
        assertEquals(6, arbol.cantidadDeVertices());
        assertEquals(5, arbol.cantidadDeAristas());
        assertTrue(tiene(arbol, 1, 4));
        assertTrue(tiene(arbol, 2, 3));
        assertTrue(tiene(arbol, 4, 6));
        assertTrue(tiene(arbol, 3, 4));
        assertTrue(tiene(arbol, 3, 5));
        assertFalse(tiene(arbol, 1, 2));   // cuesta 3: Kruskal lo descarta (cerraría un ciclo)
        assertTrue(arbol.esConexo());
        assertFalse(arbol.tieneCiclos());
        assertEquals(8.0, MainEjercicio9.costo(arbol));
    }

    /** Prim sobre el mismo grafo da el mismo costo. */
    public void testPrimDaElMismoCosto() {
        UndirectedGraph<String, WeightedEdge> zonas = MainEjercicio9.grafoEjercicio9();
        for (String v : zonas.vertices()) {
            assertEquals(8.0, MainEjercicio9.costo(algoritmos.prim(zonas, zonas.construirComparable(v))));
        }
    }
}
