package com.smartride.smartrideuserservice.services;

import com.smartride.smartrideuserservice.feign.TrajetClient;
import com.smartride.smartrideuserservice.models.Trajet;
import com.smartride.smartrideuserservice.models.User;
import com.smartride.smartrideuserservice.models.UserWithTrajets;
import com.smartride.smartrideuserservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TrajetClient trajetClient;

    // Inscription
    public User register(User user) {
        return userRepository.save(user);
    }

    // Connexion
    public Optional<User> login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(u -> u.getPassword().equals(password));
    }

    // Tous les users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // User par ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Modifier user
    public User updateUser(Long id, User user) {
        user.setId(id);
        return userRepository.save(user);
    }

    // Supprimer user
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Tous les trajets via Feign
    public List<Trajet> getAllTrajets() {
        return trajetClient.getAllTrajets();
    }

    // Trajet par ID via Feign
    public Trajet getTrajetById(Long id) {
        return trajetClient.getTrajetById(id);
    }

    // User avec ses trajets
    public UserWithTrajets getUserWithTrajets(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Trajet> trajets = trajetClient.getTrajetsByPassager(id);
        return new UserWithTrajets(
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getRole(),
                trajets
        );
    }
}