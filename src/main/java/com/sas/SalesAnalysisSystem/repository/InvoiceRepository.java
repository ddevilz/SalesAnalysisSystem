package com.sas.SalesAnalysisSystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;

import com.sas.SalesAnalysisSystem.dto.DistributorDetails;
import com.sas.SalesAnalysisSystem.models.Category;
import com.sas.SalesAnalysisSystem.models.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
	
	 Page<Invoice> findAllByOrderByInvoiceDateDesc(Pageable pageable);
	 Page<Invoice> findAllByIsReceivedOrderByInvoiceDateDesc(Boolean isReceived, Pageable pageable);

    
	
	List<Invoice> findInvoicesByInvoiceDateBetween(LocalDateTime startDate, LocalDateTime endDate);
	
	  @Query("SELECT i.invoiceNumber FROM Invoice i")
	    List<String> findAllInvoiceNumber();
	  
	  @Query("SELECT i.IRN FROM Invoice i")
	    List<String> findAllIRN();
	  
	  @Query("SELECT MIN(i.invoiceDate) FROM Invoice i")
	  LocalDateTime findInitialEntryInvoiceDate();

	  Boolean findByinvoiceNumber(String invoiceNumber);
	  
	  Boolean findByIRN(String IRN);
	

	List<Invoice> findByInvoiceDateBetween(LocalDateTime startOfMonth, LocalDateTime endOfMonth);

	@Query("SELECT KEY(ip), SUM(ip) AS totalQuantity " +
			"FROM Invoice i " +
			"JOIN i.productQuantityMap ip " +
			"WHERE i.id = :invoiceId AND i.invoiceDate BETWEEN :fromDate AND :toDate " +
			"GROUP BY KEY(ip) " +
			"ORDER BY totalQuantity DESC")
	List<Object[]> findTop5ProductsByInvoiceId(@Param("invoiceId") Long invoiceId,
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate);
	
	@Query("SELECT KEY(ip) AS product, SUM(ip.quantity) AS totalQuantity " +
		       "FROM Invoice i " +
		       "JOIN i.productGst ip " +
		       "WHERE i.invoiceDate BETWEEN :startDate AND :endDate " +
		       "AND KEY(ip).isActive = :status " +
		       "GROUP BY KEY(ip).id " +
		       "ORDER BY totalQuantity DESC")
		List<Object[]> findTopSellingProducts(@Param("startDate") LocalDateTime startDate,
		                                      @Param("endDate") LocalDateTime endDate, @Param("status") Boolean status);


	@Query("SELECT KEY(ip), SUM(ip) AS totalQuantity " +
			"FROM Invoice i " +
			"JOIN i.productQuantityMap ip " +
			"WHERE i.invoiceDate BETWEEN :fromDate AND :toDate " +
			"GROUP BY KEY(ip) " +
			"ORDER BY totalQuantity DESC " +
			"LIMIT 5")
	List<Object[]> findTop5SellingProducts(@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate);

	@Query("SELECT KEY(ip), SUM(ip) AS totalQuantity " +
			"FROM Invoice i " +
			"JOIN i.productQuantityMap ip " +
			"WHERE i.invoiceDate BETWEEN :fromDate AND :toDate " +
			"GROUP BY KEY(ip) " +
			"ORDER BY totalQuantity ASC " +
			"LIMIT 5")
	List<Object[]> findLeast5SellingProducts(@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate);

	
	@Query("SELECT KEY(ip), SUM(ip) AS totalQuantity  ,dp.agencyName AS agencyName, dp.contactPerson as ContactPerson "
			+
			"FROM Invoice i " +
			"JOIN i.productQuantityMap ip " +
			"JOIN i.distributor d " +
			"JOIN d.distributorProfile dp " +
			"WHERE dp.city = :city " +
			"AND i.invoiceDate >= :fromDate " +
			"AND i.invoiceDate <= :toDate " +
			"GROUP BY KEY(ip) " +
			"ORDER BY totalQuantity DESC " +
			"LIMIT 1")
	List<Object[]> findTop5SellingProductsByCity(@Param("city") String city,
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate);

	@Query("SELECT KEY(ip), SUM(ip) AS totalQuantity, dp.agencyName AS agencyName, dp.contactPerson as ContactPerson " +
			"FROM Invoice i " +
			"JOIN i.productQuantityMap ip " +
			"JOIN i.distributor d " +
			"JOIN d.distributorProfile dp " +
			"WHERE dp.region = :region " +
			"AND i.invoiceDate >= :fromDate " +
			"AND i.invoiceDate <= :toDate " +
			"GROUP BY KEY(ip) " +
			"ORDER BY totalQuantity DESC " +
			"LIMIT 1")
	List<Object[]> findTop5SellingProductsByRegion(@Param("region") String region,
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate);

	@Query("SELECT KEY(ip), SUM(ip) AS totalQuantity,dp.agencyName AS agencyName, dp.contactPerson as ContactPerson " +
			"FROM Invoice i " +
			"JOIN i.productQuantityMap ip " +
			"JOIN i.distributor d " +
			"JOIN d.distributorProfile dp " +
			"WHERE dp.zone = :zone " +
			"AND i.invoiceDate >= :fromDate " +
			"AND i.invoiceDate <= :toDate " +
			"GROUP BY KEY(ip) " +
			"ORDER BY totalQuantity DESC " +
			"LIMIT 1")
	List<Object[]> findTop5SellingProductsByZone(@Param("zone") String zone, @Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate);

	@Query("SELECT KEY(ip), SUM(ip) AS totalQuantity, dp.agencyName AS agencyName,dp.contactPerson as ContactPerson  " +
			"FROM Invoice i " +
			"JOIN i.productQuantityMap ip " +
			"JOIN i.distributor d " +
			"JOIN d.distributorProfile dp " +
			"WHERE dp.city = :city " +
			"AND i.invoiceDate >= :fromDate " +
			"AND i.invoiceDate <= :toDate " +
			"GROUP BY KEY(ip) " +
			"ORDER BY totalQuantity ASC " +
			"LIMIT 3")
	List<Object[]> findLeast5SellingProductsByCity(@Param("city") String city,
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate);

	@Query("SELECT KEY(ip), SUM(ip) AS totalQuantity,dp.agencyName AS agencyName,dp.contactPerson as ContactPerson  " +
			"FROM Invoice i " +
			"JOIN i.productQuantityMap ip " +
			"JOIN i.distributor d " +
			"JOIN d.distributorProfile dp " +
			"WHERE dp.region = :region " +
			"AND i.invoiceDate >= :fromDate " +
			"AND i.invoiceDate <= :toDate " +
			"GROUP BY KEY(ip) " +
			"ORDER BY totalQuantity ASC " +
			"LIMIT 1")
	List<Object[]> findLeast5SellingProductsByRegion(@Param("region") String region,
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate);

	@Query("SELECT KEY(ip), SUM(ip) AS totalQuantity,dp.agencyName AS agencyName,dp.contactPerson as ContactPerson  " +
			"FROM Invoice i " +
			"JOIN i.productQuantityMap ip " +
			"JOIN i.distributor d " +
			"JOIN d.distributorProfile dp " +
			"WHERE dp.zone = :zone " +
			"AND i.invoiceDate >= :fromDate " +
			"AND i.invoiceDate <= :toDate " +
			"GROUP BY KEY(ip) " +
			"ORDER BY totalQuantity ASC " +
			"LIMIT 1")
	List<Object[]> findLeast5SellingProductsByZone(@Param("zone") String zone,
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate);

	@Query("SELECT KEY(ip).category FROM Invoice i JOIN i.productQuantityMap ip")
	List<Category> findAllProductsInInvoices();

	@Query("SELECT KEY(ip).category.categoryName, SUM(ip) AS totalQuantity " +
			"FROM Invoice i " +
			"JOIN i.productQuantityMap ip " +
			"WHERE i.invoiceDate >= :fromDate " +
			"AND i.invoiceDate <= :toDate " +
			"GROUP BY KEY(ip).category.categoryName " +
			"ORDER BY totalQuantity DESC " +
			"LIMIT 5")
	List<Object[]> findTop5SellingCategories(
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate);

	@Query("SELECT KEY(ip).category.categoryName, SUM(ip) AS totalQuantity " +
			"FROM Invoice i " +
			"JOIN i.productQuantityMap ip " +
			"WHERE i.invoiceDate >= :fromDate " +
			"AND i.invoiceDate <= :toDate " +
			"GROUP BY KEY(ip).category.categoryName " +
			"ORDER BY totalQuantity ASC " +
			"LIMIT 5")
	List<Object[]> findTop5LeastCategories(
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate);

	@Query("SELECT KEY(ip).category.categoryName, SUM(ip) AS totalQuantity ,dp.agencyName AS agencyName ,dp.contactPerson as ContactPerson "
			+
			"FROM Invoice i " +
			"JOIN i.productQuantityMap ip " +
			"JOIN i.distributor d " +
			"JOIN d.distributorProfile dp " +
			"WHERE (:city IS NULL OR dp.city = :city) " +
			"AND (:region IS NULL OR dp.region = :region) " +
			"AND (:zone IS NULL OR dp.zone = :zone) " +
			"AND i.invoiceDate >= :fromDate " +
			"AND i.invoiceDate <= :toDate " +
			"GROUP BY KEY(ip).category.categoryName " +
			"ORDER BY totalQuantity DESC " +
			"LIMIT 5")
	List<Object[]> findTop5SellingCategoriesByCityRegionAndZone(
			@Param("city") String city,
			@Param("region") String region,
			@Param("zone") String zone,
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate);

	@Query("SELECT KEY(ip).category.categoryName, SUM(ip) AS totalQuantity , dp.agencyName AS agencyName , dp.contactPerson as ContactPerson   "
			+
			"FROM Invoice i " +
			"JOIN i.productQuantityMap ip " +
			"JOIN i.distributor d " +
			"JOIN d.distributorProfile dp " +
			"WHERE (:city IS NULL OR dp.city = :city) " +
			"AND (:region IS NULL OR dp.region = :region) " +
			"AND (:zone IS NULL OR dp.zone = :zone) " +
			"AND i.invoiceDate >= :fromDate " +
			"AND i.invoiceDate <= :toDate " +
			"GROUP BY KEY(ip).category.categoryName " +
			"ORDER BY totalQuantity ASC " +
			"LIMIT 5")
	List<Object[]> findLeast5SellingCategoriesByCityRegionAndZone(
			@Param("city") String city,
			@Param("region") String region,
			@Param("zone") String zone,
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate);

	@Query("SELECT KEY(ip).productName, SUM(ip) AS totalQuantity, dp.agencyName as AgencyName, dp.contactPerson as ContactPerson "
			+
			"FROM Invoice i " +
			"JOIN i.productQuantityMap ip " +
			"JOIN i.distributor d " +
			"JOIN d.distributorProfile dp " +
			"WHERE (:city IS NULL OR dp.city = :city) " +
			"AND (:region IS NULL OR dp.region = :region) " +
			"AND (:zone IS NULL OR dp.zone = :zone) " +
			"AND i.invoiceDate >= :fromDate " +
			"AND i.invoiceDate <= :toDate " +
			"GROUP BY KEY(ip).productName " +
			"ORDER BY totalQuantity DESC " +
			"LIMIT 5")
	List<Object[]> findTop5ProductsByCityRegionAndZone(
			@Param("city") String city,
			@Param("region") String region,
			@Param("zone") String zone,
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate);

	@Query("SELECT KEY(ip).productName, SUM(ip) AS totalQuantity, dp.agencyName as AgencyName, dp.contactPerson as ContactPerson "
			+
			"FROM Invoice i " +
			"JOIN i.productQuantityMap ip " +
			"JOIN i.distributor d " +
			"JOIN d.distributorProfile dp " +
			"WHERE (:city IS NULL OR dp.city = :city) " +
			"AND (:region IS NULL OR dp.region = :region) " +
			"AND (:zone IS NULL OR dp.zone = :zone) " +
			"AND i.invoiceDate >= :fromDate " +
			"AND i.invoiceDate <= :toDate " +
			"GROUP BY KEY(ip).productName " +
			"ORDER BY totalQuantity ASC " +
			"LIMIT 5")
	List<Object[]> findLeast5ProductsByCityRegionAndZone(
			@Param("city") String city,
			@Param("region") String region,
			@Param("zone") String zone,
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate);

	@Query("SELECT KEY(ip).category.categoryName, SUM(ip) AS totalQuantity, dp.agencyName as AgencyName, dp.contactPerson as ContactPerson "
			+
			"FROM Invoice i " +
			"JOIN i.productQuantityMap ip " +
			"JOIN i.distributor d " +
			"JOIN d.distributorProfile dp " +
			"WHERE (:city IS NULL OR dp.city = :city) " +
			"AND (:region IS NULL OR dp.region = :region) " +
			"AND (:zone IS NULL OR dp.zone = :zone) " +
			"AND i.invoiceDate >= :fromDate " +
			"AND i.invoiceDate <= :toDate " +
			"GROUP BY KEY(ip).category.categoryName " +
			"ORDER BY totalQuantity DESC " +
			"LIMIT 5")
	List<Object[]> findTop5CategerioesSoldByDistributorsByCityRegionAndZone(
			@Param("city") String city,
			@Param("region") String region,
			@Param("zone") String zone,
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate);

	@Query("SELECT KEY(ip).category.categoryName, SUM(ip) AS totalQuantity, dp.agencyName as AgencyName, dp.contactPerson as ContactPerson "
			+
			"FROM Invoice i " +
			"JOIN i.productQuantityMap ip " +
			"JOIN i.distributor d " +
			"JOIN d.distributorProfile dp " +
			"WHERE (:city IS NULL OR dp.city = :city) " +
			"AND (:region IS NULL OR dp.region = :region) " +
			"AND (:zone IS NULL OR dp.zone = :zone) " +
			"AND i.invoiceDate >= :fromDate " +
			"AND i.invoiceDate <= :toDate " +
			"GROUP BY KEY(ip).category.categoryName " +
			"ORDER BY totalQuantity DESC " +
			"LIMIT 5")
	List<Object[]> findLeast5CategerioesSoldByDistributorsByCityRegionAndZone(
			@Param("city") String city,
			@Param("region") String region,
			@Param("zone") String zone,
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate);

	@Query("SELECT dp.agencyName AS AgencyName, dp.contactPerson AS ContactPerson, ip , KEY(ip) AS Product, KEY(ip).price * ip as totalPrice "
			+
			"FROM Invoice i " +
			"JOIN i.productQuantityMap ip " +
			"JOIN i.distributor d " +
			"JOIN d.distributorProfile dp " +
			"WHERE (:city IS NULL OR dp.city = :city) " +
			"AND (:region IS NULL OR dp.region = :region) " +
			"AND (:zone IS NULL OR dp.zone = :zone) " +
			"AND i.invoiceDate >= :fromDate " +
			"AND i.invoiceDate <= :toDate " +
			"ORDER BY dp.agencyName, i.invoiceDate")
	List<Object[]> findAllInvoicesForAllDistributors( 
			@Param("city") String city,
			@Param("region") String region,
			@Param("zone") String zone,
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate);

	@Query("SELECT KEY(ip).productName, KEY(ip).price, SUM(ip) AS totalQuantity " +
			"FROM Invoice i " +
			"JOIN i.productQuantityMap ip " +
			"JOIN i.distributor d " +
			"JOIN d.distributorProfile dp " +
			"WHERE (:city IS NULL OR dp.city = :city) " +
			"AND (:region IS NULL OR dp.region = :region) " +
			"AND (:zone IS NULL OR dp.zone = :zone) " +
			"AND i.invoiceDate >= :fromDate " +
			"AND i.invoiceDate <= :toDate " +
			"GROUP BY KEY(ip).productName, KEY(ip).price " +
			"ORDER BY totalQuantity DESC")
	List<Object[]> findTopSalesProducts(@Param("city") String city,
			@Param("region") String region,
			@Param("zone") String zone,
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate);

	@Query("SELECT KEY(ip).category.categoryName, KEY(ip).price, SUM(ip) AS totalQuantity " +
			"FROM Invoice i " +
			"JOIN i.productQuantityMap ip " +
			"JOIN i.distributor d " +
			"JOIN d.distributorProfile dp " +
			"WHERE (:city IS NULL OR dp.city = :city) " +
			"AND (:region IS NULL OR dp.region = :region) " +
			"AND (:zone IS NULL OR dp.zone = :zone) " +
			"AND i.invoiceDate >= :fromDate " +
			"AND i.invoiceDate <= :toDate " +
			"GROUP BY KEY(ip).category.categoryName, KEY(ip).productName " +
			"ORDER BY totalQuantity DESC " +
			"LIMIT 5")
	List<Object[]> findTopSalesCategoriesByCityRegionAndZone(
			@Param("city") String city,
			@Param("region") String region,
			@Param("zone") String zone,
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate);

	@Query("SELECT dp.agencyName AS AgencyName, dp.contactPerson AS ContactPerson, ip , KEY(ip) AS Product, KEY(ip).price * ip as totalPrice "
			+
			"FROM Invoice i " +
			"JOIN i.productQuantityMap ip " +
			"JOIN i.distributor d " +
			"JOIN d.distributorProfile dp " +
			"WHERE (:city IS NULL OR dp.city = :city) " +
			"AND (:region IS NULL OR dp.region = :region) " +
			"AND (:zone IS NULL OR dp.zone = :zone) " +
			"AND i.invoiceDate >= :fromDate " +
			"AND i.invoiceDate <= :toDate " +
			"ORDER BY dp.agencyName, i.invoiceDate")
	List<Object[]> totalSalesbyDistributors(
			@Param("city") String city,
			@Param("region") String region,
			@Param("zone") String zone,
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate);
	
	
	@Query("SELECT KEY(ip), ip as Quantity, p " +
	        "FROM Invoice i " +
	        "JOIN i.productQuantityMap ip " +
	        "JOIN i.productGst p " +
	        "WHERE i.id = :invoiceId " +
	        "AND KEY(ip) = KEY(p)")
	List<Object[]> getInvoiceProductDetails(@Param("invoiceId") Long invoiceId);
	
	
	@Query("SELECT KEY(ip).category.categoryName, KEY(ip).price, SUM(ip) AS totalQuantity  " +
			"FROM Invoice i " +
			"JOIN i.productQuantityMap ip " +
			"WHERE i.invoiceDate >= :fromDate AND i.invoiceDate <= :toDate " 
			+ " AND KEY(ip).category.isActive = :status " +
			"GROUP BY KEY(ip).category.categoryName, KEY(ip).productName "  +
			"ORDER BY totalQuantity DESC ")
	List<Object[]> findTopSellingCategories(
			@Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate, @Param("status") Boolean status);

	@Query("SELECT dp.agencyName AS AgencyName, dp.contactPerson AS ContactPerson , i.salespersonId as salespersonId , ip , KEY(ip) AS Product, KEY(ip).price * ip as totalPrice " +
	        "FROM Invoice i " +
	        "JOIN i.productQuantityMap ip " +
	        "JOIN i.distributor d " +
	        "JOIN d.distributorProfile dp " +
	        "WHERE i.invoiceDate >= :fromDate AND i.invoiceDate <= :toDate " +
	        "AND dp.isActive = :status " +
	        "ORDER BY dp.agencyName, i.invoiceDate ")
	List<Object[]> totalSalesByDistributors(
	        @Param("fromDate") LocalDateTime fromDate,
	        @Param("toDate") LocalDateTime toDate , @Param("status") Boolean status);
	
	
	@Query("SELECT s.name AS salespersonId, SUM(i.totalAmount) AS totalInvoiceAmount, " +
		       "SUM(e.incentive) AS totalIncentive, SUM(e.salary) AS totalSalary, " +
		       "SUM(e.miscellaneous) AS totalMiscellaneous " +
		       "FROM Invoice i " +
		       "LEFT JOIN Salesperson s ON i.salespersonId = s.id " +
		       "LEFT JOIN Expenses e ON s.id = e.salesperson " +
		       "WHERE (i.salespersonId = :salespersonId OR :salespersonId IS NULL) " +
		       "AND s.isActive = :status " +
		       "AND i.invoiceDate BETWEEN :fromDate AND :toDate " +
		       "GROUP BY s.id")
		List<Object[]> totalInvoiceAmountAndExpenses(
		        @Param("salespersonId") Long salespersonId,
		        @Param("fromDate") LocalDateTime fromDate,
		        @Param("toDate") LocalDateTime toDate,
		        @Param("status") Boolean status
		);


		Page<Invoice> findByIsReceivedAndInvoiceNumberContainingIgnoreCaseOrDistributor_DistributorProfile_AgencyNameContainingIgnoreCaseOrDistributor_DistributorProfile_CityContainingIgnoreCaseOrDistributor_DistributorProfile_RegionContainingIgnoreCaseOrDistributor_DistributorProfile_ZoneContainingIgnoreCase(
				 Boolean isReceived,String searchQuery,  String searchQuery2, String searchQuery3, String searchQuery4, String searchQuery5,
			    Pageable pageable);
		
		Page<Invoice> findByInvoiceNumberContainingIgnoreCaseOrDistributor_DistributorProfile_AgencyNameContainingIgnoreCaseOrDistributor_DistributorProfile_CityContainingIgnoreCaseOrDistributor_DistributorProfile_RegionContainingIgnoreCaseOrDistributor_DistributorProfile_ZoneContainingIgnoreCase(
			    String searchQuery, String searchQuery2, String searchQuery3, String searchQuery4, String searchQuery5, Pageable pageable);



	Page<Invoice> findByIsReceived(Boolean isReceived, Pageable pageable);
	Page<Invoice> findByIsReceivedAndDistributor_DistributorProfile_GstNo(Boolean isReceived,String searchQuery1, Pageable pageable);
	Page<Invoice> findByIsReceivedAndTotalAmount(Boolean isReceived,Double searchQuery1, Pageable pageable);

	Page<Invoice> findByDistributor_DistributorProfile_GstNo(String searchQuery1, Pageable pageable);
	Page<Invoice> findByTotalAmount(Double searchQuery1, Pageable pageable);
	
	@Query("SELECT new com.sas.SalesAnalysisSystem.dto.DistributorDetails(i.invoiceNumber, i.invoiceDate, i.id, i.totalAmount, " +
		       "i.distributor.distributorProfile.agencyName, " +
		       "i.distributor.distributorProfile.city, " +
		       "i.distributor.distributorProfile.region, " +
		       "i.distributor.distributorProfile.zone,  " +
		       "s.name )"+
		       "FROM Invoice i " +
		       "LEFT JOIN Salesperson s ON i.salespersonId = s.id  " +	
		       "WHERE i.invoiceDate BETWEEN :startDate AND :endDate " +
		       "AND (:status IS NULL OR i.isReceived = :status ) " +
		       "AND (" +
		           "LOWER(i.distributor.distributorProfile.agencyName) LIKE LOWER(CONCAT('%', COALESCE(:search, ''), '%')) " +
		           "OR LOWER(i.distributor.distributorProfile.city) LIKE LOWER(CONCAT('%', COALESCE(:search, ''), '%')) " +
		           "OR LOWER(i.distributor.distributorProfile.region) LIKE LOWER(CONCAT('%', COALESCE(:search, ''), '%')) " +
		           "OR LOWER(i.distributor.distributorProfile.zone) LIKE LOWER(CONCAT('%', COALESCE(:search, ''), '%')) " +
		           "OR LOWER(s.name) LIKE LOWER(CONCAT('%', COALESCE(:search, ''), '%')) " +
		           "OR LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', COALESCE(:search, ''), '%')) " +
		       ") " 
	+
		       "GROUP BY " +
		       "i.distributor.distributorProfile.agencyName, " +
		        "i.distributor.distributorProfile.contactPerson, " +
		        "i.distributor.distributorProfile.city, " +
		        "i.distributor.distributorProfile.region, " +
		        "i.distributor.distributorProfile.zone, " +
		        "s.name")
		Page<DistributorDetails> findInvoicesByDateRange(
		        @Param("startDate") LocalDateTime startDate,
		        @Param("endDate") LocalDateTime endDate,
		        @Param("status") Boolean status,
		        @Param("search") String search,
		        Pageable page);

	
