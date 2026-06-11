package com.example.smartridetrajetservice.service;





import com.example.smartridetrajetservice.ReservationClient;
import com.example.smartridetrajetservice.User;
import com.example.smartridetrajetservice.UserClient;
import com.example.smartridetrajetservice.dto.ReservationDto;
import com.example.smartridetrajetservice.dto.TrajetRequestDTO;
import com.example.smartridetrajetservice.dto.TrajetResponseDTO;
import com.example.smartridetrajetservice.exception.TrajetNotFoundException;
import com.example.smartridetrajetservice.exception.TrajetStatutInvalideException;
import com.example.smartridetrajetservice.mapper.TrajetMapper;
import com.example.smartridetrajetservice.model.StatutTrajet;
import com.example.smartridetrajetservice.model.Trajet;
import com.example.smartridetrajetservice.model.TypeTrajet;
import com.example.smartridetrajetservice.repository.TrajetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TrajetService {

    private final TrajetRepository trajetRepository;
    private final TrajetMapper trajetMapper;



    @Autowired
    private UserClient userServiceClient;

    public  List<User> getUsers(){
        return userServiceClient.getAllUsers();
    }



    public User getuserbyid(Long id){
        return userServiceClient.getUserById(id);
    }


    // ─── Créer un trajet ───────────────────────────────────────────────────────

    public TrajetResponseDTO creerTrajet(TrajetRequestDTO requestDTO) {
        log.info("Création d'un nouveau trajet pour le passager {}", requestDTO.getPassagerId());

        Trajet trajet = trajetMapper.toEntity(requestDTO);

        // Calcul simplifié de la distance et du prix estimé
        if (coordonneesDisponibles(trajet)) {
            double distance = calculerDistance(
                    trajet.getLatitudeDepart(), trajet.getLongitudeDepart(),
                    trajet.getLatitudeArrivee(), trajet.getLongitudeArrivee()
            );
            trajet.setDistanceKm(distance);
            trajet.setPrixEstime(calculerPrix(distance, trajet.getType()));
        }

        Trajet saved = trajetRepository.save(trajet);
        log.info("Trajet créé avec l'ID {}", saved.getId());
        return trajetMapper.toDTO(saved);
    }

    // ─── Récupérer un trajet par ID ────────────────────────────────────────────

    @Transactional(readOnly = true)
    public TrajetResponseDTO getTrajetById(Long id) {
        Trajet trajet = trajetRepository.findById(id)
                .orElseThrow(() -> new TrajetNotFoundException("Trajet introuvable avec l'ID : " + id));
        return trajetMapper.toDTO(trajet);
    }

    // ─── Lister tous les trajets ───────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<TrajetResponseDTO> getAllTrajets() {
        return trajetRepository.findAll()
                .stream()
                .map(trajetMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ─── Trajets par passager ──────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<TrajetResponseDTO> getTrajetsByPassager(Long passagerId) {
        return trajetRepository.findByPassagerId(passagerId)
                .stream()
                .map(trajetMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ─── Trajets par chauffeur ─────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<TrajetResponseDTO> getTrajetsByChauffeur(Long chauffeurId) {
        return trajetRepository.findByChauffeurId(chauffeurId)
                .stream()
                .map(trajetMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ─── Trajets par statut ────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<TrajetResponseDTO> getTrajetsByStatut(StatutTrajet statut) {
        return trajetRepository.findByStatut(statut)
                .stream()
                .map(trajetMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ─── Accepter un trajet (chauffeur) ───────────────────────────────────────

    public TrajetResponseDTO accepterTrajet(Long trajetId, Long chauffeurId) {
        Trajet trajet = getTrajetOuException(trajetId);

        if (trajet.getStatut() != StatutTrajet.EN_ATTENTE) {
            throw new TrajetStatutInvalideException(
                    "Le trajet ne peut être accepté que depuis le statut EN_ATTENTE. Statut actuel : " + trajet.getStatut()
            );
        }

        trajet.setChauffeurId(chauffeurId);
        trajet.setStatut(StatutTrajet.ACCEPTE);
        log.info("Trajet {} accepté par le chauffeur {}", trajetId, chauffeurId);
        return trajetMapper.toDTO(trajetRepository.save(trajet));
    }

    // ─── Démarrer un trajet ────────────────────────────────────────────────────

    public TrajetResponseDTO demarrerTrajet(Long trajetId) {
        Trajet trajet = getTrajetOuException(trajetId);

        if (trajet.getStatut() != StatutTrajet.ACCEPTE) {
            throw new TrajetStatutInvalideException(
                    "Le trajet doit être ACCEPTE avant de démarrer. Statut actuel : " + trajet.getStatut()
            );
        }

        trajet.setStatut(StatutTrajet.EN_COURS);
        trajet.setDateDebut(LocalDateTime.now());
        log.info("Trajet {} démarré", trajetId);
        return trajetMapper.toDTO(trajetRepository.save(trajet));
    }

    // ─── Terminer un trajet ────────────────────────────────────────────────────

    public TrajetResponseDTO terminerTrajet(Long trajetId) {
        Trajet trajet = getTrajetOuException(trajetId);

        if (trajet.getStatut() != StatutTrajet.EN_COURS) {
            throw new TrajetStatutInvalideException(
                    "Le trajet doit être EN_COURS pour être terminé. Statut actuel : " + trajet.getStatut()
            );
        }

        trajet.setStatut(StatutTrajet.TERMINE);
        trajet.setDateFin(LocalDateTime.now());
        log.info("Trajet {} terminé", trajetId);
        return trajetMapper.toDTO(trajetRepository.save(trajet));
    }

    // ─── Annuler un trajet ─────────────────────────────────────────────────────

    public TrajetResponseDTO annulerTrajet(Long trajetId) {
        Trajet trajet = getTrajetOuException(trajetId);

        if (trajet.getStatut() == StatutTrajet.TERMINE || trajet.getStatut() == StatutTrajet.ANNULE) {
            throw new TrajetStatutInvalideException(
                    "Impossible d'annuler un trajet déjà " + trajet.getStatut()
            );
        }

        trajet.setStatut(StatutTrajet.ANNULE);
        log.info("Trajet {} annulé", trajetId);
        return trajetMapper.toDTO(trajetRepository.save(trajet));
    }

    // ─── Supprimer un trajet ───────────────────────────────────────────────────

    public void supprimerTrajet(Long id) {
        if (!trajetRepository.existsById(id)) {
            throw new TrajetNotFoundException("Trajet introuvable avec l'ID : " + id);
        }
        trajetRepository.deleteById(id);
        log.info("Trajet {} supprimé", id);
    }

    // ─── Helpers privés ───────────────────────────────────────────────────────

    private Trajet getTrajetOuException(Long id) {
        return trajetRepository.findById(id)
                .orElseThrow(() -> new TrajetNotFoundException("Trajet introuvable avec l'ID : " + id));
    }

    private boolean coordonneesDisponibles(Trajet trajet) {
        return trajet.getLatitudeDepart() != null && trajet.getLongitudeDepart() != null
                && trajet.getLatitudeArrivee() != null && trajet.getLongitudeArrivee() != null;
    }

    /**
     * Formule de Haversine pour calculer la distance entre deux points GPS (en km).
     */
    private double calculerDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(R * c * 100.0) / 100.0;
    }

    /**
     * Tarification simplifiée : TAXI = 1.5 TND/km, COVOITURAGE = 0.8 TND/km.
     */
    private double calculerPrix(double distanceKm, TypeTrajet type) {
        double tarifKm = (type == TypeTrajet.TAXI) ? 1.5 : 0.8;
        return Math.round(distanceKm * tarifKm * 100.0) / 100.0;
    }
}
