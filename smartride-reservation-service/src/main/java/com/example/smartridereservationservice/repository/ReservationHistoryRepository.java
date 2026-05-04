package com.example.smartridereservationservice.repository;

import com.example.smartridereservationservice.model.ReservationHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationHistoryRepository extends MongoRepository<ReservationHistory, String> {

    List<ReservationHistory> findByUserId(Long userId);

    List<ReservationHistory> findByReservationId(String reservationId);
}
