package com.smartride.smartride_payment_service.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Événement publié quand un paiement est traité. Consommé par reservation-service. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompletedEvent {
    private String reservationId;
    private String status;
    private Double amount;
}
