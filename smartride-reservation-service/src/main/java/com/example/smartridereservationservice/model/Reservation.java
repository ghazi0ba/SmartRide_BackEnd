package com.example.smartridereservationservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Document(collection = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    private String reservationId;

    @Indexed
    private Long userId;

    private Long driverId;

    @Indexed
    private Long trajetId;

    private LocalDateTime dateReservation;

    private Integer nombrePassagers;

    private Double prixTotal;

    private ReservationStatus status;

    private boolean paid;

    private LocalDateTime dateCreation;

    private LocalDateTime dateModification;

    private LocalDateTime delaiAnnulationLimite;
}
