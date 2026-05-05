package com.example.smartridetrajetservice.controller;




import com.example.smartridetrajetservice.User;
import com.example.smartridetrajetservice.dto.ReservationDto;
import com.example.smartridetrajetservice.dto.TrajetRequestDTO;
import com.example.smartridetrajetservice.dto.TrajetResponseDTO;
import com.example.smartridetrajetservice.model.StatutTrajet;
import com.example.smartridetrajetservice.service.TrajetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trajets")
@RequiredArgsConstructor
public class TrajetController {


    private final TrajetService trajetService;



   @RequestMapping("api/users")
    public List<User> getAllUsers(){
        return trajetService.getUsers();
    }




    @GetMapping("/api/users/{id}")
    public User getUserById(@PathVariable("id") Long id){
        return trajetService.getuserbyid(id);
    }


    // POST /api/trajets — Créer un trajet
    @PostMapping
    public ResponseEntity<TrajetResponseDTO> creerTrajet(@Valid @RequestBody TrajetRequestDTO requestDTO) {
        TrajetResponseDTO response = trajetService.creerTrajet(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET api/trajets — Lister tous les trajets
    @GetMapping
    public ResponseEntity<List<TrajetResponseDTO>> getAllTrajets() {
        return ResponseEntity.ok(trajetService.getAllTrajets());
    }

    // GET api/trajets/{id} — Récupérer un trajet par ID
    @GetMapping("/{id}")
    public ResponseEntity<TrajetResponseDTO> getTrajetById(@PathVariable Long id) {
        return ResponseEntity.ok(trajetService.getTrajetById(id));
    }

    // GET api/trajets/passager/{passagerId} — Trajets d'un passager
    @GetMapping("/passager/{passagerId}")
    public ResponseEntity<List<TrajetResponseDTO>> getTrajetsByPassager(@PathVariable Long passagerId) {
        return ResponseEntity.ok(trajetService.getTrajetsByPassager(passagerId));
    }

    // GET api/trajets/chauffeur/{chauffeurId} — Trajets d'un chauffeur
    @GetMapping("/chauffeur/{chauffeurId}")
    public ResponseEntity<List<TrajetResponseDTO>> getTrajetsByChauffeur(@PathVariable Long chauffeurId) {
        return ResponseEntity.ok(trajetService.getTrajetsByChauffeur(chauffeurId));
    }

    // GET api/trajets/statut/{statut} — Trajets par statut
    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<TrajetResponseDTO>> getTrajetsByStatut(@PathVariable StatutTrajet statut) {
        return ResponseEntity.ok(trajetService.getTrajetsByStatut(statut));
    }

    // PATCH api/trajets/{id}/accepter?chauffeurId=X — Accepter un trajet
    @PatchMapping("/{id}/accepter")
    public ResponseEntity<TrajetResponseDTO> accepterTrajet(
            @PathVariable Long id,
            @RequestParam Long chauffeurId) {
        return ResponseEntity.ok(trajetService.accepterTrajet(id, chauffeurId));
    }

    // PATCH api/trajets/{id}/demarrer — Démarrer un trajet
    @PatchMapping("/{id}/demarrer")
    public ResponseEntity<TrajetResponseDTO> demarrerTrajet(@PathVariable Long id) {
        return ResponseEntity.ok(trajetService.demarrerTrajet(id));
    }

    // PATCH api/trajets/{id}/terminer — Terminer un trajet
    @PatchMapping("/{id}/terminer")
    public ResponseEntity<TrajetResponseDTO> terminerTrajet(@PathVariable Long id) {
        return ResponseEntity.ok(trajetService.terminerTrajet(id));
    }

    // PATCH api/trajets/{id}/annuler — Annuler un trajet
    @PatchMapping("/{id}/annuler")
    public ResponseEntity<TrajetResponseDTO> annulerTrajet(@PathVariable Long id) {
        return ResponseEntity.ok(trajetService.annulerTrajet(id));
    }

    // DELETE api/trajets/{id} — Supprimer un trajet
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerTrajet(@PathVariable Long id) {
        trajetService.supprimerTrajet(id);
        return ResponseEntity.noContent().build();
    }
}
