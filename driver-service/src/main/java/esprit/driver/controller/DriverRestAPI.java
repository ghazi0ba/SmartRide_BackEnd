package esprit.driver.controller;

import esprit.driver.Trajet;
import esprit.driver.client.ReservationClient;
import esprit.driver.dto.DriverDTO;
import esprit.driver.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/drivers")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DriverRestAPI {

    @Autowired
    private DriverService driverService;

    @Autowired
    private ReservationClient reservationClient;


    @GetMapping("api/trajets")
    public List<Trajet> getAllTrajets(){
        return driverService.getTrajets();
    }

    @GetMapping("/api/trajets/{id}")
    public Trajet getTrajetById(@PathVariable("id") int id){
        return driverService.getrajetbyid(id);
    }

    // CREATE
    @PostMapping
    public ResponseEntity<DriverDTO> createDriver(@RequestBody DriverDTO driverDTO) {
        DriverDTO createdDriver = driverService.createDriver(driverDTO);
        return new ResponseEntity<>(createdDriver, HttpStatus.CREATED);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<DriverDTO>> getAllDrivers() {
        List<DriverDTO> drivers = driverService.getAllDrivers();
        return ResponseEntity.ok(drivers);
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<DriverDTO> getDriverById(@PathVariable Long id) {
        DriverDTO driver = driverService.getDriverById(id);
        return ResponseEntity.ok(driver);
    }

    // READ BY EMAIL
    @GetMapping("/email/{email}")
    public ResponseEntity<DriverDTO> getDriverByEmail(@PathVariable String email) {
        DriverDTO driver = driverService.getDriverByEmail(email);
        return ResponseEntity.ok(driver);
    }

    // READ BY LICENSE PLATE
    @GetMapping("/plaque/{plaque}")
    public ResponseEntity<DriverDTO> getDriverByPlaque(@PathVariable String plaque) {
        DriverDTO driver = driverService.getDriverByPlaqueImmatriculation(plaque);
        return ResponseEntity.ok(driver);
    }

    // READ BY USER ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<DriverDTO> getDriverByUserId(@PathVariable Long userId) {
        DriverDTO driver = driverService.getDriverByUserId(userId);
        return ResponseEntity.ok(driver);
    }

    // READ BY STATUS
    @GetMapping("/status/{statut}")
    public ResponseEntity<List<DriverDTO>> getDriversByStatus(@PathVariable String statut) {
        List<DriverDTO> drivers = driverService.getDriversByStatus(statut);
        return ResponseEntity.ok(drivers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverDTO> updateDriver(@PathVariable Long id, @RequestBody DriverDTO driverDTO) {
        DriverDTO updatedDriver = driverService.updateDriver(id, driverDTO);
        return ResponseEntity.ok(updatedDriver);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DriverDTO> updateDriverStatus(@PathVariable Long id, @RequestParam String statut) {
        DriverDTO updatedDriver = driverService.updateDriverStatus(id, statut);
        return ResponseEntity.ok(updatedDriver);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{driverId}/reservations")
    public ResponseEntity<List<Map<String, Object>>> getDriverReservations(@PathVariable Long driverId) {
        try {
            List<Map<String, Object>> reservations = reservationClient.getReservationsByDriverId(driverId);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .build();
        }
    }
}
