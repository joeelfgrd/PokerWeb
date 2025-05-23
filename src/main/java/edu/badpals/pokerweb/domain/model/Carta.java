package edu.badpals.pokerweb.domain.model;

import edu.badpals.pokerweb.domain.enums.Palo;
import edu.badpals.pokerweb.domain.enums.ValorCarta;
import jakarta.persistence.*;
@Entity
public class Carta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Palo palo;

    @Enumerated(EnumType.STRING)
    private ValorCarta valor;

    public Carta() {}

    public Carta(Palo palo, ValorCarta valor) {
        this.palo = palo;
        this.valor = valor;
    }

    public int getNumero() {
        return valor.getNumero();
    }

    public int getIdPalo() {
        return palo.getId();
    }

    public String getValorString() {
        return valor.getValor();
    }
    @Override
    public String toString() {
        return valor.getValorString() + " de " + palo.name();
    }

}


