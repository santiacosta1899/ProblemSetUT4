package ucu.edu.aed.Ejercicio13;

import ucu.edu.aed.tda.grafo.DirectedGraph;
import ucu.edu.aed.tda.grafo.DirectedGraphAlgorithms;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;
import ucu.edu.aed.tda.grafo.model.result.IDijkstraResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ejercicio 13: red del Servicio Nacional de Sangre.
 * Los vértices son los bancos y los hospitales, y cada trayecto es un arco con los minutos como peso.
 * Es un grafo dirigido porque ir de A a B no siempre tarda lo mismo que volver, y a veces no se puede volver.
 * redInversa tiene los mismos trayectos dados vuelta: un Dijkstra desde un hospital sobre ella da lo que
 * tarda cada banco en llegar a ese hospital.
 * Como hay muchos pedidos por día y los tiempos no dependen del stock, los caminos mínimos se calculan
 * una sola vez y se guardan; se vuelven a calcular solo si cambia la red.
 * Usa colecciones de Java en lugar de TDALista y THash.
 */
public class RedSangre {

    private final DirectedGraph<String, WeightedEdge> red = new DirectedGraph<>();
    private final DirectedGraph<String, WeightedEdge> redInversa = new DirectedGraph<>();
    private final List<String> bancos = new ArrayList<>();       // en orden de registro
    private final List<String> hospitales = new ArrayList<>();   // en orden de registro
    private final Map<String, Integer> stock = new HashMap<>();  // banco -> unidades disponibles
    private final DirectedGraphAlgorithms algoritmos = new DirectedGraphAlgorithms();

    // caminos mínimos ya calculados (se borran si cambia la red)
    private final Map<String, IDijkstraResult<String>> tiemposHacia = new HashMap<>();   // hospital -> Dijkstra en redInversa
    private final Map<String, List<String>> bancosPorTiempo = new HashMap<>();           // hospital -> bancos que llegan, del más rápido al más lento
    private Map<String, Double> mejorTiempo = null;                                      // hospital -> menor tiempo desde algún banco

    public boolean agregarBanco(String banco, int unidades) {
        if (unidades < 0) throw new IllegalArgumentException("Stock negativo en " + banco);
        if (!red.agregarVertice(banco)) return false;
        redInversa.agregarVertice(banco);
        bancos.add(banco);
        stock.put(banco, unidades);
        olvidarCaminos();
        return true;
    }

    public boolean agregarHospital(String hospital) {
        if (!red.agregarVertice(hospital)) return false;
        redInversa.agregarVertice(hospital);
        hospitales.add(hospital);
        olvidarCaminos();
        return true;
    }

    public boolean agregarTrayecto(String origen, String destino, double minutos) {
        if (minutos < 0) throw new IllegalArgumentException("Los minutos no pueden ser negativos");
        if (!red.agregarArista(origen, destino, new WeightedEdge(minutos))) return false;
        redInversa.agregarArista(destino, origen, new WeightedEdge(minutos));
        olvidarCaminos();
        return true;
    }

    public int stockDe(String banco) {
        return stock.getOrDefault(banco, 0);
    }

    private void olvidarCaminos() {
        tiemposHacia.clear();
        bancosPorTiempo.clear();
        mejorTiempo = null;
    }

    /**
     * Bancos que llegan al hospital, del más rápido al más lento (en empate, el registrado antes).
     * La primera vez hace un Dijkstra desde el hospital sobre la red invertida; después lo reutiliza.
     */
    private List<String> bancosPorTiempo(String hospital) {
        List<String> orden = bancosPorTiempo.get(hospital);
        if (orden != null) return orden;

        IDijkstraResult<String> tiempos = algoritmos.dijkstra(redInversa.construirComparable(hospital), redInversa);
        orden = new ArrayList<>();
        for (String banco : bancos) {
            if (tiempos.getCost(banco) != Double.POSITIVE_INFINITY) orden.add(banco);
        }
        orden.sort(Comparator.comparingDouble(tiempos::getCost));   // estable
        tiemposHacia.put(hospital, tiempos);
        bancosPorTiempo.put(hospital, orden);
        return orden;
    }

    /**
     * Saca las unidades de los bancos que llegan al hospital en maxMinutos o menos, empezando por el que
     * llega antes, y las descuenta del stock. Si no se puede cubrir todo el pedido a tiempo, no se envía nada
     * y el stock no cambia.
     * Precondición: los minutos de los trayectos son >= 0.
     */
    public List<Envio> abastecer(String hospital, int unidades, double maxMinutos) {
        List<Envio> envios = new ArrayList<>();
        if (!hospitales.contains(hospital) || unidades <= 0) return envios;

        List<String> orden = bancosPorTiempo(hospital);
        IDijkstraResult<String> tiempos = tiemposHacia.get(hospital);

        // primero se ve si alcanza, sin tocar el stock
        int disponibles = 0;
        for (String banco : orden) {
            if (tiempos.getCost(banco) > maxMinutos) break;   // los que siguen tardan más
            disponibles += stock.get(banco);
        }
        if (disponibles < unidades) return envios;

        int falta = unidades;
        for (int i = 0; i < orden.size() && falta > 0; i++) {
            String banco = orden.get(i);
            int cantidad = Math.min(stock.get(banco), falta);
            if (cantidad > 0) {
                envios.add(new Envio(banco, cantidad, tiempos.getCost(banco)));
                stock.put(banco, stock.get(banco) - cantidad);
                falta -= cantidad;
            }
        }
        return envios;
    }

    /**
     * Hospitales a los que ningún banco llega en maxMinutos o menos, sin importar el stock, en orden de registro.
     * La primera vez hace un Dijkstra por banco y guarda el menor tiempo de cada hospital; después solo compara.
     */
    public List<String> hospitalesSinCobertura(double maxMinutos) {
        if (mejorTiempo == null) calcularMejorTiempo();

        List<String> sinCobertura = new ArrayList<>();
        for (String hospital : hospitales) {
            if (mejorTiempo.get(hospital) > maxMinutos) sinCobertura.add(hospital);
        }
        return sinCobertura;
    }

    private void calcularMejorTiempo() {
        mejorTiempo = new HashMap<>();
        for (String hospital : hospitales) mejorTiempo.put(hospital, Double.POSITIVE_INFINITY);

        for (String banco : bancos) {
            IDijkstraResult<String> desdeBanco = algoritmos.dijkstra(red.construirComparable(banco), red);
            for (String hospital : hospitales) {
                mejorTiempo.put(hospital, Math.min(mejorTiempo.get(hospital), desdeBanco.getCost(hospital)));
            }
        }
    }
}
