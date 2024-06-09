package com.sas.SalesAnalysisSystem.service.product;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.sas.SalesAnalysisSystem.models.Product;
import com.sas.SalesAnalysisSystem.models.ProductDTO;

public interface ProductService {
	Product getProductById(Long productId);

//	List<Product> getAllProduct();
	List<Product> getAllActiveProducts();


	Product updateProduct(Long id,Product product);

	Product createProduct(Product product);


	void toggleStatusProduct(Long id);

	Long countActiveProducts();
	
	Long countInActiveProducts();

	List<Product> getAllInActiveProducts();

	List<Map<String, Object>> getTopSellingProductsWithDetails(LocalDate fromDate, LocalDate toDate, Boolean status);

	List<ProductDTO> searchProducts(String query, int page, int size);

	List<ProductDTO> getAllProducts(int page, int size);
	

}
