package edu.badpals.pokerweb.domain.services;

import java.util.List;
import java.util.Objects;

public class ManoEvaluada implements Comparable<ManoEvaluada> {

    private final int puntuacion; // 700 = poker, 600 = full, etc.
    private final int valorPrincipal; // valor de la jugada
    private final List<Integer> kickers; // cartas adicionales para desempate

    public ManoEvaluada(int puntuacion, int valorPrincipal, List<Integer> kickers) {
        this.puntuacion = puntuacion;
        this.valorPrincipal = valorPrincipal;
        this.kickers = kickers;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public int getValorPrincipal() {
        return valorPrincipal;
    }

    public List<Integer> getKickers() {
        return kickers;
    }

    @Override
    public int compareTo(ManoEvaluada otra) {
        if (this.puntuacion != otra.puntuacion) {
            return Integer.compare(this.puntuacion, otra.puntuacion);
        }

        if (this.valorPrincipal != otra.valorPrincipal) {
            return Integer.compare(this.valorPrincipal, otra.valorPrincipal);
        }

        for (int i = 0; i < Math.min(this.kickers.size(), otra.kickers.size()); i++) {
            int comparacion = Integer.compare(this.kickers.get(i), otra.kickers.get(i));
            if (comparacion != 0) return comparacion;
        }

        return 0;
    }

    @Override
    public String toString() {
        return "ManoEvaluada{" +
                "puntuacion=" + puntuacion +
                ", valorPrincipal=" + valorPrincipal +
                ", kickers=" + kickers +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ManoEvaluada that)) return false;
        return puntuacion == that.puntuacion &&
                valorPrincipal == that.valorPrincipal &&
                Objects.equals(kickers, that.kickers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(puntuacion, valorPrincipal, kickers);
    }
}
