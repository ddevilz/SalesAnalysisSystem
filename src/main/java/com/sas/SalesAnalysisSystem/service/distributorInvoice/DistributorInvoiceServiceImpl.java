package com.sas.SalesAnalysisSystem.service.distributorInvoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sas.SalesAnalysisSystem.models.Category;
import com.sas.SalesAnalysisSystem.repository.InvoiceRepository;


@Service
public class DistributorInvoiceServiceImpl implements DistributorInvoiceService {
	
	@Autowired
	private final InvoiceRepository invoiceRepository;

	public DistributorInvoiceServiceImpl(InvoiceRepository invoiceRepository) {
		this.invoiceRepository = invoiceRepository;
	}

	@Override
    public List<Category> findAllProductsInInvoices() {
        return invoiceRepository.findAllProductsInInvoices();
    }
	
	@Override
	 public List<Object[]> findTop5DistributorsByCityRegionAndZone(
	            Optional<String> city, Optional<String> region, Optional<String> zone,
	            Optional<Integer> year, Optional<String> monthName, String interval,
	            Optional<String> customFromDate, Optional<String> customToDate) {

	        LocalDateTime fromDate;
	        LocalDateTime toDate = LocalDateTime.now();

	        switch (interval.toLowerCase()) {
	            case "daily":
	                fromDate = toDate.minusDays(1);
	                break;
	            case "weekly":
	                fromDate = toDate.minusWeeks(1);
	                break;
	            case "monthly":
	                fromDate = getMonthlyFromDate(year, monthName);
	                toDate = getMonthlyToDate(year.orElse(null), monthName.orElse(null));
	                break;
	            case "annually":
	                fromDate = getAnnuallyFromDate(year);
	                toDate = getAnnuallyToDate(year);
	                break;
	            case "customdate":
	                if (customFromDate.isPresent() && customToDate.isPresent()) {
	                    fromDate = LocalDateTime.parse(customFromDate.get() + "T00:00:00");
	                    toDate = LocalDateTime.parse(customToDate.get() + "T23:59:59");
	                } else {
	                    throw new IllegalArgumentException("Both customFromDate and customToDate must be provided for customdate interval.");
	                }
	                break;
	            default:
	                throw new IllegalArgumentException("Invalid interval provided.");
	        }

	        return invoiceRepository.findTop5ProductsByCityRegionAndZone(
	                city.orElse(null),
	                region.orElse(null),
	                zone.orElse(null),
	                fromDate,
	                toDate
	        );
	    }
	 
	 @Override
	 public List<Object[]> findLeast5DistributorsByCityRegionAndZone(
	            Optional<String> city, Optional<String> region, Optional<String> zone,
	            Optional<Integer> year, Optional<String> monthName, String interval,
	            Optional<String> customFromDate, Optional<String> customToDate) {

	        LocalDateTime fromDate;
	        LocalDateTime toDate = LocalDateTime.now();

	        switch (interval.toLowerCase()) {
	            case "daily":
	                fromDate = toDate.minusDays(1);
	                break;
	            case "weekly":
	                fromDate = toDate.minusWeeks(1);
	                break;
	            case "monthly":
	                fromDate = getMonthlyFromDate(year, monthName);
	                toDate = getMonthlyToDate(year.orElse(null), monthName.orElse(null));
	                break;
	            case "annually":
	                fromDate = getAnnuallyFromDate(year);
	                toDate = getAnnuallyToDate(year);
	                break;
	            case "customdate":
	                if (customFromDate.isPresent() && customToDate.isPresent()) {
	                    fromDate = LocalDateTime.parse(customFromDate.get() + "T00:00:00");
	                    toDate = LocalDateTime.parse(customToDate.get() + "T23:59:59");
	                } else {
	                    throw new IllegalArgumentException("Both customFromDate and customToDate must be provided for customdate interval.");
	                }
	                break;
	            default:
	                throw new IllegalArgumentException("Invalid interval provided.");
	        }

	        return invoiceRepository.findLeast5ProductsByCityRegionAndZone(
	                city.orElse(null),
	                region.orElse(null),
	                zone.orElse(null),
	                fromDate,
	                toDate
	        );
	    }
	 
	 @Override
	 public List<Object[]> findTop5DistributorsByCategoriesCityRegionAndZone(
	            Optional<String> city, Optional<String> region, Optional<String> zone,
	            Optional<Integer> year, Optional<String> monthName, String interval,
	            Optional<String> customFromDate, Optional<String> customToDate) {

	        LocalDateTime fromDate;
	        LocalDateTime toDate = LocalDateTime.now();

	        switch (interval.toLowerCase()) {
	            case "daily":
	                fromDate = toDate.minusDays(1);
	                break;
	            case "weekly":
	                fromDate = toDate.minusWeeks(1);
	                break;
	            case "monthly":
	                fromDate = getMonthlyFromDate(year, monthName);
	                toDate = getMonthlyToDate(year.orElse(null), monthName.orElse(null));
	                break;
	            case "annually":
	                fromDate = getAnnuallyFromDate(year);
	                toDate = getAnnuallyToDate(year);
	                break;
	            case "customdate":
	                if (customFromDate.isPresent() && customToDate.isPresent()) {
	                    fromDate = LocalDateTime.parse(customFromDate.get() + "T00:00:00");
	                    toDate = LocalDateTime.parse(customToDate.get() + "T23:59:59");
	                } else {
	                    throw new IllegalArgumentException("Both customFromDate and customToDate must be provided for customdate interval.");
	                }
	                break;
	            default:
	                throw new IllegalArgumentException("Invalid interval provided.");
	        }

	        return invoiceRepository.findTop5CategerioesSoldByDistributorsByCityRegionAndZone(
	                city.orElse(null),
	                region.orElse(null),
	                zone.orElse(null),
	                fromDate,
	                toDate
	        );
	    }
	 
