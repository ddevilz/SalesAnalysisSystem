package com.sas.SalesAnalysisSystem.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sas.SalesAnalysisSystem.models.Category;
import com.sas.SalesAnalysisSystem.service.distributorInvoice.DistributorInvoiceService;

@RestController
@RequestMapping("/api/v1/invoice")
public class DistributorInvoiceController {
	
	@Autowired
	private final DistributorInvoiceService distributorInvoiceService;
	
	
	
	public DistributorInvoiceController(DistributorInvoiceService distributorInvoiceService) {
		this.distributorInvoiceService = distributorInvoiceService;
	}
	
	 @GetMapping("/top-distributors-by-region")
	    public ResponseEntity<Object> getTop5DistributorsByCityRegionAndZone(
	            @RequestParam(required = false) Optional<String> city,
	            @RequestParam(required = false) Optional<String> region,
	            @RequestParam(required = false) Optional<String> zone,
	            @RequestParam(required = false) Optional<Integer> year,
	            @RequestParam(required = false) Optional<String> month,
	            @RequestParam String interval,
	            @RequestParam(required = false) Optional<String> customFromDate,
	            @RequestParam(required = false) Optional<String> customToDate) {

	        try {
	            if (interval.equals("customdate")) {
	                if (customFromDate.isPresent() && customToDate.isPresent()) {
	                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	                    String fromDateTimeStr = customFromDate.get();
	                    String toDateTimeStr = customToDate.get();

	                    List<Object[]> topDistributors = distributorInvoiceService.findTop5DistributorsByCityRegionAndZone(
	                            city, region, zone, year, month, interval, customFromDate, customToDate);
	                    return ResponseEntity.ok(topDistributors);
	                } else {
	                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                            .body("Both customFromDate and customToDate must be provided for customdate interval.");
	                }
	            } else {
	                List<Object[]> topDistributors = distributorInvoiceService.findTop5DistributorsByCityRegionAndZone(
	                        city, region, zone, year, month, interval, null, null);
	                return ResponseEntity.ok(topDistributors);
	            }
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	        }
	    }
	 
	 @GetMapping("/least-distributors-by-region")
	    public ResponseEntity<Object> getLeast5DistributorsByCityRegionAndZone(
	            @RequestParam(required = false) Optional<String> city,
	            @RequestParam(required = false) Optional<String> region,
	            @RequestParam(required = false) Optional<String> zone,
	            @RequestParam(required = false) Optional<Integer> year,
	            @RequestParam(required = false) Optional<String> month,
	            @RequestParam String interval,
	            @RequestParam(required = false) Optional<String> customFromDate,
	            @RequestParam(required = false) Optional<String> customToDate) {

	        try {
	            if (interval.equals("customdate")) {
	                if (customFromDate.isPresent() && customToDate.isPresent()) {
	                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	                    String fromDateTimeStr = customFromDate.get();
	                    String toDateTimeStr = customToDate.get();

	                    List<Object[]> topDistributors = distributorInvoiceService.findLeast5DistributorsByCityRegionAndZone(
	                            city, region, zone, year, month, interval, customFromDate, customToDate);
	                    return ResponseEntity.ok(topDistributors);
	                } else {
	                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                            .body("Both customFromDate and customToDate must be provided for customdate interval.");
	                }
	            } else {
	                List<Object[]> topDistributors = distributorInvoiceService.findLeast5DistributorsByCityRegionAndZone(
	                        city, region, zone, year, month, interval, null, null);
	                return ResponseEntity.ok(topDistributors);
	            }
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	        }
	    }
	 
	 @GetMapping("/top-distributors-by-categories")
	    public ResponseEntity<Object> getTop5DistributorsByCategoriesCityRegionAndZone(
	            @RequestParam(required = false) Optional<String> city,
	            @RequestParam(required = false) Optional<String> region,
	            @RequestParam(required = false) Optional<String> zone,
	            @RequestParam(required = false) Optional<Integer> year,
	            @RequestParam(required = false) Optional<String> month,
	            @RequestParam String interval,
	            @RequestParam(required = false) Optional<String> customFromDate,
	            @RequestParam(required = false) Optional<String> customToDate) {

	        try {
	            if (interval.equals("customdate")) {
	                if (customFromDate.isPresent() && customToDate.isPresent()) {
	                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	                    String fromDateTimeStr = customFromDate.get();
	                    String toDateTimeStr = customToDate.get();

	                    List<Object[]> topDistributors = distributorInvoiceService.findTop5DistributorsByCategoriesCityRegionAndZone(
	                            city, region, zone, year, month, interval, customFromDate, customToDate);
	                    return ResponseEntity.ok(topDistributors);
	                } else {
	                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                            .body("Both customFromDate and customToDate must be provided for customdate interval.");
	                }
	            } else {
	                List<Object[]> topDistributors = distributorInvoiceService.findTop5DistributorsByCategoriesCityRegionAndZone(
	                        city, region, zone, year, month, interval, null, null);
	                return ResponseEntity.ok(topDistributors);
	            }
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	        }
	    }
	 
