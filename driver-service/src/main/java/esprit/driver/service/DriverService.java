package esprit.driver.service;

import esprit.driver.Trajet;
import esprit.driver.TrajetClient;
import esprit.driver.dto.DriverDTO;
import esprit.driver.entity.Driver;
import esprit.driver.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private TrajetClient trajetServiceClient;

    public  List<Trajet> getTrajets(){
        return trajetServiceClient.getAllTrajets();
    }



    public Trajet getrajetbyid(int id){
        return trajetServiceClient.getTrajetById(id);
    }

    // Create
    public DriverDTO createDriver(DriverDTO driverDTO) {
        Driver driver = DriverDTO.toEntity(driverDTO);
        Driver savedDriver = driverRepository.save(driver);
        return DriverDTO.fromEntity(savedDriver);
    }

    // Read All
    public List<DriverDTO> getAllDrivers() {
        return driverRepository.findAll()
                .stream()
                .map(DriverDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Read by ID
    public DriverDTO getDriverById(Long id) {
        return driverRepository.findById(id)
                .map(DriverDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
    }

    // Read by Email
    public DriverDTO getDriverByEmail(String email) {
        return driverRepository.findByEmail(email)
                .map(DriverDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("Driver not found with email: " + email));
    }

    // Read by License Plate
    public DriverDTO getDriverByPlaqueImmatriculation(String plaque) {
        return driverRepository.findByPlaqueImmatriculation(plaque)
                .map(DriverDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("Driver not found with license plate: " + plaque));
    }

    // Read by User ID
    public DriverDTO getDriverByUserId(Long userId) {
        return driverRepository.findByUserId(userId)
                .map(DriverDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("Driver not found with user id: " + userId));
    }

    // Read by Status
    public List<DriverDTO> getDriversByStatus(String statut) {
        Driver.DriverStatus status = Driver.DriverStatus.valueOf(statut.toUpperCase());
        return driverRepository.findByStatut(status)
                .stream()
                .map(DriverDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Update
    public DriverDTO updateDriver(Long id, DriverDTO driverDTO) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));

        driver.setNom(driverDTO.getNom());
        driver.setPrenom(driverDTO.getPrenom());
        driver.setEmail(driverDTO.getEmail());
        driver.setTelephone(driverDTO.getTelephone());
        driver.setMarqueVehicule(driverDTO.getMarqueVehicule());
        driver.setModeleVehicule(driverDTO.getModeleVehicule());
        driver.setPlaqueImmatriculation(driverDTO.getPlaqueImmatriculation());
        driver.setStatut(Driver.DriverStatus.valueOf(driverDTO.getStatut()));

        Driver updatedDriver = driverRepository.save(driver);
        return DriverDTO.fromEntity(updatedDriver);
    }

    // Update Status
    public DriverDTO updateDriverStatus(Long id, String newStatus) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));

        driver.setStatut(Driver.DriverStatus.valueOf(newStatus.toUpperCase()));
        Driver updatedDriver = driverRepository.save(driver);
        return DriverDTO.fromEntity(updatedDriver);
    }

    // Delete
    public void deleteDriver(Long id) {
        if (!driverRepository.existsById(id)) {
            throw new RuntimeException("Driver not found with id: " + id);
        }
        driverRepository.deleteById(id);
    }
}
