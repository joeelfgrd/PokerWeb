package edu.badpals.pokerweb.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Jugador {

    @Id
    private String id;

    private int fichas;
    private boolean activo;

    @OneToOne(cascade = CascadeType.ALL)
    private Mano mano;

    @ManyToOne
    @JsonBackReference
    private Mesa mesa;

    @ManyToOne
    @JsonBackReference
    private Partida partida;

    @ManyToOne(optional = false)
    private Usuario usuario;

    private boolean allIn = false;

    public Jugador() {}

    public Jugador(Usuario usuario, Mesa mesa, Partida partida) {
        this.id = java.util.UUID.randomUUID().toString();
        this.usuario = usuario;
        this.fichas = usuario.getDinero();
        this.activo = true;
        this.mesa = mesa;
        this.partida = partida;
    }

    public String getId() {
        return id;
    }

    public int getFichas() {
        return fichas;
    }

    public void setFichas(int fichas) {
        this.fichas = fichas;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Mano getMano() {
        return mano;
    }

    public void setMano(Mano mano) {
        this.mano = mano;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public Partida getPartida() {
        return partida;
    }

    public void setPartida(Partida partida) {
        this.partida = partida;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getNombre() {
        return usuario.getNombreCompleto();
    }

    public boolean isAllIn() {
        return allIn;
    }

    public void setAllIn(boolean allIn) {
        this.allIn = allIn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Jugador jugador = (Jugador) o;
        return fichas == jugador.fichas && activo == jugador.activo && allIn == jugador.allIn && Objects.equals(id, jugador.id) && Objects.equals(mano, jugador.mano) && Objects.equals(mesa, jugador.mesa) && Objects.equals(partida, jugador.partida) && Objects.equals(usuario, jugador.usuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fichas, activo, mano, mesa, partida, usuario, allIn);
    }
}
