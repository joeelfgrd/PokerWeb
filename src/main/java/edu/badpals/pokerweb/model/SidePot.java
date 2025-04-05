package edu.badpals.pokerweb.model;

import java.util.HashSet;
import java.util.Set;

public class SidePot {
    private int cantidad;
    private Set<String> participantes = new HashSet<>();

    public SidePot(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void añadirCantidad(int cantidad) {
        this.cantidad += cantidad;
    }

    public Set<String> getParticipantes() {
        return participantes;
    }

    public void añadirParticipante(String idJugador) {
        this.participantes.add(idJugador);
    }
}
