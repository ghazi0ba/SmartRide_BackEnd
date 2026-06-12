package com.smartride.smartride_payment_service.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Copie locale de l'événement émis par reservation-service (mappé par nom de champ). */
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
