package fr.esgi.projetannuel.controller.rest;


import fr.esgi.projetannuel.business.Hero;
import fr.esgi.projetannuel.service.HeroService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/heroes")
public class HeroController {
    private HeroService heroService;
    @GetMapping("/")
    @Operation(summary="Récupère liste d'heros")
    public List<Hero> getHeros() {
        return heroService.recupererHeros();
    }
    @GetMapping("/{id}")
    @Operation(summary="Récupère un hero")
    public Hero getHero(Long id) {
        return heroService.recupererHero(id);
    }
}
