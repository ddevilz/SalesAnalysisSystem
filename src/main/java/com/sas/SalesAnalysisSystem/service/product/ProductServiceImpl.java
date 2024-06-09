package com.sas.SalesAnalysisSystem.service.product;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.sas.SalesAnalysisSystem.exception.ResourceNotFoundException;
import com.sas.SalesAnalysisSystem.models.Category;
import com.sas.SalesAnalysisSystem.models.Product;
import com.sas.SalesAnalysisSystem.repository.CategoryRepository;
import com.sas.SalesAnalysisSystem.repository.InvoiceRepository;
import com.sas.SalesAnalysisSystem.repository.ProductRepository;

import com.sas.SalesAnalysisSystem.models.ProductDTO;

@Service
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InvoiceRepository invoiceRepository;
    
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository,
			InvoiceRepository invoiceRepository) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
		this.invoiceRepository = invoiceRepository;
	}

    @Override
    public Product createProduct(Product product) {
    	Optional<Category> categoryOptional = categoryRepository.findById(product.getCategory().getId());
        if (categoryOptional.isPresent()) {
            return productRepository.save(product);
        } else {
            throw new ResourceNotFoundException("Category not found with id: " + product.getCategory().getId());
        }
        
    }
    
	public List<Product> getProductsByIds(List<Long> productIds) {
        return productRepository.findAllById(productIds);
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        Optional<Product> productDb = productRepository.findById(id);
    	Optional<Category> categoryOptional = categoryRepository.findById(product.getCategory().getId());
    	if (!categoryOptional.isPresent()) {
            throw new ResourceNotFoundException("Category not found with id: " + product.getCategory().getId());
        }
        if (productDb.isPresent()) {
            Product productUpdate = productDb.get();
            productUpdate.setProductName(product.getProductName());
            productUpdate.setPrice(product.getPrice());
            productUpdate.setHSNSAC(product.getHSNSAC());
            productUpdate.setCategory(product.getCategory());
            productUpdate.setIsActive(product.getIsActive());
            return productRepository.save(productUpdate);
        } else {
            throw new ResourceNotFoundException("Record not found with id: " + product.getId());
        }
    }

    @Override
    public List<Product> getAllInActiveProducts() {
        List<Product> products = productRepository.findByIsActiveFalse();
        if (products.isEmpty()){
            throw new ResourceNotFoundException("No Product found");
        }
        return products;
    }
    
    @Override
    public List<Product> getAllActiveProducts() {
        List<Product> products = productRepository.findByIsActiveTrue();
        if (products.isEmpty()){
            throw new ResourceNotFoundException("No Product found");
        }
        return products;
    }
    @Override
    public List<ProductDTO> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> productPage = productRepository.findAllProducts(pageable);
        return productPage.getContent();
    }
    @Override
    public List<ProductDTO> searchProducts(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> productPage = productRepository.findProductIdAndProductName(query, pageable);
        return productPage.getContent();
    }
    

    @Override
    public Product getProductById(Long productId) {
        Optional<Product> productDb = productRepository.findById(productId);
        if (productDb.isEmpty()) {
            throw new ResourceNotFoundException("Record not found with id: " + productId);
        }
        return productDb.get();
    }


    @Override
    public void toggleStatusProduct(Long productId) {
        Optional<Product> productDb = productRepository.findById(productId);
        if (productDb.isPresent()) {
        	Product product = productDb.get();
        	if(product.getIsActive()==true) {
        		product.setIsActive(false);
        		productRepository.save(product);
        	}
        	else {
        		product.setIsActive(true);
        		productRepository.save(product);
        	}
        } else {
            throw new ResourceNotFoundException("Record not found with id: " + productId);
        }
    }

    @Override
    public Long countActiveProducts() {
        return productRepository.countAllActiveProducts(true);
    }
    
    @Override
    public Long countInActiveProducts() {
        return productRepository.countAllActiveProducts(false);
    }

    @Override
    public List<Map<String, Object>> getTopSellingProductsWithDetails(LocalDate fromDate, LocalDate toDate,Boolean status) {
    	LocalDateTime fromDateC = LocalDateTime.parse(fromDate + "T00:00:00");
    	LocalDateTime toDateC = LocalDateTime.parse(toDate + "T23:59:59");
        List<Object[]> topSellingProducts = invoiceRepository.findTopSellingProducts(fromDateC, toDateC,status);
        List<Product> allProducts;
        if(status == true) {
        	  allProducts = productRepository.findByIsActiveTrue();
        }
        else{
        	allProducts = productRepository.findByIsActiveFalse();
        }
        List<Map<String, Object>> result = new ArrayList<>();

        Map<Long, Integer> topSellingQuantities = topSellingProducts.stream()
                .collect(Collectors.toMap(
                        obj -> ((Product) obj[0]).getId(),
                        obj -> {
                            Object quantityObj = obj[1];
                            if (quantityObj instanceof Integer) {
                                return (Integer) quantityObj;
                            } else if (quantityObj instanceof Long) {
                                return ((Long) quantityObj).intValue(); 
                            } else {
                                throw new IllegalArgumentException("Unexpected quantity type: " + quantityObj.getClass());
                            }
                        },
                        Integer::max
                ));

        for (Product product : allProducts) {
            Map<String, Object> productDetails = new LinkedHashMap<>(); 

            productDetails.put("productName", product.getProductName());
            productDetails.put("productPrice", product.getPrice());

            int quantitySold = topSellingQuantities.getOrDefault(product.getId(), 0);
            productDetails.put("productQuantity", quantitySold);

            double totalPrice = product.getPrice() * quantitySold;
            productDetails.put("totalPrice", totalPrice);

            result.add(productDetails); 
        }

        return result;
    }

}
