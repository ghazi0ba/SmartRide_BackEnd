package com.smartride.smartride_payment_service.dto;

public class TrajetDTO {

    private Long id;
    private Double prixEstime;

    public Long getPassagerId() {
        return passagerId;
    }

    public void setPassagerId(Long passagerId) {
        this.passagerId = passagerId;
    }

    private Long passagerId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPrixEstime() {
        return prixEstime;
    }

    public void setPrixEstime(Double prixEstime) {
        this.prixEstime = prixEstime;
    }
// getters setters
}