package fr.esgi.projetannuel.service.impl;

import fr.esgi.projetannuel.exception.UserInexistantExeption;
import fr.esgi.projetannuel.business.User;
import fr.esgi.projetannuel.repository.UserRepository;
import fr.esgi.projetannuel.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Page<User> recupererUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User recupererUser(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new UserInexistantExeption()
        );
    }

    @Override
    public User ajouterUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void supprimerUser(Long id) {
        User user = recupererUser(id);
        userRepository.delete(user);
    }
}
