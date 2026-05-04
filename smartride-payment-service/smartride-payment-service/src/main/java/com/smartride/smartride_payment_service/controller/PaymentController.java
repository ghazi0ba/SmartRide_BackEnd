package com.smartride.smartride_payment_service.controller;


import com.smartride.smartride_payment_service.dto.PaymentRequest;
import com.smartride.smartride_payment_service.dto.PaymentResponse;
import com.smartride.smartride_payment_service.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public PaymentResponse makePayment(@RequestBody PaymentRequest request) {
        return paymentService.processPayment(request);
    }
}