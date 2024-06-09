package com.sas.SalesAnalysisSystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sas.SalesAnalysisSystem.models.Expenses;

public interface ExpensesRepository extends JpaRepository<Expenses, Long>  {
	
	@Query("SELECT e.createDate, e.updateDate, e.incentive, e.miscellaneous, e.salary FROM Expenses e WHERE e.salesperson = :salespersonId")
	List<Object[]> findBySalesperson(@Param("salespersonId") Long salespersonId);


}
