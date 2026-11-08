package ucu.edu.aed.tda.grafo;

import junit.framework.TestCase;
import ucu.edu.aed.Ejercicio13.Envio;
import ucu.edu.aed.Ejercicio13.MainEjercicio13;
import ucu.edu.aed.Ejercicio13.RedSangre;

import java.util.List;

public class Ejercicio13Test extends TestCase {

    private RedSangre red;

    @Override
    protected void setUp() {
        red = MainEjercicio13.redDelEjercicio();
    }

    private void assertStock(int bc, int bn, int be) {
        assertEquals(bc, red.stockDe("BC"));
        assertEquals(bn, red.stockDe("BN"));
        assertEquals(be, red.stockDe("BE"));
    }

    /** Pregunta 1.3: BC llega en 20 (por HM) y BN en 30 justo: se sacan 6 de BC y 2 de BN. */
    public void testClinicasPideOchoEnTreinta() {
        List<Envio> envios = red.abastecer("HC", 8, 30);
        assertEquals(List.of(new Envio("BC", 6, 20), new Envio("BN", 2, 30)), envios);
        assertStock(0, 6, 16);
    }

    /** Un banco que llega justo en el máximo sirve (con < quedaría afuera BN y no alcanzaría). */
    public void testElMaximoEsInclusivo() {
        assertFalse(red.abastecer("HC", 8, 30).isEmpty());
    }

    /** Solo se saca lo que falta: con 5 unidades alcanza BC y BN no se toca. */
    public void testSoloSeSacaLoQueFalta() {
        assertEquals(List.of(new Envio("BC", 5, 20)), red.abastecer("HC", 5, 30));
        assertStock(1, 8, 16);
    }

    /** Si no alcanza, no se envía nada y el stock queda como estaba. */
    public void testPedidoQueNoSeCubreNoTocaElStock() {
        assertTrue(red.abastecer("HC", 20, 30).isEmpty());
        assertStock(6, 8, 16);
    }

    /** El segundo pedido ve el stock que dejó el primero. */
    public void testElStockSeDescuentaEntrePedidos() {
        red.abastecer("HC", 8, 30);
        assertTrue(red.abastecer("HC", 8, 30).isEmpty());   // quedan 6 en BN y BC está vacío
        assertEquals(List.of(new Envio("BN", 6, 30)), red.abastecer("HC", 6, 30));
        assertStock(0, 0, 16);
    }

    /** Con más tiempo entra BE (38 minutos). */
    public void testConMasTiempoEntraBancoEste() {
        assertEquals(List.of(new Envio("BC", 6, 20), new Envio("BN", 8, 30), new Envio("BE", 6, 38)),
                red.abastecer("HC", 20, 40));
        assertStock(0, 0, 10);
    }

    /** Al Hospital Español solo llega BE a tiempo (14 minutos). */
    public void testHospitalEspanol() {
        assertEquals(List.of(new Envio("BE", 10, 14)), red.abastecer("HE", 10, 20));
    }

    /** Pedidos inválidos: lugar que no es hospital, unidades <= 0. */
    public void testPedidosInvalidos() {
        assertTrue(red.abastecer("BC", 1, 100).isEmpty());
        assertTrue(red.abastecer("XX", 1, 100).isEmpty());
        assertTrue(red.abastecer("HC", 0, 100).isEmpty());
        assertTrue(red.abastecer("HC", -3, 100).isEmpty());
        assertStock(6, 8, 16);
    }

    /** Pregunta 1.3: con 15 minutos solo queda sin cobertura el Hospital de Clínicas (el más cercano tarda 20). */
    public void testSinCoberturaQuince() {
        assertEquals(List.of("HC"), red.hospitalesSinCobertura(15));
    }

    public void testSinCoberturaOtrosLimites() {
        assertEquals(List.of(), red.hospitalesSinCobertura(20));
        assertEquals(List.of("HC", "HP"), red.hospitalesSinCobertura(14));
        assertEquals(List.of("HC", "HP", "HM", "HE"), red.hospitalesSinCobertura(10));
    }

    /** Si cambia la red, los caminos guardados se recalculan. */
    public void testUnTrayectoNuevoCambiaLosTiempos() {
        assertEquals(List.of("HC"), red.hospitalesSinCobertura(15));
        assertTrue(red.abastecer("HC", 8, 12).isEmpty());   // hoy nadie llega a HC en 12

        red.agregarTrayecto("BN", "HC", 12);
        assertEquals(List.of(), red.hospitalesSinCobertura(15));
        assertEquals(List.of(new Envio("BN", 8, 12)), red.abastecer("HC", 8, 12));
    }

    /** La cobertura no depende del stock. */
    public void testSinCoberturaNoMiraElStock() {
        red.abastecer("HC", 14, 30);   // vacía BC y BN
        assertEquals(List.of("HC"), red.hospitalesSinCobertura(15));
    }
}
