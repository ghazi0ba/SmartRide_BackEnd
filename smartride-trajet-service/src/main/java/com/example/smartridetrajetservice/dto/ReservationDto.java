package com.example.smartridetrajetservice.dto;


import lombok.*;

import java.time.LocalDateTime;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class ReservationDto {

        private String reservationId;

        private Long userId;

        private Long driverId;

        private Long trajetId;

        private LocalDateTime dateReservation;

        private Integer nombrePassagers;

        private Double prixTotal;

        private String status;

        private LocalDateTime dateCreation;

        private LocalDateTime dateModification;

        private LocalDateTime delaiAnnulationLimite;
    }



