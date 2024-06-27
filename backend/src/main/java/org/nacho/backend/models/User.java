package org.nacho.backend.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="usuarios")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="nombre", nullable = false, unique = true)
    private String username;

    @Column(name="contraseña", nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    public User(String username, String password, String email) {
        this.username=username;
        this.password=password;
        this.email=email;
    }
}
