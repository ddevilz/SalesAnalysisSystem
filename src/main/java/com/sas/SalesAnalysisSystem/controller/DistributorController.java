package com.sas.SalesAnalysisSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sas.SalesAnalysisSystem.exception.CustomErrorResponse;
import com.sas.SalesAnalysisSystem.exception.ResourceNotFoundException;
import com.sas.SalesAnalysisSystem.models.Distributor;
import com.sas.SalesAnalysisSystem.models.DistributorProfile;
import com.sas.SalesAnalysisSystem.models.DistributorSalespersonDTO;
import com.sas.SalesAnalysisSystem.models.Product;
import com.sas.SalesAnalysisSystem.service.distributor.DistributorService;

import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;



@RestController
@RequestMapping("/api/v1/distributors")
public class DistributorController {
	@Autowired
    private DistributorService distributorService;

    @GetMapping("/all")
    public ResponseEntity<Object> getAllDistributors() {
        try {
            List<Distributor> distributors = distributorService.getAllActiveDistributors();
            return ResponseEntity.ok().body(distributors);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No distributors found");
        }
    }
    @GetMapping("/all-inactive")
    public ResponseEntity<Object> getAllInActiveDistributors() {
        try {
            List<Distributor> distributors = distributorService.getAllInActiveDistributors();
            return ResponseEntity.ok().body(distributors);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No distributors found");
        }
    }
    
    @GetMapping("/salesperson/{salespersonId}")
    public ResponseEntity<List<Distributor>> getDistributorsBySalespersonId(@PathVariable Long salespersonId) {
        List<Distributor> distributors = distributorService.getDistributorsBySalespersonId(salespersonId);
        return ResponseEntity.ok(distributors);
    }
    
    
    @GetMapping("/no-salesperson")
    public List<Distributor> getDistributorsWithNoSalesperson() {
        return distributorService.findDistributorsWithNoSalesperson();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getDistributorById(@PathVariable("id") Long id) {
        try {
            Distributor distributor = distributorService.getDistributorById(id);
            return ResponseEntity.ok().body(distributor);
        } catch (ResourceNotFoundException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("No distributor Found for this id - " + id);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setPath("/api/v1/distributors/all/" + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
    
    @PutMapping("/distributor-status")
    public ResponseEntity<Object> toggleStatusDistributor(@RequestParam("id") Long id) {
        try {
            distributorService.toggleStatusDistributor(id);
            return ResponseEntity.ok("distributor status change successfully.");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Distributor not found with id: " + id);
        }
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<Object> getProductsByDistributorId(@PathVariable("id") Long id) {
        try {
            List<Product> products = distributorService.getProductsByDistributorId(id);
            return ResponseEntity.ok().body(products);
        } catch (ResourceNotFoundException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("No products Found for distributor with id - " + id);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setPath("/api/v1/distributors/all/" + id + "/products");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
 
    
//    @PostMapping("/{distributorId}/add-product")
//    public ResponseEntity<Object> addProductToDistributor(
//            @PathVariable("distributorId") Long distributorId,
//            @RequestParam("productId") Long productId) {
//        try {
//            distributorService.addProductToDistributor(distributorId, productId);
//            return ResponseEntity.ok("Product added to distributor successfully.");
//        } catch (ResourceNotFoundException e) {
//            CustomErrorResponse errorResponse = new CustomErrorResponse();
//            errorResponse.setTimestamp(LocalDateTime.now());
//            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
//            errorResponse.setError("Distributor Id Not Found - " + distributorId);
//            errorResponse.setMessage(e.getMessage());
//            errorResponse.setPath("/api/v1/distributors/" + distributorId + "/add-product");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
//        } 
//    }
    
    @PostMapping("/{distributorId}/add-products")
    public ResponseEntity<Object> addProductsToDistributor(
            @PathVariable("distributorId") Long distributorId,
            @RequestBody List<Long> productIds)  {
        try {
        	System.out.println("Hey");
            distributorService.addProductsToDistributor(distributorId, productIds);
            return ResponseEntity.ok("Products added to distributor successfully.");
        } catch (ResourceNotFoundException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("Distributor Id Not Found - " + distributorId);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setPath("/api/v1/distributors/" + distributorId + "/add-products");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
    @GetMapping("/distributor-salesperson-details")
    public ResponseEntity<List<DistributorSalespersonDTO>> getDistributorSalespersonDetails() {
        List<DistributorSalespersonDTO> details = distributorService.getDistributorSalespersonDetails();
        return ResponseEntity.ok(details);
    }
 
    @PostMapping("/create/{distributorProfileId}")
    public ResponseEntity<Object> createDistributor(
            @PathVariable("distributorProfileId") Long distributorProfileId,
            @RequestParam("salespersonId") Long salespersonId) {
        try {
            DistributorProfile distributorProfile = distributorService.createDistributor(distributorProfileId);
            return ResponseEntity.ok(distributorProfile);
        } catch (ResourceNotFoundException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("Distributor Profile Not Found");
            errorResponse.setMessage(e.getMessage());
            errorResponse.setPath("/api/distributors/create/" + distributorProfileId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
    
    @GetMapping("/total-sales-by-distributors")
    public ResponseEntity<?> getTotalSalesByDistributors(
    		@RequestParam("fromDate") String fromDateStr,
            @RequestParam("toDate") String toDateStr,
            @RequestParam("status") Boolean status) {
    	List<Map<String, Object>> totalSumByMonth;
        LocalDate fromDate;
        LocalDate toDate;
        try {
        	fromDate = LocalDate.parse(fromDateStr);
            toDate = LocalDate.parse(toDateStr);
            totalSumByMonth = distributorService.getTotalSalesByDistributors(
                    LocalDate.parse(fromDateStr), 
                    LocalDate.parse(toDateStr),status);
        } catch (DateTimeParseException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            errorResponse.setError("Invalid date format provided.");
            errorResponse.setMessage("Invalid date format provided.");
            errorResponse.setPath("total-sum-by-month-custom-date");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (ResourceNotFoundException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("No total sum of invoices found for the specified date range.");
            errorResponse.setMessage("No total sum of invoices found for the specified date range.");
            errorResponse.setPath("total-sum-by-month-custom-date");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        return new ResponseEntity<>(totalSumByMonth, HttpStatus.OK);
    }
    
    @PutMapping("/{distributorId}/salesperson/{salespersonId}")
    public ResponseEntity<Distributor> updateDistributorSalesperson(
            @PathVariable Long distributorId,
            @PathVariable Long salespersonId) {
        try {
            Distributor updatedDistributor = distributorService.updateSalesperson(distributorId, salespersonId);
            return ResponseEntity.ok(updatedDistributor);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    
    

    
    

}
