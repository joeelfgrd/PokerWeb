package edu.badpals.pokerweb.dtos;

import java.time.LocalDate;
/*Creo una dto que es la que se va a quedar en el movil porque no me interesa que se quede la contraseña en el movil */
public class UsuarioLogueadoDTO {
    private String id;
    private String nombreCompleto;
    private String dni;
    private LocalDate fechaNacimiento;
    private String email;
    private int dinero;

    public UsuarioLogueadoDTO() {}

    public UsuarioLogueadoDTO(String id, String nombreCompleto, String dni, LocalDate fechaNacimiento, String email, int dinero) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
        this.email = email;
        this.dinero = dinero;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getDinero() {
        return dinero;
    }

    public void setDinero(int dinero) {
        this.dinero = dinero;
    }
}
