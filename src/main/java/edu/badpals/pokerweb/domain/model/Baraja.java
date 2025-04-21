package edu.badpals.pokerweb.domain.model;

import edu.badpals.pokerweb.domain.enums.Palo;
import edu.badpals.pokerweb.domain.enums.ValorCarta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class Baraja {
    private final List<Carta> listaCartas;

    /**
     * Constructor de la clase Baraja.
     * Inicializa la baraja y la baraja automáticamente.
     */
    public Baraja() {
        this.listaCartas = new ArrayList<>();
        inicializarYBarajar();
    }

    /**
     * Inicializa la baraja con todas las cartas y las baraja.
     */
    public void inicializarYBarajar() {
        listaCartas.clear();

        for (Palo palo : Palo.values()) {
            for (ValorCarta valor : ValorCarta.values()) {
                listaCartas.add(new Carta(palo, valor));
            }
        }
        barajar();
    }

    /**
     * Baraja la baraja de cartas.
     */
    public void barajar() {
        Collections.shuffle(listaCartas);
    }

    /**
     * Reparte una carta de la baraja.
     * Si la baraja está vacía, se reinicia automáticamente.
     *
     * @return La carta repartida.
     */
    public Carta repartirCarta() {
        if (listaCartas.isEmpty()) {
            System.out.println("La baraja está vacía,se reinicia automáticamente.");
            inicializarYBarajar();
        }
        return listaCartas.remove(0);
    }

    /**
     * Devuelve la lista de cartas de la baraja.
     *
     * @return La lista de cartas.
     */
    public List<Carta> getCartas() {
        return new ArrayList<>(listaCartas);
    }
}
