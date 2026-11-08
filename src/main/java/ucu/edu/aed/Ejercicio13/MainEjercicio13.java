package ucu.edu.aed.Ejercicio13;

/**
 * Ejercicio 13, parte 1.3: qué se envía si el Hospital de Clínicas pide 8 unidades en 30 minutos
 * y qué hospitales quedan sin cobertura con 15 minutos. Las respuestas escritas están en Ejercicio13.txt.
 */
public class MainEjercicio13 {

    /** La red del enunciado: bancos con su stock, hospitales y trayectos (origen, destino, minutos). */
    public static RedSangre redDelEjercicio() {
        RedSangre red = new RedSangre();
        red.agregarBanco("BC", 6);
        red.agregarBanco("BN", 8);
        red.agregarBanco("BE", 16);
        red.agregarHospital("HC");
        red.agregarHospital("HP");
        red.agregarHospital("HM");
        red.agregarHospital("HE");
        red.agregarTrayecto("BN", "HP", 15);
        red.agregarTrayecto("BN", "BC", 10);
        red.agregarTrayecto("BC", "HC", 25);
        red.agregarTrayecto("BC", "HM", 12);
        red.agregarTrayecto("HM", "HC", 8);
        red.agregarTrayecto("BE", "HM", 30);
        red.agregarTrayecto("BE", "HC", 50);
        red.agregarTrayecto("HP", "HC", 20);
        red.agregarTrayecto("HC", "BE", 5);
        red.agregarTrayecto("HM", "BN", 18);
        red.agregarTrayecto("BE", "HE", 14);
        return red;
    }

    public static void main(String[] args) {
        RedSangre red = redDelEjercicio();

        System.out.println("HC pide 8 unidades en 30 minutos como máximo:");
        System.out.println("  envíos: " + red.abastecer("HC", 8, 30));
        System.out.println("  stock después: BC " + red.stockDe("BC") + ", BN " + red.stockDe("BN") + ", BE " + red.stockDe("BE"));

        System.out.println("\nHospitales sin cobertura con 15 minutos: " + red.hospitalesSinCobertura(15));
    }
}
