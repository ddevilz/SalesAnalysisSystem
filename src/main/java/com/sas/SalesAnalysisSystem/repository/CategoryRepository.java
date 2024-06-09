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
import com.sas.SalesAnalysisSystem.models.CategoryDTO;


public interface CategoryRepository extends JpaRepository<Category, Long> {
	boolean existsByCategoryName(String categoryName);
	 List<Category> findByIsActiveTrue();

	 List<Category> findByIsActiveFalse();

	    @Query("SELECT COUNT(c) FROM Category c WHERE c.isActive = :isActive")
	    Long countAllActiveCategories(@Param("isActive") boolean isActive);
	    
	@Query("SELECT COUNT(p) FROM Product p WHERE p.category = :category")
    Long countProductsByCategory(@Param("category") Category category);
	
	 @Query("SELECT new com.sas.SalesAnalysisSystem.models.CategoryDTO(p.id, p.categoryName) FROM Category p WHERE LOWER(p.categoryName) LIKE %:name%")
	    Page<CategoryDTO> findCategoryIdAndCategoryName(@Param("name") String name, Pageable pageable);
	    
		
		@Query("SELECT new com.sas.SalesAnalysisSystem.models.CategoryDTO(p.id, p.categoryName) FROM Category p")
	    Page<CategoryDTO> findAllCategories(Pageable pageable); 
	    
	

}
