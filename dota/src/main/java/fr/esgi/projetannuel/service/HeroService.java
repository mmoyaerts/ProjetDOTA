package fr.esgi.projetannuel.service;

import fr.esgi.projetannuel.business.Hero;

import java.util.List;

public interface HeroService {
    Hero recupererHero(Long id);

    List<Hero> recupererHeros();
}
