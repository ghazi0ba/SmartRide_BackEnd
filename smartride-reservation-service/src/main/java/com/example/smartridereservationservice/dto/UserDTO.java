package com.example.smartridereservationservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;

    private String nom;

    private String prenom;

    private String email;

    private String telephone;

    private String role;
}
