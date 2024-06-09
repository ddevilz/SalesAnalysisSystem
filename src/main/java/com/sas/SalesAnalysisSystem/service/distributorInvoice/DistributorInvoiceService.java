package com.sas.SalesAnalysisSystem.service.distributorInvoice;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sas.SalesAnalysisSystem.models.Category;

@Service
public interface DistributorInvoiceService {

    public List<Category> findAllProductsInInvoices();

	List<Object[]> findTop5DistributorsByCityRegionAndZone(Optional<String> city, Optional<String> region,
			Optional<String> zone, Optional<Integer> year, Optional<String> monthName, String interval,
			Optional<String> customFromDate, Optional<String> customToDate);

	List<Object[]> findLeast5DistributorsByCityRegionAndZone(Optional<String> city, Optional<String> region,
			Optional<String> zone, Optional<Integer> year, Optional<String> monthName, String interval,
			Optional<String> customFromDate, Optional<String> customToDate);

	List<Object[]> findTop5DistributorsByCategoriesCityRegionAndZone(Optional<String> city, Optional<String> region,
			Optional<String> zone, Optional<Integer> year, Optional<String> monthName, String interval,
			Optional<String> customFromDate, Optional<String> customToDate);

	List<Object[]> findLeast5DistributorsByCategoriesCityRegionAndZone(Optional<String> city, Optional<String> region,
			Optional<String> zone, Optional<Integer> year, Optional<String> monthName, String interval,
			Optional<String> customFromDate, Optional<String> customToDate);


	public List<Map<String, Object>> findTopSellingDistributors(Optional<String> city, Optional<String> region,
			Optional<String> zone, Optional<Integer> year, Optional<String> month, String interval, String type,
			Optional<String> customFromDate, Optional<String> customToDate);

	List<Map<String, Object>> totalSalesByDistributors(Optional<String> city, Optional<String> region,
			Optional<String> zone, Optional<Integer> year, Optional<String> monthName, String interval, String type,
			Optional<String> customFromDate, Optional<String> customToDate);

}
