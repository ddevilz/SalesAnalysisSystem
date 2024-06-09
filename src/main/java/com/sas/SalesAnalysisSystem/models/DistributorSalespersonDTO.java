package com.sas.SalesAnalysisSystem.models;

public class DistributorSalespersonDTO {
    private Long distributorId;
    private Long salespersonId;
    private String salespersonName;

    public DistributorSalespersonDTO(Long distributorId, Long salespersonId, String salespersonName) {
        this.distributorId = distributorId;
        this.salespersonId = salespersonId;
        this.salespersonName = salespersonName;
    }

    public Long getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(Long distributorId) {
        this.distributorId = distributorId;
    }

    public Long getSalespersonId() {
        return salespersonId;
    }

    public void setSalespersonId(Long salespersonId) {
        this.salespersonId = salespersonId;
    }

    public String getSalespersonName() {
        return salespersonName;
    }

    public void setSalespersonName(String salespersonName) {
        this.salespersonName = salespersonName;
    }
}

