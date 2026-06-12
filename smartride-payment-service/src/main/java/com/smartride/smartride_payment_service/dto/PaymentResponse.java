package com.smartride.smartride_payment_service.dto;

public class PaymentResponse {
    private Long id;

    public PaymentResponse() {

    }

    public PaymentResponse(Long id, String status, Double amount) {
        this.id = id;
        this.status = status;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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