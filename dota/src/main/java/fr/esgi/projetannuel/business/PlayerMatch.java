package fr.esgi.projetannuel.business;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "player_match")
public class PlayerMatch {

    @EmbeddedId
    private PlayerMatchId id;

    @ManyToOne
    @MapsId("heroId")
    @JoinColumn(name = "id_heroes")
    private Hero hero;

    @ManyToOne
    @MapsId("matchId")
    @JoinColumn(name = "id_matches")
    private Match match;

    @ManyToOne
    @MapsId("steamId")
    @JoinColumn(name = "id_steam")
    private Pro pro;

    private Boolean win;
    private Integer kill;
    private Integer deaths;
    private Integer assists;
}
