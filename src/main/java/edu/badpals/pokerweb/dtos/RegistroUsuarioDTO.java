package edu.badpals.pokerweb.dtos;

import java.time.LocalDate;
/*CREO UNA CLASE PARA GESTIONAR EL POST QUE SE HACE CUANDO SE REGISTRA*/
public class RegistroUsuarioDTO {
    private String nombreCompleto;
    private String dni;
    private LocalDate fechaNacimiento;
    private String email;
    private String password;

    public RegistroUsuarioDTO() {}

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
