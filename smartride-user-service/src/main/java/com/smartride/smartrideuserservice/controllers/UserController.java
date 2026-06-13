package com.smartride.smartrideuserservice.controllers;

import com.smartride.smartrideuserservice.models.User;
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
    private final JwtService jwtService;  // ← ajoutez ceci
    

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.register(user));
    }

   // Connexion avec JWT
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

    @GetMapping("/email/{email}")
    public ResponseEntity<Map<String, Object>> getUserByEmail(@PathVariable String email) {
        // Projection sans mot de passe : utilisée par le front pour résoudre l'id de l'utilisateur connecté.
        return userService.getUserByEmail(email)
                .map(u -> ResponseEntity.ok(Map.of(
                        "id", (Object) u.getId(),
                        "nom", u.getNom(),
                        "prenom", u.getPrenom(),
                        "email", u.getEmail(),
                        "role", u.getRole().name()
                )))
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
}