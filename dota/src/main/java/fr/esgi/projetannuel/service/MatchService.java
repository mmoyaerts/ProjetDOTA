package fr.esgi.projetannuel.service;

import fr.esgi.projetannuel.business.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MatchService {
    /**
     * Cette méthode renvoie une page de Matchs car elle a en paramètre
     * une demande de page (pageable
     * @return
     */
    Page<Match> recupererMatches(Pageable pageable);
    Page<Match> recupererMatchesEnCours(Pageable pageable);
    Match recupererMatch(Long id);
    Match ajouterMatch(Match match);
    //Match ajouterMatch(MatchDto match);
    //MatchDto patchMatch(Long id, MatchDto match);
    void supprimerMatch(Long id);
}
