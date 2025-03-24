package edu.badpals.pokerweb.model;

import jakarta.persistence.*;

@Entity
public class Jugador {
    private int fichas;
    private boolean activo;

    @Id
    @Column(unique = true, nullable = false)
    private String id;

    @OneToOne(cascade = CascadeType.ALL)
    private Mano mano;

    @ManyToOne
    private Mesa mesa;

    @ManyToOne(optional = false)
    private Usuario usuario;

    public Jugador() {}

    public Jugador(Usuario usuario, Mesa mesa) {
        this.id = java.util.UUID.randomUUID().toString();
        this.usuario = usuario;
        this.fichas = usuario.getDinero(); // Puedes limitarlo si solo mete parte del dinero
        this.activo = true;
        this.mesa = mesa;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public String getNombre() {
        return usuario.getNombreCompleto();
    }
}
