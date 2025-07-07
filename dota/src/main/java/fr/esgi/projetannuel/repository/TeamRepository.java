package fr.esgi.projetannuel.repository;

import fr.esgi.projetannuel.business.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

}
