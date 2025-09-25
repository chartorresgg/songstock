package com.songstock.dto;

import com.songstock.entity.VerificationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class ProviderManagementDTO {

    @NotNull(message = "El estado de verificación es obligatorio")
    private VerificationStatus verificationStatus;

    @Size(max = 1000, message = "Las notas no pueden exceder 1000 caracteres")
    private String verificationNotes;

    @Size(max = 500, message = "La razón no puede exceder 500 caracteres")
    private String changeReason;

    // Para actualización de información de negocio
    private String businessName;
    private String taxId;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private BigDecimal commissionRate;

    // Constructores
    public ProviderManagementDTO() {
    }

    public ProviderManagementDTO(VerificationStatus verificationStatus, String verificationNotes) {
        this.verificationStatus = verificationStatus;
        this.verificationNotes = verificationNotes;
    }

    // Getters y Setters
    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getVerificationNotes() {
        return verificationNotes;
    }

    public void setVerificationNotes(String verificationNotes) {
        this.verificationNotes = verificationNotes;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }
}
