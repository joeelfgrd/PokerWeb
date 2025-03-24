package edu.badpals.pokerweb.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Partida {
    private LocalDateTime inicio;
    private LocalDateTime fin;
    private String idGanador;
    @Id
    private String id;

    @ManyToOne
    private Mesa mesa;

    @OneToMany(mappedBy = "partida", cascade = CascadeType.ALL)
    private List<Jugador> jugadores;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Carta> cartasComunitarias = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private EstadoPartida estado = EstadoPartida.EN_CURSO;

    public Partida() {
        this.id = UUID.randomUUID().toString();
        this.inicio = LocalDateTime.now();
        this.jugadores = new ArrayList<>();
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public void setInicio(LocalDateTime inicio) {
        this.inicio = inicio;
    }

    public LocalDateTime getFin() {
        return fin;
    }

    public void setFin(LocalDateTime fin) {
        this.fin = fin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public void setJugadores(List<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public List<Carta> getCartasComunitarias() {
        return cartasComunitarias;
    }

    public void setCartasComunitarias(List<Carta> cartasComunitarias) {
        this.cartasComunitarias = cartasComunitarias;
    }

    public EstadoPartida getEstado() {
        return estado;
    }

    public void setEstado(EstadoPartida estado) {
        this.estado = estado;
    }

    public String getIdGanador() {
        return idGanador;
    }

    public void setIdGanador(String idGanador) {
        this.idGanador = idGanador;
    }
}
