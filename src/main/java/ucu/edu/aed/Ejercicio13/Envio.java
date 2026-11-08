package ucu.edu.aed.Ejercicio13;

/** Un envío de sangre: de qué banco sale, cuántas unidades lleva y cuántos minutos tarda en llegar. */
public class Envio {

    private final String banco;
    private final int unidades;
    private final double minutos;

    public Envio(String banco, int unidades, double minutos) {
        this.banco = banco;
        this.unidades = unidades;
        this.minutos = minutos;
    }

    public String getBanco() {
        return banco;
    }

    public int getUnidades() {
        return unidades;
    }

    public double getMinutos() {
        return minutos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Envio otro)) return false;
        return banco.equals(otro.banco) && unidades == otro.unidades && minutos == otro.minutos;
    }

    @Override
    public int hashCode() {
        return banco.hashCode() * 31 + unidades;
    }

    @Override
    public String toString() {
        return "(" + banco + ", " + unidades + " unidades, " + minutos + " min)";
    }
}
