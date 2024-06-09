package com.sas.SalesAnalysisSystem.service.sales;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sas.SalesAnalysisSystem.exception.ResourceNotFoundException;
import com.sas.SalesAnalysisSystem.models.Product;
import com.sas.SalesAnalysisSystem.models.Sales;
import com.sas.SalesAnalysisSystem.repository.InvoiceRepository;
import com.sas.SalesAnalysisSystem.repository.SalesRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

@Service
public class SalesServiceImpl implements SalesService {

    private final EntityManager entityManager;
    private final SalesRepository salesRepository;
    private final InvoiceRepository invoiceRepository;

    @Autowired
    public SalesServiceImpl(EntityManager entityManager, SalesRepository salesRepository,
            InvoiceRepository invoiceRepository) {
        this.entityManager = entityManager;
        this.salesRepository = salesRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional
    public void updateSalesTotalAmount() {
        double totalAmount = getTotalAmountOfAllInvoices();

        Query updateQuery = entityManager.createQuery("UPDATE Sales s SET s.totalAmount = :totalAmount");
        updateQuery.setParameter("totalAmount", totalAmount);

        updateQuery.executeUpdate();
    }

    public double getTotalAmountOfAllInvoices() {
        Query query = entityManager.createQuery(
                "SELECT COALESCE(SUM(i.totalAmount), 0.0) FROM Invoice i ");
        return (double) query.getSingleResult();
    }

    @Transactional
    public void updateSalesTotalQuantity() {
        int totalQuantity = getTotalQuantityOfAllInvoices();

        Query updateQuery = entityManager.createQuery("UPDATE Sales s SET s.totalQuantity = :totalQuantity");
        updateQuery.setParameter("totalQuantity", totalQuantity);

        updateQuery.executeUpdate();
    }

    public int getTotalQuantityOfAllInvoices() {
        Query query = entityManager.createQuery(
                "SELECT COALESCE(SUM(i.quantity), 1) FROM Invoice i");
        return ((Number) query.getSingleResult()).intValue();
    }

    public Double getTotalPriceOfAllProductsInInvoices() {
        // Implementation using the SalesRepository
        // java.util.Optional<Double> totalPriceOptional =
        // salesRepository.sumPriceOfProducts();
        // return totalPriceOptional.orElse(0.0);
        return 0.0;
    }

    public List<Sales> findByInvoice_By_State(String region) {
        List<Sales> sales = salesRepository.findByInvoice_Distributor_DistributorProfile_State(region);
        if (sales.isEmpty()) {
            throw new ResourceNotFoundException("No sales records found for the specified region");
        }
        return sales;
    }

    public List<Sales> getSalesByProduct(Long productId) {
        List<Sales> sales = salesRepository.findByInvoice_Distributor_Products_Id(productId);
        if (sales.isEmpty()) {
            throw new ResourceNotFoundException("No sales records found for the specified product");
        }
        return sales;
    }

    public List<Sales> getSalesByInvoiceId(Long id) {
        List<Sales> sales = salesRepository.findByInvoiceId(id);
        if (sales.isEmpty()) {
            throw new ResourceNotFoundException("No sales records found for the specified invoice ID");
        }
        return sales;
    }

    @Override
    public List<Map<String, Object>> getSalesOfProducts(Optional<String> city, Optional<String> region,
            Optional<String> zone,
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
                    throw new IllegalArgumentException(
                            "Both customFromDate and customToDate must be provided for customdate interval.");
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid interval provided.");
        }

        List<Object[]> dataList = invoiceRepository.findTopSalesProducts(
                city.orElse(null),
                region.orElse(null),
                zone.orElse(null),
                fromDate,
                toDate);

        List<Map<String, Object>> productData = new ArrayList<>();

        for (Object[] objects : dataList) {
            Map<String, Object> productInfo = new HashMap<>();
            productInfo.put("Product Quantity", objects[2]);
            productInfo.put("Product Price", objects[1]);
            productInfo.put("Product Name", objects[0]);

            Integer quanString = Integer.parseInt(objects[2].toString());
            Double priceString = Double.parseDouble(objects[1].toString());
            Double totalPrice = quanString * priceString;

            productInfo.put("Total Price", totalPrice);

            productData.add(productInfo);
        }

        return productData;
    }

    @Override
    public List<Map<String, Object>> getSalesOfCategories(Optional<String> city, Optional<String> region, Optional<String> zone,
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
        
        List<Object[]> dataList = invoiceRepository.findTopSalesCategoriesByCityRegionAndZone(
                city.orElse(null),
                region.orElse(null),
                zone.orElse(null),
                fromDate,
                toDate
        );
        
        List<Map<String, Object>> result = process(dataList);
        
		return result;
	}
    
    private List<Map<String, Object>> process(List<Object[]> dataList) {
        Map<String, Map<String, Object>> categoryMap = new HashMap<>();

        for (Object[] data : dataList) {
            String categoryName = (String) data[0];
            Integer quantity = Integer.parseInt(data[2].toString());
            Double price = Double.parseDouble(data[1].toString());

            if (!categoryMap.containsKey(categoryName)) {
                Map<String, Object> categoryInfo = new HashMap<>();
                categoryInfo.put("categoryName", categoryName);
                categoryInfo.put("totalPrice", 0.0);
                categoryInfo.put("products", new ArrayList<>());
                categoryMap.put(categoryName, categoryInfo);
            }

            Double totalPrice = (Double) categoryMap.get(categoryName).get("totalPrice") + (quantity * price);
            categoryMap.get(categoryName).put("totalPrice", totalPrice);

            Map<String, Object> productInfo = new HashMap<>();
            productInfo.put("price", price);
            productInfo.put("quantity", quantity);

            ((List<Map<String, Object>>) categoryMap.get(categoryName).get("products")).add(productInfo);
        }

        return new ArrayList<>(categoryMap.values());
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
        LocalDate endOfMonth = LocalDate.of(year, Month.valueOf(monthName.toUpperCase()), 1)
                .with(TemporalAdjusters.lastDayOfMonth());
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

}
