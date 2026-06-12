package com.example.smartridereservationservice.repository;

import com.example.smartridereservationservice.model.Reservation;
import com.example.smartridereservationservice.model.ReservationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends MongoRepository<Reservation, String> {

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByDriverId(Long driverId);

    List<Reservation> findByStatus(ReservationStatus status);

    Optional<Reservation> findByUserIdAndTrajetId(Long userId, Long trajetId);

    List<Reservation> findByTrajetId(Long trajetId);
}
