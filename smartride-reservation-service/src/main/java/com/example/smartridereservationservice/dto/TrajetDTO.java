package com.example.smartridereservationservice.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrajetDTO {

    private Long id;

    private String adresseDepart;

    private String adresseArrivee;

    private Double latitudeDepart;

    private Double longitudeDepart;

    private Double latitudeArrivee;

    private Double longitudeArrivee;

    private Long passagerId;

    private Long chauffeurId;

    private String type;

    private String statut;

    private Double distanceKm;

    // Tarif estimé du trajet, tel que renvoyé par le trajet-service.
    private Double prixEstime;

    private LocalDateTime dateCreation;
}
