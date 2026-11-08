package ucu.edu.aed.tda.grafo;

import junit.framework.TestCase;
import ucu.edu.aed.Ejercicio12.RedBacon;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Casos de prueba del ejercicio 12 (número de Bacon): ver Ejercicio12.txt. */
public class Ejercicio12Test extends TestCase {

    private UndirectedGraph<String, String> grafo;

    @Override
    protected void setUp() {
        grafo = new UndirectedGraph<>();
        grafo.agregarVertice(RedBacon.KEVIN_BACON);
    }

    private void actores(String... nombres) {
        for (String n : nombres) grafo.agregarVertice(n);
    }

    private void pelicula(String actor1, String actor2, String nombre) {
        grafo.agregarArista(actor1, actor2, nombre);
    }

    public void testKevinBaconTieneNumeroCero() {
        assertEquals(0, RedBacon.numBacon(grafo, "Kevin_Bacon"));
    }

    /** Ejemplo del enunciado: Tom Hanks trabajó con Bacon en Apolo 13 (1) y Sally Field con Tom Hanks en Forrest Gump (2). */
    public void testEjemploDelEnunciado() {
        actores("Tom_Hanks", "Sally_Field");
        pelicula("Kevin_Bacon", "Tom_Hanks", "Apolo_13");
        pelicula("Tom_Hanks", "Sally_Field", "Forrest_Gump");

        assertEquals(1, RedBacon.numBacon(grafo, "Tom_Hanks"));
        assertEquals(2, RedBacon.numBacon(grafo, "Sally_Field"));
    }

    public void testCadenaLarga() {
        actores("A", "B", "C", "D");
        pelicula("Kevin_Bacon", "A", "p1");
        pelicula("A", "B", "p2");
        pelicula("B", "C", "p3");
        pelicula("C", "D", "p4");
        assertEquals(4, RedBacon.numBacon(grafo, "D"));
    }

    /** Hay dos cadenas posibles hacia D (Bacon-A-D y Bacon-B-C-D): se devuelve la más corta. */
    public void testEligeElCaminoMasCorto() {
        actores("A", "B", "C", "D");
        pelicula("Kevin_Bacon", "B", "p1");
        pelicula("B", "C", "p2");
        pelicula("C", "D", "p3");
        pelicula("Kevin_Bacon", "A", "p4");
        pelicula("A", "D", "p5");
        assertEquals(2, RedBacon.numBacon(grafo, "D"));
    }

    /** Con ciclos entre actores la búsqueda termina y no cuenta de más. */
    public void testConCiclos() {
        actores("A", "B", "C");
        pelicula("Kevin_Bacon", "A", "p1");
        pelicula("A", "B", "p2");
        pelicula("B", "C", "p3");
        pelicula("C", "A", "p4");
        assertEquals(2, RedBacon.numBacon(grafo, "C"));
    }

    public void testActorSinConexionDevuelveMenosUno() {
        actores("Aislado", "X", "Y");
        pelicula("X", "Y", "p1");   // X e Y están conectados entre sí pero no con Bacon
        assertEquals(-1, RedBacon.numBacon(grafo, "Aislado"));
        assertEquals(-1, RedBacon.numBacon(grafo, "X"));
    }

    public void testActorQueNoEstaEnElGrafo() {
        assertEquals(-1, RedBacon.numBacon(grafo, "No_Existe"));
    }

    public void testSinKevinBaconLanzaExcepcion() {
        UndirectedGraph<String, String> otro = new UndirectedGraph<>();
        otro.agregarVertice("A");
        try {
            RedBacon.numBacon(otro, "A");
            fail("debía lanzar excepción");
        } catch (IllegalStateException esperado) {
            // ok
        }
    }

    public void testArgumentosNulos() {
        try {
            RedBacon.numBacon(grafo, null);
            fail("debía lanzar excepción");
        } catch (IllegalArgumentException esperado) {
            // ok
        }
        try {
            RedBacon.numBacon(null, "A");
            fail("debía lanzar excepción");
        } catch (IllegalArgumentException esperado) {
            // ok
        }
    }

