package esprit.driver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "drivers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String telephone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriverStatus statut = DriverStatus.HORS_LIGNE;

    @Column(nullable = false)
    private String marqueVehicule;

    @Column(nullable = false)
    private String modeleVehicule;

    @Column(nullable = false, unique = true)
    private String plaqueImmatriculation;

    @Column(name = "user_id")
    private Long userId;

    public enum DriverStatus {
        DISPONIBLE,
        OCCUPÉ,
        HORS_LIGNE
    }
}
