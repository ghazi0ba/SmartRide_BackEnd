package com.example.smartridereservationservice.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
