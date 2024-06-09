package com.sas.SalesAnalysisSystem.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@NamedQuery(
//	    name = "Sales.sumPriceOfProducts",
//	    query = "SELECT COALESCE(SUM(p.price), 0.0) " +
//	            "FROM Sales s " +
//	            "JOIN s.invoice i " +
//	            "JOIN i.products p " +
//	            "WHERE i.isActive = true"
//	)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sales")
public class Sales extends BaseEntity {

    @Column(name = "no_of_product_sold")
    private int numberOfProductSold;

   
    @Column(name = "total_amount")
    private int totalAmount;


    @Column(name = "total_quantity")
    private int totalQuantity;


    @ManyToOne
    @JoinColumn(name = "invoice_no")
    private Invoice invoice;

	public int getNumberOfProductSold() {
		return numberOfProductSold;
	}

	public void setNumberOfProductSold(int numberOfProductSold) {
		this.numberOfProductSold = numberOfProductSold;
	}

	public int getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}

	public int getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(int totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
    
    

}

