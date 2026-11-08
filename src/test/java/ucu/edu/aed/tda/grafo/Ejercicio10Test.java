package ucu.edu.aed.tda.grafo;

import junit.framework.TestCase;
import ucu.edu.aed.Ejercicio10.MainEjercicio10;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;

import java.util.ArrayList;
import java.util.List;

public class Ejercicio10Test extends TestCase {

    private final UndirectedGraphAlgorithms algoritmos = new UndirectedGraphAlgorithms();
    private UndirectedGraph<String, WeightedEdge> grafo;

    @Override
    protected void setUp() {
        grafo = new UndirectedGraph<>();
    }

    private void vertices(String... nombres) {
        for (String n : nombres) grafo.agregarVertice(n);
    }

    private void arista(String a, String b) {
        grafo.agregarArista(a, b, new WeightedEdge(1));
    }

    private List<String> recorrido() {
        List<String> orden = new ArrayList<>();
        algoritmos.bea(grafo, orden::add);
        return orden;
    }

    public void testGrafoDeLaFigura() {
        UndirectedGraph<String, WeightedEdge> g = MainEjercicio10.grafoFigura();
        List<String> orden = new ArrayList<>();
        algoritmos.bea(g, orden::add);

        assertEquals(List.of("E", "D", "F", "G", "B", "C", "H", "A"), orden);
        assertEquals(List.of("E-D", "E-F", "E-G", "D-B", "D-C", "F-H", "B-A"),
                MainEjercicio10.aristasDelArbol(g, orden));
    }

    public void testGrafoVacio() {
        assertTrue(recorrido().isEmpty());
    }

    public void testUnSoloVertice() {
        vertices("A");
        assertEquals(List.of("A"), recorrido());
    }

    /** A-B-C-A es un ciclo: cada vértice se visita una sola vez. */
    public void testCicloNoRepite() {
        vertices("A", "B", "C");
        arista("A", "B");
        arista("B", "C");
        arista("C", "A");
        assertEquals(List.of("A", "B", "C"), recorrido());
    }

    /** Grafo no conexo: se visitan las dos componentes, y los vértices aislados también. */
    public void testNoConexoVisitaTodo() {
        vertices("A", "B", "C", "D", "E");
        arista("A", "B");
        arista("C", "D");
        assertEquals(List.of("A", "B", "C", "D", "E"), recorrido());
    }

    public void testPorNiveles() {
        vertices("A", "B", "C", "D");
        arista("A", "B");
        arista("A", "C");
        arista("B", "D");
        assertEquals(List.of("A", "B", "C", "D"), recorrido());
    }

    // ---- UndirectedGraph ----

    public void testAristaEsSimetrica() {
        vertices("A", "B");
        assertTrue(grafo.agregarArista("A", "B", new WeightedEdge(5)));
        assertTrue(grafo.existeArista(grafo.construirComparable("B"), grafo.construirComparable("A")));
        assertEquals(1, grafo.cantidadDeAristas());
        assertFalse(grafo.agregarArista("B", "A", new WeightedEdge(5)));   // repetida
        assertFalse(grafo.agregarArista("A", "A", new WeightedEdge(1)));   // lazo
    }

    public void testEliminarYRemover() {
        vertices("A", "B", "C");
        arista("A", "B");
        arista("B", "C");
        assertTrue(grafo.eliminarArista(grafo.construirComparable("B"), grafo.construirComparable("A")));
        assertFalse(grafo.existeArista(grafo.construirComparable("A"), grafo.construirComparable("B")));
        assertTrue(grafo.removerVertice(grafo.construirComparable("B")));
        assertTrue(grafo.adyacencias(grafo.construirComparable("C")).isEmpty());
        assertEquals(0, grafo.cantidadDeAristas());
    }

    public void testConexoYCiclos() {
        assertTrue(grafo.esConexo());
        vertices("A", "B", "C");
        arista("A", "B");
        assertFalse(grafo.esConexo());
        arista("B", "C");
        assertTrue(grafo.esConexo());
        assertFalse(grafo.tieneCiclos());
        arista("C", "A");
        assertTrue(grafo.tieneCiclos());
    }
}
