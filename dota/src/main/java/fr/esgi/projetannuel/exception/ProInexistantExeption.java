package fr.esgi.projetannuel.exception;

public class ProInexistantExeption extends RuntimeException {
  public ProInexistantExeption(String message) {
    super(message);
  }
  public ProInexistantExeption() {super();}
}
