package com.example.smartridetrajetservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "trajets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trajet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "L'adresse de départ est obligatoire")
    @Column(nullable = false)
    private String adresseDepart;

    @NotBlank(message = "L'adresse d'arrivée est obligatoire")
    @Column(nullable = false)
    private String adresseArrivee;

    @Column
    private Double latitudeDepart;

    @Column
    private Double longitudeDepart;

    @Column
    private Double latitudeArrivee;

    @Column
    private Double longitudeArrivee;

    @NotNull(message = "L'ID du passager est obligatoire")
    @Column(nullable = false)
    private Long passagerId;

    @Column
    private Long chauffeurId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutTrajet statut = StatutTrajet.EN_ATTENTE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeTrajet type;

    @Column
    private Double distanceKm;

    @Column
    private Double prixEstime;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column
    private LocalDateTime dateDebut;

    @Column
    private LocalDateTime dateFin;

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
    }
}
