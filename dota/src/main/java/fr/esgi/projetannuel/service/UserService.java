package fr.esgi.projetannuel.service;

import fr.esgi.projetannuel.business.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    /**
     * Cette méthode renvoie une page d'Users car elle a en paramètre
     * une demande de page (pageable
     * @return
     */
    Page<User> recupererUsers(Pageable pageable);
    User recupererUser(Long id);
    User ajouterUser(User user);
    //User patchUser(Long id, User user);
    void supprimerUser(Long id);
}
