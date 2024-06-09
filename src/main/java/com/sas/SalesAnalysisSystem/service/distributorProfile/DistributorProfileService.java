package com.sas.SalesAnalysisSystem.service.distributorProfile;

import java.util.List;

import com.sas.SalesAnalysisSystem.models.DistributorProfile;
import com.sas.SalesAnalysisSystem.models.DistributorProfileDTO;

public interface DistributorProfileService {
	
	 DistributorProfile getDistributorProfileById(Long Id);

	    List<DistributorProfile> getAllDistributorProfile();

	    void deleteDistributorProfile(Long Id);

		Long countAllDistributorProfile();

		void deactivateActivateDistributorProfile(Long id);

		List<String> getAllLocations(String locationType);

		List<DistributorProfileDTO> searchDistributorProfiles(String search, int page, int size);

	    List<DistributorProfileDTO> getAllDistributorProfiles(int page, int size);

		DistributorProfile createDistributorProfile(DistributorProfile distributorProfile, Long salespersonId);

		DistributorProfile updateDistributorProfile(Long id, DistributorProfile updatedProfile);
	    
	    

}
