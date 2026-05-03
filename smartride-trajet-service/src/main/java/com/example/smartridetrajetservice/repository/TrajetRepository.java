package com.example.smartridetrajetservice.repository;

import com.example.smartridetrajetservice.model.StatutTrajet;
import com.example.smartridetrajetservice.model.Trajet;
import com.example.smartridetrajetservice.model.TypeTrajet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrajetRepository extends JpaRepository<Trajet, Long> {

    List<Trajet> findByPassagerId(Long passagerId);

    List<Trajet> findByChauffeurId(Long chauffeurId);

    List<Trajet> findByStatut(StatutTrajet statut);

    List<Trajet> findByType(TypeTrajet type);

    List<Trajet> findByPassagerIdAndStatut(Long passagerId, StatutTrajet statut);
}
