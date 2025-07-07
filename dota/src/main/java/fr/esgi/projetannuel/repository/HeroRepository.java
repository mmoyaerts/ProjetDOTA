package fr.esgi.projetannuel.repository;

import fr.esgi.projetannuel.business.Hero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeroRepository extends JpaRepository<Hero, Long> {

}
