package com.sas.SalesAnalysisSystem.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sas.SalesAnalysisSystem.models.Invoice;
import com.sas.SalesAnalysisSystem.service.invoice.InvoiceService;
import com.sas.SalesAnalysisSystem.service.sales.SalesService;

@RestController
@RequestMapping("/api/v1/sales")
public class SalesController {

    private final SalesService salesService;
    private final InvoiceService invoiceService;

    @Autowired
    public SalesController(SalesService salesService, InvoiceService invoiceService) {
        this.salesService = salesService;
        this.invoiceService = invoiceService;
    }

//    @GetMapping("/invoices")
//    public ResponseEntity<List<Invoice>> getAllInvoicesInSales() {
//        List<Invoice> invoices = invoiceService.getAllInvoices();
//        return new ResponseEntity<>(invoices, HttpStatus.OK);
//    }

    @GetMapping("/updateSales")
    public void updateSales() {
        salesService.updateSalesTotalAmount();
    }

    @GetMapping("/getTotalAmount")
    public double getTotalAmount() {
        return salesService.getTotalAmountOfAllInvoices();
    }

    @GetMapping("/totalQuantity")
    public int getTotalQuantityOfAllInvoices() {
        return salesService.getTotalQuantityOfAllInvoices();
    }

    @GetMapping("/total-sales-by-products")
    public ResponseEntity<?> getTotalSalesByProducts(
            @RequestParam(required = false) Optional<String> city,
            @RequestParam(required = false) Optional<String> region,
            @RequestParam(required = false) Optional<String> zone,
            @RequestParam(required = false) Optional<Integer> year,
            @RequestParam(required = false) Optional<String> month,
            @RequestParam String interval,
            @RequestParam(required = false) Optional<String> customFromDate,
            @RequestParam(required = false) Optional<String> customToDate) {
        try {
            if (interval.equals("customdate")) {
                if (customFromDate.isPresent() && customToDate.isPresent()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate fromDate = LocalDate.parse(customFromDate.get(), formatter);
                    LocalDate toDate = LocalDate.parse(customToDate.get(), formatter);

                    String fromDateTimeStr = fromDate.toString();
                    String toDateTimeStr = toDate.toString();

                    List<Map<String, Object>> Products = salesService.getSalesOfProducts(
                            city, region, zone, year, month, interval, Optional.of(fromDateTimeStr),
                            Optional.of(toDateTimeStr));
                    return ResponseEntity.ok(Products);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Both customFromDate and customToDate must be provided for customdate interval.");
                }
            } else {
                List<Map<String, Object>> Products = salesService.getSalesOfProducts(
                        city, region, zone, year, month, interval, null, null);
                return ResponseEntity.ok(Products);
            }
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format provided.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/total-sales-by-category")
    public ResponseEntity<?> getTotalSalesByCategory(
    		@RequestParam(required = false) Optional<String> city,
            @RequestParam(required = false) Optional<String> region,
            @RequestParam(required = false) Optional<String> zone,
            @RequestParam(required = false) Optional<Integer> year,
            @RequestParam(required = false) Optional<String> month,
            @RequestParam String interval,
            @RequestParam(required = false) Optional<String> customFromDate,
            @RequestParam(required = false) Optional<String> customToDate) {
    	 try {
             if (interval.equals("customdate")) {
                 if (customFromDate.isPresent() && customToDate.isPresent()) {
                 	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                     LocalDate fromDate = LocalDate.parse(customFromDate.get(), formatter);
                     LocalDate toDate = LocalDate.parse(customToDate.get() , formatter);
                     
                     String fromDateTimeStr = fromDate.toString();
                     String toDateTimeStr = toDate.toString();
                     
                     List<Map<String, Object>> category = salesService.getSalesOfCategories(
                             city, region, zone, year, month, interval, Optional.of(fromDateTimeStr), Optional.of(toDateTimeStr));
                     return ResponseEntity.ok(category);
                 } else {
                     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Both customFromDate and customToDate must be provided for customdate interval.");
                 }
             } else {
            	 List<Map<String, Object>> category = salesService.getSalesOfCategories(
                         city, region, zone, year, month, interval, null, null);
                 return ResponseEntity.ok(category);
             }
         } catch (DateTimeParseException e) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format provided.");
         } catch (IllegalArgumentException e) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
         }
    }
}