//	
	@Query("SELECT " +
            "i.distributor.distributorProfile.agencyName, " +
            "i.distributor.id, " +
            "i.distributor.distributorProfile.contactPerson, " +
            "i.distributor.distributorProfile.city, " +
            "i.distributor.distributorProfile.region, " +
            "i.distributor.distributorProfile.zone, " +
            "GROUP_CONCAT(DISTINCT CONCAT(KEY(p).productName, ' : ', VALUE(p).quantity)) AS productNames, " +
            "GROUP_CONCAT(DISTINCT KEY(p).category.categoryName) AS categoryNames, " +
            "GROUP_CONCAT(DISTINCT(CONCAT( i.invoiceNumber, ' : ' , i.totalAmount))) AS invoiceNumbers " +
        "FROM Invoice i " +
        "JOIN i.productGst p " +
        "WHERE (:productId IS NULL OR KEY(p).id = :productId) " +
        "AND (:categoryId IS NULL OR KEY(p).category.id = :categoryId) " +
        "AND (COALESCE(:invoiceNumber, '') = '' OR i.invoiceNumber = :invoiceNumber) " +
        "AND (:selectQuery IS NULL OR " +
            "i.distributor.distributorProfile.agencyName LIKE %:selectQuery% " +
            "OR i.distributor.distributorProfile.contactPerson LIKE %:selectQuery% " +
            "OR i.distributor.distributorProfile.city LIKE %:selectQuery% " +
            "OR i.distributor.distributorProfile.region LIKE %:selectQuery% " +
            "OR i.distributor.distributorProfile.zone LIKE %:selectQuery% " +
            "OR i.invoiceNumber LIKE %:selectQuery%) " +
        "AND i.invoiceDate BETWEEN :fromDate AND :toDate " +
        "GROUP BY " +
            "i.distributor.distributorProfile.agencyName, " +
            "i.distributor.distributorProfile.contactPerson, " +
            "i.distributor.distributorProfile.city, " +
            "i.distributor.distributorProfile.region, " +
            "i.distributor.distributorProfile.zone")
