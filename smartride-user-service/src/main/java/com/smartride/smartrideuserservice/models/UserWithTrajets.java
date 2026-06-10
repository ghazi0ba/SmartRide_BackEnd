package com.smartride.smartrideuserservice.models;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWithTrajets {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private UserRole role;
    private List<Trajet> trajets;
}