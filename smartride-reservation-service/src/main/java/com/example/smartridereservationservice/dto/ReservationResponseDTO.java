package com.example.smartridereservationservice.dto;

import com.example.smartridereservationservice.model.ReservationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponseDTO {

    private String reservationId;

    private Long userId;

    private Long driverId;

    private Long trajetId;

    private LocalDateTime dateReservation;

    private Integer nombrePassagers;

    private Double prixTotal;

    private ReservationStatus status;

    private LocalDateTime dateCreation;

    private LocalDateTime dateModification;

    private LocalDateTime delaiAnnulationLimite;

    private String message;
}
