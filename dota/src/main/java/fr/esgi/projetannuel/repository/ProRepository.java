package fr.esgi.projetannuel.repository;

import fr.esgi.projetannuel.business.Pro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProRepository extends JpaRepository<Pro, Long> {

}
