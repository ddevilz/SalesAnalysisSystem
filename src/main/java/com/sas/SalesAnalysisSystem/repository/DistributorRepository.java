package com.sas.SalesAnalysisSystem.repository;


import java.util.List;
import java.util.Optional;
import java.util.function.LongFunction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sas.SalesAnalysisSystem.models.Distributor;
import com.sas.SalesAnalysisSystem.models.DistributorProfile;
import com.sas.SalesAnalysisSystem.models.DistributorSalespersonDTO;

import jakarta.transaction.Transactional;


public interface DistributorRepository extends JpaRepository<Distributor, Long>{

	Optional<Distributor> findByDistributorProfile(DistributorProfile distributorProfile);
	
	List<Distributor> findByIsActiveFalse();
	List<Distributor> findByIsActiveTrue();
	List<Distributor> findBySalespersonId(Long salespersonId);
	
	    @Modifying
	    @Transactional
	    @Query("UPDATE Distributor d SET d.salesperson = NULL WHERE d.salesperson.id = :salespersonId AND d.salesperson.isActive = false")
	    void nullifySalespersonIdsForInactiveSalespersons(Long salespersonId);
	    
	    @Query("SELECT d FROM Distributor d WHERE d.salesperson IS NULL")
	    List<Distributor> findDistributorsWithNoSalesperson();
	    
	    @Query("SELECT d FROM Distributor d WHERE d.id = :distributorId AND d.salesperson IS NULL")
	    Distributor findDistributorByIdWithNoSalesperson(Long distributorId);
	    
	    
	    @Query("SELECT new com.sas.SalesAnalysisSystem.models.DistributorSalespersonDTO(d.id, s.id, s.name) " +
	            "FROM Distributor d JOIN d.salesperson s")
	     List<DistributorSalespersonDTO> findDistributorSalespersonDetails();
	    
	    @Query("SELECT d FROM Distributor d WHERE d.salesperson IS NULL AND d.id = :distributorId ")
	   Optional< Distributor> findDistributorWithNoSalesperson(Long distributorId);
	    
	    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Distributor d WHERE d.id = :distributorId AND d.salesperson.id = :salespersonId")
	    Boolean existsByDistributorIdAndSalespersonId(@Param("distributorId") Long distributorId, @Param("salespersonId") Long salespersonId);
	    
	    


}
