package fr.esgi.projetannuel.controller.rest;

import fr.esgi.projetannuel.business.User;
import fr.esgi.projetannuel.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    private final PasswordEncoder passwordEncoder;

    public UserController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("")
    @Operation(summary="Récupère une page d'utilisateurs")
    public Page<User> getUsers(@PageableDefault(size = 10, sort = "username", direction = Sort.Direction.DESC) Pageable pageable) {
        return userService.recupererUsers(pageable);
    }
    @GetMapping("/{id}")
    @Operation(summary="Récupère un utilisateur par son id")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.recupererUser(id));
    }
    @PostMapping
    @Operation(summary="Ajoute un nouveau utilisateur")
    @ResponseStatus(code = HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        //return userService.ajouterUser(user);
        // Appliquer les valeurs par défaut
        String hashedPassword = passwordEncoder.encode(user.getPasswordHash());
        user.setPasswordHash(hashedPassword);
        user.setRole("user");
        user.setCreatedAt(LocalDate.now());
        return userService.ajouterUser(user);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprime un user existant")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimerUser(@PathVariable Long id) {
        // Le service va potentiellement lever l'exception maison
        // JeuInexistantException
        // (c'est un des rôles du service que de lever des exceptions maison)
        userService.supprimerUser(id);
    }
}
