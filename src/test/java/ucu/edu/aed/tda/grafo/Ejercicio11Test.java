package ucu.edu.aed.tda.grafo;

import junit.framework.TestCase;
import ucu.edu.aed.tda.grafo.model.IGraph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Ejercicio 11: puntos de articulación con la firma de la letra, sobre la red de ciudades de África. */
public class Ejercicio11Test extends TestCase {

    private final UndirectedGraphAlgorithms algoritmos = new UndirectedGraphAlgorithms();

    private static UndirectedGraph<String, String> red() {
        UndirectedGraph<String, String> g = new UndirectedGraph<>();
        g.agregarVertices(List.of("Nairobi", "Cairo", "Monrovia", "Garoua", "Mekele", "Praia"));
        g.agregarArista("Nairobi", "Cairo", "N-C");
        g.agregarArista("Nairobi", "Monrovia", "N-M");
        g.agregarArista("Nairobi", "Garoua", "N-G");
        g.agregarArista("Monrovia", "Garoua", "M-G");
        g.agregarArista("Monrovia", "Mekele", "M-Me");
        g.agregarArista("Garoua", "Mekele", "G-Me");
        g.agregarArista("Mekele", "Praia", "Me-P");
        return g;
    }

    /** Nairobi aísla a Cairo y Mekele aísla a Praia; las demás ciudades se pueden perder sin partir la red. */
    public void testRedDelEnunciado() {
        IGraph<String, String> grafo = red();   // se usa como IGraph, igual que en la firma pedida
        List<String> puntos = algoritmos.puntosDeArticulacion(grafo);

        assertEquals(2, puntos.size());
        assertEquals(Set.of("Nairobi", "Mekele"), new HashSet<>(puntos));
    }

    public void testCadaPuntoApareceUnaSolaVez() {
        UndirectedGraph<String, String> g = new UndirectedGraph<>();
        g.agregarVertices(List.of("A", "B", "C", "D"));
        g.agregarArista("A", "B", "x");
        g.agregarArista("B", "C", "x");
        g.agregarArista("B", "D", "x");   // B separa tres hojas: se agrega una sola vez
        assertEquals(List.of("B"), algoritmos.puntosDeArticulacion(g));
    }

    public void testGrafoSinPuntos() {
        UndirectedGraph<String, String> g = new UndirectedGraph<>();
        g.agregarVertices(List.of("A", "B", "C"));
        g.agregarArista("A", "B", "x");
        g.agregarArista("B", "C", "x");
        g.agregarArista("C", "A", "x");
        assertTrue(algoritmos.puntosDeArticulacion(g).isEmpty());
    }

    public void testGrafoVacio() {
        assertTrue(algoritmos.puntosDeArticulacion(new UndirectedGraph<String, String>()).isEmpty());
    }
}
