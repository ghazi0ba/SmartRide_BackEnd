package com.example.smartridereservationservice.messaging;

import com.example.smartridereservationservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationEventListener {

    private final ReservationService reservationService;

    /** Scénario 1 : trajet annulé → annuler les réservations liées. */
    @RabbitListener(queues = RabbitMQConfig.Q_TRAJET_STATUS)
    public void onTrajetStatusChanged(TrajetStatusChangedEvent event) {
        log.info("[RabbitMQ ←] TrajetStatusChangedEvent reçu (trajetId={}, statut={})",
                event.getTrajetId(), event.getStatut());
        if ("ANNULE".equalsIgnoreCase(event.getStatut())) {
            reservationService.cancelReservationsForTrajet(event.getTrajetId());
        }
    }

    /** Scénario 3 : paiement abouti → marquer la réservation comme payée. */
    @RabbitListener(queues = RabbitMQConfig.Q_PAYMENT_COMPLETED)
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        log.info("[RabbitMQ ←] PaymentCompletedEvent reçu (reservationId={}, status={})",
                event.getReservationId(), event.getStatus());
        if ("SUCCESS".equalsIgnoreCase(event.getStatus())) {
            reservationService.markReservationAsPaid(event.getReservationId());
        }
    }
}
