package edu.badpals.pokerweb.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class Jugador {
    @Id
    private String id;
    
    private String nombre;
    private int fichas;
    private boolean activo;

    @OneToOne(cascade = CascadeType.ALL)
    private Mano mano;

    @ManyToOne
    private Mesa mesa;

    public Jugador() {}

    public Jugador(String nombre, Mesa mesa) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.fichas = 1000;
        this.activo = true;
        this.mesa = mesa;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
