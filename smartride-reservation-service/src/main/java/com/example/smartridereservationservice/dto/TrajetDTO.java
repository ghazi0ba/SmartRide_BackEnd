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

    private Integer nombrePlaces;

    private Integer placesDisponibles;

    private Double prix;

    private LocalDateTime dateCreation;
}
