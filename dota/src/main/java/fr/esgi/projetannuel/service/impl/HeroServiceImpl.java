package fr.esgi.projetannuel.service.impl;

import fr.esgi.projetannuel.exception.HeroInexistantExeption;
import fr.esgi.projetannuel.business.Hero;
import fr.esgi.projetannuel.repository.HeroRepository;
import fr.esgi.projetannuel.service.HeroService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HeroServiceImpl implements HeroService {
    private HeroRepository heroRepository;

    public HeroServiceImpl(HeroRepository heroRepository) {
        this.heroRepository = heroRepository;
    }

    @Override
    public Hero recupererHero(Long idHero) {
        return heroRepository.findById(idHero).orElseThrow(
                () -> new HeroInexistantExeption()
        );
    }

    @Override
    public List<Hero> recupererHeros() {
        return heroRepository.findAll();
    }
}
