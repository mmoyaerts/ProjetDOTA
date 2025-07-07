package fr.esgi.projetannuel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link fr.clelia.avis.business.Jeu}
 */
@Value
public class JeuDto implements Serializable {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;
    @Size(message = "Le nom du jeu doit comporter au moins {min} caractères", min = 1)
    @NotBlank(message = "Merci de préciser le nom du jeu")
    String nom;
    LocalDate dateDeSortie;
    String description;
    EditeurDto editeur;
    List<PlateformeDto> plateformes;
    boolean possedeImage;
}