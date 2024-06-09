package com.sas.SalesAnalysisSystem.service.distributorProfile;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sas.SalesAnalysisSystem.exception.ResourceNotFoundException;
import com.sas.SalesAnalysisSystem.models.Category;
import com.sas.SalesAnalysisSystem.models.CategoryDTO;
import com.sas.SalesAnalysisSystem.models.Distributor;
import com.sas.SalesAnalysisSystem.models.DistributorProfile;
import com.sas.SalesAnalysisSystem.models.DistributorProfileDTO;
import com.sas.SalesAnalysisSystem.models.Salesperson;
import com.sas.SalesAnalysisSystem.repository.DistributorProfileRepository;
import com.sas.SalesAnalysisSystem.repository.DistributorRepository;
import com.sas.SalesAnalysisSystem.repository.SalespersonRepository;

import jakarta.transaction.Transactional;

@Service
public class DistributorProfileServiceImpl implements DistributorProfileService {
	
	@Autowired
	private final DistributorProfileRepository distributorProfileRepository;
	private final DistributorRepository distributorRepository;
	private final SalespersonRepository salespersonRepository;

	@Autowired
    public DistributorProfileServiceImpl(DistributorProfileRepository distributorProfileRepository, DistributorRepository distributorRepository,SalespersonRepository salespersonRepository) {
        this.distributorProfileRepository = distributorProfileRepository;
		this.distributorRepository = distributorRepository;
		this.salespersonRepository=salespersonRepository;
    }

	@Override
	public DistributorProfile getDistributorProfileById(Long id) {
	    Optional<DistributorProfile> distributorProfile = distributorProfileRepository.findById(id);
	    if (!distributorProfile.isPresent()) {
	        throw new ResourceNotFoundException("Record not found with id: " + id);
	    }
	    return distributorProfile.get();
	}
	
	 @Override
	    public List<DistributorProfileDTO> getAllDistributorProfiles(int page, int size) {
	        Pageable pageable = PageRequest.of(page, size);
	        Page<DistributorProfileDTO> productPage = distributorProfileRepository.findAllProfiles(pageable);
	        return productPage.getContent();
	    }
	    @Override
	    public List<DistributorProfileDTO> searchDistributorProfiles(String query, int page, int size) {
	        Pageable pageable = PageRequest.of(page, size);
	        Page<DistributorProfileDTO> productPage = distributorProfileRepository.findDistributorIdAndDistributorName(query, pageable);
	        return productPage.getContent();
	    }

	@Override
	public List<DistributorProfile> getAllDistributorProfile() {
		
		List<DistributorProfile> distributorProfile = distributorProfileRepository.findByIsActiveTrue();
        if (distributorProfile.isEmpty() ) {
            throw new ResourceNotFoundException("No categories found " + distributorProfile.size());
        }
        return distributorProfile;
	}
	@Override
	public List<String> getAllLocations(String locationType) {
        switch (locationType) {
            case "city":
                return distributorProfileRepository.findAllCities();
            case "region":
                return distributorProfileRepository.findAllRegions();
            case "zone":
                return distributorProfileRepository.findAllZones();
            default:
            	 throw new IllegalArgumentException("Invalid location type: " + locationType);
 
        }
    }

	@Override
	public void deleteDistributorProfile(Long Id) {
		Optional<DistributorProfile> distributorDb = distributorProfileRepository.findById(Id);
		Optional<Distributor> distributorDb1 = distributorRepository.findByDistributorProfile(distributorDb.get());
        if (distributorDb.isPresent()&& distributorDb1.isPresent()) {
        	distributorProfileRepository.delete(distributorDb.get());
        	distributorRepository.delete(distributorDb1.get());
        } else {
            throw new ResourceNotFoundException("Record not found with id: " + Id);
        }
	}

