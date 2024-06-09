package com.sas.SalesAnalysisSystem.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

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
import com.sas.SalesAnalysisSystem.models.Product;
import com.sas.SalesAnalysisSystem.models.ProductDTO;
import com.sas.SalesAnalysisSystem.service.product.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
	@Autowired
	private ProductService productService;
	
	@GetMapping("/all")
	public ResponseEntity<Object> getAllProduct() {
	    try {
	        List<Product> products = productService.getAllActiveProducts();
	        return ResponseEntity.ok().body( products);
	    } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No products found");
        }
	}
	 @GetMapping("/productlist")
	    public List<ProductDTO> getProducts(@RequestParam(defaultValue = "0") int page,
	                                        @RequestParam(defaultValue = "10") int size,
	                                        @RequestParam(required = false) String search) {
	        if (search != null && !search.isEmpty()) {
	            return productService.searchProducts(search, page, size);
	        } else {
	            return productService.getAllProducts(page, size);
	        }
	    }
	
	@GetMapping("/inactive-all")
	public ResponseEntity<Object> getAllInActiveProduct() {
	    try {
	        List<Product> products = productService.getAllInActiveProducts();
	        return ResponseEntity.ok().body( products);
	    } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No products found");
        }
	}
	
	
	@GetMapping("/{id}")
    public ResponseEntity<Object> getProductById(@PathVariable("id") Long id) {
        try {
            Product product = productService.getProductById(id);
            return ResponseEntity.ok().body(product);
        } catch (ResourceNotFoundException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("No product found for this id - " + id);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setPath("/api/v1/products/" + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
	
	@PostMapping("/add-product")
	public ResponseEntity<Object> createProduct(@Valid @RequestBody Product product){
        try {
    		Product createdProduct = productService.createProduct(product);
            return ResponseEntity.ok().body(createdProduct);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
	}
	
	@PutMapping("/update-product/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable("id") Long id, @Valid @RequestBody Product product) {
        try {
            Product updatedProduct = productService.updateProduct(id, product);
            return ResponseEntity.ok().body(updatedProduct);
        } catch (ResourceNotFoundException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("No product found for this id - " + id);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setPath("/api/v1/products/update-product/" + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
	
	@PutMapping("/product-status")
    public ResponseEntity<Object> toggleStatusProduct(@RequestParam("id") Long id) {
        try {
            productService.toggleStatusProduct(id);
            return ResponseEntity.ok("Product successfully deleted.");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found with id: " + id);
        }
    }
	
	
	@GetMapping("/count-active")
    public ResponseEntity<Long> countAllActiveProducts() {
		Long count = productService.countActiveProducts();
        return ResponseEntity.ok(count);
    }
	
	@GetMapping("/count-inactive")
    public ResponseEntity<Long> countAllInActiveProducts() {
		Long count = productService.countInActiveProducts();
        return ResponseEntity.ok(count);
    }
	
	 @GetMapping("/top-selling")
	    public List<Map<String, Object>> getTopSellingProductsWithDetails(
	            @RequestParam(required = true) String customFromDate,
	            @RequestParam(required = true) String customToDate,
	            @RequestParam(required = true) Boolean status
	           ) {
	        
	        try {

	            if (customFromDate != null && customToDate != null) {
	            	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate fromDate = LocalDate.parse(customFromDate, formatter);
                    LocalDate toDate = LocalDate.parse(customToDate , formatter);
	                System.out.println(fromDate);
	                return productService.getTopSellingProductsWithDetails(fromDate, toDate,status);
	            } else {
	                // Handle case when customFromDate or customToDate is not provided
	                throw new IllegalArgumentException("Both customFromDate and customToDate must be provided.");
	            }
	        } catch (DateTimeParseException e) {
	            // Handle invalid date format
	            throw new IllegalArgumentException("Invalid date format provided.");
	        }
	    }
	 }
