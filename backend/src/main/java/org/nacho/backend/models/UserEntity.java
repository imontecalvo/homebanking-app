package org.nacho.backend.models;

import jakarta.persistence.*;
import lombok.*;
import org.nacho.backend.models.roles_authorities.Role;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="usuarios")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="nombre", nullable = false, unique = true)
    private String username;

    @Column(name="contraseña", nullable = false)
    private String password;

/*    @Column(unique = true, nullable = false)
    private String email;*/

    @Column(name = "is_enabled")
    @Builder.Default
    private boolean isEnabled = true;

    @Column(name = "account_non_expired")
    @Builder.Default
    private boolean isAccountNonExpired = true;

    @Column(name = "account_non_locked")
    @Builder.Default
    private boolean isAccountNonLocked = true;

    @Column(name = "credentials_non_expired")
    @Builder.Default
    private boolean isCredentialsNonExpired = true;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();
}
