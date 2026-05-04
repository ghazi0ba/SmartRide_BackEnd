package com.example.smartridereservationservice.controller;

import com.example.smartridereservationservice.dto.ReservationRequestDTO;
import com.example.smartridereservationservice.dto.ReservationResponseDTO;
import com.example.smartridereservationservice.model.ReservationHistory;
import com.example.smartridereservationservice.model.ReservationStatus;
import com.example.smartridereservationservice.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * POST /api/reservations — Créer une réservation
     */
    @PostMapping
    public ResponseEntity<ReservationResponseDTO> creerReservation(@Valid @RequestBody ReservationRequestDTO requestDTO) {
        ReservationResponseDTO response = reservationService.creerReservation(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/reservations/{id} — Récupérer une réservation
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponseDTO> getReservationById(@PathVariable String id) {
        ReservationResponseDTO response = reservationService.getReservationById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/reservations/user/{userId} — Récupérer les réservations d'un utilisateur
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationResponseDTO>> getReservationsByUser(@PathVariable Long userId) {
        List<ReservationResponseDTO> reservations = reservationService.getReservationsByUser(userId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * GET /api/reservations/trajet/{trajetId} — Récupérer les réservations d'un trajet
     */
    @GetMapping("/trajet/{trajetId}")
    public ResponseEntity<List<ReservationResponseDTO>> getReservationsByTrajet(@PathVariable Long trajetId) {
        List<ReservationResponseDTO> reservations = reservationService.getReservationsByTrajet(trajetId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * GET /api/reservations/status/{status} — Récupérer les réservations par statut
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ReservationResponseDTO>> getReservationsByStatus(@PathVariable ReservationStatus status) {
        List<ReservationResponseDTO> reservations = reservationService.getReservationsByStatus(status);
        return ResponseEntity.ok(reservations);
    }

    /**
     * PATCH /api/reservations/{id}/confirmer — Confirmer une réservation
     */
    @PatchMapping("/{id}/confirmer")
    public ResponseEntity<ReservationResponseDTO> confirmerReservation(@PathVariable String id) {
        ReservationResponseDTO response = reservationService.confirmerReservation(id);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/reservations/{id}/completer — Compléter une réservation
     */
    @PatchMapping("/{id}/completer")
    public ResponseEntity<ReservationResponseDTO> completerReservation(@PathVariable String id) {
        ReservationResponseDTO response = reservationService.completerReservation(id);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/reservations/{id}/annuler — Annuler une réservation
     */
    @PatchMapping("/{id}/annuler")
    public ResponseEntity<ReservationResponseDTO> annulerReservation(@PathVariable String id) {
        ReservationResponseDTO response = reservationService.annulerReservation(id);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/reservations/historique/{userId} — Récupérer l'historique d'un utilisateur
     */
    @GetMapping("/historique/{userId}")
    public ResponseEntity<List<ReservationHistory>> getHistoriqueUser(@PathVariable Long userId) {
        List<ReservationHistory> historique = reservationService.getHistoriqueUser(userId);
        return ResponseEntity.ok(historique);
    }
}
