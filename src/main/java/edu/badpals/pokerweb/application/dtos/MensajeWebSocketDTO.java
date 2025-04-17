package edu.badpals.pokerweb.application.dtos;

public class MensajeWebSocketDTO {
    private String accion;
    private String idUsuario;
    private String codigoInvitacion;
    private Integer cantidad;
    private String idPartida;


    // Getters y setters
    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCodigoInvitacion() {
        return codigoInvitacion;
    }

    public void setCodigoInvitacion(String codigoInvitacion) {
        this.codigoInvitacion = codigoInvitacion;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getIdPartida() {
        return idPartida;
    }
    public void setIdPartida(String idPartida) {
        this.idPartida = idPartida;
    }
}
