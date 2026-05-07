package com.example.smartridetrajetservice.dto;

import com.example.smartridetrajetservice.model.TypeTrajet;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrajetRequestDTO {

    @NotBlank(message = "L'adresse de départ est obligatoire")
    private String adresseDepart;

    @NotBlank(message = "L'adresse d'arrivée est obligatoire")
    private String adresseArrivee;

    private Double latitudeDepart;
    private Double longitudeDepart;
    private Double latitudeArrivee;
    private Double longitudeArrivee;

    @NotNull(message = "L'ID du passager est obligatoire")
    private Long passagerId;

    @NotNull(message = "Le type de trajet est obligatoire")
    private TypeTrajet type;
}
