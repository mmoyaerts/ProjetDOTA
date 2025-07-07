package fr.esgi.projetannuel.business;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class PlayerMatchId implements Serializable {
    @Column(name = "id_heroes")
    private Long heroId;

    @Column(name = "id_matches")
    private Long matchId;

    @Column(name = "id_steam")
    private Long steamId;
}

