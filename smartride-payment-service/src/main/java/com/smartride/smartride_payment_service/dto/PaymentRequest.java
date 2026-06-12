package com.smartride.smartride_payment_service.dto;

public class PaymentRequest {

    private Long rideId;
    private Double amount;

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
// getters & setters
}