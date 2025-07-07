package fr.esgi.projetannuel.exception;

public class MatchInexistantExeption extends RuntimeException {
    public MatchInexistantExeption(String message) {
        super(message);
    }
    public MatchInexistantExeption() {super();}
}
