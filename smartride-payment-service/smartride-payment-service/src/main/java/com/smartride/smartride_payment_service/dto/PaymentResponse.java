package com.smartride.smartride_payment_service.dto;

public class PaymentResponse {

    private String status;
    private Double amount;

    public PaymentResponse(String status, Double amount) {
        this.status = status;
        this.amount = amount;
    }

    // getters & setters

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}