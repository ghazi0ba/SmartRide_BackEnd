package com.smartride.smartride_payment_service.service;


import com.smartride.smartride_payment_service.client.TrajetClient;
import com.smartride.smartride_payment_service.dto.PaymentRequest;
import com.smartride.smartride_payment_service.dto.PaymentResponse;
import com.smartride.smartride_payment_service.dto.TrajetDTO;
import com.smartride.smartride_payment_service.entity.Payment;
import com.smartride.smartride_payment_service.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final TrajetClient trajetClient;

    public PaymentServiceImpl(PaymentRepository paymentRepository, TrajetClient trajetClient) {
        this.paymentRepository = paymentRepository;
        this.trajetClient = trajetClient;
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

    public PaymentResponse createPayment(PaymentRequest request) {



        TrajetDTO trajet = trajetClient.getTrajetById(request.getRideId());

        if (trajet == null) {
            throw new RuntimeException("Trajet introuvable");
        }


        if (!trajet.getPrixEstime().equals(request.getAmount())) {
            throw new RuntimeException("Montant invalide");
        }


        Payment payment = new Payment();
        payment.getRideId(trajet.getId());
        payment.setAmount(request.getAmount());
        payment.setStatus("SUCCESS");

        Payment saved = paymentRepository.save(payment);

        PaymentResponse response = new PaymentResponse();
        response.setId(saved.getId());
        response.setStatus(saved.getStatus());
        response.setAmount(saved.getAmount());

        return response;
    }

    private PaymentResponse mapToResponse(Payment payment) {

        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setStatus(payment.getStatus());
        response.setAmount(payment.getAmount());

        return response;
    }

    public List<PaymentResponse> getByTrajet(Long trajetId) {

        return paymentRepository.findByRideId(trajetId)
                .stream()
                .map(payment -> {
                    PaymentResponse response = new PaymentResponse();
                    response.setId(payment.getId());
                    response.setStatus(payment.getStatus());
                    response.setAmount(payment.getAmount());
                    return response;
                })
                .toList();
    }



}