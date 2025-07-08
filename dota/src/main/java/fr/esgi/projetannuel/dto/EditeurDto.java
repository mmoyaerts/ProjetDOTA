package fr.esgi.projetannuel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link Editeur}
 */
@Value
public class EditeurDto implements Serializable {

    Long id;

    @Size(message = "Le nom de l'éditeur doit comporter au moins {min} caractères", min = 2)
    @NotBlank(message = "Merci de préciser le nom de l'éditeur")
    String nom;

    String logo;
}