package ucu.edu.aed.tda.grafo;

import junit.framework.TestCase;
import ucu.edu.aed.Ejercicio8.MainEjercicio8;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;

import java.util.List;
import java.util.Set;

/** Casos de prueba de la parte 2.3 del Ejercicio 8 que usan el grafo de las zonas. */
public class Ejercicio8Test extends TestCase {

    private final UndirectedGraphAlgorithms algoritmos = new UndirectedGraphAlgorithms();
    private UndirectedGraph<String, WeightedEdge> zonas;

    @Override
    protected void setUp() {
        zonas = MainEjercicio8.grafoEjercicio8();
    }

    private IUndirectedGraph<String, WeightedEdge> prim(UndirectedGraph<String, WeightedEdge> g, String origen) {
        return algoritmos.prim(g, g.construirComparable(origen));
    }

    private boolean tiene(IUndirectedGraph<String, WeightedEdge> g, int a, int b) {
        return g.existeArista(g.construirComparable("Zona " + a), g.construirComparable("Zona " + b));
    }

    /** a) Grafo del ejercicio desde Zona 1: 6 zonas, 5 aristas, conexo, sin ciclos, 8 km. */
    public void testGrafoDelEjercicio() {
        IUndirectedGraph<String, WeightedEdge> arbol = prim(zonas, "Zona 1");
        assertEquals(6, arbol.cantidadDeVertices());
        assertEquals(5, arbol.cantidadDeAristas());
        assertTrue(arbol.esConexo());
        assertFalse(arbol.tieneCiclos());
        assertEquals(8.0, MainEjercicio8.costo(arbol));
    }

    /** Las aristas del paso a paso de la parte 1.4. */
    public void testAristasDelPasoAPaso() {
        IUndirectedGraph<String, WeightedEdge> arbol = prim(zonas, "Zona 1");
        assertTrue(tiene(arbol, 1, 4));
        assertTrue(tiene(arbol, 4, 6));
        assertTrue(tiene(arbol, 1, 2));
        assertTrue(tiene(arbol, 2, 3));
        assertTrue(tiene(arbol, 3, 5));
    }

    /** b) Desde cualquier zona el costo es 8. */
    public void testCualquierOrigen() {
        for (String v : zonas.vertices()) {
            assertEquals(v, 8.0, MainEjercicio8.costo(prim(zonas, v)));
        }
    }

    /** c) Escenario reducido de 4 zonas: (1,4), (1,2) y (2,3), costo 4. */
    public void testCuatroZonas() {
        UndirectedGraph<String, WeightedEdge> cuatro = MainEjercicio8.grafoCuatroZonas();
        IUndirectedGraph<String, WeightedEdge> arbol = prim(cuatro, "Zona 1");
        assertEquals(3, arbol.cantidadDeAristas());
        assertTrue(tiene(arbol, 1, 4));
        assertTrue(tiene(arbol, 1, 2));
        assertTrue(tiene(arbol, 2, 3));
        assertEquals(4.0, MainEjercicio8.costo(arbol));
    }

    /** d) El grafo original no cambia. */
    public void testNoModificaElOriginal() {
        prim(zonas, "Zona 1");
        assertEquals(6, zonas.cantidadDeVertices());
        assertEquals(15, zonas.cantidadDeAristas());
    }

    /** e) Origen inexistente. */
    public void testOrigenInexistente() {
        try {
            prim(zonas, "Zona 9");
            fail("Debía lanzar IllegalArgumentException");
        } catch (IllegalArgumentException esperada) {
            // ok
        }
    }

    /** j) buscarMin elige la menor que cruza de U al resto. */
    public void testBuscarMinPrimerPaso() {
        Edge<String, WeightedEdge> e = algoritmos.searchMinEdge(zonas, List.of("Zona 1"),
                List.of("Zona 2", "Zona 3", "Zona 4", "Zona 5", "Zona 6"));
        assertEquals("Zona 1", e.source());
        assertEquals("Zona 4", e.target());
    }

    /** k) Empate del paso 3: gana (1,2) porque es la primera de costo 2 que encuentra. */
    public void testBuscarMinEmpate() {
        Edge<String, WeightedEdge> e = algoritmos.searchMinEdge(zonas, List.of("Zona 1", "Zona 4", "Zona 6"),
                Set.of("Zona 2", "Zona 3", "Zona 5"));
        assertEquals("Zona 1", e.source());
        assertEquals("Zona 2", e.target());
        assertEquals(2.0, e.dato().getWeight());
    }
}
