package fr.esgi.projetannuel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link fr.clelia.avis.business.Plateforme}
 */
@Value
public class PlateformeDto implements Serializable {
    Long id;
    @Size(message = "Le nom de la plateforme doit comporter au moins {min} caractères", min = 1)
    @NotBlank(message = "Merci de préciser le nom de la plateforme")
    String nom;
}