package fr.esgi.projetannuel.exception;

public class UserInexistantExeption extends RuntimeException {
    public UserInexistantExeption(String message) {
        super(message);
    }
    public UserInexistantExeption() {super();}
}
