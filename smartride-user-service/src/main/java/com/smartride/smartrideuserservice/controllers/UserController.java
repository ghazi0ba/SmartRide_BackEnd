package com.smartride.smartrideuserservice.controllers;

import com.smartride.smartrideuserservice.models.Trajet;
import com.smartride.smartrideuserservice.models.User;
import com.smartride.smartrideuserservice.models.UserWithTrajets;
import com.smartride.smartrideuserservice.security.JwtService;
import com.smartride.smartrideuserservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        return userService.login(email, password)
                .map(u -> {
                    String token = jwtService.generateToken(u.getEmail(), u.getRole().name());
                    return ResponseEntity.ok(Map.of(
                            "token", token,
                            "email", u.getEmail(),
                            "role", u.getRole(),
                            "nom", u.getNom(),
                            "prenom", u.getPrenom()
                    ));
                })
                .orElse(ResponseEntity.status(401).build());
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(u -> ResponseEntity.ok(u))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(u -> {
                    userService.deleteUser(id);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/trajets")
    public ResponseEntity<List<Trajet>> getAllTrajets() {
        return ResponseEntity.ok(userService.getAllTrajets());
    }

    @GetMapping("/trajets/{id}")
    public ResponseEntity<Trajet> getTrajetById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getTrajetById(id));
    }

    // User avec ses trajets ← NOUVEAU
    @GetMapping("/{id}/trajets")
    public ResponseEntity<UserWithTrajets> getUserWithTrajets(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserWithTrajets(id));
    }
}