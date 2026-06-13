package esprit.driver.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Événement reçu quand une réservation est créée (publié par reservation-service).
 * Réplique locale pour permettre la désérialisation JSON côté driver-service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationCreatedEvent {
    private String reservationId;
    private Long userId;
    private Long driverId;
    private Long trajetId;
    private Integer nombrePassagers;
    private Double prixTotal;
}
