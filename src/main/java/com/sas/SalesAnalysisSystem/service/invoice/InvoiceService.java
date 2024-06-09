package com.sas.SalesAnalysisSystem.service.invoice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sas.SalesAnalysisSystem.dto.DistributorDetails;
import com.sas.SalesAnalysisSystem.models.Category;
import com.sas.SalesAnalysisSystem.models.Invoice;
import com.sas.SalesAnalysisSystem.models.Product;
import com.sas.SalesAnalysisSystem.models.ProductDetails;

public interface InvoiceService {
	Invoice createInvoice(Invoice invoice);
	Page<Invoice> getAllInvoices(Pageable page);
	void deleteInvoice(Long invoice_number);
	Invoice updateInvoice(Long id, Invoice updatedInvoice);
	Invoice getInvoiceById(Long id);
	void addProductToInvoice(Long invoiceId, Map<Long, ProductDetails> productQuantities);
	List<Invoice> findInvoicesBetweenDates(LocalDate startDate, LocalDate endDate);
	Map<String, Double> getTotalSumOfInvoicesByMonth();
	Map<String, Double> getTotalSumOfInvoicesByFinancialYear();
	List<Object[]> findTop5ProductsByInvoiceId(Long invoiceId);
	List<Object[]> findTopSellingProductsByCity(String city, Optional<Integer> year, Optional<String> monthName,
			String interval, Optional<String> customFromDate, Optional<String> customToDate);
	List<Object[]> findTopSellingProductsByRegion(String region, Optional<Integer> year, Optional<String> monthName,
			String interval, Optional<String> customFromDate, Optional<String> customToDate);
	List<Object[]> findTopSellingProductsByZone(String zone, Optional<Integer> year, Optional<String> monthName,
			String interval, Optional<String> customFromDate, Optional<String> customToDate);
	
	List<Object[]> findTopSellingProducts(String interval, Optional<Integer> year, Optional<String> monthName,
			Optional<String> customFromDate, Optional<String> customToDate);
	List<Object[]> findLeastSellingProducts(String interval, Optional<Integer> year, Optional<String> monthName,
			Optional<String> customFromDateStr, Optional<String> customToDateStr);

	List<Object[]> findLeastSellingProductsByRegion(String region, Optional<Integer> year, Optional<String> monthName,
			String interval, Optional<String> customFromDate, Optional<String> customToDate);
	List<Object[]> findLeastSellingProductsByZone(String zone, Optional<Integer> year, Optional<String> monthName,
			String interval, Optional<String> customFromDate, Optional<String> customToDate);
	List<Object[]> findLeastSellingProductsByCity(String city, Optional<Integer> year, Optional<String> monthName,
			String interval, Optional<String> customFromDate, Optional<String> customToDate);
	List<Object[]> findTop5SellingProductsByCategory(Optional<Integer> year, Optional<String> monthName,
			String interval, Optional<String> customFromDate, Optional<String> customToDate);
	List<Category> findAllProductsInInvoices();
	List<Object[]> findTop5LeastProductsByCategory(Optional<Integer> year, Optional<String> monthName, String interval,
			Optional<String> customFromDate, Optional<String> customToDate);
	List<Object[]> findLeastSellingCategoriesByCityRegionAndZone(Optional<String> city, Optional<String> region,
			Optional<String> zone, Optional<Integer> year, Optional<String> monthName, String interval,
			Optional<String> customFromDate, Optional<String> customToDate);
	List<Object[]> findMostSellingCategoriesByCityRegionAndZone(Optional<String> city, Optional<String> region,
			Optional<String> zone, Optional<Integer> year, Optional<String> month, String interval, Optional<String> of,
			Optional<String> of2);
	Map<String, Double> getTotalSumOfInvoicesByMonth(LocalDate customStartDate, LocalDate customEndDate);
	List<Object[]> getInvoiceProductDetails(Long invoiceId);
	void updateProductToInvoice(Long invoiceId, Map<Long, ProductDetails> productQuantities);
	
	List<String> findAllInvoiceNumbers();
	List<String> findAllIRNs();
	List<Map<String, Object>> totalInvoiceAmountAndExpenses(Optional<Long> salespersonId, String interval,
			Optional<Integer> year, Optional<String> monthName, Optional<String> customFromDate,
			Optional<String> customToDate, Boolean status);
	List<Invoice> getAllInvoices();
	void removeProductFromInvoice(Long invoiceId, Long productId);
	Page<Invoice> searchInvoicesByReceivedFlag(Boolean isReceived,String searchQuery, Pageable pageable);

	Page<Invoice> searchInvoices(String searchQuery, Pageable pageable);
	Page<Invoice> findByTotalAmount( String searchQuery, Pageable pageable);
	Page<Invoice> findByGSTNo(String searchQuery, Pageable pageable);

	Page<Invoice> findByTotalAmountWithFlag(Boolean isReceived, String searchQuery, Pageable pageable);
	Page<Invoice> findByGSTNoWithFlag(Boolean isReceived, String searchQuery, Pageable pageable);
	Page<Invoice> getAllInvoicesByIsReceived(Boolean isReceived, Pageable page);
	void toggleStatus(Long id);

	Page<Map<String, Object>> findByDistributorAgencyName(String searchQuery, Pageable pageable);
	Page<Map<String, Object>> findAllDistributorsWithDetailsByDistributorIds(List<Long> distributorIds, Pageable page);
	
	List<Object[]> findProductsSoldToDistributorThisMonthAndSameMonthLastYear(Long distributorId);
	Page<DistributorDetails> findInvoicesByInvoiceAge(int days, boolean status, String search, Pageable page);
	List<Map<String, Object>> findAllDistributorsWithDetailsByDistributorId(Long distributorId, Optional<Integer> year,
			Optional<String> monthName, String interval, Optional<String> customFromDate, Optional<String> customToDate
			);
	Page<Map<String, Object>> findAllDistributorsWithDetails(Long productId, Long categoryId, String invoiceNumber,
			String searchQuery, Optional<Integer> year, Optional<String> monthName, String interval,
			Optional<String> customFromDate, Optional<String> customToDate, Pageable page);
//	byte[]  generatePdfForDistributor(Long distributorId, LocalDateTime startDate, LocalDateTime endDate)
//			throws Exception;
	byte[] generatePdfForDistributorWithInterval(Long distributorId, Optional<Integer> year, Optional<String> monthName,
			String interval, Optional<String> customFromDate, Optional<String> customToDate) throws Exception;
	Page<Map<String, Object>> findAllDistributorsWithDetailsAndSeperatedFilters(String productName, String categoryName,
			String invoiceNumber, Optional<String> searchQuery, Optional<Integer> year, Optional<String> monthName,
			String interval, Optional<String> distributor, Optional<String> salespersonName,
			Optional<String> invoiceSearch, Optional<String> customFromDate, Optional<String> customToDate,
			Pageable page);
	
	
	
	
	
	
}