	 @Override
	 public List<Object[]> findLeast5DistributorsByCategoriesCityRegionAndZone(
	            Optional<String> city, Optional<String> region, Optional<String> zone,
	            Optional<Integer> year, Optional<String> monthName, String interval,
	            Optional<String> customFromDate, Optional<String> customToDate) {

	        LocalDateTime fromDate;
	        LocalDateTime toDate = LocalDateTime.now();

	        switch (interval.toLowerCase()) {
	            case "daily":
	                fromDate = toDate.minusDays(1);
	                break;
	            case "weekly":
	                fromDate = toDate.minusWeeks(1);
	                break;
	            case "monthly":
	                fromDate = getMonthlyFromDate(year, monthName);
	                toDate = getMonthlyToDate(year.orElse(null), monthName.orElse(null));
	                break;
	            case "annually":
	                fromDate = getAnnuallyFromDate(year);
	                toDate = getAnnuallyToDate(year);
	                break;
	            case "customdate":
	                if (customFromDate.isPresent() && customToDate.isPresent()) {
	                    fromDate = LocalDateTime.parse(customFromDate.get() + "T00:00:00");
	                    toDate = LocalDateTime.parse(customToDate.get() + "T23:59:59");
	                } else {
	                    throw new IllegalArgumentException("Both customFromDate and customToDate must be provided for customdate interval.");
	                }
	                break;
	            default:
	                throw new IllegalArgumentException("Invalid interval provided.");
	        }

	        return invoiceRepository.findLeast5CategerioesSoldByDistributorsByCityRegionAndZone(
	                city.orElse(null),
	                region.orElse(null),
	                zone.orElse(null),
	                fromDate,
	                toDate
	        );
	    }
	 
	 @Override
	 public List<Map<String, Object>> findTopSellingDistributors(
	            Optional<String> city, Optional<String> region, Optional<String> zone,
	            Optional<Integer> year, Optional<String> monthName, String interval,
	            String type,
	            Optional<String> customFromDate, Optional<String> customToDate) {

	        LocalDateTime fromDate;
	        LocalDateTime toDate = LocalDateTime.now();

	        switch (interval.toLowerCase()) {
	            case "daily":
	                fromDate = toDate.minusDays(1);
	                break;
	            case "weekly":
	                fromDate = toDate.minusWeeks(1);
	                break;
	            case "monthly":
	                fromDate = getMonthlyFromDate(year, monthName);
	                toDate = getMonthlyToDate(year.orElse(null), monthName.orElse(null));
	                break;
	            case "annually":
	                fromDate = getAnnuallyFromDate(year);
	                toDate = getAnnuallyToDate(year);
	                break;
	            case "customdate":
	                if (customFromDate.isPresent() && customToDate.isPresent()) {
	                    fromDate = LocalDateTime.parse(customFromDate.get() + "T00:00:00");
	                    toDate = LocalDateTime.parse(customToDate.get() + "T23:59:59");
	                } else {
	                    throw new IllegalArgumentException("Both customFromDate and customToDate must be provided for customdate interval.");
	                }
	                break;
	            default:
	                throw new IllegalArgumentException("Invalid interval provided.");
	        }

	        List<Object[]> dataList = invoiceRepository.findAllInvoicesForAllDistributors(
	                city.orElse(null),
	                region.orElse(null),
	                zone.orElse(null),
	                fromDate,
	                toDate
	        );
	        
	        List<Map<String, Object>> processedDataList = process(dataList);
	       
	        processedDataList.sort(Comparator.comparingInt(distributor -> (int) distributor.get("totalPrice")));

	        if (type.equalsIgnoreCase("Top")) {
	            Collections.reverse(processedDataList);
	        }
	        
	        if (processedDataList.size() > 5) {
	            processedDataList = processedDataList.subList(0, 5);
	        }

	        return processedDataList;
	    }
	 
