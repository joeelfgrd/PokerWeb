package edu.badpals.pokerweb.application.dtos;

import edu.badpals.pokerweb.domain.model.Carta;
import edu.badpals.pokerweb.domain.enums.FaseJuego;

import java.util.List;

public class EstadoPartidaDTO {
    private FaseJuego fase;
    private int bote;
    private List<Carta> cartasComunitarias;
    private List<EstadoJugadorDTO> jugadores;

    public EstadoPartidaDTO(FaseJuego fase, int bote, List<Carta> cartasComunitarias, List<EstadoJugadorDTO> jugadores) {
        this.fase = fase;
        this.bote = bote;
        this.cartasComunitarias = cartasComunitarias;
        this.jugadores = jugadores;
    }

    public FaseJuego getFase() {
        return fase;
    }

    public void setFase(FaseJuego fase) {
        this.fase = fase;
    }

    public int getBote() {
        return bote;
    }

    public void setBote(int bote) {
        this.bote = bote;
    }

    public List<Carta> getCartasComunitarias() {
        return cartasComunitarias;
    }

    public void setCartasComunitarias(List<Carta> cartasComunitarias) {
        this.cartasComunitarias = cartasComunitarias;
    }

    public List<EstadoJugadorDTO> getJugadores() {
        return jugadores;
    }

    public void setJugadores(List<EstadoJugadorDTO> jugadores) {
        this.jugadores = jugadores;
    }
}
