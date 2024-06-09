package com.sas.SalesAnalysisSystem.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sas.SalesAnalysisSystem.models.Salesperson;


public interface SalespersonRepository extends JpaRepository<Salesperson, Long> {

	boolean existsByName(String name);
	
	List<Salesperson> findByIsActiveTrue();

	List<Salesperson> findByIsActiveFalse();
	 
	 
	@Query("SELECT COUNT(s) FROM Salesperson s WHERE s.isActive = :isActive")
	Long countAllActiveSalesperson(@Param("isActive") boolean isActive);
	
	@Query("SELECT DISTINCT p.name FROM Salesperson p WHERE LOWER(p.name) LIKE %:name%")
    Page<String> findSalespersonName(@Param("name") String name, Pageable pageable);
    
	
	@Query("SELECT DISTINCT p.name FROM Salesperson p")
    Page<String> findAllSalespersonNames(Pageable pageable);
	

    @Query("SELECT DISTINCT p.id, p.name FROM Salesperson p")
    Page<Object[]> findAllSalespersonNamesAndIDs(Pageable pageable);
    
    
   
	
	
	
	

	 
}
