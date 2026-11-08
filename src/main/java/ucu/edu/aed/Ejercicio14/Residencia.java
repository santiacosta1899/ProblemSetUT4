package ucu.edu.aed.Ejercicio14;

import ucu.edu.aed.tda.grafo.IUndirectedGraph;
import ucu.edu.aed.tda.grafo.UndirectedGraph;
import ucu.edu.aed.tda.grafo.UndirectedGraphAlgorithms;
import ucu.edu.aed.tda.grafo.model.edge.Edge;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Ejercicio 14: brote de gripe en una residencia.
 * contactos: grafo NO dirigido; vértices = residentes, aristas = contacto estrecho (el contagio va en los
 * dos sentidos). El dato de la arista es el tipo de contacto (String).
 */
public class Residencia {

    private final IUndirectedGraph<String, String> contactos = new UndirectedGraph<>();
    private final UndirectedGraphAlgorithms algoritmos = new UndirectedGraphAlgorithms();

    /** La residencia del enunciado: 13 residentes y 14 contactos estrechos. */
    public static Residencia delEnunciado() {
        Residencia r = new Residencia();
        for (String nombre : new String[]{"Ana", "Beto", "Caro", "Dani", "Eli", "Fede", "Gabi", "Hugo", "Ines", "Juan", "Lu", "Mati", "Nico"}) {
            r.registrarResidente(nombre);
        }
        String[][] pares = {
                {"Ana", "Beto"}, {"Ana", "Caro"}, {"Beto", "Caro"}, {"Caro", "Dani"}, {"Dani", "Eli"},
                {"Dani", "Fede"}, {"Eli", "Fede"}, {"Fede", "Gabi"}, {"Gabi", "Hugo"}, {"Gabi", "Ines"},
                {"Hugo", "Ines"}, {"Hugo", "Juan"}, {"Lu", "Mati"}, {"Mati", "Nico"}};
        for (String[] par : pares) r.registrarContacto(par[0], par[1]);
        return r;
    }

    public boolean registrarResidente(String nombre) {
        return nombre != null && contactos.agregarVertice(nombre);
    }

    /** Registra un contacto estrecho entre dos residentes ya registrados (vale en los dos sentidos). */
    public boolean registrarContacto(String residente1, String residente2) {
        return contactos.agregarArista(residente1, residente2, "contacto");
    }

    /**
     * Día de contagio de cada residente si no se hace nada. Los pacientes cero se contagian el día 0 y cada
     * contagiado contagia a todos sus contactos estrechos al día siguiente. Es una búsqueda en amplitud con
     * varios orígenes: cada residente entra a la cola una sola vez, la primera vez que se lo alcanza, y esa
     * primera vez es el día más temprano posible. Se marca al encolar con el diccionario de días, que también
     * sirve para saber en O(1) si un residente ya se contagió.
     * Precondición: ninguna obligatoria (pacientesCero puede estar vacío y tener nombres que no están en la red).
     * Postcondiciones: el resultado tiene solo residentes contagiados hasta el día "dias" inclusive, en el orden
     * en que se contagian; los pacientes cero que no están en la red se ignoran; si dias < 0 el resultado está
     * vacío; la residencia no cambia.
     */
    public Map<String, Integer> simularBrote(Collection<String> pacientesCero, int dias) {
        Map<String, Integer> dia = new LinkedHashMap<>();
        if (pacientesCero == null || dias < 0) return dia;

        Queue<String> cola = new ArrayDeque<>();
        for (String paciente : pacientesCero) {
            if (contactos.existeVertice(contactos.construirComparable(paciente)) && !dia.containsKey(paciente)) {
                dia.put(paciente, 0);
                cola.add(paciente);
            }
        }

        while (!cola.isEmpty()) {
            String actual = cola.poll();
            int d = dia.get(actual);
            if (d == dias) continue;   // contagiaría a sus contactos el día dias + 1, que ya no se simula
            for (Edge<String, String> contacto : contactos.adyacencias(contactos.construirComparable(actual))) {
                String vecino = contacto.target();
                if (!dia.containsKey(vecino)) {
                    dia.put(vecino, d + 1);
                    cola.add(vecino);
                }
            }
        }
        return dia;
    }

    /**
     * Residentes que, si se los aísla, separan a otros residentes que hoy pueden contagiarse entre sí: son
     * los puntos de articulación del grafo de contactos. La red puede tener más de un grupo de residentes
     * sin relación entre sí; se buscan los puntos de cada grupo.
     * Postcondición: lista sin repetidos; la residencia no cambia.
     */
    public List<String> residentesPuente() {
        return algoritmos.puntosDeArticulacion(contactos);
    }
}
