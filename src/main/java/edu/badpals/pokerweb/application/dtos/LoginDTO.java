package edu.badpals.pokerweb.application.dtos;
/*Para iniciar sesion*/
public class LoginDTO {
    private String email;
    private String password;

    public LoginDTO() {}

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