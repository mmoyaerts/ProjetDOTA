package fr.esgi.projetannuel.initialisation;

import fr.esgi.projetannuel.business.Hero;
import fr.esgi.projetannuel.business.Pro;
import fr.esgi.projetannuel.repository.GameModeRepository;
import fr.esgi.projetannuel.repository.HeroRepository;
import fr.esgi.projetannuel.repository.ProRepository;
import fr.esgi.projetannuel.repository.TeamRepository;
import fr.esgi.projetannuel.service.HeroService;
import fr.esgi.projetannuel.service.ProService;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
@Transactional
public class AjoutDonneesInitiale {
    private HeroRepository heroRepository;
    private ProRepository proRepository;
    private TeamRepository teamRepository;
    private GameModeRepository gameModeRepository;

    private ProService proService;

    // @Override
    @EventListener(ApplicationReadyEvent.class)
    // public void run(String... args) throws Exception {
    public void init() {
        try {
            Pro pro = proService.recupererPro(365939353L);
            System.out.println("Pro trouvé : " + pro.getPseudo());
        } catch (Exception e) {
            System.out.println("Une erreur est survenue : " + e.getMessage());
        }
    }

    private void ajouterPro() {
        System.out.println("=== Récupération des pro depuis la base ===");

        var pros = proRepository.findAll();

        if(pros.isEmpty()) {
            System.out.println("Aucun pro trouvé en base.");
        } else {
            pros.forEach(pro -> System.out.println(pro.getPseudo())); // ou toString() selon ton entit
        }
    }

    private void ajouterTeam() {
        System.out.println("=== Récupération des Team depuis la base ===");

        var teams = teamRepository.findAll();

        if(teams.isEmpty()) {
            System.out.println("Aucune teams trouvé en base.");
        } else {
            teams.forEach(team -> System.out.println(team.getName())); // ou toString() selon ton entit
        }
    }

    private void ajouterGameMode() {
        System.out.println("=== Récupération des gameMode depuis la base ===");

        var gameModes = gameModeRepository.findAll();

        if(gameModes.isEmpty()) {
            System.out.println("Aucun héros trouvé en base.");
        } else {
            gameModes.forEach(gameMode -> System.out.println(gameMode.getName())); // ou toString() selon ton entit
        }
    }

    private void ajouterHero() {
        System.out.println("=== Récupération des héros depuis la base ===");

        var heros = heroRepository.findAll();

        if (heros.isEmpty()) {
            System.out.println("Aucun héros trouvé en base.");
        } else {
            heros.forEach(hero -> System.out.println(hero.getName())); // ou toString() selon ton entité
        }
    }

}
