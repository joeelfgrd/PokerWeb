package edu.badpals.pokerweb.domain.model;

import edu.badpals.pokerweb.domain.model.enums.Palo;
import edu.badpals.pokerweb.domain.model.enums.ValorCarta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class Baraja {
    private final List<Carta> listaCartas;

    public Baraja() {
        this.listaCartas = new ArrayList<>();
        inicializarYBarajar();
    }

    public void inicializarYBarajar() {
        listaCartas.clear();

        for (Palo palo : Palo.values()) {
            for (ValorCarta valor : ValorCarta.values()) {
                listaCartas.add(new Carta(palo, valor));
            }
        }
        barajar();
    }

    public void barajar() {
        Collections.shuffle(listaCartas);
    }

    public Carta repartirCarta() {
        if (listaCartas.isEmpty()) {
            System.out.println("La baraja está vacía. Se reinicia automáticamente.");
            inicializarYBarajar();
        }
        return listaCartas.remove(0);
    }

    public List<Carta> getCartas() {
        return new ArrayList<>(listaCartas);
    }
}