Page<Object[]> findAllDistributorsWithDetails(
    @Param("productId") Long productId,
    @Param("categoryId") Long categoryId,
    @Param("invoiceNumber") String invoiceNumber,
    @Param("selectQuery") String selectQuery,
    @Param("fromDate") LocalDateTime fromDate,
    @Param("toDate") LocalDateTime toDate,
    Pageable pageable);


@Query("SELECT " +
        "i.distributor.distributorProfile.agencyName, " +
        "i.distributor.distributorProfile.contactPerson, " +
        "i.distributor.distributorProfile.address, " +
        "i.distributor.distributorProfile.gstNo, " +
        "i.distributor.distributorProfile.panNo, " +
        "i.distributor.distributorProfile.city, " +
        "i.distributor.distributorProfile.zone, " +
        "i.distributor.distributorProfile.region, " +
        "GROUP_CONCAT(CONCAT(KEY(p).productName, ' : ', VALUE(p).quantity, ' : ', DATE_FORMAT(i.invoiceDate, '%d-%M-%y') "
        + ")) AS productNames, " +
        "GROUP_CONCAT( DISTINCT CONCAT(KEY(p).category.categoryName, ' : ', DATE_FORMAT(i.invoiceDate, '%d-%M-%y')\r\n"
        + ", ' : ', " +
            "(SELECT COUNT(p2) FROM i.productGst p2 WHERE KEY(p2).category = KEY(p).category))) AS categoryDetails, " +
        "GROUP_CONCAT(DISTINCT(CONCAT(i.invoiceNumber, ' : ', i.totalAmount , ' : ' , DATE_FORMAT(i.invoiceDate, '%d-%M-%y') "
        + " ))) AS invoiceNumbers " +
    "FROM Invoice i " +
    "JOIN i.productGst p " +
    "WHERE i.distributor.id = :distributorId " +
    "AND i.invoiceDate BETWEEN :startDate AND :endDate " +
    "GROUP BY " +
    "i.distributor.id")
