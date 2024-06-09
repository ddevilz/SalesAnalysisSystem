package com.sas.SalesAnalysisSystem.service.sales;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.sas.SalesAnalysisSystem.models.Sales;

public interface SalesService {

	List<Sales> findByInvoice_By_State(String state);

	List<Sales> getSalesByProduct(Long productId);

	List<Sales> getSalesByInvoiceId(Long id);

	Double getTotalPriceOfAllProductsInInvoices();

	void updateSalesTotalAmount();

	double getTotalAmountOfAllInvoices();

	int getTotalQuantityOfAllInvoices();

	List<Map<String, Object>> getSalesOfProducts(Optional<String> city, Optional<String> region, Optional<String> zone,
			Optional<Integer> year, Optional<String> monthName, String interval,
			Optional<String> customFromDate, Optional<String> customToDate);

	List<Map<String, Object>> getSalesOfCategories(Optional<String> city, Optional<String> region,
			Optional<String> zone, Optional<Integer> year, Optional<String> monthName, String interval,
			Optional<String> customFromDate, Optional<String> customToDate);
}
