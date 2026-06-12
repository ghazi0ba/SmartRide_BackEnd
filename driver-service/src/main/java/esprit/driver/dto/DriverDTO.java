package esprit.driver.dto;

import esprit.driver.entity.Driver;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverDTO {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String statut;
    private String marqueVehicule;
    private String modeleVehicule;
    private String plaqueImmatriculation;
    private Long userId;

    public static DriverDTO fromEntity(Driver driver) {
        return new DriverDTO(
                driver.getId(),
                driver.getNom(),
                driver.getPrenom(),
                driver.getEmail(),
                driver.getTelephone(),
                driver.getStatut().toString(),
                driver.getMarqueVehicule(),
                driver.getModeleVehicule(),
                driver.getPlaqueImmatriculation(),
                driver.getUserId()
        );
    }

    public static Driver toEntity(DriverDTO dto) {
        Driver driver = new Driver();
        driver.setId(dto.getId());
        driver.setNom(dto.getNom());
        driver.setPrenom(dto.getPrenom());
        driver.setEmail(dto.getEmail());
        driver.setTelephone(dto.getTelephone());
        driver.setStatut(Driver.DriverStatus.valueOf(dto.getStatut()));
        driver.setMarqueVehicule(dto.getMarqueVehicule());
        driver.setModeleVehicule(dto.getModeleVehicule());
        driver.setPlaqueImmatriculation(dto.getPlaqueImmatriculation());
        driver.setUserId(dto.getUserId());
        return driver;
    }
}