	 @Override
	 public List<Map<String, Object>> totalSalesByDistributors(
	            Optional<String> city, Optional<String> region, Optional<String> zone,
	            Optional<Integer> year, Optional<String> monthName, String interval,
	            String type,
	            Optional<String> customFromDate, Optional<String> customToDate) {

	        LocalDateTime fromDate;
	        LocalDateTime toDate = LocalDateTime.now();

	        switch (interval.toLowerCase()) {
	            case "daily":
	                fromDate = toDate.minusDays(1);
	                break;
	            case "weekly":
	                fromDate = toDate.minusWeeks(1);
	                break;
	            case "monthly":
	                fromDate = getMonthlyFromDate(year, monthName);
	                toDate = getMonthlyToDate(year.orElse(null), monthName.orElse(null));
	                break;
	            case "annually":
	                fromDate = getAnnuallyFromDate(year);
	                toDate = getAnnuallyToDate(year);
	                break;
	            case "customdate":
	                if (customFromDate.isPresent() && customToDate.isPresent()) {
	                    fromDate = LocalDateTime.parse(customFromDate.get() + "T00:00:00");
	                    toDate = LocalDateTime.parse(customToDate.get() + "T23:59:59");
	                } else {
	                    throw new IllegalArgumentException("Both customFromDate and customToDate must be provided for customdate interval.");
	                }
	                break;
	            default:
	                throw new IllegalArgumentException("Invalid interval provided.");
	        }

	        List<Object[]> dataList = invoiceRepository.totalSalesbyDistributors(
	                city.orElse(null),
	                region.orElse(null),
	                zone.orElse(null),
	                fromDate,
	                toDate
	        );
	        
	        List<Map<String, Object>> processedDataList = process(dataList);
	       
	        processedDataList.sort(Comparator.comparingInt(distributor -> (int) distributor.get("totalPrice")));

	        if (type.equalsIgnoreCase("Top")) {
	            Collections.reverse(processedDataList);
	        }

	        return processedDataList;
	    }
	
	 
	 public List<Map<String, Object>> process(List<Object[]> dataList) {
		    Map<List<String>, List<Map<String, Object>>> distributorGroups = new HashMap<>();

		    for (Object[] data : dataList) {
		        String agencyName = (String) data[0];
		        String contactPerson = (String) data[1];
		        List<String> key = Arrays.asList(agencyName, contactPerson);
		        distributorGroups.putIfAbsent(key, new ArrayList<>());
	
		        Map<String, Object> productInfo = new HashMap<>();
		        productInfo.put("quantity", data[2]);
		        productInfo.put("total-price", data[4]);
		        productInfo.put("product", data[3]);
		        distributorGroups.get(key).add(productInfo);
		    }

		    List<Map<String, Object>> result = new ArrayList<>();
		    for (Map.Entry<List<String>, List<Map<String, Object>>> entry : distributorGroups.entrySet()) {
		        Map<String, Object> distributorInfo = new HashMap<>();
		        distributorInfo.put("agencyName", entry.getKey().get(0));
		        distributorInfo.put("contactPerson", entry.getKey().get(1));
		        distributorInfo.put("products", entry.getValue());
		        
		        int totalPrice = entry.getValue().stream()
		                .mapToInt(product -> (int) product.get("total-price"))
		                .sum();
		        distributorInfo.put("totalPrice", totalPrice);
		        
		        result.add(distributorInfo);
		    }
		    
		    return result;
		}


	 
	 private LocalDateTime getMonthlyFromDate(Optional<Integer> year, Optional<String> month) {
	        LocalDate startOfMonth;
	        if (year.isPresent() && month.isPresent()) {
	            int selectedYear = year.get();
	            Month selectedMonth = Month.valueOf(month.get().toUpperCase());
	            startOfMonth = LocalDate.of(selectedYear, selectedMonth, 1);
	            System.out.println(startOfMonth);
	        } else {
	            startOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
	        }
	        return startOfMonth.atStartOfDay();
	    }
	    
	    private LocalDateTime getMonthlyToDate(Integer year, String monthName) {
	        LocalDate endOfMonth = LocalDate.of(year, Month.valueOf(monthName.toUpperCase()), 1).with(TemporalAdjusters.lastDayOfMonth());
	        return endOfMonth.atStartOfDay();
	    }
	    
	    private LocalDateTime getAnnuallyFromDate(Optional<Integer> year) {
	        LocalDate startOfYear;
	        if (year.isPresent()) {
	            int selectedYear = year.get();
	            startOfYear = LocalDate.of(selectedYear, Month.APRIL, 1);
	            if (LocalDate.now().isBefore(startOfYear)) {
	                startOfYear = startOfYear.minusYears(1);
	            }
	        } else {
	            int currentYear = LocalDate.now().getYear();
	            startOfYear = LocalDate.of(currentYear - 1, Month.APRIL, 1);
	        }
	        return startOfYear.atStartOfDay();
	    }

	    private LocalDateTime getAnnuallyToDate(Optional<Integer> year) {
	        LocalDate startOfYear = getAnnuallyFromDate(year).toLocalDate();
	        int endYear = startOfYear.plusYears(1).getYear();
	        LocalDate endOfYear = LocalDate.of(endYear, Month.MARCH, 31);
	        return endOfYear.atTime(LocalTime.MAX);
	    }
	    
	    //top sakes by distributor
	    
}
