package com.smartride.smartride_payment_service.service;

import com.smartride.smartride_payment_service.dto.PaymentRequest;
import com.smartride.smartride_payment_service.dto.PaymentResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request);
    PaymentResponse createPayment (PaymentRequest request);
    List<PaymentResponse> getByTrajet(Long trajetId) ;
}