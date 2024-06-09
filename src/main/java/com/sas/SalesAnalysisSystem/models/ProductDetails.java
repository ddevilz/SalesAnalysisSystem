package com.sas.SalesAnalysisSystem.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="ProductDetails")
public class ProductDetails extends BaseEntity {

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "cgst/sgst")
    private Double cgstSgst=0.0;
    
    private Double hsnSac=0.0;

    @Column(name = "igst")
    private Double igst=0.0;
    
    @Column(name = "igstAmount")
    private Double igstAmount=0.0;
    
    @Column(name = "icgstAmount")
    private Double cgstAmount=0.0;
    
    @Column(name = "sgstAmount")
    private Double sgstAmount=0.0;
    
    @Column(name="discountAmount")
    private Double discountAmount=0.0;
    
    @Column(name="discount-in-price")
    private Double discountInPrice=0.0; 
    
    @Column(name="discount-in-percent")
    private Double discountInPercent=0.0; 
    
    @Column(name="total-amount-without-tax-discount")
    private Double totalAmountWithoutTaxDiscount=0.0;
    
    @Column(name = "TotalAmount-with-tax")
    private Double totalAmountWithTax;
    
    @Column(name = "TotalAmount-without-tax")
    private Double totalAmountWithoutTax;
    
    
    
	public Double getHsnSac() {
		return hsnSac;
	}
	public void setHsnSac(Double hsnSac) {
		this.hsnSac = hsnSac;
	}
	public Double getDiscountAmount() {
		return discountAmount;
	}
	public void setDiscountAmount(Double discountAmount) {
		this.discountAmount = discountAmount;
	}
	public Double getIgstAmount() {
		return igstAmount;
	}
	public void setIgstAmount(Double igstAmount) {
		this.igstAmount = igstAmount;
	}
	public Double getCgstAmount() {
		return cgstAmount;
	}
	public void setCgstAmount(Double cgstAmount) {
		this.cgstAmount = cgstAmount;
	}
	public Double getSgstAmount() {
		return sgstAmount;
	}
	public void setSgstAmount(Double sgstAmount) {
		this.sgstAmount = sgstAmount;
	}
	public Double getTotalAmountWithoutTaxDiscount() {
		return totalAmountWithoutTaxDiscount;
	}
	public void setTotalAmountWithoutTaxDiscount(Double totalAmountWithoutTaxDiscount) {
		this.totalAmountWithoutTaxDiscount = totalAmountWithoutTaxDiscount;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getCgstSgst() {
		return cgstSgst;
	}
	public void setCgstSgst(Double cgstSgst) {
		this.cgstSgst = cgstSgst;
	}
	public Double getTotalAmountWithTax() {
		return totalAmountWithTax;
	}
	public void setTotalAmountWithTax(Double totalAmountWithTax) {
		this.totalAmountWithTax = totalAmountWithTax;
	}
	public Double getTotalAmountWithoutTax() {
		return totalAmountWithoutTax;
	}
	public void setTotalAmountWithoutTax(Double totalAmountWithoutTax) {
		this.totalAmountWithoutTax = totalAmountWithoutTax;
	}
	public Double getIgst() {
		return igst;
	}
	public void setIgst(Double igst) {
		this.igst = igst;
	}
	public Double getDiscountInPrice() {
		return discountInPrice;
	}
	public void setDiscountInPrice(Double discountInPrice) {
		this.discountInPrice = discountInPrice;
	}
	public Double getDiscountInPercent() {
		return discountInPercent;
	}
	public void setDiscountInPercent(Double discountInPercent) {
		this.discountInPercent = discountInPercent;
	}
	@Override
	public String toString() {
		return "ProductDetails [quantity=" + quantity + ", cgstSgst=" + cgstSgst + ", igst=" + igst
				+ ", discountInPrice=" + discountInPrice + ", discountInPercent=" + discountInPercent
				+ ", totalAmountWithoutTaxDiscount=" + totalAmountWithoutTaxDiscount + ", totalAmountWithTax="
				+ totalAmountWithTax + ", totalAmountWithoutTax=" + totalAmountWithoutTax + "]";
	}
	
	
	
	
}
