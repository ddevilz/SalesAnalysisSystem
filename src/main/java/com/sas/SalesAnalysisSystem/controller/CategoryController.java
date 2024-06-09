package com.sas.SalesAnalysisSystem.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sas.SalesAnalysisSystem.exception.CustomErrorResponse;
import com.sas.SalesAnalysisSystem.exception.ResourceNotFoundException;
import com.sas.SalesAnalysisSystem.models.Category;
import com.sas.SalesAnalysisSystem.models.CategoryDTO;
import com.sas.SalesAnalysisSystem.models.ProductDTO;
import com.sas.SalesAnalysisSystem.repository.CategoryRepository;
import com.sas.SalesAnalysisSystem.service.category.CategoryService;
import com.sas.SalesAnalysisSystem.service.product.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
	
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/all")
    public ResponseEntity<Object> getAllActiveCategories() {
        try {
            List<Category> categories = categoryService.getAllActiveCategories();
            return ResponseEntity.ok().body(categories);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No categories found");
        }
    }
    
    @GetMapping("/all-inactive-categories")
    public ResponseEntity<Object> getAllInactiveCategories() {
        try {
            List<Category> categories = categoryService.getAllInActiveCategories();
            return ResponseEntity.ok().body(categories);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No categories found");
        }
    }
    @GetMapping("/categorieslist")
    public List<CategoryDTO> getCategories(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            return categoryService.searchCategories(search, page, size);
        } else {
            return categoryService.getAllCategories(page, size);
        }
    }

    @GetMapping("/{id}") 
    public ResponseEntity<Object> getCategoryById(@PathVariable("id") Long id) {
        try {
            Category category = categoryService.getCategoryById(id);
            return ResponseEntity.ok().body(category);
        } catch (ResourceNotFoundException e) {
        	CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("No categories Found for this id - "+ id);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setPath("/api/v1/categories/all/" + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }


    @PostMapping("/add-category")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        Category createdCategory = categoryService.createCategory(category);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }
    
    @PutMapping("update-category/{id}")
    public ResponseEntity<Object> updateCategory(@PathVariable("id") Long id, @Valid @RequestBody Category category) {
        try {
            Category updatedCategory = categoryService.updateCategory(id, category);
            return ResponseEntity.ok().body(updatedCategory);
        } catch (ResourceNotFoundException e) {
        	CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("No categories Found for this id - "+ id);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setPath("/api/v1/categories/all/" + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
    
    @PutMapping("/category-status")
    public ResponseEntity<Object> toggleStatusCategory(@RequestParam("id") Long id) {
        try {
            categoryService.toggleStatusCategory(id);
            return ResponseEntity.ok("Category successfully deleted.");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found with id: " + id);
        }
    }
    
    @GetMapping("/count-active")
    public ResponseEntity<Long> countAllCategories() {
        Long count = categoryService.countAllActiveCategories();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/count-inactive")
    public ResponseEntity<Long> countAllActiveProducts() {
		Long count = categoryService.countAllInActiveCategories();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/with-product-count")
    public List<Map<String, Object>> getAllCategoriesWithProductCount() 
    {
        List<Category> categories = categoryService.getAllCategories();

        List<Map<String, Object>> categoryList = new ArrayList<>();
        for (Category category : categories) {
            Long categoryId = category.getId();
            String categoryName = category.getCategoryName();
            boolean categoryStatus = category.getIsActive();
            Long productCount = categoryService.countProductsByCategory(categoryId);

            Map<String, Object> categoryInfo = new HashMap<>();
            categoryInfo.put("id", categoryId);
            categoryInfo.put("name", categoryName);
            categoryInfo.put("status", categoryStatus);
            categoryInfo.put("productCount", productCount);

            categoryList.add(categoryInfo);
        }
        return categoryList;
      }
    
    @GetMapping("/top-selling-categories")
    public ResponseEntity<Object> getTopSellingCategoriesWithDetails(
            @RequestParam("fromDate") LocalDate fromDate,
            @RequestParam("toDate") LocalDate toDate,
            @RequestParam("status") Boolean status) {
        try {
            List<Map<String, Object>> topSellingCategories = categoryService.getTopSellingCategoriesWithDetails(fromDate, toDate,status);
            return ResponseEntity.ok().body(topSellingCategories);
        } catch (Exception e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setError("Internal Server Error");
            errorResponse.setMessage("An internal server error occurred while processing the request.");
            errorResponse.setPath("/api/v1/top-selling-categories");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
