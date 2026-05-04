package com.smartride.smartride_payment_service.service;

import com.smartride.smartride_payment_service.dto.PaymentRequest;
import com.smartride.smartride_payment_service.dto.PaymentResponse;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request);
}