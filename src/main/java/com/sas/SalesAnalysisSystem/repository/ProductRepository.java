package com.sas.SalesAnalysisSystem.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.sas.SalesAnalysisSystem.models.Category;
import com.sas.SalesAnalysisSystem.models.Product;
import com.sas.SalesAnalysisSystem.models.ProductDTO;

public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findByIsActiveTrue();
	List<Product> findByIsActiveFalse();

    @Transactional
    @Modifying
    @Query("UPDATE Product p SET p.isActive = :status WHERE p.category = :category")
    void updateProductsStatusByCategory(@Param("category") Category category, @Param("status") boolean status);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.isActive = :isActive")
    Long countAllActiveProducts(@Param("isActive") boolean isActive);
    
    @Query("SELECT new com.sas.SalesAnalysisSystem.models.ProductDTO(p.id, p.productName) FROM Product p WHERE LOWER(p.productName) LIKE %:name%")
    Page<ProductDTO> findProductIdAndProductName(@Param("name") String name, Pageable pageable);
    
	
	@Query("SELECT new com.sas.SalesAnalysisSystem.models.ProductDTO(p.id, p.productName) FROM Product p")
    Page<ProductDTO> findAllProducts(Pageable pageable);
    
}