    public void testNoModificaElGrafo() {
        actores("A", "B");
        pelicula("Kevin_Bacon", "A", "p1");
        pelicula("A", "B", "p2");
        int vertices = grafo.cantidadDeVertices();
        int aristas = grafo.cantidadDeAristas();
        RedBacon.numBacon(grafo, "B");
        assertEquals(vertices, grafo.cantidadDeVertices());
        assertEquals(aristas, grafo.cantidadDeAristas());
    }

    // ---- carga desde CSV ----

    private Path archivo(String nombre, List<String> lineas) throws IOException {
        Path ruta = Files.createTempFile(nombre, ".csv");
        ruta.toFile().deleteOnExit();
        Files.write(ruta, lineas);
        return ruta;
    }

    /** Los archivos reales del ejercicio: los números de Bacon pedidos y las relaciones que se descartan. */
    public void testConLosArchivosDelEjercicio() throws IOException {
        RedBacon.Carga carga = RedBacon.cargar(
                Path.of("src/main/resources/ut04/actores.txt"), Path.of("src/main/resources/ut04/en_pelicula.txt"));
        UndirectedGraph<String, String> g = carga.getGrafo();

        assertEquals(61, g.cantidadDeVertices());
        assertEquals(81, g.cantidadDeAristas());
        assertEquals(2, carga.getRelacionesDescartadas());
        assertEquals(4, RedBacon.numBacon(g, "John_Goodman"));
        assertEquals(4, RedBacon.numBacon(g, "Tom_Cruise"));
        assertEquals(4, RedBacon.numBacon(g, "Jason_Statham"));
        assertEquals(5, RedBacon.numBacon(g, "Lukas_Haas"));
        assertEquals(6, RedBacon.numBacon(g, "Djimon_Hounsou"));
    }

    public void testCargaDesdeArchivos() throws IOException {
        Path actores = archivo("actores", List.of("Kevin_Bacon", "Tom_Hanks", "Sally_Field", ""));
        Path peliculas = archivo("peliculas", List.of("Kevin_Bacon,Tom_Hanks,Apolo_13", "Tom_Hanks,Sally_Field,Forrest_Gump", ""));
        RedBacon.Carga carga = RedBacon.cargar(actores, peliculas);

        assertEquals(3, carga.getGrafo().cantidadDeVertices());
        assertEquals(2, carga.getGrafo().cantidadDeAristas());
        assertEquals(0, carga.getRelacionesDescartadas());
        assertEquals(2, RedBacon.numBacon(carga.getGrafo(), "Sally_Field"));
    }

    public void testCargaConEncabezadoYPuntoYComa() throws IOException {
        Path actores = archivo("actores", List.of("actor", "Kevin_Bacon", "Tom_Hanks"));
        Path peliculas = archivo("peliculas", List.of("actor1;actor2;pelicula", "Kevin_Bacon;Tom_Hanks;Apolo_13"));
        RedBacon.Carga carga = RedBacon.cargar(actores, peliculas);

        assertEquals(2, carga.getGrafo().cantidadDeVertices());
        assertEquals(1, carga.getGrafo().cantidadDeAristas());
        assertEquals(1, RedBacon.numBacon(carga.getGrafo(), "Tom_Hanks"));
    }

    /** Una relación con un actor que no figura en actores.csv, y otra con columnas de menos, se descartan y se cuentan. */
    public void testCargaDescartaRelacionesInvalidas() throws IOException {
        Path actores = archivo("actores", List.of("Kevin_Bacon", "Tom_Hanks"));
        Path peliculas = archivo("peliculas", List.of(
                "Kevin_Bacon,Tom_Hanks,Apolo_13",
                "Kevin_Bacon,Desconocido,Otra",
                "Kevin_Bacon,Tom_Hanks"));
        RedBacon.Carga carga = RedBacon.cargar(actores, peliculas);

        assertEquals(1, carga.getGrafo().cantidadDeAristas());
        assertEquals(2, carga.getRelacionesDescartadas());
    }
}
