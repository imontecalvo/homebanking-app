package org.nacho.backend;

import org.nacho.backend.models.roles_authorities.Permission;
import org.nacho.backend.models.roles_authorities.Role;
import org.nacho.backend.models.roles_authorities.RoleEnum;
import org.nacho.backend.repositories.IRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    @Profile("!test")
    CommandLineRunner init(IRoleRepository roleRepository) {
        return args -> {
            if (roleRepository.findAll().isEmpty()){
                Permission readPermission = Permission.builder().name("READ").build();
                Permission exchangePermission = Permission.builder().name("EXCHANGE").build();
                Permission transferPermission = Permission.builder().name("TRANSFER").build();
                Permission withdrawPermission = Permission.builder().name("WITHDRAW").build();
                Permission depositPermission = Permission.builder().name("DEPOSIT").build();
                Permission deleteTransactionPermission = Permission.builder().name("DELETE_TRANSACTION").build();
                Permission deleteUserPermission = Permission.builder().name("DELETE_USER").build();

                Role userRole = Role.builder()
                        .name(RoleEnum.USER)
                        .permissions(Set.of(
                                readPermission,
                                exchangePermission,
                                transferPermission,
                                withdrawPermission,
                                depositPermission
                        ))
                        .build();

                Role adminRole = Role.builder()
                        .name(RoleEnum.ADMIN)
                        .permissions(Set.of(
                                readPermission,
                                deleteUserPermission,
                                deleteTransactionPermission
                        ))
                        .build();

                Role invitedRole = Role.builder()
                        .name(RoleEnum.INVITED)
                        .permissions(Set.of(readPermission))
                        .build();

                roleRepository.saveAll(List.of(userRole, adminRole, invitedRole));
            }
        };
    }
}