List<Object[]> findAllDistributorsWithDetailsByDistributorId(
    @Param("distributorId") Long distributorId,
    @Param("startDate") LocalDateTime startDate,
    @Param("endDate") LocalDateTime endDate);

@Query("SELECT " +
        "i.distributor.distributorProfile.agencyName, " +
        "i.distributor.distributorProfile.contactPerson, " +
        "i.distributor.distributorProfile.city, " +
        "i.distributor.distributorProfile.region, " +
        "GROUP_CONCAT(DISTINCT KEY(p).productName) AS productNames, " +
        "GROUP_CONCAT(DISTINCT KEY(p).category.categoryName) AS categoryNames, " +
        "GROUP_CONCAT(DISTINCT(CONCAT(i.invoiceNumber, ' : ', i.totalAmount))) AS invoiceNumbers " +
    "FROM Invoice i " +
    "JOIN i.productGst p " +
    "WHERE i.distributor.id IN :distributorIds " + 
    "GROUP BY " +
        "i.distributor.distributorProfile.agencyName, " +
        "i.distributor.distributorProfile.contactPerson, " +
        "i.distributor.distributorProfile.city, " +
        "i.distributor.distributorProfile.region")
Page<Object[]> findAllDistributorsWithDetailsByDistributorIds(@Param("distributorIds") List<Long> distributorIds, Pageable pageable);
Page<Object[]> findByDistributor_DistributorProfile_agencyName(String searchQuery, Pageable pageable);


