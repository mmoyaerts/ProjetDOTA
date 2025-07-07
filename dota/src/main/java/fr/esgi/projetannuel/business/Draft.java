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
@Table(name = "draft")
public class Draft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_draft", unique = true)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "id_matches")
    public Match match;

    @ManyToOne
    @JoinColumn(name = "id_heroes")
    public Hero hero;

    @Column(name = "is_pick")
    public Boolean isPick;

    @Column(name = "is_radiant")
    public Boolean isRadiant;
}