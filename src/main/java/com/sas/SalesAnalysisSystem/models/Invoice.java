package com.sas.SalesAnalysisSystem.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;


import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyJoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invoice")
public class Invoice extends BaseEntity {	

	@Column(name = "invoice_number")
	private String invoiceNumber;
	
	@Column(name = "irn")
	private String IRN;

	@Column(name = "Ack_No")
	private double AckNo;
	
	@Column(name="invoice_Date")
	private LocalDateTime invoiceDate;

	@Column(name = "dispatched_through")
	private String DispatchedThrough;

	@Column(name = "destination")
	private String Destination;

	@Column(name = "vehicle_no")
	private String vechicleNo;

	@Column(name = "cgst")
	private double cgst; 

	@Column(name = "sgst")
	private double sgst;
	
	@Column(name = "igst")
	private double igst;
	
	@ElementCollection
	@CollectionTable(name = "product_gst_details", joinColumns = @JoinColumn(name = "invoice_id"))
	@MapKeyJoinColumn(name = "product_id")
	@Column(name = "product_details")
	private Map<Product, ProductDetails> productGst = new HashMap<>();
	
	@Column(name = "total_amount")
	private double totalAmount;
	
	@Column(name = "amount")
	private double Amount;

	@Column(name = "purchase_number")
	private String purchaseNumber;

	@Column(name = "delivery_date")
	private LocalDate deliveryDate;

	@Column(name = "supplier_name")
	private String supplierName;

	@Column(name = "discount-in-percentage")
	private double discountPercentage = 0.0;
	
	@Column(name = "discount-in-Price")
	private double discountPrice = 0.0;

	@Column(name = "total_quantity_nos")
	private int totalQuantity_Nos;

	@Column(name = "total_quantity_doz")
	private int totalQuantity_Doz;

	@Column(name = "terms_of_delivery")
	private String TermsOfDelivery;

	@ManyToOne
	@JoinColumn(name = "distributor_id")
	private Distributor distributor;  
	
	@Column(name = "salesperson_id")
    private Long salespersonId ;
	
	@Column(name="discount")
	private double Discount;
	
	@ElementCollection
	@CollectionTable(name = "invoice_product_quantities", joinColumns = @JoinColumn(name = "invoice_id"))
	@MapKeyJoinColumn(name = "product_id")
	@Column(name = "quantity")
	private Map<Product, Integer> productQuantityMap = new HashMap<>();

	 @OneToOne(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
	 @JsonIgnore
	 private Eway eway;

	@Column(name = "is_active")
	private Boolean isActive = true; 
	
	@Column(name="is_received")
	private Boolean isReceived=false;
	
	 
	public Boolean getIsReceived() {
		return isReceived;
	}

	public void setIsReceived(Boolean isReceived) {
		this.isReceived = isReceived;
	}

	public double getDiscount() {
		return Discount;
	}

	public void setDiscount(double discount) {
		Discount = discount;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getIRN() {
		return IRN;
	}

	public void setIRN(String iRN) {
		IRN = iRN;
	}

	public Map<Product, ProductDetails> getProductGst() {
		return productGst;
	}

	public void setProductGst(Map<Product, ProductDetails> productGst) {
		this.productGst = productGst;
	}


	public Long getSalespersonId() {
		return salespersonId;
	}

	public void setSalespersonId(Long salespersonId) {
		this.salespersonId = salespersonId;
	}
	
	public LocalDateTime getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(LocalDateTime invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public double getAckNo() {
		return AckNo;
	}

	public double getIgst() {
		return igst;
	}

	public void setIgst(double igst) {
		this.igst = igst;
	}

	public void setAckNo(double ackNo) {
		AckNo = ackNo;
	}

	public String getDispatchedThrough() {
		return DispatchedThrough;
	}

	public void setDispatchedThrough(String dispatchedThrough) {
		DispatchedThrough = dispatchedThrough;
	}

	public String getDestination() {
		return Destination;
	}

	public void setDestination(String destination) {
		Destination = destination;
	}

	public String getVechicleNo() {
		return vechicleNo;
	}

	public void setVechicleNo(String vechicleNo) {
		this.vechicleNo = vechicleNo;
	}

	public double getCgst() {
		return cgst;
	}

	public void setCgst(double cgst) {
		this.cgst = cgst;
	}

	public double getSgst() {
		return sgst;
	}

	public void setSgst(double sgst) {
		this.sgst = sgst;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getPurchaseNumber() {
		return purchaseNumber;
	}

	public void setPurchaseNumber(String purchaseNumber) {
		this.purchaseNumber = purchaseNumber;
	}

	public LocalDate getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(LocalDate deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public double getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(double discountPercentage) {
		this.discountPercentage = discountPercentage;
	}
	
	public double getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(double discountPrice) {
		this.discountPrice = discountPrice;
	}

	public int getTotalQuantity_Nos() {
		return totalQuantity_Nos;
	}

	public void setTotalQuantity_Nos(int totalQuantity_Nos) {
		this.totalQuantity_Nos = totalQuantity_Nos;
	}

	public int getTotalQuantity_Doz() {
		return totalQuantity_Doz;
	}

	public void setTotalQuantity_Doz(int totalQuantity_Doz) {
		this.totalQuantity_Doz = totalQuantity_Doz;
	}


	public double getAmount() {
		return Amount;
	}

	public void setAmount(double amount) {
		Amount = amount;
	}

	public String getTermsOfDelivery() {
		return TermsOfDelivery;
	}

	public void setTermsOfDelivery(String termsOfDelivery) {
		TermsOfDelivery = termsOfDelivery;
	}

	public Eway getEway() {
		return eway;
	}

	public void setEway(Eway eway) {
		this.eway = eway;
	}

	public Distributor getDistributor() {
		return distributor;
	}

	public void setDistributor(Distributor distributor) {
		this.distributor = distributor;
	}

	public Map<Product, Integer> getProductQuantityMap() {
		return productQuantityMap;
	}

	public void setProductQuantityMap(Map<Product, Integer> productQuantityMap) {
		this.productQuantityMap = productQuantityMap;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "Invoice [totalAmount=" + totalAmount + ", getId()=" + getId() + ", getCreateDate()=" + getCreateDate()
				+ "]";
	}

}
