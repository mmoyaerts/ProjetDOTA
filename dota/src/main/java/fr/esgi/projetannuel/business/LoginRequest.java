package fr.esgi.projetannuel.business;

public class LoginRequest {
    private String email;
    private String password;

    // getters et setters

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

