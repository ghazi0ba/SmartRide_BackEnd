package com.example.smartridetrajetservice.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Événement publié quand un trajet est terminé.
 * Consommé par rating-service (NestJS) qui crée un avis "en attente de notation".
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrajetTerminatedEvent {
    private Long userId;       // = passagerId du trajet
    private Long chauffeurId;
    private Long trajetId;
}
