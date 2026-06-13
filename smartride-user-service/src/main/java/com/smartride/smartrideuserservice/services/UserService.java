package com.smartride.smartrideuserservice.services;

import com.smartride.smartrideuserservice.keycloak.KeycloakAdminService;
import com.smartride.smartrideuserservice.models.User;
import com.smartride.smartrideuserservice.models.UserRole;
import com.smartride.smartrideuserservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final KeycloakAdminService keycloakAdminService;

    public User register(User user) {
        User saved = userRepository.save(user);
        // Provisionne aussi le compte dans Keycloak pour permettre la connexion via Keycloak
        try {
            String role = user.getRole() != null ? user.getRole().name() : UserRole.CLIENT.name();
            keycloakAdminService.createUser(
                    user.getEmail(),   // username = email
                    user.getEmail(),
                    user.getPrenom(),
                    user.getNom(),
                    user.getPassword(),
                    role
            );
        } catch (Exception e) {
            // L'utilisateur local est créé même si Keycloak est indisponible ; on logue l'échec.
            log.error("[Keycloak] Echec de creation de l'utilisateur '{}' dans Keycloak : {}",
                    user.getEmail(), e.getMessage());
        }
        return saved;
    }

    public Optional<User> login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(u -> u.getPassword().equals(password));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User updateUser(Long id, User user) {
        user.setId(id);
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}