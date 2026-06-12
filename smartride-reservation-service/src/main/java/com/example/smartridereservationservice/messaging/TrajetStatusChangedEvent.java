package com.example.smartridereservationservice.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Copie locale de l'événement émis par trajet-service (mappé par nom de champ). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrajetStatusChangedEvent {
    private Long trajetId;
    private String statut;
}
