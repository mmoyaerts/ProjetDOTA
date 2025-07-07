package fr.esgi.projetannuel.repository;

import fr.esgi.projetannuel.business.GameMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameModeRepository extends JpaRepository<GameMode, Long> {

}
