package fr.esgi.projetannuel.service.impl;

import fr.esgi.projetannuel.exception.MatchInexistantExeption;
import fr.esgi.projetannuel.business.Match;
import fr.esgi.projetannuel.repository.MatchRepository;
import fr.esgi.projetannuel.service.MatchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MatchServiceImpl implements MatchService {
    private MatchRepository matchRepository;

    public MatchServiceImpl(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Override
    public Page<Match> recupererMatches(Pageable pageable) {
        return matchRepository.findAll(pageable);
    }

    @Override
    public Match recupererMatch(Long idMatch) {
        return matchRepository.findById(idMatch).orElseThrow(
                () -> new MatchInexistantExeption()
        );
    }

    @Override
    public Match ajouterMatch(Match match) {
        return matchRepository.save(match);
    }

    @Override
    public void supprimerMatch(Long id) {
        Match match = recupererMatch(id);
        matchRepository.delete(match);
    }

    @Override
    public Page<Match> recupererMatchesEnCours(Pageable pageable) {return matchRepository.findByInProgress(true, pageable);}

  
}
