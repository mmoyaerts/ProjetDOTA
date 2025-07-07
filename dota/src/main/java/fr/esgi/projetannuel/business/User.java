package fr.esgi.projetannuel.business;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_users", unique = true)
    public Long id;

    @Column(length = 50)
    public String username;

    @Column(length = 100, unique = true)
    public String email;

    @Column(name = "password_hash", columnDefinition = "TEXT")
    private String passwordHash;

    @Column(length = 20)
    public String role;

    @Column(name="created_at")
    public java.time.LocalDate createdAt;
}
