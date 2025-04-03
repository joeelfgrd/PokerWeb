package edu.badpals.pokerweb.dtos;

import edu.badpals.pokerweb.model.Carta;

import java.util.List;

public class EstadoJugadorDTO {
    private String nombre;
    private int fichas;
    private boolean activo;
    private boolean allIn;
    private List<Carta> mano;

    public EstadoJugadorDTO(String nombre, int fichas, boolean activo, boolean allIn, List<Carta> mano) {
        this.nombre = nombre;
        this.fichas = fichas;
        this.activo = activo;
        this.allIn = allIn;
        this.mano = mano;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getFichas() {
        return fichas;
    }

    public void setFichas(int fichas) {
        this.fichas = fichas;
    }

    public boolean isAllIn() {
        return allIn;
    }

    public void setAllIn(boolean allIn) {
        this.allIn = allIn;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<Carta> getMano() {
        return mano;
    }

    public void setMano(List<Carta> mano) {
        this.mano = mano;
    }
}
