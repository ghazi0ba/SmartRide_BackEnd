package com.example.smartridereservationservice.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Événement publié quand une réservation est confirmée.
 * Consommé par payment-service (qui crée le paiement).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationConfirmedEvent {
    private String reservationId;
    private Long userId;
    private Long driverId;
    private Long trajetId;
    private Double montant;
}
