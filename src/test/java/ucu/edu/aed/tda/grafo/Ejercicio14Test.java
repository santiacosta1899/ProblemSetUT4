package ucu.edu.aed.tda.grafo;

import junit.framework.TestCase;
import ucu.edu.aed.Ejercicio14.Residencia;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Casos de prueba del ejercicio 14: ver Ejercicio14.txt (incluye los casos que muestran los errores de la hoja). */
public class Ejercicio14Test extends TestCase {

    private Residencia residencia;

    @Override
    protected void setUp() {
        residencia = Residencia.delEnunciado();
    }

    private static Residencia conContactos(String[] residentes, String[]... contactos) {
        Residencia r = new Residencia();
        for (String nombre : residentes) r.registrarResidente(nombre);
        for (String[] par : contactos) r.registrarContacto(par[0], par[1]);
        return r;
    }

    // ---- simularBrote ----

    /** Pregunta 3 del enunciado: Ana y Juan como pacientes cero, 10 días. Lu, Mati y Nico no se contagian nunca. */
    public void testEnunciadoAnaYJuanDiezDias() {
        Map<String, Integer> dia = residencia.simularBrote(List.of("Ana", "Juan"), 10);

        assertEquals(10, dia.size());
        assertEquals(Integer.valueOf(0), dia.get("Ana"));
        assertEquals(Integer.valueOf(0), dia.get("Juan"));
        assertEquals(Integer.valueOf(1), dia.get("Beto"));
        assertEquals(Integer.valueOf(1), dia.get("Caro"));
        assertEquals(Integer.valueOf(1), dia.get("Hugo"));
        assertEquals(Integer.valueOf(2), dia.get("Dani"));
        assertEquals(Integer.valueOf(2), dia.get("Gabi"));
        assertEquals(Integer.valueOf(2), dia.get("Ines"));
        assertEquals(Integer.valueOf(3), dia.get("Eli"));
        assertEquals(Integer.valueOf(3), dia.get("Fede"));
        assertFalse(dia.containsKey("Lu"));
        assertFalse(dia.containsKey("Mati"));
        assertFalse(dia.containsKey("Nico"));
    }

    /** Hasta el día 1 inclusive: solo los pacientes cero y sus contactos directos (la hoja llegaba al día 2). */
    public void testElDiaLimiteEsInclusivoPeroNoSeSuperaElLimite() {
        Map<String, Integer> dia = residencia.simularBrote(List.of("Ana", "Juan"), 1);

        assertEquals(Set.of("Ana", "Juan", "Beto", "Caro", "Hugo"), dia.keySet());
        for (int d : dia.values()) assertTrue(d <= 1);
    }

    public void testDiaCeroSoloLosPacientesCero() {
        assertEquals(Set.of("Ana", "Juan"), residencia.simularBrote(List.of("Ana", "Juan"), 0).keySet());
    }

    public void testDiasNegativos() {
        assertTrue(residencia.simularBrote(List.of("Ana"), -1).isEmpty());
    }

    public void testUnSoloPacienteCero() {
        Map<String, Integer> dia = residencia.simularBrote(List.of("Ana"), 3);
        assertEquals(Set.of("Ana", "Beto", "Caro", "Dani", "Eli", "Fede"), dia.keySet());
        assertEquals(Integer.valueOf(3), dia.get("Fede"));
    }

    /** Se contagia el día más temprano, aunque haya un camino más largo (la hoja, con pila, podía asignar un día mayor). */
    public void testSeContagiaPorElCaminoMasCorto() {
        Residencia r = conContactos(new String[]{"P", "A", "B", "C"},
                new String[]{"P", "A"}, new String[]{"A", "B"}, new String[]{"B", "C"}, new String[]{"P", "C"});
        Map<String, Integer> dia = r.simularBrote(List.of("P"), 10);

        assertEquals(Integer.valueOf(1), dia.get("C"));
        assertEquals(Integer.valueOf(1), dia.get("A"));
        assertEquals(Integer.valueOf(2), dia.get("B"));
    }

