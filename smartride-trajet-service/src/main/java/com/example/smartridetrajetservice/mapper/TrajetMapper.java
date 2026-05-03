package com.example.smartridetrajetservice.mapper;

import com.example.smartridetrajetservice.dto.TrajetRequestDTO;
import com.example.smartridetrajetservice.dto.TrajetResponseDTO;
import com.example.smartridetrajetservice.model.Trajet;
import org.springframework.stereotype.Component;

@Component
public class TrajetMapper {

    public Trajet toEntity(TrajetRequestDTO dto) {
        return Trajet.builder()
                .adresseDepart(dto.getAdresseDepart())
                .adresseArrivee(dto.getAdresseArrivee())
                .latitudeDepart(dto.getLatitudeDepart())
                .longitudeDepart(dto.getLongitudeDepart())
                .latitudeArrivee(dto.getLatitudeArrivee())
                .longitudeArrivee(dto.getLongitudeArrivee())
                .passagerId(dto.getPassagerId())
                .type(dto.getType())
                .build();
    }

    public TrajetResponseDTO toDTO(Trajet trajet) {
        return TrajetResponseDTO.builder()
                .id(trajet.getId())
                .adresseDepart(trajet.getAdresseDepart())
                .adresseArrivee(trajet.getAdresseArrivee())
                .latitudeDepart(trajet.getLatitudeDepart())
                .longitudeDepart(trajet.getLongitudeDepart())
                .latitudeArrivee(trajet.getLatitudeArrivee())
                .longitudeArrivee(trajet.getLongitudeArrivee())
                .passagerId(trajet.getPassagerId())
                .chauffeurId(trajet.getChauffeurId())
                .statut(trajet.getStatut())
                .type(trajet.getType())
                .distanceKm(trajet.getDistanceKm())
                .prixEstime(trajet.getPrixEstime())
                .dateCreation(trajet.getDateCreation())
                .dateDebut(trajet.getDateDebut())
                .dateFin(trajet.getDateFin())
                .build();
    }
}
