package fr.esgi.projetannuel.repository;

import fr.esgi.projetannuel.business.PlayerMatch;
import fr.esgi.projetannuel.business.PlayerMatchId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerMatchRepository 
    extends JpaRepository<PlayerMatch, PlayerMatchId> {

    // Méthode Spring Data JPA : join sur player_match.pro.steamId
    Page<PlayerMatch> findByPro_IdSteam(Long steamId, Pageable pageable);
}
