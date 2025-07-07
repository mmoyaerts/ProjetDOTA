package fr.esgi.projetannuel.repository;

import fr.esgi.projetannuel.business.Draft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DraftRepository extends JpaRepository<Draft, Long> {

}

