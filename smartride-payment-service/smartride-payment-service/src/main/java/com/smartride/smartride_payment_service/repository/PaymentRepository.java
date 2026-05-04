package com.smartride.smartride_payment_service.repository;

import com.smartride.smartride_payment_service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}