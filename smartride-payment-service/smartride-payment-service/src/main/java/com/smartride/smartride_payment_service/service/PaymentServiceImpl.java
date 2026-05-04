package com.smartride.smartride_payment_service.service;


import com.smartride.smartride_payment_service.dto.PaymentRequest;
import com.smartride.smartride_payment_service.dto.PaymentResponse;
import com.smartride.smartride_payment_service.entity.Payment;
import com.smartride.smartride_payment_service.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {

        Payment payment = new Payment();
        payment.setRideId(request.getRideId());
        payment.setAmount(request.getAmount());
        payment.setStatus("SUCCESS");
        payment.setPaymentDate(LocalDateTime.now());

        paymentRepository.save(payment);

        return new PaymentResponse("SUCCESS", request.getAmount());
    }
}