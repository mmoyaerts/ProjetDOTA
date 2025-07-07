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
@Table(name = "pro")
public class Pro {

    @Id
    @Column(name = "id_steam", unique = true)
    private Long idSteam;

    @Column(length = 50)
    private String pseudo;

    @ManyToOne
    @JoinColumn(name = "id_team", nullable = false)
    private Team team;
}

