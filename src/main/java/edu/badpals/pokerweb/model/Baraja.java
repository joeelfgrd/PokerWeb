package edu.badpals.pokerweb.model;

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
        String[] palos = {"Corazones", "Diamantes", "Tréboles", "Picas"};
        int[] idPalos = {0, 1, 2, 3};
        String[] valores = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        int[] numerosvalor = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};

        for (int i = 0; i < palos.length; i++) {
            for (int j = 0; j < valores.length; j++) {
                listaCartas.add(new Carta(palos[i], valores[j], numerosvalor[j], idPalos[i]));
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
