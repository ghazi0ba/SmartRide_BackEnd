package com.smartride.smartrideuserservice.models;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trajet {
    private Long id;
    private String adresseDepart;
    private String adresseArrivee;
    private Double latitudeDepart;
    private Double longitudeDepart;
    private Double latitudeArrivee;
    private Double longitudeArrivee;
    private Long passagerId;
    private Long chauffeurId;
    private String statut;
    private String type;
    private Double distanceKm;
    private Double prixEstime;
    private LocalDateTime dateCreation;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
}