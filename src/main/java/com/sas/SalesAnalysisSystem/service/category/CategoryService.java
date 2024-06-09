package com.sas.SalesAnalysisSystem.service.category;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.sas.SalesAnalysisSystem.models.Category;
import com.sas.SalesAnalysisSystem.models.CategoryDTO;

public interface CategoryService {

    Category getCategoryById(Long categoryId);

    Category createCategory(Category category);

	Category updateCategory(Long id, Category category);

	Long countProductsByCategory(Long categoryId);

	void toggleStatusCategory(Long categoryId);

	List<Category> getAllActiveCategories();

	List<Category> getAllInActiveCategories();

	Long countAllActiveCategories();

	Long countAllInActiveCategories();

	List<Map<String, Object>> getTopSellingCategoriesWithDetails(LocalDate fromDate, LocalDate toDate, Boolean status);

	List<Category> getAllCategories();

	List<CategoryDTO> getAllCategories(int page, int size);

	List<CategoryDTO> searchCategories(String query, int page, int size);
}

