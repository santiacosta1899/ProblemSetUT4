package ucu.edu.aed.Ejercicio12;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Ejercicio 12: carga el grafo de actores desde actores.txt y en_pelicula.txt, calcula el número de Bacon
 * de los actores pedidos, lo muestra por consola y lo guarda en salida.txt.
 *
 * Uso: MainEjercicio12 [actores] [en_pelicula]
 * Por defecto lee src/main/resources/ut04/actores.txt y src/main/resources/ut04/en_pelicula.txt.
 */
public class MainEjercicio12 {

    private static final String[] ACTORES_A_CONSULTAR = {
            "John_Goodman", "Tom_Cruise", "Jason_Statham", "Lukas_Haas", "Djimon_Hounsou"
    };

    public static void main(String[] args) throws IOException {
        Path actores = Path.of(args.length > 0 ? args[0] : "src/main/resources/ut04/actores.txt");
        Path peliculas = Path.of(args.length > 1 ? args[1] : "src/main/resources/ut04/en_pelicula.txt");

        RedBacon.Carga carga = RedBacon.cargar(actores, peliculas);
        List<String> salida = new ArrayList<>();
        salida.add("Actores: " + carga.getGrafo().cantidadDeVertices()
                + ", películas (aristas): " + carga.getGrafo().cantidadDeAristas()
                + ", relaciones descartadas: " + carga.getRelacionesDescartadas());
        salida.add("Número de Bacon:");
        for (String actor : ACTORES_A_CONSULTAR) {
            int numero = RedBacon.numBacon(carga.getGrafo(), actor);
            salida.add(actor + ": " + (numero >= 0 ? String.valueOf(numero) : "sin conexión con " + RedBacon.KEVIN_BACON));
        }

        salida.forEach(System.out::println);
        Path destino = Path.of("src/main/java/ucu/edu/aed/Ejercicio12/salida.txt");
        Files.write(destino, salida, StandardCharsets.UTF_8);
        System.out.println("\nResultado guardado en " + destino);
    }
}
