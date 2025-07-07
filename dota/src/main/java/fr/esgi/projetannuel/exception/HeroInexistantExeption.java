package fr.esgi.projetannuel.exception;

public class HeroInexistantExeption extends RuntimeException {
    public HeroInexistantExeption(String message) {
        super(message);
    }
    public HeroInexistantExeption() {super();}
}
