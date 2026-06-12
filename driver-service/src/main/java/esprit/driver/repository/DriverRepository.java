package esprit.driver.repository;

import esprit.driver.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByEmail(String email);
    Optional<Driver> findByPlaqueImmatriculation(String plaque);
    List<Driver> findByStatut(Driver.DriverStatus statut);
    Optional<Driver> findByUserId(Long userId);
}
