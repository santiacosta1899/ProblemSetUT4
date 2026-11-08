package ucu.edu.aed.Ejercicio14;

import java.util.List;
import java.util.Map;

/** Ejercicio 14, parte 1.3: las dos preguntas del enunciado sobre la residencia. */
public class MainEjercicio14 {

    public static void main(String[] args) {
        Residencia residencia = Residencia.delEnunciado();

        Map<String, Integer> dia = residencia.simularBrote(List.of("Ana", "Juan"), 10);
        System.out.println("Día de contagio con Ana y Juan como pacientes cero (10 días):");
        dia.forEach((residente, d) -> System.out.println("  " + residente + ": día " + d));

        System.out.println("\nResidentes que separan a otros si se los aísla: " + residencia.residentesPuente());
    }
}
