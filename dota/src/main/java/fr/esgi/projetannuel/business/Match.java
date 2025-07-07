package fr.esgi.projetannuel.business;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "matches")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_matches", unique = true)
    public Long id;

    @Column(name = "start_time")
    public LocalDateTime startTime;

    public Long duration;

    @Column(name = "in_progress")
    public Boolean inProgress;

    @ManyToOne
    @JoinColumn(name = "id_game_modes", nullable = false)
    public GameMode gameMode;

    @OneToMany(mappedBy = "match")
    public List<Draft> drafts;

    @OneToMany(mappedBy = "match")
    public List<PlayerMatch> playerMatches;
}