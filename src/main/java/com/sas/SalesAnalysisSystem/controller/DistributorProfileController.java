package com.sas.SalesAnalysisSystem.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sas.SalesAnalysisSystem.exception.CustomErrorResponse;
import com.sas.SalesAnalysisSystem.exception.ResourceNotFoundException;
import com.sas.SalesAnalysisSystem.models.CategoryDTO;
import com.sas.SalesAnalysisSystem.models.DistributorProfile;
import com.sas.SalesAnalysisSystem.models.DistributorProfileDTO;
import com.sas.SalesAnalysisSystem.service.distributorProfile.DistributorProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/distributorProfiles")
public class DistributorProfileController {

    @Autowired
    private DistributorProfileService distributorProfileService;

    @GetMapping("/all")
    public ResponseEntity<Object> getAllDistributorProfiles() {
        try {
            List<DistributorProfile> distributorProfiles = distributorProfileService.getAllDistributorProfile();
            return ResponseEntity.ok().body(distributorProfiles);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No distributor profiles found");
        }
    }
    @GetMapping("/locations")
    public List<String> getAllLocations(@RequestParam("locationType") String locationType) {
        try {
            return distributorProfileService.getAllLocations(locationType);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
    @GetMapping("/distributorslist")
    public List<DistributorProfileDTO> getProfiles(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            return distributorProfileService.searchDistributorProfiles(search, page, size);
        } else {
            return distributorProfileService.getAllDistributorProfiles(page, size);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getDistributorProfileById(@PathVariable("id") Long id) {
        try {
            DistributorProfile distributorProfile = distributorProfileService.getDistributorProfileById(id);
            return ResponseEntity.ok().body(distributorProfile);
        } catch (ResourceNotFoundException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("No distributorProfile Found for this id - "+ id);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setPath("/api/v1/distributorProfiles/all/" + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PostMapping("/add-distributorProfile")
    public ResponseEntity<DistributorProfile> createDistributorProfile(@Valid @RequestBody DistributorProfile distributorProfile, @RequestParam("salespersonId") Long SalespersonId) {
        DistributorProfile createdDistributorProfile = distributorProfileService.createDistributorProfile(distributorProfile,SalespersonId);
        return new ResponseEntity<>(createdDistributorProfile, HttpStatus.CREATED);
    }

    @PutMapping("update-distributorProfile/{id}")
    public ResponseEntity<Object> updateDistributorProfile(@PathVariable("id") Long id, @Valid @RequestBody DistributorProfile distributorProfile) {
        try {
            DistributorProfile updatedDistributorProfile = distributorProfileService.updateDistributorProfile(id, distributorProfile);
            return ResponseEntity.ok().body(updatedDistributorProfile);
        } catch (ResourceNotFoundException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("No distributorProfile Found for this id - "+ id);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setPath("/api/v1/distributorProfiles/all/" + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
    
    @PutMapping("/distributorProfile-status")
    public ResponseEntity<Object> deactivatActivateDistributorProfile(@RequestParam("id") Long id) {
        try {
            distributorProfileService.deactivateActivateDistributorProfile(id);
            return ResponseEntity.ok("Successfully updation of distributor Profile status is done.");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Distributor profile not found with id: " + id);
        }
    }

    @DeleteMapping("/delete-distributorProfile")
    public ResponseEntity<Object> deleteDistributorProfile(@RequestParam("id") Long id) {
        try {
            distributorProfileService.deleteDistributorProfile(id);
            return ResponseEntity.ok("Distributor profile successfully deleted.");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Distributor profile not found with id: " + id);
        }
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> countAllCategories() {
        Long count = distributorProfileService.countAllDistributorProfile();
        return ResponseEntity.ok(count);
    }
}