	@Transactional
	@Override
	public DistributorProfile createDistributorProfile(DistributorProfile distributorProfile, Long salespersonId) {
	    if (distributorProfile.getAddress() == null || distributorProfile.getAddress().isEmpty()) {
	        throw new IllegalArgumentException("Address cannot be null or empty.");
	    }
	    if (distributorProfileRepository.existsByEmail(distributorProfile.getEmail())) {
	        throw new IllegalArgumentException("Distributor profile with the same email already exists.");
	    }
	    
	    DistributorProfile distributorProfile2=new DistributorProfile();
	    distributorProfile2= distributorProfileRepository.save(distributorProfile);
	    Distributor d= new Distributor();
	    Optional<Salesperson> existingSalesperson = salespersonRepository.findById(salespersonId);
	    if(existingSalesperson.isPresent()) {
		    d.setSalesperson(existingSalesperson.get());
	    }
	    d.setDistributorProfile(distributorProfile2);
	    distributorRepository.save(d);
	    return distributorProfile2;
	}


	@Override
	public DistributorProfile updateDistributorProfile(Long id, DistributorProfile updatedProfile) {
	    Optional<DistributorProfile> distributorProfileDb = distributorProfileRepository.findById(id);
	    if (distributorProfileDb.isPresent()) {
	        DistributorProfile distributorProfileToUpdate = distributorProfileDb.get();
	        if (updatedProfile.getAddress() != null && !updatedProfile.getAddress().isEmpty()) {
	            distributorProfileToUpdate.setAddress(updatedProfile.getAddress());
	        }

	        if (updatedProfile.getState() != null && !updatedProfile.getState().isEmpty()) {
	            distributorProfileToUpdate.setState(updatedProfile.getState());
	        }

	        if (updatedProfile.getAgencyName() != null && !updatedProfile.getAgencyName().isEmpty()) {
	            distributorProfileToUpdate.setAgencyName(updatedProfile.getAgencyName());
	        }

	        if (updatedProfile.getContactPerson() != null && !updatedProfile.getContactPerson().isEmpty()) {
	            distributorProfileToUpdate.setContactPerson(updatedProfile.getContactPerson());
	        }

	        if (updatedProfile.getContactNumber() != null && !updatedProfile.getContactNumber().isEmpty()) {
	            distributorProfileToUpdate.setContactNumber(updatedProfile.getContactNumber());
	        }

	        if (updatedProfile.getEmail() != null && !updatedProfile.getEmail().isEmpty()) {
	            distributorProfileToUpdate.setEmail(updatedProfile.getEmail());
	        }
	        if (updatedProfile.getGstNo() != null && !updatedProfile.getGstNo().isEmpty()) {
	            distributorProfileToUpdate.setGstNo(updatedProfile.getGstNo());
	        }
	        if (updatedProfile.getPanNo() != null && !updatedProfile.getPanNo().isEmpty()) {
	            distributorProfileToUpdate.setPanNo(updatedProfile.getPanNo());
	        }
	        distributorProfileToUpdate.setIsActive(updatedProfile.getIsActive() != null ? updatedProfile.getIsActive() : true);
	        return distributorProfileRepository.save(distributorProfileToUpdate);
	    } else {
	        throw new ResourceNotFoundException("Record not found with id: " + id);
	    }
	}

	@Override
	public Long countAllDistributorProfile() {
		return distributorProfileRepository.countAllDistributorProfile();
	}

	@Override
	public void deactivateActivateDistributorProfile(Long id) {
	        Optional<DistributorProfile> distributorProfile = distributorProfileRepository.findById(id);
	        if (distributorProfile.isPresent()) {
	        	DistributorProfile distributor=distributorProfile.get();
	        	boolean newStatus = !distributor.getIsActive(); 

	        	distributor.setIsActive(newStatus);
	        	distributorProfileRepository.save(distributor);
	            }
	        	 
	    else {
	            throw new ResourceNotFoundException("Record not found with id: " + id);
	        }
	      }
}
