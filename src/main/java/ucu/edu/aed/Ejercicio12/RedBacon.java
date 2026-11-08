package ucu.edu.aed.Ejercicio12;

import ucu.edu.aed.tda.grafo.IUndirectedGraph;
import ucu.edu.aed.tda.grafo.UndirectedGraph;
import ucu.edu.aed.tda.grafo.model.edge.Edge;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Ejercicio 12: número de Bacon. Los vértices son actores (String) y cada arista guarda el nombre de la
 * película (String) en la que trabajaron juntos los dos actores.
 */
public class RedBacon {

    public static final String KEVIN_BACON = "Kevin_Bacon";

    private RedBacon() {
    }

    /**
     * Número de Bacon de un actor: la cantidad mínima de películas encadenadas que lo unen con Kevin_Bacon.
     * Es una búsqueda en amplitud por niveles desde Kevin_Bacon: el nivel en que se encuentra al actor es
     * su número (Kevin_Bacon tiene 0, sus compañeros de película 1, los compañeros de estos 2, etc.).
     * Devuelve -1 si el actor no está en el grafo o no tiene ninguna cadena de películas con Kevin_Bacon.
     * Precondiciones: el grafo contiene a Kevin_Bacon y actor no es nulo.
     */
    public static int numBacon(IUndirectedGraph<String, String> grafo, String actor) {
        if (grafo == null || actor == null) {
            throw new IllegalArgumentException("El grafo y el actor no pueden ser nulos");
        }
        if (!grafo.existeVertice(grafo.construirComparable(KEVIN_BACON))) {
            throw new IllegalStateException("El grafo no contiene a " + KEVIN_BACON);
        }
        if (!grafo.existeVertice(grafo.construirComparable(actor))) return -1;

        Map<String, Integer> nivel = new HashMap<>();   // también hace de conjunto de visitados
        Queue<String> cola = new ArrayDeque<>();
        nivel.put(KEVIN_BACON, 0);
        cola.add(KEVIN_BACON);
        while (!cola.isEmpty()) {
            String actual = cola.poll();
            if (actual.equals(actor)) return nivel.get(actual);
            for (Edge<String, String> pelicula : grafo.adyacencias(grafo.construirComparable(actual))) {
                String companiero = pelicula.target();
                if (!nivel.containsKey(companiero)) {
                    nivel.put(companiero, nivel.get(actual) + 1);
                    cola.add(companiero);
                }
            }
        }
        return -1;
    }

    /**
     * Carga el grafo desde dos archivos CSV.
     * - actores: un actor por línea (si la línea tiene comas o punto y coma, se toma la primera columna).
     * - películas: una relación por línea, "actor1,actor2,película" (también con punto y coma).
     * Se ignoran las líneas vacías y la primera línea si es un encabezado. Las relaciones que nombran a un
     * actor que no está en el archivo de actores, o que repiten un par de actores, no se agregan y se cuentan
     * en el resultado como relaciones descartadas.
     */
    public static Carga cargar(Path actores, Path peliculas) throws IOException {
        UndirectedGraph<String, String> grafo = new UndirectedGraph<>();
        int descartadas = 0;

        List<String> lineasActores = Files.readAllLines(actores);
        for (int i = 0; i < lineasActores.size(); i++) {
            String[] columnas = columnas(lineasActores.get(i));
            if (columnas.length == 0) continue;
            if (i == 0 && esEncabezado(columnas[0], Set.of("actor", "actores", "nombre", "name"))) continue;
            grafo.agregarVertice(columnas[0]);
        }

        List<String> lineasPeliculas = Files.readAllLines(peliculas);
        Set<String> vistas = new HashSet<>();
        for (int i = 0; i < lineasPeliculas.size(); i++) {
            String[] columnas = columnas(lineasPeliculas.get(i));
            if (columnas.length == 0) continue;
            if (i == 0 && esEncabezado(columnas[0], Set.of("actor", "actor1", "actor_1", "actor 1", "origen"))) continue;
            if (columnas.length < 3 || !grafo.agregarArista(columnas[0], columnas[1], columnas[2])) {
                descartadas++;
            }
        }
        return new Carga(grafo, descartadas);
    }

    private static String[] columnas(String linea) {
        String limpia = linea.replace("﻿", "").trim();   // quita el BOM de algunos CSV
        if (limpia.isEmpty()) return new String[0];
        String[] partes = limpia.split("[,;]");
        for (int i = 0; i < partes.length; i++) partes[i] = partes[i].trim();
        return partes;
    }

    private static boolean esEncabezado(String primera, Set<String> conocidos) {
        return conocidos.contains(primera.toLowerCase());
    }

    /** El grafo cargado y la cantidad de relaciones que no se pudieron agregar. */
    public static class Carga {
        private final UndirectedGraph<String, String> grafo;
        private final int relacionesDescartadas;

        Carga(UndirectedGraph<String, String> grafo, int relacionesDescartadas) {
            this.grafo = grafo;
            this.relacionesDescartadas = relacionesDescartadas;
        }

        public UndirectedGraph<String, String> getGrafo() {
            return grafo;
        }

        public int getRelacionesDescartadas() {
            return relacionesDescartadas;
        }
    }
}
