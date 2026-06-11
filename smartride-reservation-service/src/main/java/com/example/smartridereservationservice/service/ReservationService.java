package com.example.smartridereservationservice.service;

import com.example.smartridereservationservice.client.TrajetClient;
import com.example.smartridereservationservice.client.UserClient;
import com.example.smartridereservationservice.dto.ReservationRequestDTO;
import com.example.smartridereservationservice.dto.ReservationResponseDTO;
import com.example.smartridereservationservice.dto.TrajetDTO;
import com.example.smartridereservationservice.dto.UserDTO;
import com.example.smartridereservationservice.exception.*;
import com.example.smartridereservationservice.model.Reservation;
import com.example.smartridereservationservice.model.ReservationHistory;
import com.example.smartridereservationservice.model.ReservationStatus;
import com.example.smartridereservationservice.repository.ReservationHistoryRepository;
import com.example.smartridereservationservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationHistoryRepository historyRepository;
    private final UserClient userClient;
    private final TrajetClient trajetClient;
    private final com.example.smartridereservationservice.messaging.ReservationEventPublisher reservationEventPublisher;

    private static final int DELAI_ANNULATION_MINUTES = 30;

    /**
     * Créer une nouvelle réservation avec validations
     */
    public ReservationResponseDTO creerReservation(ReservationRequestDTO requestDTO) {
        log.info("Création d'une réservation pour l'utilisateur: {}", requestDTO.getUserId());

        // Validation 1: Vérifier que l'utilisateur existe
        UserDTO user = validateUser(requestDTO.getUserId());
        log.info("Utilisateur validé: {}", user.getId());

        // Validation 2: Vérifier que le trajet existe et est disponible
        TrajetDTO trajet = validateTrajet(requestDTO.getTrajetId());
        log.info("Trajet validé: {}", trajet.getId());

        // Validation 3: Vérifier le nombre de passagers
        validateNombrePassagers(requestDTO.getNombrePassagers(), trajet.getPlacesDisponibles());
        log.info("Nombre de passagers validé");

        // Validation 4: Empêcher les double réservations
        validateDoublesReservations(requestDTO.getUserId(), requestDTO.getTrajetId());
        log.info("Vérification des doubles réservations complétée");

        // Créer la réservation
        Reservation reservation = Reservation.builder()
                .userId(requestDTO.getUserId())
                .trajetId(requestDTO.getTrajetId())
                .nombrePassagers(requestDTO.getNombrePassagers())
                .status(ReservationStatus.PENDING)
                .dateReservation(LocalDateTime.now())
                .dateCreation(LocalDateTime.now())
                .dateModification(LocalDateTime.now())
                .prixTotal(trajet.getPrix() * requestDTO.getNombrePassagers())
                .delaiAnnulationLimite(LocalDateTime.now().plusMinutes(DELAI_ANNULATION_MINUTES))
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);
        log.info("Réservation créée avec succès: {}", savedReservation.getReservationId());

        return mapToResponseDTO(savedReservation, "Réservation créée avec succès");
    }

    /**
     * Confirmer une réservation
     */
    public ReservationResponseDTO confirmerReservation(String reservationId) {
        log.info("Confirmation de la réservation: {}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Réservation non trouvée: " + reservationId));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new InvalidReservationException("Seules les réservations en attente peuvent être confirmées");
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setDateModification(LocalDateTime.now());

        Reservation updated = reservationRepository.save(reservation);
        log.info("Réservation confirmée: {}", reservationId);

        // Scénario 2 (async) : notifier payment-service de créer le paiement
        reservationEventPublisher.publishConfirmed(
                new com.example.smartridereservationservice.messaging.ReservationConfirmedEvent(
                        updated.getReservationId(),
                        updated.getUserId(),
                        updated.getDriverId(),
                        updated.getTrajetId(),
                        updated.getPrixTotal()
                )
        );

        return mapToResponseDTO(updated, "Réservation confirmée");
    }

    /**
     * Compléter une réservation
     */
    public ReservationResponseDTO completerReservation(String reservationId) {
        log.info("Complétion de la réservation: {}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Réservation non trouvée: " + reservationId));

        reservation.setStatus(ReservationStatus.COMPLETED);
        reservation.setDateModification(LocalDateTime.now());

        Reservation updated = reservationRepository.save(reservation);

        // Ajouter à l'historique
        ReservationHistory history = ReservationHistory.builder()
                .userId(reservation.getUserId())
                .reservationId(reservationId)
                .trajetId(reservation.getTrajetId())
                .status(ReservationStatus.COMPLETED)
                .dateReservation(reservation.getDateReservation())
                .dateCompletion(LocalDateTime.now())
                .prixTotal(reservation.getPrixTotal())
                .dateCreation(LocalDateTime.now())
                .build();

        historyRepository.save(history);
        log.info("Réservation complétée et ajoutée à l'historique: {}", reservationId);

        return mapToResponseDTO(updated, "Réservation complétée");
    }

    /**
     * Annuler une réservation avec vérification du délai
     */
    public ReservationResponseDTO annulerReservation(String reservationId) {
        log.info("Annulation de la réservation: {}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Réservation non trouvée: " + reservationId));

        // Vérifier le délai d'annulation
        if (LocalDateTime.now().isAfter(reservation.getDelaiAnnulationLimite())) {
            throw new InvalidReservationException("Le délai d'annulation a expiré. Vous ne pouvez plus annuler cette réservation.");
        }

        if (reservation.getStatus() == ReservationStatus.COMPLETED || reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new InvalidReservationException("Impossible d'annuler une réservation complétée ou déjà annulée");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setDateModification(LocalDateTime.now());

        Reservation updated = reservationRepository.save(reservation);

        // Ajouter à l'historique
        ReservationHistory history = ReservationHistory.builder()
                .userId(reservation.getUserId())
                .reservationId(reservationId)
                .trajetId(reservation.getTrajetId())
                .status(ReservationStatus.CANCELLED)
                .dateReservation(reservation.getDateReservation())
                .dateCompletion(LocalDateTime.now())
                .prixTotal(reservation.getPrixTotal())
                .dateCreation(LocalDateTime.now())
                .build();

        historyRepository.save(history);
        log.info("Réservation annulée: {}", reservationId);

        return mapToResponseDTO(updated, "Réservation annulée avec succès");
    }

    /**
     * Récupérer toutes les réservations d'un utilisateur
     */
    public List<ReservationResponseDTO> getReservationsByUser(Long userId) {
        log.info("Récupération des réservations de l'utilisateur: {}", userId);
        return reservationRepository.findByUserId(userId)
                .stream()
                .map(r -> mapToResponseDTO(r, ""))
                .collect(Collectors.toList());
    }

    public List<ReservationResponseDTO> getReservationsByDriver(Long driverId) {
        log.info("Récupération des réservations du chauffeur: {}", driverId);
        return reservationRepository.findByDriverId(driverId)
                .stream()
                .map(r -> mapToResponseDTO(r, ""))
                .collect(Collectors.toList());
    }

    /**
     * Scénario async 1 : annule toutes les réservations actives liées à un trajet annulé.
     */
    public void cancelReservationsForTrajet(Long trajetId) {
        List<Reservation> reservations = reservationRepository.findByTrajetId(trajetId);
        for (Reservation r : reservations) {
            if (r.getStatus() == ReservationStatus.PENDING || r.getStatus() == ReservationStatus.CONFIRMED) {
                r.setStatus(ReservationStatus.CANCELLED);
                r.setDateModification(LocalDateTime.now());
                reservationRepository.save(r);
                log.info("Réservation {} annulée suite à l'annulation du trajet {}", r.getReservationId(), trajetId);
            }
        }
    }

    /**
     * Scénario async 3 : marque une réservation comme payée après un paiement réussi.
     */
    public void markReservationAsPaid(String reservationId) {
        reservationRepository.findById(reservationId).ifPresent(r -> {
            r.setPaid(true);
            r.setDateModification(LocalDateTime.now());
            reservationRepository.save(r);
            log.info("Réservation {} marquée comme payée", reservationId);
        });
    }

    /**
     * Récupérer l'historique d'un utilisateur
     */
    public List<ReservationHistory> getHistoriqueUser(Long userId) {
        log.info("Récupération de l'historique de l'utilisateur: {}", userId);
        return historyRepository.findByUserId(userId);
    }

    /**
     * Récupérer une réservation par ID
     */
    public ReservationResponseDTO getReservationById(String reservationId) {
        log.info("Récupération de la réservation: {}", reservationId);
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Réservation non trouvée: " + reservationId));
        return mapToResponseDTO(reservation, "");
    }

    /**
     * Récupérer toutes les réservations pour un trajet
     */
    public List<ReservationResponseDTO> getReservationsByTrajet(Long trajetId) {
        log.info("Récupération des réservations pour le trajet: {}", trajetId);
        return reservationRepository.findByTrajetId(trajetId)
                .stream()
                .map(r -> mapToResponseDTO(r, ""))
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les réservations par statut
     */
    public List<ReservationResponseDTO> getReservationsByStatus(ReservationStatus status) {
        log.info("Récupération des réservations avec le statut: {}", status);
        return reservationRepository.findByStatus(status)
                .stream()
                .map(r -> mapToResponseDTO(r, ""))
                .collect(Collectors.toList());
    }

    /**
     * Validations métier
     */
    private UserDTO validateUser(Long userId) {
        try {
            UserDTO user = userClient.getUserById(userId);
            if (user == null) {
                throw new UserNotFoundException("Utilisateur non trouvé: " + userId);
            }
            return user;
        } catch (Exception e) {
            log.error("Erreur lors de la validation de l'utilisateur: {}", userId, e);
            throw new UserNotFoundException("Utilisateur non trouvé ou service indisponible: " + userId, e);
        }
    }

    private TrajetDTO validateTrajet(Long trajetId) {
        try {
            TrajetDTO trajet = trajetClient.getTrajetById(trajetId);
            if (trajet == null) {
                throw new TrajetNotFoundException("Trajet non trouvé: " + trajetId);
            }

            // Vérifier que le trajet est disponible
            if (!trajet.getStatut().equalsIgnoreCase("DISPONIBLE")) {
                throw new InvalidReservationException("Le trajet n'est pas disponible pour la réservation");
            }

            return trajet;
        } catch (Exception e) {
            log.error("Erreur lors de la validation du trajet: {}", trajetId, e);
            throw new TrajetNotFoundException("Trajet non trouvé ou service indisponible: " + trajetId, e);
        }
    }

    private void validateNombrePassagers(Integer nombrePassagers, Integer placesDisponibles) {
        if (nombrePassagers == null || nombrePassagers <= 0) {
            throw new InvalidReservationException("Le nombre de passagers doit être supérieur à 0");
        }

        if (nombrePassagers > placesDisponibles) {
            throw new InvalidReservationException(
                    String.format("Nombre de passagers invalide. Places disponibles: %d, demandé: %d",
                            placesDisponibles, nombrePassagers)
            );
        }
    }

    private void validateDoublesReservations(Long userId, Long trajetId) {
        Optional<Reservation> existingReservation = reservationRepository.findByUserIdAndTrajetId(userId, trajetId);

        if (existingReservation.isPresent()) {
            Reservation reservation = existingReservation.get();
            if (reservation.getStatus() != ReservationStatus.CANCELLED) {
                throw new InvalidReservationException(
                        "Vous avez déjà une réservation active pour ce trajet"
                );
            }
        }
    }

    private ReservationResponseDTO mapToResponseDTO(Reservation reservation, String message) {
        return ReservationResponseDTO.builder()
                .reservationId(reservation.getReservationId())
                .userId(reservation.getUserId())
                .driverId(reservation.getDriverId())
                .trajetId(reservation.getTrajetId())
                .dateReservation(reservation.getDateReservation())
                .nombrePassagers(reservation.getNombrePassagers())
                .prixTotal(reservation.getPrixTotal())
                .status(reservation.getStatus())
                .dateCreation(reservation.getDateCreation())
                .dateModification(reservation.getDateModification())
                .delaiAnnulationLimite(reservation.getDelaiAnnulationLimite())
                .message(message)
                .build();
    }
}
