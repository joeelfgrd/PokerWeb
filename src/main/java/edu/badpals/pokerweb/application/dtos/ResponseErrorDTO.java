package edu.badpals.pokerweb.application.dtos;

import java.time.LocalDateTime;

public class ResponseErrorDTO {

    private final String error;
    private final String mensaje;
    private final int codigo;
    private final LocalDateTime timestamp;

    public ResponseErrorDTO(String error, String mensaje, int codigo) {
        this.error = error;
        this.mensaje = mensaje;
        this.codigo = codigo;
        this.timestamp = LocalDateTime.now();
    }

    public String getError() {
        return error;
    }

    public String getMensaje() {
        return mensaje;
    }

    public int getCodigo() {
        return codigo;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
