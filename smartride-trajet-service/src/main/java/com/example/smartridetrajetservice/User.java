package com.example.smartridetrajetservice;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;

    private String nom;
    private String prenom;
    private String email;



}
