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
@Table(name = "game_modes")
public class GameMode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_game_modes", unique = true)
    public Long id;

    @Column(length = 100)
    public String name;
}
