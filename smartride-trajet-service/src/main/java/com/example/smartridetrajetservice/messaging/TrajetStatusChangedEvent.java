package com.example.smartridetrajetservice.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Événement publié quand le statut d'un trajet change (ACCEPTE, DEMARRE, TERMINE, ANNULE).
 * Consommé par reservation-service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrajetStatusChangedEvent {
    private Long trajetId;
    private String statut;
}
