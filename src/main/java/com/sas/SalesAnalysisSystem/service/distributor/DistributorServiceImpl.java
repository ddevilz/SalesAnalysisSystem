package com.sas.SalesAnalysisSystem.service.distributor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sas.SalesAnalysisSystem.exception.ResourceNotFoundException;
import com.sas.SalesAnalysisSystem.models.Distributor;
import com.sas.SalesAnalysisSystem.models.DistributorProfile;
import com.sas.SalesAnalysisSystem.models.DistributorSalespersonDTO;
import com.sas.SalesAnalysisSystem.models.Product;
import com.sas.SalesAnalysisSystem.models.Salesperson;
import com.sas.SalesAnalysisSystem.repository.DistributorProfileRepository;
import com.sas.SalesAnalysisSystem.repository.DistributorRepository;
import com.sas.SalesAnalysisSystem.repository.InvoiceRepository;
import com.sas.SalesAnalysisSystem.repository.ProductRepository;
import com.sas.SalesAnalysisSystem.repository.SalespersonRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class DistributorServiceImpl implements DistributorService{
	 private final DistributorRepository distributorRepository;
	 private final ProductRepository productRepository;
	 private final DistributorProfileRepository distributorProfileRepository;
	 private final InvoiceRepository invoiceRepository;
	 private final SalespersonRepository salespersonRepository;
	 
	 
	 @Autowired
	    public DistributorServiceImpl(DistributorRepository distributorRepository, ProductRepository productRepository,
			DistributorProfileRepository distributorProfileRepository, InvoiceRepository invoiceRepository,SalespersonRepository salespersonRepository) {
		this.distributorRepository = distributorRepository;
		this.productRepository = productRepository;
		this.distributorProfileRepository = distributorProfileRepository;
		this.invoiceRepository = invoiceRepository;
		this.salespersonRepository=salespersonRepository;
	}

		@Override
	    public List<Distributor> getAllActiveDistributors() {
	        List<Distributor> distributors = distributorRepository.findByIsActiveTrue();
	       
	        if (distributors.isEmpty()) {
	            throw new ResourceNotFoundException("No distributors found");
	        }
	        return distributors;
	    }
		
		@Override
	    public List<Distributor> getAllInActiveDistributors() {
	        List<Distributor> distributors = distributorRepository.findByIsActiveFalse();
	       
	        if (distributors.isEmpty()) {
	            throw new ResourceNotFoundException("No distributors found");
	        }
	        return distributors;
	    }

	    @Override
	    public Distributor getDistributorById(Long id) {
	        Optional<Distributor> distributor = distributorRepository.findById(id);
	        if (distributor.isEmpty()) {
	            throw new ResourceNotFoundException("Distributor not found with id: " + id);
	        }
	        return distributor.get();
	    }
	    @Override
	    public List<Distributor> getDistributorsBySalespersonId(Long salespersonId) {
	        return distributorRepository.findBySalespersonId(salespersonId);
	    }

	    @Override
	    public List<Product> getProductsByDistributorId(Long distributorId) {
	        Optional<Distributor> distributor = distributorRepository.findById(distributorId);
	        if (distributor.isEmpty()) {
	            throw new ResourceNotFoundException("Distributor not found with id: " + distributorId);
	        }
	        return distributor.get().getProducts();
	    }
	    
	    @Override
	    public List<Distributor> findDistributorsWithNoSalesperson() {
	        return distributorRepository.findDistributorsWithNoSalesperson();
	    }
	    
	    
	    // service to add single product
	    @Override
	    public void addProductToDistributor(Long distributorId, Long productId) {
	        Optional<DistributorProfile> distributorProfile = 					distributorProfileRepository.findById(distributorId);
	        
	        if (distributorProfile.isPresent()) {
	            DistributorProfile distributorDb= distributorProfile.get();
	            Distributor newDistributor= new Distributor();
	            newDistributor.setDistributorProfile(distributorDb);
	            Optional<Product> optionalProduct = productRepository.findById(productId);
	            
	            if (optionalProduct.isPresent()) {
	                Product product = optionalProduct.get();
	                List<Product> productList= new ArrayList<>();
	                productList.add(product);
	                newDistributor.setProducts(productList);
	                distributorRepository.save(newDistributor);
	            } else {
	                throw new ResourceNotFoundException("Product not found with id: " + productId);
	            }
	        } else {
	            throw new ResourceNotFoundException("Distributor not found with id: " + distributorId);
	        }
	    }
	    
	    @Override
	    public void addProductsToDistributor(Long distributorId, List<Long> productIds) {
	        Optional<Distributor> distributorOptional = distributorRepository.findById(distributorId);
	        if (distributorOptional.isPresent()) {
	            Distributor distributor = distributorOptional.get();
	            List<Product> productsToAdd = new ArrayList<>();
	            for (Long productId : productIds) {
	                Optional<Product> productOptional = productRepository.findById(productId);
	                if (productOptional.isPresent()) {
	                    Product product = productOptional.get();
	                    if (!distributor.getProducts().contains(product)) {
	                        productsToAdd.add(product);
	                    } else {
	                        throw new IllegalStateException("Product with ID " + productId + " is already added to the distributor.");
	                    }
	                } else {
	                    throw new ResourceNotFoundException("Product not found with ID: " + productId);
	                }
	            }
	            distributor.getProducts().addAll(productsToAdd);
	            distributorRepository.save(distributor);
	        } else {
	            throw new ResourceNotFoundException("Distributor not found with ID: " + distributorId);
	        }
	    }


		@Override
		public DistributorProfile createDistributor(Long distributorProfileId) {
	        Optional<DistributorProfile> existingProfile = distributorProfileRepository.findById(distributorProfileId);
	        if (existingProfile.isPresent()) {
	            return existingProfile.get();
	        }  else {
                throw new ResourceNotFoundException("Distributor not found with id: " + distributorProfileId);
            }
	    }
	  
		@Override
		public List<Map<String, Object>> getTotalSalesByDistributors(LocalDate fromDate, LocalDate toDate,Boolean status) {
		    LocalDateTime fromDateTime = fromDate.atStartOfDay();
		    
		    LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);
		    List<Object[]> salesData = invoiceRepository.totalSalesByDistributors(fromDateTime, toDateTime,status);
		    Map<String, Map<String, Object>> distributorSalesMap = new HashMap<>();
		    List<Distributor> distributors;
		    if(status==true) {
		    	distributors = distributorRepository.findByIsActiveTrue();
		    }
		    else {
		    	distributors = distributorRepository.findByIsActiveFalse();
		    }
		    for (Distributor distributor : distributors) {
		        String agencyName = distributor.getDistributorProfile().getAgencyName();
		        String contactPerson = distributor.getDistributorProfile().getContactPerson();

		        Map<String, Object> distributorSalesInfo = new LinkedHashMap<>();
		        distributorSalesInfo.put("agencyName", agencyName);
		        distributorSalesInfo.put("contactPerson", contactPerson);
		        distributorSalesInfo.put("totalSales", 0.0);
		        distributorSalesInfo.put("totalQuantitySold", 0);

		        distributorSalesMap.put(agencyName, distributorSalesInfo);
		    }

		    for (Object[] rowData : salesData) {
		        String agencyName = (String) rowData[0];
		        int quantitySold = ((Number) rowData[3]).intValue(); 
		        double totalPrice = ((Number) rowData[5]).doubleValue(); 		        
		        Map<String, Object> distributorSalesInfo = distributorSalesMap.get(agencyName);
		        distributorSalesInfo.put("totalSales", (double) distributorSalesInfo.get("totalSales") + totalPrice);
		        distributorSalesInfo.put("totalQuantitySold", (int) distributorSalesInfo.get("totalQuantitySold") + quantitySold);
		    }

		    return new ArrayList<>(distributorSalesMap.values());
		}

		@Override
		@Transactional
		public void toggleStatusDistributor(Long id) {
		    Optional<Distributor> distributorDb = distributorRepository.findById(id);
		    if (distributorDb.isPresent()) {
		        Distributor distributor = distributorDb.get();
		        
		        Optional<DistributorProfile> distributorProfileDb = distributorProfileRepository.findById(distributor.getDistributorProfile().getId());
		        if (distributorProfileDb.isPresent()) {
		            DistributorProfile distributorProfile = distributorProfileDb.get();
		            boolean newStatus = !distributorProfile.getIsActive();

		            distributorProfile.setIsActive(newStatus);
		            distributorProfileRepository.save(distributorProfile);
		        } else {
		            throw new ResourceNotFoundException("Distributor Profile not found with id: " + id);
		        }
		        
		        boolean newStatus = !distributor.getIsActive();
		        distributor.setIsActive(newStatus);
		        distributorRepository.save(distributor);
		    } else {
		        throw new ResourceNotFoundException("Record not found with id: " + id);
		    }
		}

		@Override
		public List<DistributorSalespersonDTO> getDistributorSalespersonDetails() {
	        return distributorRepository.findDistributorSalespersonDetails();
	    }

		@Override
		public Distributor updateSalesperson(Long distributorId, Long salespersonId) {
		    Optional<Distributor> distributorOptional = distributorRepository.findById(distributorId);
		    if (distributorOptional.isPresent()) {
		        Distributor existingDistributor = distributorOptional.get();

		        Optional<Salesperson> salespersonOptional = salespersonRepository.findById(salespersonId);
		        if (salespersonOptional.isPresent()) {
		            Salesperson newSalesperson = salespersonOptional.get();

		            existingDistributor.setSalesperson(newSalesperson);

		            return distributorRepository.save(existingDistributor);
		        } else {
		            throw new ResourceNotFoundException("Salesperson with id " + salespersonId + " not found");
		        }
		    } else {
		        Optional<Distributor> distributorWithNoSalesperson = distributorRepository.findDistributorWithNoSalesperson(distributorId);
		        if (distributorWithNoSalesperson.isPresent()) {
		            Distributor existingDistributor = distributorWithNoSalesperson.get();
		            
		            Optional<Salesperson> salespersonOptional = salespersonRepository.findById(salespersonId);
		            if (salespersonOptional.isPresent()) {
		                Salesperson newSalesperson = salespersonOptional.get();
		                
		                existingDistributor.setSalesperson(newSalesperson);
		                
		                return distributorRepository.save(existingDistributor);
		            } else {
		                throw new ResourceNotFoundException("Salesperson with id " + salespersonId + " not found");
		            }
		        } else {
		            throw new ResourceNotFoundException("Distributor with id " + distributorId + " not found");
		        }
		    }
		}







}