    /** Con dos pacientes cero, cada residente toma el día más temprano de los dos orígenes. */
    public void testDosOrigenesTomaElMasTemprano() {
        Residencia r = conContactos(new String[]{"P1", "A", "B", "P2"},
                new String[]{"P1", "A"}, new String[]{"A", "B"}, new String[]{"B", "P2"});
        Map<String, Integer> dia = r.simularBrote(List.of("P1", "P2"), 10);

        assertEquals(Integer.valueOf(1), dia.get("A"));
        assertEquals(Integer.valueOf(1), dia.get("B"));
    }

    /** Los pacientes cero que no están en la red se ignoran (la hoja los dejaba en el resultado). */
    public void testPacientesCeroQueNoEstanEnLaRed() {
        Map<String, Integer> dia = residencia.simularBrote(List.of("Zoe", "Lu"), 10);

        assertEquals(Set.of("Lu", "Mati", "Nico"), dia.keySet());
        assertFalse(dia.containsKey("Zoe"));
        assertTrue(residencia.simularBrote(List.of("Zoe"), 10).isEmpty());
    }

    public void testSinPacientesCero() {
        assertTrue(residencia.simularBrote(List.of(), 10).isEmpty());
        assertTrue(residencia.simularBrote(null, 10).isEmpty());
    }

    /** Un paciente cero repetido se procesa una sola vez. */
    public void testPacientesCeroRepetidos() {
        Map<String, Integer> dia = residencia.simularBrote(List.of("Lu", "Lu"), 10);
        assertEquals(3, dia.size());
        assertEquals(Integer.valueOf(0), dia.get("Lu"));
    }

    /** Simular no cambia la residencia: repetir la simulación y la búsqueda de puentes da lo mismo. */
    public void testNoModificaLaResidencia() {
        Map<String, Integer> primera = residencia.simularBrote(List.of("Ana"), 10);
        List<String> puentes = residencia.residentesPuente();

        assertEquals(primera, residencia.simularBrote(List.of("Ana"), 10));
        assertEquals(puentes, residencia.residentesPuente());
    }

    /** Pacientes cero en los dos grupos: se contagian los 13 residentes. */
    public void testTodosLosResidentes() {
        assertEquals(13, residencia.simularBrote(List.of("Ana", "Lu"), 10).size());
    }

    // ---- residentesPuente ----

    /** Pregunta 3 del enunciado: Caro, Dani, Fede, Gabi y Hugo separan al grupo conexo grande; Mati separa a Lu y Nico. */
    public void testPuentesDelEnunciado() {
        List<String> puentes = residencia.residentesPuente();

        assertEquals(6, puentes.size());
        assertEquals(Set.of("Caro", "Dani", "Fede", "Gabi", "Hugo", "Mati"), new HashSet<>(puentes));
    }

    /** Ana es la primera residente y es la raíz de la búsqueda con un solo hijo: no es puente (la hoja la incluía). */
    public void testLaPrimeraResidenteNoEsPuenteSiNoSeparaNada() {
        assertFalse(residencia.residentesPuente().contains("Ana"));
    }

    /** Mati está en otro grupo que el primer residente: la hoja solo exploraba el grupo de residentes.obtener(0). */
    public void testSeExploranTodosLosGrupos() {
        assertTrue(residencia.residentesPuente().contains("Mati"));
    }

    public void testCadena() {
        Residencia r = conContactos(new String[]{"A", "B", "C"}, new String[]{"A", "B"}, new String[]{"B", "C"});
        assertEquals(List.of("B"), r.residentesPuente());
    }

    public void testTrianguloNoTienePuentes() {
        Residencia r = conContactos(new String[]{"A", "B", "C"},
                new String[]{"A", "B"}, new String[]{"B", "C"}, new String[]{"C", "A"});
        assertTrue(r.residentesPuente().isEmpty());
    }

    public void testSinResidentes() {
        assertTrue(new Residencia().residentesPuente().isEmpty());
    }

    public void testResidenteSolo() {
        Residencia r = conContactos(new String[]{"A"});
        assertTrue(r.residentesPuente().isEmpty());
    }

    /** Estrella: el centro separa a las tres hojas; cada hoja se puede aislar sin separar a nadie. */
    public void testEstrella() {
        Residencia r = conContactos(new String[]{"A", "B", "C", "D"},
                new String[]{"A", "B"}, new String[]{"A", "C"}, new String[]{"A", "D"});
        assertEquals(List.of("A"), r.residentesPuente());
    }
}
