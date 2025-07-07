package fr.esgi.projetannuel.repository;

import fr.esgi.projetannuel.business.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
