package com.example.smartridetrajetservice.dto;

import com.example.smartridetrajetservice.model.StatutTrajet;
import com.example.smartridetrajetservice.model.TypeTrajet;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrajetResponseDTO {

    private Long id;
    private String adresseDepart;
    private String adresseArrivee;
    private Double latitudeDepart;
    private Double longitudeDepart;
    private Double latitudeArrivee;
    private Double longitudeArrivee;
    private Long passagerId;
    private Long chauffeurId;
    private StatutTrajet statut;
    private TypeTrajet type;
    private Double distanceKm;
    private Double prixEstime;
    private LocalDateTime dateCreation;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
}