	 @GetMapping("/least-distributors-by-categories")
	    public ResponseEntity<Object> getLeast5DistributorsByCategoriesCityRegionAndZone(
	            @RequestParam(required = false) Optional<String> city,
	            @RequestParam(required = false) Optional<String> region,
	            @RequestParam(required = false) Optional<String> zone,
	            @RequestParam(required = false) Optional<Integer> year,
	            @RequestParam(required = false) Optional<String> month,
	            @RequestParam String interval,
	            @RequestParam(required = false) Optional<String> customFromDate,
	            @RequestParam(required = false) Optional<String> customToDate) {

	        try {
	            if (interval.equals("customdate")) {
	                if (customFromDate.isPresent() && customToDate.isPresent()) {
	                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	                    String fromDateTimeStr = customFromDate.get();
	                    String toDateTimeStr = customToDate.get();

	                    List<Object[]> topDistributors = distributorInvoiceService.findLeast5DistributorsByCategoriesCityRegionAndZone(
	                            city, region, zone, year, month, interval, customFromDate, customToDate);
	                    return ResponseEntity.ok(topDistributors);
	                } else {
	                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                            .body("Both customFromDate and customToDate must be provided for customdate interval.");
	                }
	            } else {
	                List<Object[]> topDistributors = distributorInvoiceService.findLeast5DistributorsByCategoriesCityRegionAndZone(
	                        city, region, zone, year, month, interval, null, null);
	                return ResponseEntity.ok(topDistributors);
	            }
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	        }
	    }
	 
	 @GetMapping("/top-distributors")
	    public ResponseEntity<?> findTopSellingDistributors(
	            @RequestParam(required = false) Optional<String> city,
	            @RequestParam(required = false) Optional<String> region,
	            @RequestParam(required = false) Optional<String> zone,
	            @RequestParam(required = false) Optional<Integer> year,
	            @RequestParam(required = false) Optional<String> month,
	            @RequestParam String interval,
	            @RequestParam String type ,
	            @RequestParam(required = false) Optional<String> customFromDate,
	            @RequestParam(required = false) Optional<String> customToDate) {

	        try {
	            if (interval.equals("customdate")) {
	                if (customFromDate.isPresent() && customToDate.isPresent()) {
	                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	                    String fromDateTimeStr = customFromDate.get();
	                    String toDateTimeStr = customToDate.get();

	                    List<Map<String, Object>> topDistributors = distributorInvoiceService.findTopSellingDistributors(
	                            city, region, zone, year, month, interval,type ,customFromDate, customToDate);
	                    return ResponseEntity.ok(topDistributors);
	                } else {
	                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                            .body("Both customFromDate and customToDate must be provided for customdate interval.");
	                }
	            } else {
	            	List<Map<String, Object>> topDistributors = distributorInvoiceService.findTopSellingDistributors(
	                        city, region, zone, year, month, interval,type ,null, null);
	                return ResponseEntity.ok(topDistributors);
	            }
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	        }
	    }
	 
	 @GetMapping("/totalsales-of-top-distributors")
	    public ResponseEntity<?> totalSalesByDistributors(
	            @RequestParam(required = false) Optional<String> city,
	            @RequestParam(required = false) Optional<String> region,
	            @RequestParam(required = false) Optional<String> zone,
	            @RequestParam(required = false) Optional<Integer> year,
	            @RequestParam(required = false) Optional<String> month,
	            @RequestParam String interval,
	            @RequestParam String type ,
	            @RequestParam(required = false) Optional<String> customFromDate,
	            @RequestParam(required = false) Optional<String> customToDate) {

	        try {
	            if (interval.equals("customdate")) {
	                if (customFromDate.isPresent() && customToDate.isPresent()) {
	                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	                    String fromDateTimeStr = customFromDate.get();
	                    String toDateTimeStr = customToDate.get();

	                    List<Map<String, Object>> topDistributors = distributorInvoiceService.findTopSellingDistributors(
	                            city, region, zone, year, month, interval,type ,customFromDate, customToDate);
	                    return ResponseEntity.ok(topDistributors);
	                } else {
	                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                            .body("Both customFromDate and customToDate must be provided for customdate interval.");
	                }
	            } else {
	            	List<Map<String, Object>> topDistributors = distributorInvoiceService.totalSalesByDistributors(
	                        city, region, zone, year, month, interval,type ,null, null);
	                return ResponseEntity.ok(topDistributors);
	            }
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	        }
	    }


	@GetMapping("/allproducts")
    public List<Category> getAllProductsInInvoices() {
        return distributorInvoiceService.findAllProductsInInvoices();
    }

}
