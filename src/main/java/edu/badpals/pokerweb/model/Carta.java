package edu.badpals.pokerweb.model;

import jakarta.persistence.*;


@Entity
public class Carta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String palo;
    private String valor;
    private int numero;
    private int idPalo;

    public Carta(String palo, String valore, int numero, int idPalo) {
        this.palo = palo;
        this.valor = valore;
        this.numero = numero;
        this.idPalo = idPalo;
    }

    public Carta() {
    }

    public Carta(String palo, Long id, String valor, int numero, int idPalo) {
        this.palo = palo;
        this.id = id;
        this.valor = valor;
        this.numero = numero;
        this.idPalo = idPalo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPalo() {
        return palo;
    }

    public void setPalo(String palo) {
        this.palo = palo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getIdPalo() {
        return idPalo;
    }

    public void setIdPalo(int idPalo) {
        this.idPalo = idPalo;
    }
}
