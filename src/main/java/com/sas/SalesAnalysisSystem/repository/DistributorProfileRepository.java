package com.sas.SalesAnalysisSystem.repository;

import com.sas.SalesAnalysisSystem.models.DistributorProfileDTO;
import com.sas.SalesAnalysisSystem.models.DistributorProfile;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DistributorProfileRepository extends JpaRepository<DistributorProfile, Long> {

	boolean existsByEmail(String email);

	Long countAllDistributorProfile();

	 List<DistributorProfile> findByIsActiveTrue();
	 

	    @Query("SELECT DISTINCT dp.city FROM DistributorProfile dp WHERE dp.city IS NOT NULL")
	    List<String> findAllCities();

	    @Query("SELECT DISTINCT dp.region FROM DistributorProfile dp  WHERE dp.region IS NOT NULL")
	    List<String> findAllRegions();

	    @Query("SELECT DISTINCT dp.zone FROM DistributorProfile dp  WHERE dp.zone IS NOT NULL")
	    List<String> findAllZones();
	    

		 @Query("SELECT   new com.sas.SalesAnalysisSystem.models.DistributorProfileDTO(p.id,p.distributorProfile.agencyName) FROM Distributor p WHERE LOWER(p.distributorProfile.agencyName) LIKE %:name%")
		    Page<DistributorProfileDTO> findDistributorIdAndDistributorName(@Param("name") String name, Pageable pageable);
		    
			
			@Query("SELECT  new com.sas.SalesAnalysisSystem.models.DistributorProfileDTO( p.id,p.distributorProfile.agencyName) FROM Distributor p")
		    Page<DistributorProfileDTO> findAllProfiles(Pageable pageable);
			
			@Query("SELECT d from DistributorProfile d WHERE d.agencyName=:agencyName " )
			DistributorProfileDTO findDistributorByAgencyName(@Param("agencyName") String name );

}
