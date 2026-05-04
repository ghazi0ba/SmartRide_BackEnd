package com.example.smartridereservationservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Document(collection = "reservation_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationHistory {

    @Id
    private String historyId;

    @Indexed
    private Long userId;

    private String reservationId;

    private Long trajetId;

    private ReservationStatus status;

    private LocalDateTime dateReservation;

    private LocalDateTime dateCompletion;

    private Double prixTotal;

    private LocalDateTime dateCreation;
}
