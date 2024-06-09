package com.sas.SalesAnalysisSystem.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "eway")
public class Eway extends BaseEntity {
	@OneToOne
	@MapsId
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

	@Column(name = "eway_Doc_No")
    private String ewayDocNumber;

    @Column(name = "eway_Bill_No")
    private String eWayBillNo;

    @Column(name = "eway_Mode")
    private String eWayMode;

    @Column(name = "eway_Approx_distance")
    private String eWayApproxDistance;

    @Column(name = "eway_Valid_Upto")
    private String eWayValidUpto;

    @Column(name = "eway_Supply_type")
    private String eWaySupplyType;

    @Column(name = "eway_transaction_type")
    private String eWayTransactionType;
    
    @Column(name = "eway_transaction_id")
    private String eWayTransactionId;

    @Column(name = "eway_GSTIN")
    private String eWayGSTIN;

    @Column(name = "eway_from")
    private String eWayfrom;

    @Column(name = "eway_to")
    private String eWayTo;

    @Column(name = "eway_dispatch_from")
    private String eWayDistpatchFrom;

    @Column(name = "eway_ship_to")
    private String eWayShipTo;
    
    @Column(name = "eway_tax_amount")
    private String ewaytaxAmount;

    @Column(name = "eway_tax_rate")
    private String ewaytaxRate;


    @Column(name = "eway_TransportationID")
    private String ewayTransportationID;

    @Column(name = "eway_VechileNo")
    private String ewayVechileNo;

    @Column(name = "eway_VehicleFrom")
    private String ewayVehicleFrom;

	public String getEwayDocNumber() {
		return ewayDocNumber;
	}

	public String geteWayBillNo() {
		return eWayBillNo;
	}

	public void seteWayBillNo(String eWayBillNo) {
		this.eWayBillNo = eWayBillNo;
	}

	public String geteWayMode() {
		return eWayMode;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public String geteWayTransactionId() {
		return eWayTransactionId;
	}

	public void seteWayTransactionId(String eWayTransactionId) {
		this.eWayTransactionId = eWayTransactionId;
	}

	public String getEwaytaxRate() {
		return ewaytaxRate;
	}

	public void setEwaytaxRate(String ewaytaxRate) {
		this.ewaytaxRate = ewaytaxRate;
	}

	public void seteWayMode(String eWayMode) {
		this.eWayMode = eWayMode;
	}

	public String geteWayApproxDistance() {
		return eWayApproxDistance;
	}

	public void seteWayApproxDistance(String eWayApproxDistance) {
		this.eWayApproxDistance = eWayApproxDistance;
	}

	public String geteWayValidUpto() {
		return eWayValidUpto;
	}

	public void seteWayValidUpto(String eWayValidUpto) {
		this.eWayValidUpto = eWayValidUpto;
	}

	public String geteWaySupplyType() {
		return eWaySupplyType;
	}

	public void seteWaySupplyType(String eWaySupplyType) {
		this.eWaySupplyType = eWaySupplyType;
	}

	public String geteWayTransactionType() {
		return eWayTransactionType;
	}

	public void seteWayTransactionType(String eWayTransactionType) {
		this.eWayTransactionType = eWayTransactionType;
	}

	public String geteWayGSTIN() {
		return eWayGSTIN;
	}

	public void seteWayGSTIN(String eWayGSTIN) {
		this.eWayGSTIN = eWayGSTIN;
	}

	public String geteWayfrom() {
		return eWayfrom;
	}

	public void seteWayfrom(String eWayfrom) {
		this.eWayfrom = eWayfrom;
	}

	public String geteWayTo() {
		return eWayTo;
	}

	public void seteWayTo(String eWayTo) {
		this.eWayTo = eWayTo;
	}

	public String geteWayDistpatchFrom() {
		return eWayDistpatchFrom;
	}

	public void seteWayDistpatchFrom(String eWayDistpatchFrom) {
		this.eWayDistpatchFrom = eWayDistpatchFrom;
	}

	public String geteWayShipTo() {
		return eWayShipTo;
	}

	public void seteWayShipTo(String eWayShipTo) {
		this.eWayShipTo = eWayShipTo;
	}

	public String getEwaytaxAmount() {
		return ewaytaxAmount;
	}

	public void setEwaytaxAmount(String ewaytaxAmount) {
		this.ewaytaxAmount = ewaytaxAmount;
	}

	
	public String getEwayTransportationID() {
		return ewayTransportationID;
	}

	public void setEwayTransportationID(String ewayTransportationID) {
		this.ewayTransportationID = ewayTransportationID;
	}

	public String getEwayVechileNo() {
		return ewayVechileNo;
	}

	public void setEwayVechileNo(String ewayVechileNo) {
		this.ewayVechileNo = ewayVechileNo;
	}

	public String getEwayVehicleFrom() {
		return ewayVehicleFrom;
	}

	public void setEwayVehicleFrom(String ewayVehicleFrom) {
		this.ewayVehicleFrom = ewayVehicleFrom;
	}

	public void setEwayDocNumber(String ewayDocNumber) {
		this.ewayDocNumber = ewayDocNumber;
	}


}
