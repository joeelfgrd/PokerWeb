package edu.badpals.pokerweb.dtos;

public class AccionJugadorDTO {
    private String idJugador;
    private int cantidad;

    public String getIdJugador() {
        return idJugador;
    }

    public void setIdJugador(String idJugador) {
        this.idJugador = idJugador;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
