package com.sas.SalesAnalysisSystem.models;


import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
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
@Table(name = "expenses")
public class Expenses extends BaseEntity{
	
	
	  	@Column(name = "salary")
	    private Double salary;

	    @Column(name = "incentive")
	    private Double incentive;

	    @Column(name = "miscellaneous")
	    private Double miscellaneous;
	    
	    @Column(name = "salesperson")
	    private Long salesperson;
	    
	    @Column(name="Expense_date")
	    private LocalDateTime expenseDate;
	    

		public double getSalary() {
			return salary;
		}

		public void setSalary(double salary) {
			this.salary = salary;
		}

		public double getIncentive() {
			return incentive;
		}

		public void setIncentive(double incentive) {
			this.incentive = incentive;
		}

		public double getMiscellaneous() {
			return miscellaneous;
		}

		public void setMiscellaneous(double miscellaneous) {
			this.miscellaneous = miscellaneous;
		}

		public Long getSalesperson() {
			return salesperson;
		}

		public void setSalesperson(Long salesperson) {
			this.salesperson = salesperson;
		}

		public LocalDateTime getExpenseDate() {
			return expenseDate;
		}

		public void setExpenseDate(LocalDateTime expenseDate) {
			this.expenseDate = expenseDate;
		}

		

}
