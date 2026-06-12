package esprit.driver.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Écouteur des événements de réservation côté driver-service.
 *
 * Scénario 4 : une nouvelle réservation est créée → driver-service est notifié
 * (alimentation du flux des réservations disponibles pour les chauffeurs).
 * NB : à la création, la réservation n'a pas encore de chauffeur assigné — il
 * s'agit donc d'une diffusion, pas d'une notification ciblée.
 */
@Component
@Slf4j
public class ReservationEventListener {

    @RabbitListener(queues = RabbitMQConfig.Q_RESERVATION_CREATED)
    public void onReservationCreated(ReservationCreatedEvent event) {
        log.info("[RabbitMQ ←] ReservationCreatedEvent reçu (reservationId={}, driverId={}, trajetId={}, passagers={}, prix={}) — notification chauffeur",
                event.getReservationId(), event.getDriverId(), event.getTrajetId(), event.getNombrePassagers(), event.getPrixTotal());
    }

    /** Scénario 5 : une réservation est annulée → retirer du flux des chauffeurs. */
    @RabbitListener(queues = RabbitMQConfig.Q_RESERVATION_CANCELLED)
    public void onReservationCancelled(ReservationCancelledEvent event) {
        log.info("[RabbitMQ ←] ReservationCancelledEvent reçu (reservationId={}, trajetId={}, passagers={}) — mise à jour flux chauffeur",
                event.getReservationId(), event.getTrajetId(), event.getNombrePassagers());
    }
}
