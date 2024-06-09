package com.sas.SalesAnalysisSystem.service.salesperson;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sas.SalesAnalysisSystem.exception.ResourceNotFoundException;
import com.sas.SalesAnalysisSystem.models.Distributor;
import com.sas.SalesAnalysisSystem.models.Salesperson;
import com.sas.SalesAnalysisSystem.repository.DistributorRepository;
import com.sas.SalesAnalysisSystem.repository.SalespersonRepository;

import jakarta.transaction.Transactional;

@Service
public class salespersonServiceImpl implements SalespersonService{
	
	private final SalespersonRepository salespersonRepository ;
	private final DistributorRepository distributorRepository;

    @Autowired
    public salespersonServiceImpl(SalespersonRepository salespersonRepository , DistributorRepository distributorRepository) {
        this.salespersonRepository = salespersonRepository;
        this.distributorRepository=distributorRepository;
    }

    @Override
    public Salesperson getSalespersonById(Long salespersonId) {
        Optional<Salesperson> salespersonDb = salespersonRepository.findById(salespersonId);
        if (salespersonDb.isEmpty()) {
            throw new ResourceNotFoundException("Record not found with id: " + salespersonId);
        }
        return salespersonDb.get();
    }
    @Override
    public List<String> getAllDistributorProfiles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<String> salespersonpage = salespersonRepository.findAllSalespersonNames(pageable);
        return salespersonpage.getContent();
    }
    
    @Override
    public List<Object[]> getAllDistributorProfilesWithId(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> salespersonpage = salespersonRepository.findAllSalespersonNamesAndIDs(pageable);
        return salespersonpage.getContent();
    }
    
    @Override
    public List<String> findAllSalespersonNames(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<String> salespersonpage = salespersonRepository.findSalespersonName(query, pageable);
        return salespersonpage.getContent();
    }

    @Override
    public List<Salesperson> getAllActiveSalespersons() {
        List<Salesperson> salespersons = salespersonRepository.findByIsActiveTrue();
        if (salespersons.isEmpty()) {
            throw new ResourceNotFoundException("No salespersons found.");
        }
        return salespersons;
    }
    
    @Override
    public List<Salesperson> getAllInActiveSalespersons() {
        List<Salesperson> salespersons = salespersonRepository.findByIsActiveFalse();
        if (salespersons.isEmpty()) {
            throw new ResourceNotFoundException("No salespersons found.");
        }
        return salespersons;
    }


    @Override
    @Transactional
    public Salesperson createSalesperson(String name, String contactNumber, String email, Boolean isActive, List<Long> distributorIds) {

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Salesperson name cannot be null or empty.");
        }
        if (salespersonRepository.existsByName(name)) {
            throw new IllegalArgumentException("Salesperson with the same name already exists.");
        }

        Salesperson salesperson = new Salesperson();
        salesperson.setName(name);
        salesperson.setContactNumber(contactNumber);
        salesperson.setEmail(email);
        salesperson.setIsActive(isActive);
        Salesperson newSalesperson = salespersonRepository.save(salesperson);
        
        System.out.println(distributorIds);
        if (distributorIds != null && !distributorIds.isEmpty()) {
            System.out.println("hey");
            for (Long distributorId : distributorIds) {
                Distributor distributor = distributorRepository.findDistributorByIdWithNoSalesperson(distributorId);
                if(distributor!=null) {
                	distributor.setSalesperson(newSalesperson);
                	distributorRepository.save(distributor);
                }
                else {
        	        throw new ResourceNotFoundException("Salesperson is already assigned for this id: " + distributorId);
                }
            }
        }

        return newSalesperson;
    }


    @Override
    @Transactional
    public Salesperson updateSalesperson(Long id, String name, String contactNumber, String email, Boolean isActive) {
        Optional<Salesperson> optionalSalesperson = salespersonRepository.findById(id);
        if (optionalSalesperson.isPresent()) {
            Salesperson salesperson = optionalSalesperson.get();

            if (name != null) {
                salesperson.setName(name);
            }
            if (contactNumber != null) {
                salesperson.setContactNumber(contactNumber);
            }
            if (email != null) {
                salesperson.setEmail(email);
            }
            if (isActive != null) {
                salesperson.setIsActive(isActive);
            }

            Salesperson updatedSalesperson = salespersonRepository.save(salesperson);

            return updatedSalesperson;
        } else {
            throw new ResourceNotFoundException("Record not found with id: " + id);
        }
    }
    
    @Override
    @Transactional
    public String updateSalespersonDistributor(Long salespersonId, List<Long> distributorIds) {
        Optional<Salesperson> salespersonOptional = salespersonRepository.findById(salespersonId);
        if (!salespersonOptional.isPresent()) {
            throw new ResourceNotFoundException("Salesperson with id " + salespersonId + " not found");
        }

        Salesperson existingSalesperson = salespersonOptional.get();
        List<Distributor> existingDistributors = existingSalesperson.getDistributors();
        
        Set<Long> existingDistributorIds = existingDistributors.stream().map(Distributor::getId).collect(Collectors.toSet());
        
        for (Distributor existingDistributor : existingDistributors) {
            if (!distributorIds.contains(existingDistributor.getId())) {
                existingDistributor.setSalesperson(null);
                distributorRepository.save(existingDistributor);
            }
        }
        for (Long distributorId : distributorIds) {
            if (!existingDistributorIds.contains(distributorId)) {
                Distributor distributorOptional = distributorRepository.findDistributorByIdWithNoSalesperson(distributorId);
                if (distributorOptional != null) {
                    distributorOptional.setSalesperson(existingSalesperson);
                    distributorRepository.save(distributorOptional);
                } else {
                    throw new ResourceNotFoundException("Distributor with id " + distributorId + " not found or already assigned to another salesperson");
                }
            }
        }


            return "Updated sucessfully";
    }
    
    
    
    

	
	@Override
	@Transactional
	public void toggleStatusSalesperson(Long id) {
	    Optional<Salesperson> salespersonOptional = salespersonRepository.findById(id);
	    if (salespersonOptional.isPresent()) {
	        Salesperson salesperson = salespersonOptional.get();
	        boolean newStatus = !salesperson.getIsActive(); 
	        salesperson.setIsActive(newStatus);
	        salespersonRepository.save(salesperson);
	        
	        if (!newStatus) {
	            distributorRepository.nullifySalespersonIdsForInactiveSalespersons(id);
	        }
	    } else {
	        throw new ResourceNotFoundException("Record not found with id: " + id);
	    }
	}

	
	 @Override
	    public Long countAllActiveSalesperson() {
	    	 return salespersonRepository.countAllActiveSalesperson(true);
	    }
	    
	    @Override
	    public Long countAllInActiveSalesperson() {
	    	 return salespersonRepository.countAllActiveSalesperson(false);
	    }


}
