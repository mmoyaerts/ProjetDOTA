package fr.esgi.projetannuel.repository;

import fr.esgi.projetannuel.business.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    Page<Match> findByInProgress(boolean b, Pageable pageable);
}
