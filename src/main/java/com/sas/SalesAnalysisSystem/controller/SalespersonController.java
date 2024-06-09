package com.sas.SalesAnalysisSystem.controller;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sas.SalesAnalysisSystem.exception.CustomErrorResponse;
import com.sas.SalesAnalysisSystem.exception.ResourceNotFoundException;
import com.sas.SalesAnalysisSystem.models.CategoryDTO;
import com.sas.SalesAnalysisSystem.models.Distributor;
import com.sas.SalesAnalysisSystem.models.Salesperson;
import com.sas.SalesAnalysisSystem.service.salesperson.SalespersonService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/salespersons")
public class SalespersonController {

    @Autowired
    private SalespersonService salespersonService;

    @GetMapping("/all")
    public ResponseEntity<Object> getAllActiveSalespersons() {
        try {
            List<Salesperson> salespersons = salespersonService.getAllActiveSalespersons();
            return ResponseEntity.ok().body(salespersons);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No salespersons found");
        }
    }
    @GetMapping("/all-inactive")
    public ResponseEntity<Object> getAllInActiveSalespersons() {
        try {
            List<Salesperson> salespersons = salespersonService.getAllInActiveSalespersons();
            return ResponseEntity.ok().body(salespersons);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No salespersons found");
        }
    }	
    
    @GetMapping("/salespersonlist")
    public List<String> getCategories(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            return salespersonService.findAllSalespersonNames(search, page, size);
        } else {
            return salespersonService.getAllDistributorProfiles(page, size);
        }
    }
    
    
    @GetMapping("/salespersonlist-names-ids")
    public List<Object[]> getAllSalespersonNamesAndIDs(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return salespersonService.getAllDistributorProfilesWithId(page,size);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Object> getSalespersonById(@PathVariable("id") Long id) {
        try {
            Salesperson salesperson = salespersonService.getSalespersonById(id);
            return ResponseEntity.ok().body(salesperson);
        } catch (ResourceNotFoundException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("No salesperson Found for this id - " + id);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setPath("/api/v1/salespersons/all/" + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PostMapping("/add-salesperson")
    public ResponseEntity<Object> createSalesperson(@RequestParam("name") String name,
                                                    @RequestParam("contactNumber") String contactNumber,
                                                    @RequestParam("email") String email,
                                                    @RequestParam(value = "isActive", required = false, defaultValue = "true") Boolean isActive,
                                                    @RequestParam(value = "distributorIds", required = false) List<Long> distributorIds) {
        try {
            Salesperson createdSalesperson = salespersonService.createSalesperson(name, contactNumber, email, isActive, distributorIds);
            return new ResponseEntity<>(createdSalesperson, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            errorResponse.setError("Invalid Request");
            errorResponse.setMessage(e.getMessage());
            errorResponse.setPath("/api/v1/salespersons/add-salesperson");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setError("Internal Server Error");
            errorResponse.setMessage("An unexpected error occurred");
            errorResponse.setPath("/api/v1/salespersons/add-salesperson");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/update-salesperson/{id}")
    public ResponseEntity<Object> updateSalesperson(@PathVariable("id") Long id,
                                                     @RequestParam(value = "name", required = false) String name,
                                                     @RequestParam(value = "contactNumber", required = false) String contactNumber,
                                                     @RequestParam(value = "email", required = false) String email,
                                                     @RequestParam(value = "isActive", required = false) Boolean isActive) {
        try {
            Salesperson updatedSalesperson = salespersonService.updateSalesperson(id, name, contactNumber, email, isActive);
            return ResponseEntity.ok().body(updatedSalesperson);
        } catch (ResourceNotFoundException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("No salesperson found for this id - " + id);                			errorResponse.setMessage(e.getMessage());
            errorResponse.setPath("/api/v1/salespersons/all/" + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
    
    @PutMapping("/{salespersonId}/distributors")
    public ResponseEntity<String> updateSalespersonDistributor(
            @PathVariable Long salespersonId,
            @RequestBody List<Long> distributorIds) {
        try {
            String updatedSalesperson = salespersonService.updateSalespersonDistributor(salespersonId, distributorIds);
            return ResponseEntity.ok(updatedSalesperson);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @PutMapping("/salesperson-status")
    public ResponseEntity<Object> toggleSalesperson(@RequestParam("id") Long id) {
        try {
            salespersonService.toggleStatusSalesperson(id);
            return ResponseEntity.ok("Salesperson status successfully updated.");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Salesperson not found with id: " + id);
        }
    } 
    
    
    
}