@Query("SELECT " +
        "i.distributor.distributorProfile.agencyName, " +
        "i.distributor.id, " +
        "i.distributor.distributorProfile.contactPerson, " +
        "i.distributor.distributorProfile.city, " +
        "i.distributor.distributorProfile.region, " +
        "i.distributor.distributorProfile.zone, " +
        "GROUP_CONCAT(DISTINCT CONCAT(KEY(p).productName, ' : ', VALUE(p).quantity)) AS productNames, " +
        "GROUP_CONCAT(DISTINCT KEY(p).category.categoryName) AS categoryNames, " +
        "GROUP_CONCAT(DISTINCT(CONCAT( i.invoiceNumber, ' : ' , i.totalAmount))) AS invoiceNumbers, " +
        "SUM(e.salary) AS totalExpenses " + 
    "FROM Invoice i " +
    "JOIN i.productGst p " +
    "LEFT JOIN Salesperson s ON i.salespersonId = s.id " +
    "LEFT JOIN Expenses e ON s.id = e.salesperson " +
    "WHERE (:productId IS NULL OR KEY(p).id = :productId) " +
    "AND (:categoryId IS NULL OR KEY(p).category.id = :categoryId) " +
    "AND (COALESCE(:invoiceNumber, '') = '' OR i.invoiceNumber = :invoiceNumber) " +
    "AND (:selectQuery IS NULL OR " +
        "i.distributor.distributorProfile.agencyName LIKE %:selectQuery% " +
        "OR i.distributor.distributorProfile.contactPerson LIKE %:selectQuery% " +
        "OR i.distributor.distributorProfile.city LIKE %:selectQuery% " +
        "OR i.distributor.distributorProfile.region LIKE %:selectQuery% " +
        "OR i.distributor.distributorProfile.zone LIKE %:selectQuery%) " +
    "AND (:fromDate IS NULL OR i.invoiceDate >= :fromDate) " +
    "AND (:toDate IS NULL OR i.invoiceDate <= :toDate) " +
    "AND (:salespersonId IS NULL OR s.id = :salespersonId) " + 
    "GROUP BY " + 
        "i.distributor.distributorProfile.agencyName, " +
        "i.distributor.distributorProfile.contactPerson, " +
        "i.distributor.distributorProfile.city, " +
        "i.distributor.distributorProfile.region, " +
        "i.distributor.distributorProfile.zone")
