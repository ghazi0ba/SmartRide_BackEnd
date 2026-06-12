package com.example.smartridereservationservice.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Copie locale de l'événement émis par payment-service (mappé par nom de champ). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompletedEvent {
    private String reservationId;
    private String status;
    private Double amount;
}
