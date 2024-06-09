package com.sas.SalesAnalysisSystem.service.category;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.web.server.csrf.XorServerCsrfTokenRequestAttributeHandler;
import org.springframework.stereotype.Service;

import com.sas.SalesAnalysisSystem.exception.ResourceNotFoundException;
import com.sas.SalesAnalysisSystem.models.Category;
import com.sas.SalesAnalysisSystem.models.Product;
import com.sas.SalesAnalysisSystem.models.CategoryDTO;
import com.sas.SalesAnalysisSystem.repository.CategoryRepository;
import com.sas.SalesAnalysisSystem.repository.InvoiceRepository;
import com.sas.SalesAnalysisSystem.repository.ProductRepository;

@Service
public class CategoryServiceImpl implements CategoryService { //interface and classes
	
	private final CategoryRepository categoryRepository;
	private final InvoiceRepository invoiceRepository;
	private final ProductRepository productRepository; 

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, InvoiceRepository invoiceRepository,ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
		this.invoiceRepository = invoiceRepository;
		this .productRepository=productRepository;
    }


    @Override
    public Category createCategory(Category category) {
    	if (category.getCategoryName() == null || category.getCategoryName().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty.");
        }
    	if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
            throw new IllegalArgumentException("Category with the same name already exists.");
        }
    	Category savedCategory = categoryRepository.save(category);
        return savedCategory;
    }
    
    @Override
    public List<CategoryDTO> getAllCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryDTO> productPage = categoryRepository.findAllCategories(pageable);
        return productPage.getContent();
    }
    @Override
    public List<CategoryDTO> searchCategories(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryDTO> productPage = categoryRepository.findCategoryIdAndCategoryName(query, pageable);
        return productPage.getContent();
    }

    @Override
    public Category updateCategory(Long id,Category category) {
        Optional<Category> categoryDb = categoryRepository.findById(id);
        if (categoryDb.isPresent()) {
            Category categoryUpdate = categoryDb.get();
            categoryUpdate.setCategoryName(category.getCategoryName());
            categoryUpdate.setIsActive(category.getIsActive());
            return categoryRepository.save(categoryUpdate);
        } else {
            throw new ResourceNotFoundException("Record not found with id: " + id);
        }
    }
    
    @Override
    public List<Category> getAllCategories() {
        List<Category> activeCategories = categoryRepository.findAll();
        if (activeCategories.isEmpty()) {
            throw new ResourceNotFoundException("No active categories found");
        }
        return activeCategories;
    }
    @Override
    public List<Category> getAllActiveCategories() {
        List<Category> activeCategories = categoryRepository.findByIsActiveTrue();
        if (activeCategories.isEmpty()) {
            throw new ResourceNotFoundException("No active categories found");
        }
        return activeCategories;
    }
    @Override
    public List<Category> getAllInActiveCategories() {
        List<Category> activeCategories = categoryRepository.findByIsActiveFalse();
        if (activeCategories.isEmpty()) {
            throw new ResourceNotFoundException("No Inactive categories found");
        }
        return activeCategories;
    }

    @Override
    public Category getCategoryById(Long categoryId) {
    	Optional<Category> categoryDb = categoryRepository.findById(categoryId);
        if (categoryDb.isEmpty()) {
            throw new ResourceNotFoundException("Record not found with id: " + categoryId);
        }
        return categoryDb.get();
    }

    @Override
    public void toggleStatusCategory(Long categoryId)
    {
        Optional<Category> categoryDb = categoryRepository.findById(categoryId);
        if (categoryDb.isPresent()) {
        	Category category=categoryDb.get();
        	boolean newStatus = !category.getIsActive(); 

            category.setIsActive(newStatus);
            categoryRepository.save(category);

            productRepository.updateProductsStatusByCategory(category, newStatus);
            }
        	 
    else {
            throw new ResourceNotFoundException("Record not found with id: " + categoryId);
        }
      }


    @Override
    public Long countAllActiveCategories() {
    	 return categoryRepository.countAllActiveCategories(true);
    }
    
    @Override
    public Long countAllInActiveCategories() {
    	 return categoryRepository.countAllActiveCategories(false);
    }
    
    
    @Override
    public Long countProductsByCategory(Long categoryId) {
    	Optional<Category> category=categoryRepository.findById(categoryId);
    	if (category.isPresent()) {
    		Category categorydb=category.get();
        return categoryRepository.countProductsByCategory(categorydb);}
    	else {
    		 throw new ResourceNotFoundException("Record not found with id: " + categoryId);
    	}
    }

    @Override
    public List<Map<String, Object>> getTopSellingCategoriesWithDetails(LocalDate fromDate, LocalDate toDate,Boolean status) {
        LocalDateTime fromDateC = LocalDateTime.parse(fromDate + "T00:00:00");
        LocalDateTime toDateC = LocalDateTime.parse(toDate + "T23:59:59");
        List<Object[]> topSellingCategories = invoiceRepository.findTopSellingCategories(fromDateC, toDateC,status);
        
        List<Category> allCategories;
        if(status==true) {
        	 allCategories = categoryRepository.findByIsActiveTrue();
        }
        else {
        	allCategories = categoryRepository.findByIsActiveFalse();
        }

        Map<String, Map<String, Object>> categorySalesMap = new HashMap<>();

        for (Category category : allCategories) {
            String categoryName = category.getCategoryName();

            Map<String, Object> categorySalesInfo = new LinkedHashMap<>();
            categorySalesInfo.put("categoryName", categoryName);
            categorySalesInfo.put("totalQuantitySold", 0);
            categorySalesInfo.put("totalPrice", 0.0);

            categorySalesMap.put(categoryName, categorySalesInfo);
        }

        for (Object[] rowData : topSellingCategories) {
            String categoryName = (String) rowData[0];
            int quantitySold = ((Number) rowData[2]).intValue(); 
            double Price = ((Number) rowData[1]).doubleValue();
            
            double totalPrice=Price*quantitySold;
            
            Map<String, Object> categorySalesInfo = categorySalesMap.get(categoryName);
            categorySalesInfo.put("totalQuantitySold", (int) categorySalesInfo.get("totalQuantitySold") + quantitySold);
            categorySalesInfo.put("totalPrice", (double) categorySalesInfo.get("totalPrice") + totalPrice);
        }

        return new ArrayList<>(categorySalesMap.values());
    }




    
}