Page<Object[]> findAllDistributorsWithDetailsAndExpenses(
    @Param("productId") Long productId,
    @Param("categoryId") Long categoryId,
    @Param("invoiceNumber") String invoiceNumber,
    @Param("selectQuery") String selectQuery,
    @Param("fromDate") LocalDateTime fromDate,
    @Param("toDate") LocalDateTime toDate,
    Pageable pageable);


@Query("SELECT " +
        "KEY(p), " +
        "VALUE(p) " +
    "FROM Invoice i " +
    "JOIN i.productGst p " +
    "WHERE i.distributor.id = :distributorId " + 
    "AND ((MONTH(i.invoiceDate) = :currentMonth AND YEAR(i.invoiceDate) = :currentYear) " +
   "OR (MONTH(i.invoiceDate) = :lastYearSameMonth AND YEAR(i.invoiceDate) = :lastYear)) " +
    "GROUP BY KEY(p).productName")
		List<Object[]> findProductsSoldToDistributorThisMonthAndSameMonthLastYear(
			@Param("distributorId") Long distributorId,
			@Param("currentMonth") int currentMonth,
			@Param("currentYear") int currentYear,
			@Param("lastYearSameMonth") int lastYearSameMonth,
			@Param("lastYear") int lastYear);
		
		
		@Query("SELECT " +
		        "i.distributor.distributorProfile.agencyName, " +
		        "i.distributor.distributorProfile.contactPerson, " +
		        "i.distributor.distributorProfile.address, " +
		        "i.distributor.distributorProfile.gstNo, " +
		        "i.distributor.distributorProfile.panNo, " +
		        "i.distributor.distributorProfile.city, " +
		        "i.distributor.distributorProfile.zone, " +
		        "i.distributor.distributorProfile.region, " +
		        "GROUP_CONCAT( DISTINCT CONCAT(KEY(p).productName, ' : ', VALUE(p).quantity, ' : ', DATE_FORMAT(i.invoiceDate, '%M-%y') "
		        + ")) AS productNames, " +
		        "GROUP_CONCAT(DISTINCT CONCAT(KEY(p).category.categoryName, ' : ', DATE_FORMAT(i.invoiceDate, '%M-%y')\r\n"
		        + ", ' : ', " +
		            "(SELECT COUNT(p2) FROM i.productGst p2 WHERE KEY(p2).category = KEY(p).category))) AS categoryDetails, " +
		        "GROUP_CONCAT(DISTINCT(CONCAT(i.invoiceNumber, ' : ', i.totalAmount , ' : ' , DATE_FORMAT(i.invoiceDate, '%M-%y') "
		        + " ))) AS invoiceNumbers " +
		    "FROM Invoice i " +
		    "JOIN i.productGst p " +
		    "WHERE i.distributor.id = :distributorId " +
		    "AND i.invoiceDate BETWEEN :startDate AND :endDate " +
		    "GROUP BY i.distributor.id " +
			    "ORDER BY i.invoiceDate ASC")
		List<Object[]> findAllDistributorsWithDetailsByDistributorIdPdf(
		    @Param("distributorId") Long distributorId,
		    @Param("startDate") LocalDateTime startDate,
		    @Param("endDate") LocalDateTime endDate);
		
		
		@Query("SELECT " +
	            "i.distributor.distributorProfile.agencyName, " +
	            "i.distributor.id, " +
	            "GROUP_CONCAT(DISTINCT  s.name ), "+
	            "i.distributor.distributorProfile.city, " +
	            "i.distributor.distributorProfile.region, " +
	            "i.distributor.distributorProfile.zone, " +
	            "GROUP_CONCAT(DISTINCT CONCAT(KEY(p).productName, ' : ', VALUE(p).quantity)) AS productNames, " +
	            "GROUP_CONCAT(DISTINCT KEY(p).category.categoryName) AS categoryNames, " +
	            "GROUP_CONCAT(DISTINCT(CONCAT( i.invoiceNumber, ' : ' , i.totalAmount))) AS invoiceNumbers " +
	        "FROM Invoice i " +
	        "LEFT JOIN Salesperson s ON s.id = i.salespersonId " +
	        "JOIN i.productGst p " +
	        "WHERE (:productName IS NULL OR KEY(p).productName LIKE %:productName%) " +
	        "AND (:categoryName IS NULL OR KEY(p).category.categoryName LIKE %:categoryName%) " +
	        "AND (COALESCE(:invoiceNumber, '') = '' OR i.invoiceNumber = :invoiceNumber) " +
	        "AND (:distributor IS NULL OR " +
            	"i.distributor.distributorProfile.agencyName LIKE %:distributor% )" +
	        "AND (:salespersonName IS NULL OR " +
	            "s.name LIKE %:salespersonName% )" +
	        "AND (:invoiceSearch IS NULL OR " +
	            "i.invoiceNumber LIKE %:invoiceSearch%)" +
	        "AND (:selectQuery IS NULL OR " +
	            " i.distributor.distributorProfile.city LIKE %:selectQuery% " +
	            "OR i.distributor.distributorProfile.region LIKE %:selectQuery% " +
	            "OR i.distributor.distributorProfile.zone LIKE %:selectQuery%  ) "+ 
	        "AND i.invoiceDate BETWEEN :fromDate AND :toDate " +
	        "GROUP BY " +
	            "i.distributor.distributorProfile.agencyName, " +
	            "i.distributor.distributorProfile.contactPerson, " +
	            "i.distributor.distributorProfile.city, " +
	            "i.distributor.distributorProfile.region, " +
	            "i.distributor.distributorProfile.zone ")
	Page<Object[]> findAllDistributorsWithDetailsAndSeperatedFilters(
	    @Param("productName") String productName,
	    @Param("categoryName") String categoryName,
	    @Param("invoiceNumber") String invoiceNumber,
	    @Param("distributor") String distributor,
	    @Param("salespersonName") String salespersonName,
	    @Param("selectQuery") String selectQuery,
	    @Param("invoiceSearch") String invoiceSearch,
	    @Param("fromDate") LocalDateTime fromDate,
	    @Param("toDate") LocalDateTime toDate,
	    Pageable pageable);
		

}