package com.example.smartridereservationservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequestDTO {

    @NotNull(message = "L'ID utilisateur est obligatoire")
    private Long userId;

    @NotNull(message = "L'ID trajet est obligatoire")
    private Long trajetId;

    @NotNull(message = "Le nombre de passagers est obligatoire")
    @Positive(message = "Le nombre de passagers doit être supérieur à 0")
    private Integer nombrePassagers;
}
