package com.sas.SalesAnalysisSystem.dto;

import java.time.LocalDateTime;

public class DistributorDetails {
    private String id;
    private LocalDateTime timestamp;
    private Long someInteger;
    private Double amount;
    private String distributorName;
    private String city;
    private String state;
    private String region;
    private String salespersonName;

	public String getSalespersonName() {
		return salespersonName;
	}
	public void setSalespersonName(String salespersonName) {
		this.salespersonName = salespersonName;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	
	public Long getSomeInteger() {
		return someInteger;
	}
	public void setSomeInteger(Long someInteger) {
		this.someInteger = someInteger;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getDistributorName() {
		return distributorName;
	}
	public void setDistributorName(String distributorName) {
		this.distributorName = distributorName;
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
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public DistributorDetails(String id, LocalDateTime timestamp, Long someInteger, Double amount,
			String distributorName, String city, String state, String region, String salespersonName) {
		super();
		this.id = id;
		this.timestamp = timestamp;
		this.someInteger = someInteger;
		this.amount = amount;
		this.distributorName = distributorName;
		this.city = city;
		this.state = state;
		this.region = region;
		this.salespersonName = salespersonName;
	}
	
	
}
