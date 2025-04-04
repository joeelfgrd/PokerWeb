package edu.badpals.pokerweb.dtos;

import edu.badpals.pokerweb.model.Carta;

import java.util.List;

public class ResultadoShowdownDTO {
    private String nombreGanador;
    private List<Carta> manoGanadora;
    private int fichas;
    private int boteGanado;
    private List<Carta> cartasComunitarias;

    public ResultadoShowdownDTO(String nombreGanador, List<Carta> manoGanadora, int fichas, int boteGanado, List<Carta> cartasComunitarias) {
        this.nombreGanador = nombreGanador;
        this.manoGanadora = manoGanadora;
        this.fichas = fichas;
        this.boteGanado = boteGanado;
        this.cartasComunitarias = cartasComunitarias;
    }

    public String getNombreGanador() {
        return nombreGanador;
    }

    public List<Carta> getManoGanadora() {
        return manoGanadora;
    }

    public int getFichas() {
        return fichas;
    }

    public int getBoteGanado() {
        return boteGanado;
    }

    public List<Carta> getCartasComunitarias() {
        return cartasComunitarias;
    }
}
