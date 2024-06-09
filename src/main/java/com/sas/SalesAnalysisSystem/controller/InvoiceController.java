package com.sas.SalesAnalysisSystem.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.IOException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;

import com.sas.SalesAnalysisSystem.dto.DistributorDetails;
import com.sas.SalesAnalysisSystem.exception.CustomErrorResponse;
import com.sas.SalesAnalysisSystem.exception.ResourceNotFoundException;
import com.sas.SalesAnalysisSystem.models.Category;
import com.sas.SalesAnalysisSystem.models.Distributor;
import com.sas.SalesAnalysisSystem.models.Invoice;
import com.sas.SalesAnalysisSystem.models.Product;
import com.sas.SalesAnalysisSystem.models.ProductDetails;
import com.sas.SalesAnalysisSystem.models.Salesperson;
import com.sas.SalesAnalysisSystem.repository.InvoiceRepository;
import com.sas.SalesAnalysisSystem.repository.SalespersonRepository;
import com.sas.SalesAnalysisSystem.service.invoice.InvoiceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/invoices")
public class InvoiceController {
    @Autowired
    private InvoiceService invoiceService;
    private final SalespersonRepository salespersonRepository;

    public InvoiceController(InvoiceService invoiceService, SalespersonRepository salespersonRepository) {
        this.invoiceService = invoiceService;
        this.salespersonRepository = salespersonRepository;
    }


    @PostMapping("/createInvoice")
    public ResponseEntity<Object> createInvoice(
            @RequestParam("invoiceNumber") String invoiceNumber,
            @RequestParam("irn") String irn,
            @RequestParam("ackNo") double ackNo,
            @RequestParam("dispatchedThrough") String dispatchedThrough,
            @RequestParam("destination") String destination,
            @RequestParam("vehicleNo") String vehicleNo,
            @RequestParam("cgst") double cgst,
            @RequestParam("sgst") double sgst,
            @RequestParam("igst") double igst,
            @RequestParam("totalAmount") double totalAmount,
            @RequestParam("purchaseNumber") String purchaseNumber,
            @RequestParam("deliveryDate") String deliveryDate,
            @RequestParam("supplierName") String supplierName,
            @RequestParam("discountPercentage") double discountPercentage,
            @RequestParam("discountPrice") double discountPrice,
            @RequestParam("totalQuantityNos") int totalQuantityNos,
            @RequestParam("totalQuantityDoz") int totalQuantityDoz,
            @RequestParam("amount") double amount,
            @RequestParam("termsOfDelivery") String termsOfDelivery,
            @RequestParam("distributorId") Long distributorId,
            @RequestParam("salespersonId") Long salespersonId,
            @RequestParam("isReceived") Boolean isReceived,
            @RequestParam("invoiceDate") String invoiceDate) {
        try {
            Invoice invoice = new Invoice();
            invoice.setInvoiceNumber(invoiceNumber);
            invoice.setIRN(irn);
            invoice.setAckNo(ackNo);
            invoice.setDispatchedThrough(dispatchedThrough);
            invoice.setDestination(destination);
            invoice.setVechicleNo(vehicleNo);
            invoice.setCgst(cgst);
            invoice.setSgst(sgst);
            invoice.setIgst(igst);
            invoice.setTotalAmount(totalAmount);
            invoice.setPurchaseNumber(purchaseNumber);
            invoice.setDeliveryDate(LocalDate.parse(deliveryDate));
            invoice.setSupplierName(supplierName);
            invoice.setDiscountPercentage(discountPercentage);
            invoice.setDiscountPrice(discountPrice);
            invoice.setTotalQuantity_Nos(totalQuantityNos);
            invoice.setTotalQuantity_Doz(totalQuantityDoz);
            invoice.setAmount(amount);
            invoice.setIsReceived(isReceived);
            invoice.setTermsOfDelivery(termsOfDelivery);
            LocalDateTime newDate= LocalDateTime.parse(invoiceDate +"T"+ LocalTime.now());
            invoice.setInvoiceDate(newDate);
            Distributor distributor = new Distributor();
            distributor.setId(distributorId);
            invoice.setDistributor(distributor);

            Optional<Salesperson> foundSasOptional = salespersonRepository.findById(salespersonId);
            if (foundSasOptional.isPresent()) {
                Salesperson salesperson = foundSasOptional.get();
                invoice.setSalespersonId(salesperson.getId());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sales person not found");
            }

            Invoice createdInvoice = invoiceService.createInvoice(invoice);
            return new ResponseEntity<>(createdInvoice, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    private boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean containsDecimal(String input) {
        Pattern pattern = Pattern.compile(".*\\d+\\.\\d+.*");
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
    public static boolean isValidGSTNumber(String gstNumber) {
        Pattern pattern = Pattern.compile("^\\d{2}[A-Z]{5}\\d{4}[A-Z]{1}\\d[Z]{1}[A-Z\\d]{1}$");
        Matcher matcher = pattern.matcher(gstNumber);
        return matcher.matches();
    }
    
    @PutMapping("/invoice-received-status")
    public ResponseEntity<Object> toggleStatusCategory(@RequestParam("id") Long id) {
        try {
            invoiceService.toggleStatus(id);
            return ResponseEntity.ok("Invoice status successfully toggle.");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invoice not found with id: " + id);
        }
    }
    
    @GetMapping("/days-wise-invoices")
    public ResponseEntity<?> getInvoicesByAge(
        @RequestParam int days,
        @RequestParam(required = false) Boolean status,
        @RequestParam(required = false) String search, 
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        
        try {

            Pageable pageable = PageRequest.of(page, size);
            Page<DistributorDetails> invoices = invoiceService.findInvoicesByInvoiceAge(days, status, search, pageable);
            Map<String, Object> response = new HashMap<>();
            response.put("invoices", invoices.getContent());
            response.put("currentPage", invoices.getNumber());
            response.put("totalItems", invoices.getTotalElements());
            response.put("totalPages", invoices.getTotalPages());
            
            return ResponseEntity.ok().body(response);
            
        } catch (Exception e) {            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error occurred while fetching invoices. " + e.getMessage());
        }
    }
    
    
    @GetMapping("/products-sold")
    public ResponseEntity<?> getProductsSoldToDistributorThisMonthAndSameMonthLastYear(@RequestParam Long distributorId) {
        try {
            List<Object[]> productsSold = invoiceService.findProductsSoldToDistributorThisMonthAndSameMonthLastYear(distributorId);
            return ResponseEntity.ok(productsSold);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request");
        }
    }
    
    @GetMapping("/all-pages")
    public ResponseEntity<Object> getAllInvoices(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false )Boolean isReceived,
        @RequestParam(required = false) String selectQuery
    ) {	
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Invoice> invoicePage;
            if(isReceived==null &&selectQuery != null && !selectQuery.isEmpty() && !containsDecimal(selectQuery) && !isValidGSTNumber(selectQuery)) {
            	invoicePage = invoiceService.searchInvoices(selectQuery, pageable);
            }
            else if(isReceived == null&&selectQuery != null && !selectQuery.isEmpty() && isValidGSTNumber(selectQuery)) {
           	 invoicePage=invoiceService.findByGSTNo(selectQuery,pageable);
            } else if(isReceived == null && selectQuery != null && !selectQuery.isEmpty() && isDouble(selectQuery)&&  !isValidGSTNumber(selectQuery)){
            	invoicePage = invoiceService.findByTotalAmount(selectQuery, pageable);
            }
            else if (isReceived != null &&selectQuery != null && !selectQuery.isEmpty() && !containsDecimal(selectQuery) && !isValidGSTNumber(selectQuery)) {
            	 if (isReceived) {
                     invoicePage = invoiceService.searchInvoicesByReceivedFlag(true,selectQuery, pageable);
                 } 
            	 else {
                     invoicePage = invoiceService.searchInvoicesByReceivedFlag(false,selectQuery, pageable);
                 }
            } 
             else if (isReceived != null&&selectQuery != null && !selectQuery.isEmpty() && isValidGSTNumber(selectQuery)) {
                 if (isReceived) {
                	 invoicePage=invoiceService.findByGSTNoWithFlag(true,selectQuery,pageable);
                 }
                 else {
                	 invoicePage=invoiceService.findByGSTNoWithFlag(false,selectQuery,pageable);
                 }
             }
             else if (isReceived != null && selectQuery != null && !selectQuery.isEmpty() && isDouble(selectQuery)) {
            	 if (isReceived) {
                	 invoicePage=invoiceService.findByTotalAmountWithFlag(true,selectQuery,pageable);
                 }
                 else {
                	 invoicePage=invoiceService.findByTotalAmountWithFlag(false,selectQuery,pageable);
                 }
            }else if(selectQuery == null && isReceived != null ) {
            	 if (isReceived) {
                	 invoicePage=invoiceService.getAllInvoicesByIsReceived(true,pageable);
                 }
                 else {
                	 invoicePage=invoiceService.getAllInvoicesByIsReceived(false,pageable);
                 }
            }
             else {
            	
                invoicePage = invoiceService.getAllInvoices(pageable);
            }

            Map<String, Object> response = new HashMap<>();
            List<Invoice> invoices = invoicePage.getContent();
            response.put("invoices", invoices);
            response.put("currentPage", invoicePage.getNumber());
            response.put("totalItems", invoicePage.getTotalElements());
            response.put("totalPages", invoicePage.getTotalPages());
            return ResponseEntity.ok().body(response);
        } catch (ResourceNotFoundException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("No invoices found");
            errorResponse.setMessage(e.getMessage());
            errorResponse.setPath("/api/v1/invoices/all-pages");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }


    
    @GetMapping("/all")
    public ResponseEntity<Object> getAllInvoices() {
        try {
            List<Invoice> invoices = invoiceService.getAllInvoices();
            return ResponseEntity.ok().body(invoices);
        } catch (ResourceNotFoundException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("No invoices found");
            errorResponse.setMessage(e.getMessage());
            errorResponse.setPath("/api/v1/invoices/all");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @GetMapping("/invoiceNumbers")
    public ResponseEntity<List<String>> getAllInvoiceNumbers() {
        List<String> invoiceNumbers = invoiceService.findAllInvoiceNumbers();
        return ResponseEntity.ok(invoiceNumbers);
    }

    @GetMapping("/irns")
    public ResponseEntity<List<String>> getAllIRNs() {
        List<String> irns = invoiceService.findAllIRNs();
        return ResponseEntity.ok(irns);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getInvoiceById(@PathVariable("id") Long id) {
        try {
            Invoice invoice = invoiceService.getInvoiceById(id);
            return ResponseEntity.ok().body(invoice);
        } catch (ResourceNotFoundException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("No invoice found for this id - " + id);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setPath("/api/v1/invoices/all/" + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
    
    @DeleteMapping("/invoices/{invoiceId}/products/{productId}")
    public ResponseEntity<String> removeProductFromInvoice(
            @PathVariable Long invoiceId,
            @PathVariable Long productId
    ) {
        try {
            invoiceService.removeProductFromInvoice(invoiceId, productId);
            return ResponseEntity.ok("Product removed from the invoice successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (ResourceNotFoundException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("No invoice found for this id - " + invoiceId);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setPath("/invoices/"+ invoiceId + "products/" + productId );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/update-invoice/{id}")
    public ResponseEntity<Object> updateInvoice(
            @PathVariable("id") Long id,
            @RequestParam(value = "invoiceNumber", required = false) String invoiceNumber,
            @RequestParam(value = "irn", required = false) String irn,
            @RequestParam(value = "ackNo", required = false) Double ackNo,
            @RequestParam(value = "dispatchedThrough", required = false) String dispatchedThrough,
            @RequestParam(value = "destination", required = false) String destination,
            @RequestParam(value = "vehicleNo", required = false) String vehicleNo,
            @RequestParam(value = "cgst", required = false) Double cgst,
            @RequestParam(value = "sgst", required = false) Double sgst,
            @RequestParam(value = "igst", required = false) Double igst,
            @RequestParam(value = "totalAmount", required = false) Double totalAmount,
            @RequestParam(value = "purchaseNumber", required = false) String purchaseNumber,
            @RequestParam(value = "deliveryDate", required = false) String deliveryDate,
            @RequestParam(value = "supplierName", required = false) String supplierName,
            @RequestParam(value = "discountPercentage", required = false) Double discountPercentage,
            @RequestParam(value = "discountPrice", required = false) Double discountPrice,
            @RequestParam(value = "totalQuantityNos", required = false) Integer totalQuantityNos,
            @RequestParam(value = "totalQuantityDoz", required = false) Integer totalQuantityDoz,
            @RequestParam(value = "amount", required = false) Double amount,
            @RequestParam(value = "termsOfDelivery", required = false) String termsOfDelivery,
            @RequestParam(value = "distributorId", required = false) Long distributorId,
            @RequestParam(value = "salespersonId", required = false) Long salespersonId,
    		@RequestParam(value ="invoiceDate" , required = false) String invoiceDate,
    		@RequestParam(value ="isReceived" , required=false) Boolean isReceived)
    {
        try {
            Invoice existingInvoice = invoiceService.getInvoiceById(id);
            if (existingInvoice == null) {
                throw new ResourceNotFoundException("Invoice not found with id: " + id);
            }

            if (invoiceNumber != null) {
                existingInvoice.setInvoiceNumber(invoiceNumber);
            }
            if (irn != null) {
                existingInvoice.setIRN(irn);
            }
            if (ackNo != null) {
                existingInvoice.setAckNo(ackNo);
            }
            if (dispatchedThrough != null) {
                existingInvoice.setDispatchedThrough(dispatchedThrough);
            }
            if (destination != null) {
                existingInvoice.setDestination(destination);
            }
            if (vehicleNo != null) {
                existingInvoice.setVechicleNo(vehicleNo);
            }
            if (cgst != null) {
                existingInvoice.setCgst(cgst);
            }
            if (sgst != null) {
                existingInvoice.setSgst(sgst);
            }
            if (igst != null) {
                existingInvoice.setIgst(igst);
            }
            if (totalAmount != null) {
                existingInvoice.setTotalAmount(totalAmount);
            }
            if (purchaseNumber != null) {
                existingInvoice.setPurchaseNumber(purchaseNumber);
            }
            if (deliveryDate != null) {
                existingInvoice.setDeliveryDate(LocalDate.parse(deliveryDate));
            }
            if (supplierName != null) {
                existingInvoice.setSupplierName(supplierName);
            }
            if (discountPercentage != null) {
                existingInvoice.setDiscountPercentage(discountPercentage);
            }
            if (discountPrice != null) {
                existingInvoice.setDiscountPrice(discountPrice);
            }
            if (totalQuantityNos != null) {
                existingInvoice.setTotalQuantity_Nos(totalQuantityNos);
            }
            if (totalQuantityDoz != null) {
                existingInvoice.setTotalQuantity_Doz(totalQuantityDoz);
            }
            if (amount != null) {
                existingInvoice.setAmount(amount);
            }
            if(isReceived !=null){
            	existingInvoice.setIsReceived(isReceived);
            }
            if (termsOfDelivery != null) {
                existingInvoice.setTermsOfDelivery(termsOfDelivery);
            }
            if (distributorId != null) {
                Distributor distributor = new Distributor();
                distributor.setId(distributorId);
                existingInvoice.setDistributor(distributor);
            }
            if(invoiceDate != null) {
                LocalDateTime newDate= LocalDateTime.parse(invoiceDate +"T"+  LocalTime.now());
                existingInvoice.setInvoiceDate(newDate);
            }
            if (salespersonId != null) {
                Optional<Salesperson> foundSasOptional = salespersonRepository.findById(salespersonId);
                if (foundSasOptional.isPresent()) {
                    Salesperson salesperson = foundSasOptional.get();
                    existingInvoice.setSalespersonId(salesperson.getId());
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sales person not found");
                }
            }

            Invoice updatedInvoice = invoiceService.updateInvoice(id, existingInvoice);
            return ResponseEntity.ok().body(updatedInvoice);
        } catch (ResourceNotFoundException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("No invoice found for this id - " + id);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setPath("/api/v1/invoices/all/" + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-invoice/{id}")
    public ResponseEntity<Object> deleteInvoice(@PathVariable("id") Long id) {
        try {
            invoiceService.deleteInvoice(id);
            return ResponseEntity.ok("Invoice and Eway successfully deleted.");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invoice not found with id: " + id);
        }
    }

    @PostMapping("/{invoiceId}/add-products")
    public ResponseEntity<Object> addProductsToInvoice(
            @PathVariable("invoiceId") Long invoiceId,
            @RequestBody Map<Long, ProductDetails> productQuantities) {
        try {
            invoiceService.addProductToInvoice(invoiceId, productQuantities);
            return ResponseEntity.ok("Products added to invoice successfully.");
        } catch (ResourceNotFoundException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("Invoice Id Not Found - " + invoiceId);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setPath("/api/invoices/" + invoiceId + "/add-products");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PutMapping("/{invoiceId}/update-products")
    public ResponseEntity<Object> updateProductsInInvoice(
            @PathVariable("invoiceId") Long invoiceId,
            @RequestBody Map<Long, ProductDetails> productQuantities) {
        try {
            invoiceService.updateProductToInvoice(invoiceId, productQuantities);
            return ResponseEntity.ok("Products updated in invoice successfully.");
        } catch (IllegalArgumentException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            errorResponse.setError("Invalid Request");
            errorResponse.setMessage(e.getMessage());
            errorResponse.setPath("/api/invoices/" + invoiceId + "/products");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/get-invoices-between-dates")
    public ResponseEntity<Object> getInvoicesBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Invoice> invoices = invoiceService.findInvoicesBetweenDates(startDate, endDate);
        if (invoices.isEmpty()) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("No invoices found between the provided dates.");
            errorResponse.setMessage("No invoices found between " + startDate + " and " + endDate);
            errorResponse.setPath("/api/get-invoices-between-dates");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } else {
            return ResponseEntity.ok(invoices);
        }
    }

    @GetMapping("/total-sum-by-month")
    public ResponseEntity<Object> getTotalSumOfInvoicesByMonth() {
        Map<String, Double> totalSumByMonth;
        try {
            totalSumByMonth = invoiceService.getTotalSumOfInvoicesByMonth();
        } catch (ResourceNotFoundException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("No total sum of invoices found by month.");
            errorResponse.setMessage("No total sum of invoices found by month.");
            errorResponse.setPath("/api/total-sum-by-month");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        return new ResponseEntity<>(totalSumByMonth, HttpStatus.OK);
    }

    @GetMapping("/total-sum-by-month-custom-date")
    public ResponseEntity<Object> getTotalSumOfInvoicesByMonthCustomDate(
            @RequestParam("fromDate") String fromDateStr,
            @RequestParam("toDate") String toDateStr) {

        Map<String, Double> totalSumByMonth;
        LocalDate fromDate;
        LocalDate toDate;
        try {
            fromDate = LocalDate.parse(fromDateStr);
            toDate = LocalDate.parse(toDateStr);
            // if (!isWithinLastThreeFinancialYears(fromDate, toDate)) {
            // return outOfFinancialYearRangeResponse();
            // }

            totalSumByMonth = invoiceService.getTotalSumOfInvoicesByMonth(
                    LocalDate.parse(fromDateStr),
                    LocalDate.parse(toDateStr));
        } catch (DateTimeParseException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            errorResponse.setError("Invalid date format provided.");
            errorResponse.setMessage("Invalid date format provided.");
            errorResponse.setPath("total-sum-by-month-custom-date");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (ResourceNotFoundException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("No total sum of invoices found for the specified date range.");
            errorResponse.setMessage("No total sum of invoices found for the specified date range.");
            errorResponse.setPath("total-sum-by-month-custom-date");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        return new ResponseEntity<>(totalSumByMonth, HttpStatus.OK);
    }

    private boolean isWithinLastThreeFinancialYears(LocalDate fromDate, LocalDate toDate) {
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int lastYear = currentYear - 1;
        int twoYearsAgo = currentYear - 2;
        int threeYearsAgo = currentYear - 3;

        LocalDate financialYearStart = LocalDate.of(currentYear, Month.APRIL, 1);
        LocalDate financialYearEnd = LocalDate.of(currentYear + 1, Month.MARCH, 31);

        if ((fromDate.isBefore(financialYearStart) || fromDate.isEqual(financialYearStart))
                && (toDate.isAfter(financialYearEnd) || toDate.isEqual(financialYearEnd))) {
            return true;
        }

        financialYearStart = LocalDate.of(lastYear, Month.APRIL, 1);
        financialYearEnd = LocalDate.of(lastYear + 1, Month.MARCH, 31);

        if ((fromDate.isBefore(financialYearStart) || fromDate.isEqual(financialYearStart))
                && (toDate.isAfter(financialYearEnd) || toDate.isEqual(financialYearEnd))) {
            return true;
        }

        financialYearStart = LocalDate.of(twoYearsAgo, Month.APRIL, 1);
        financialYearEnd = LocalDate.of(twoYearsAgo + 1, Month.MARCH, 31);

        if ((fromDate.isBefore(financialYearStart) || fromDate.isEqual(financialYearStart))
                && (toDate.isAfter(financialYearEnd) || toDate.isEqual(financialYearEnd))) {
            return true;
        }

        return false;
    }

    private ResponseEntity<Object> outOfFinancialYearRangeResponse() {
        CustomErrorResponse errorResponse = new CustomErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setError("Dates provided fall outside the last three financial years.");
        errorResponse.setMessage("Dates provided fall outside the last three financial years.");
        errorResponse.setPath("total-sum-by-month-custom-date");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @GetMapping("/total-sum-by-financial-year")
    public ResponseEntity<Object> getTotalSumOfInvoicesByFinancialYear() {
        Map<String, Double> totalSumByMonth;
        try {
            totalSumByMonth = invoiceService.getTotalSumOfInvoicesByFinancialYear();
        } catch (ResourceNotFoundException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("No total sum of invoices found by financial year.");
            errorResponse.setMessage("No total sum of invoices found by financial year.");
            errorResponse.setPath("/api/total-sum-by-financial-year");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        return new ResponseEntity<>(totalSumByMonth, HttpStatus.OK);
    }

    @GetMapping("/{invoiceId}/top-5-products")
    public ResponseEntity<Object> getTop5ProductsByInvoiceId(@PathVariable Long invoiceId) {
        List<Object[]> topProducts = invoiceService.findTop5ProductsByInvoiceId(invoiceId);
        if (topProducts.isEmpty()) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("No top products found for the provided invoiceId.");
            errorResponse.setMessage("No top products found for invoiceId: " + invoiceId);
            errorResponse.setPath("/api/invoices/" + invoiceId + "/top-5-products");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } else {
            return ResponseEntity.ok(topProducts);
        }
    }

    @GetMapping("/totalInvoiceAmountAndExpenses")
    public ResponseEntity<Object> getTotalInvoiceAmountAndExpenses(
            @RequestParam("Salesperson") Optional<Long> salespersonName,
            @RequestParam("interval") String interval,
            @RequestParam("year") Optional<Integer> year,
            @RequestParam("monthName") Optional<String> monthName,
            @RequestParam("customFromDate") Optional<String> customFromDate,
            @RequestParam("customToDate") Optional<String> customToDate,
            @RequestParam("status") Boolean status) {

        List<Map<String, Object>> totalInvoiceAndExpenses;
        try {
            if (interval.equals("customdate")) {
                LocalDateTime fromDate = LocalDateTime.parse(customFromDate.orElseThrow(),
                        DateTimeFormatter.ISO_DATE_TIME);
                LocalDateTime toDate = LocalDateTime.parse(customToDate.orElseThrow(), DateTimeFormatter.ISO_DATE_TIME);
                totalInvoiceAndExpenses = invoiceService.totalInvoiceAmountAndExpenses(salespersonName, interval, year,
                        monthName, customFromDate, customToDate, status);
            } else {
                totalInvoiceAndExpenses = invoiceService.totalInvoiceAmountAndExpenses(salespersonName, interval, year,
                        monthName, Optional.empty(), Optional.empty(), status);
            }
            return ResponseEntity.ok(totalInvoiceAndExpenses);
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format provided.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the request.");
        }
    }

    @GetMapping("/top-selling-products")
    public ResponseEntity<Object> getTopSellingProducts(
            @RequestParam("interval") String interval,
            @RequestParam("year") Optional<Integer> year,
            @RequestParam("month") Optional<String> month,
            @RequestParam("fromDate") Optional<String> fromDateStr,
            @RequestParam("toDate") Optional<String> toDateStr) {
        List<Object[]> topProducts;
        try {
            if (interval.equals("customdate")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate fromDate = LocalDate.parse(fromDateStr.get(), formatter);
                LocalDate toDate = LocalDate.parse(toDateStr.get(), formatter);
                System.out.println(fromDate + " " + toDate);

                String fromDateTimeStr = fromDate.toString();
                String toDateTimeStr = toDate.toString();
                System.out.println(fromDateTimeStr + " " + toDateTimeStr);

                topProducts = invoiceService.findTopSellingProducts(interval, year, month, Optional.of(fromDateTimeStr),
                        Optional.of(toDateTimeStr));
            } else {
                topProducts = invoiceService.findTopSellingProducts(interval, year, month, Optional.empty(),
                        Optional.empty());
            }
            return ResponseEntity.ok(topProducts);
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format provided.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return handleResourceNotFoundException(e, "/api/top-selling-products");
        }
    }

    @GetMapping("/least-selling-products")
    public ResponseEntity<Object> getLeastSellingProducts(
            @RequestParam("interval") String interval,
            @RequestParam("year") Optional<Integer> year,
            @RequestParam("month") Optional<String> month,
            @RequestParam("fromDate") Optional<String> fromDateStr,
            @RequestParam("toDate") Optional<String> toDateStr) {
        List<Object[]> leastProducts;
        try {
            if (interval.equals("customdate")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate fromDate = LocalDate.parse(fromDateStr.get(), formatter);
                LocalDate toDate = LocalDate.parse(toDateStr.get(), formatter);
                System.out.println(fromDate + " " + toDate);

                String fromDateTimeStr = fromDate.toString();
                String toDateTimeStr = toDate.toString();
                System.out.println(fromDateTimeStr + " " + toDateTimeStr);

                leastProducts = invoiceService.findLeastSellingProducts(interval, year, month,
                        Optional.of(fromDateTimeStr), Optional.of(toDateTimeStr));
            } else {
                leastProducts = invoiceService.findLeastSellingProducts(interval, year, month, Optional.empty(),
                        Optional.empty());
            }
            return ResponseEntity.ok(leastProducts);
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format provided.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return handleResourceNotFoundException(e, "/api/least-selling-products-by-region");
        }
    }

    @GetMapping("/top-selling-products-by-city")
    public ResponseEntity<Object> getTopSellingProductsByCity(
            @RequestParam String city,
            @RequestParam Optional<Integer> year,
            @RequestParam Optional<String> month,
            @RequestParam String interval,
            @RequestParam("fromDate") Optional<String> fromDateStr,
            @RequestParam("toDate") Optional<String> toDateStr) {
        try {
            if (interval.equals("customdate")) {
                if (fromDateStr.isPresent() && toDateStr.isPresent()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate fromDate = LocalDate.parse(fromDateStr.get(), formatter);
                    LocalDate toDate = LocalDate.parse(toDateStr.get(), formatter);

                    String fromDateTimeStr = fromDate.toString();
                    String toDateTimeStr = toDate.toString();

                    List<Object[]> topProducts = invoiceService.findTopSellingProductsByCity(city, year, month,
                            interval, Optional.of(fromDateTimeStr), Optional.of(toDateTimeStr));
                    return ResponseEntity.ok(topProducts);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Both fromDate and toDate must be provided for customdate interval.");
                }
            } else {
                List<Object[]> topProducts = invoiceService.findTopSellingProductsByCity(city, year, month, interval,
                        Optional.empty(), Optional.empty());
                return ResponseEntity.ok(topProducts);
            }
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format provided.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return handleResourceNotFoundException(e, "/api/least-selling-products-by-region");
        }
    }

    @GetMapping("/top-selling-products-by-region")
    public ResponseEntity<Object> getTopSellingProductsByRegion(
            @RequestParam String region,
            @RequestParam Optional<Integer> year,
            @RequestParam Optional<String> month,
            @RequestParam String interval,
            @RequestParam("fromDate") Optional<String> fromDateStr,
            @RequestParam("toDate") Optional<String> toDateStr) {
        try {
            if (interval.equals("customdate")) {
                if (fromDateStr.isPresent() && toDateStr.isPresent()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate fromDate = LocalDate.parse(fromDateStr.get(), formatter);
                    LocalDate toDate = LocalDate.parse(toDateStr.get(), formatter);

                    String fromDateTimeStr = fromDate.toString();
                    String toDateTimeStr = toDate.toString();

                    List<Object[]> topProducts = invoiceService.findTopSellingProductsByRegion(region, year, month,
                            interval, Optional.of(fromDateTimeStr), Optional.of(toDateTimeStr));
                    return ResponseEntity.ok(topProducts);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Both fromDate and toDate must be provided for customdate interval.");
                }
            } else {

                List<Object[]> topProducts = invoiceService.findTopSellingProductsByRegion(region, year, month,
                        interval, Optional.empty(), Optional.empty());
                return ResponseEntity.ok(topProducts);
            }
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format provided.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return handleResourceNotFoundException(e, "/api/least-selling-products-by-region");
        }
    }

    @GetMapping("/top-selling-products-by-zone")
    public ResponseEntity<Object> getTopSellingProductsByZone(
            @RequestParam String zone,
            @RequestParam Optional<Integer> year,
            @RequestParam Optional<String> month,
            @RequestParam String interval,
            @RequestParam("fromDate") Optional<String> fromDateStr,
            @RequestParam("toDate") Optional<String> toDateStr) {
        try {
            if (interval.equals("customdate")) {
                if (fromDateStr.isPresent() && toDateStr.isPresent()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate fromDate = LocalDate.parse(fromDateStr.get(), formatter);
                    LocalDate toDate = LocalDate.parse(toDateStr.get(), formatter);

                    String fromDateTimeStr = fromDate.toString();
                    String toDateTimeStr = toDate.toString();

                    List<Object[]> topProducts = invoiceService.findTopSellingProductsByZone(zone, year, month,
                            interval, Optional.of(fromDateTimeStr), Optional.of(toDateTimeStr));
                    return ResponseEntity.ok(topProducts);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Both fromDate and toDate must be provided for customdate interval.");
                }
            } else {
                List<Object[]> topProducts = invoiceService.findTopSellingProductsByZone(zone, year, month, interval,
                        Optional.empty(), Optional.empty());
                return ResponseEntity.ok(topProducts);
            }
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format provided.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return handleResourceNotFoundException(e, "/api/least-selling-products-by-region");
        }
    }

    @GetMapping("/least-selling-products-by-region")
    public ResponseEntity<Object> getLeastSellingProductsByRegion(
            @RequestParam String region,
            @RequestParam Optional<Integer> year,
            @RequestParam Optional<String> month,
            @RequestParam String interval,
            @RequestParam("fromDate") Optional<String> fromDateStr,
            @RequestParam("toDate") Optional<String> toDateStr) {
        try {
            if (interval.equals("customdate")) {
                if (fromDateStr.isPresent() && toDateStr.isPresent()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate fromDate = LocalDate.parse(fromDateStr.get(), formatter);
                    LocalDate toDate = LocalDate.parse(toDateStr.get(), formatter);

                    String fromDateTimeStr = fromDate.toString();
                    String toDateTimeStr = toDate.toString();

                    List<Object[]> leastProducts = invoiceService.findLeastSellingProductsByRegion(region, year, month,
                            interval, Optional.of(fromDateTimeStr), Optional.of(toDateTimeStr));
                    return ResponseEntity.ok(leastProducts);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Both fromDate and toDate must be provided for customdate interval.");
                }
            } else {
                List<Object[]> leastProducts = invoiceService.findLeastSellingProductsByRegion(region, year, month,
                        interval, Optional.empty(), Optional.empty());
                return ResponseEntity.ok(leastProducts);
            }
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format provided.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return handleResourceNotFoundException(e, "/api/least-selling-products-by-region");
        }
    }

    @GetMapping("/least-selling-products-by-zone")
    public ResponseEntity<Object> getLeastSellingProductsByZone(
            @RequestParam("zone") String zone,
            @RequestParam("interval") String interval,
            @RequestParam("year") Optional<Integer> year,
            @RequestParam("month") Optional<String> month,
            @RequestParam("fromDate") Optional<String> fromDateStr,
            @RequestParam("toDate") Optional<String> toDateStr) {
        try {
            if (interval.equals("customdate")) {
                if (fromDateStr.isPresent() && toDateStr.isPresent()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate fromDate = LocalDate.parse(fromDateStr.get(), formatter);
                    LocalDate toDate = LocalDate.parse(toDateStr.get(), formatter);

                    String fromDateTimeStr = fromDate.toString();
                    String toDateTimeStr = toDate.toString();

                    List<Object[]> leastProducts = invoiceService.findLeastSellingProductsByZone(zone, year, month,
                            interval, Optional.of(fromDateTimeStr), Optional.of(toDateTimeStr));
                    return ResponseEntity.ok(leastProducts);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Both fromDate and toDate must be provided for customdate interval.");
                }
            } else {
                List<Object[]> leastProducts = invoiceService.findLeastSellingProductsByZone(zone, year, month,
                        interval, Optional.empty(), Optional.empty());
                return ResponseEntity.ok(leastProducts);
            }
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format provided.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return handleResourceNotFoundException(e, "/api/least-selling-products-by-zone");
        }
    }

    @GetMapping("/least-selling-products-by-city")
    public ResponseEntity<Object> getLeastSellingProductsByCity(
            @RequestParam("city") String city,
            @RequestParam("interval") String interval,
            @RequestParam("year") Optional<Integer> year,
            @RequestParam("month") Optional<String> month,
            @RequestParam("fromDate") Optional<String> fromDateStr,
            @RequestParam("toDate") Optional<String> toDateStr) {
        try {
            if (interval.equals("customdate")) {
                if (fromDateStr.isPresent() && toDateStr.isPresent()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate fromDate = LocalDate.parse(fromDateStr.get(), formatter);
                    LocalDate toDate = LocalDate.parse(toDateStr.get(), formatter);

                    String fromDateTimeStr = fromDate.toString();
                    String toDateTimeStr = toDate.toString();

                    List<Object[]> leastProducts = invoiceService.findLeastSellingProductsByCity(city, year, month,
                            interval, Optional.of(fromDateTimeStr), Optional.of(toDateTimeStr));
                    return ResponseEntity.ok(leastProducts);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Both fromDate and toDate must be provided for customdate interval.");
                }
            } else {
                List<Object[]> leastProducts = invoiceService.findLeastSellingProductsByCity(city, year, month,
                        interval, Optional.empty(), Optional.empty());
                return ResponseEntity.ok(leastProducts);
            }
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format provided.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return handleResourceNotFoundException(e, "/api/least-selling-products-by-city");
        }
    }

    @GetMapping("/top-selling-products-by-category")
    public ResponseEntity<Object> getTop5SellingProductsByCategory(
            @RequestParam("interval") String interval,
            @RequestParam("year") Optional<Integer> year,
            @RequestParam("month") Optional<String> month,
            @RequestParam("fromDate") Optional<String> fromDateStr,
            @RequestParam("toDate") Optional<String> toDateStr) {
        try {
            if (interval.equals("customdate")) {
                if (fromDateStr.isPresent() && toDateStr.isPresent()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate fromDate = LocalDate.parse(fromDateStr.get(), formatter);
                    LocalDate toDate = LocalDate.parse(toDateStr.get(), formatter);

                    String fromDateTimeStr = fromDate.toString();
                    String toDateTimeStr = toDate.toString();

                    List<Object[]> topProducts = invoiceService.findTop5SellingProductsByCategory(year, month, interval,
                            Optional.of(fromDateTimeStr), Optional.of(toDateTimeStr));
                    return ResponseEntity.ok(topProducts);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Both fromDate and toDate must be provided for customdate interval.");
                }
            } else {
                List<Object[]> topProducts = invoiceService.findTop5SellingProductsByCategory(year, month, interval,
                        Optional.empty(), Optional.empty());
                return ResponseEntity.ok(topProducts);
            }
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format provided.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return handleResourceNotFoundException(e, "/api/top-selling-products-by-category");
        }
    }

    @GetMapping("/top-least-products-by-category")
    public ResponseEntity<Object> getTop5LeastProductsByCategory(
            @RequestParam("interval") String interval,
            @RequestParam("year") Optional<Integer> year,
            @RequestParam("month") Optional<String> month,
            @RequestParam("fromDate") Optional<String> fromDateStr,
            @RequestParam("toDate") Optional<String> toDateStr) {
        try {
            if (interval.equals("customdate")) {
                if (fromDateStr.isPresent() && toDateStr.isPresent()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate fromDate = LocalDate.parse(fromDateStr.get(), formatter);
                    LocalDate toDate = LocalDate.parse(toDateStr.get(), formatter);

                    String fromDateTimeStr = fromDate.toString();
                    String toDateTimeStr = toDate.toString();

                    List<Object[]> topProducts = invoiceService.findTop5LeastProductsByCategory(year, month, interval,
                            Optional.of(fromDateTimeStr), Optional.of(toDateTimeStr));
                    return ResponseEntity.ok(topProducts);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Both fromDate and toDate must be provided for customdate interval.");
                }
            } else {
                List<Object[]> topProducts = invoiceService.findTop5SellingProductsByCategory(year, month, interval,
                        Optional.empty(), Optional.empty());
                return ResponseEntity.ok(topProducts);
            }
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format provided.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return handleResourceNotFoundException(e, "/api/top-selling-products-by-category");
        }
    }

    @GetMapping("/most-selling-products-by-category")
    public ResponseEntity<Object> getMostSellingProductsByCategory(
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

                    List<Object[]> leastProducts = invoiceService.findMostSellingCategoriesByCityRegionAndZone(
                            city, region, zone, year, month, interval, Optional.of(fromDateTimeStr),
                            Optional.of(toDateTimeStr));
                    return ResponseEntity.ok(leastProducts);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Both customFromDate and customToDate must be provided for customdate interval.");
                }
            } else {
                List<Object[]> leastProducts = invoiceService.findMostSellingCategoriesByCityRegionAndZone(
                        city, region, zone, year, month, interval, null, null);
                return ResponseEntity.ok(leastProducts);
            }
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format provided.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/least-selling-products-by-category") 
    public ResponseEntity<Object> getLeastSellingProductsByCategory(
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

                    List<Object[]> leastProducts = invoiceService.findLeastSellingCategoriesByCityRegionAndZone(
                            city, region, zone, year, month, interval, Optional.of(fromDateTimeStr),
                            Optional.of(toDateTimeStr));
                    return ResponseEntity.ok(leastProducts);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Both customFromDate and customToDate must be provided for customdate interval.");
                }
            } else {
                List<Object[]> leastProducts = invoiceService.findLeastSellingCategoriesByCityRegionAndZone(
                        city, region, zone, year, month, interval, null, null);
                return ResponseEntity.ok(leastProducts);
            }
        } catch (DateTimeParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format provided.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/allproducts")
    public List<Category> getAllProductsInInvoices() {
        return invoiceService.findAllProductsInInvoices();
    }

    @GetMapping("/get-invoice-products-by-id/{id}")
    public List<Object[]> getInvoiceProductDetails(@PathVariable("id") Long id) {
        return invoiceService.getInvoiceProductDetails(id);
    }
    
    @GetMapping("/distributors/details")
    public ResponseEntity<Map<String, Object>> getAllDistributorsWithDetails(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) Long productId,
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) String invoiceNumber,
        @RequestParam(required =false)String query,
        @RequestParam(required = false) Optional<Integer> year,
        @RequestParam(required = false) Optional<String> month,
        @RequestParam String interval,
        @RequestParam(required = false) Optional<String> customFromDate,
        @RequestParam(required = false) Optional<String> customToDate)  
 {

        Pageable pageable = PageRequest.of(page, size);
        Page<Map<String, Object>> mappedDataPage;

        mappedDataPage = invoiceService.findAllDistributorsWithDetails(productId, categoryId, invoiceNumber,query,year,month,interval,customFromDate,customToDate,pageable);

        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> invoices = mappedDataPage.getContent();
        response.put("DistributorDetails", invoices);
        response.put("currentPage", mappedDataPage.getNumber());
        response.put("totalItems", mappedDataPage.getTotalElements());
        response.put("totalPages", mappedDataPage.getTotalPages());
        return ResponseEntity.ok().body(response);
    }
    
    @GetMapping("/distributors/details/{distributorId}")
    public ResponseEntity<?> getDistributorDetailsById(
            @PathVariable Long distributorId,
            @RequestParam(required = false) Optional<Integer> year,
            @RequestParam(required = false) Optional<String> monthName,
            @RequestParam String interval,
            @RequestParam(required = false) Optional<String> customFromDate,
            @RequestParam(required = false) Optional<String> customToDate) {

        try {
            if (!isValidInterval(interval)) {
                throw new IllegalArgumentException("Invalid interval provided. Please provide a valid interval.");
            }

            List<Map<String, Object>> mappedDataPage = invoiceService.findAllDistributorsWithDetailsByDistributorId(
                    distributorId, year, monthName, interval, customFromDate, customToDate);
            
            return ResponseEntity.ok().body(mappedDataPage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    private boolean isValidInterval(String interval) {
        return Arrays.asList("daily", "weekly", "monthly", "annually", "customdate").contains(interval.toLowerCase());
    }

    @GetMapping("/distributorIds/details")
    public ResponseEntity<Map<String, Object>> getDistributorDetailsByIds(
            @RequestParam List<Long> distributorIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Map<String, Object>> mappedDataPage = invoiceService.findAllDistributorsWithDetailsByDistributorIds(distributorIds, pageable);

        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> distributorDetails = mappedDataPage.getContent();
        response.put("DistributorDetails", distributorDetails);
        response.put("currentPage", mappedDataPage.getNumber());
        response.put("totalItems", mappedDataPage.getTotalElements());
        response.put("totalPages", mappedDataPage.getTotalPages());
        return ResponseEntity.ok().body(response);
    }


    private ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException e, String path) {
        CustomErrorResponse errorResponse = new CustomErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponse.setError(e.getMessage());
        errorResponse.setMessage(e.getMessage());
        errorResponse.setPath(path);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    
    @GetMapping("/invoices/distributor/{distributorId}/pdf")
    public ResponseEntity<byte[]> getInvoicePdfWithInterval(@PathVariable Long distributorId,
            @RequestParam(required = false) Optional<Integer> year,
            @RequestParam(required = false) Optional<String> monthName,
            @RequestParam String interval,
            @RequestParam(required = false) Optional<String> customFromDate,
            @RequestParam(required = false) Optional<String> customToDate) throws Exception {
        byte[] pdfContent = invoiceService.generatePdfForDistributorWithInterval(distributorId, year,monthName, interval, customFromDate, customToDate);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=distributor_invoice.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
        
        return ResponseEntity.ok()
                             .headers(headers)
                             .body(pdfContent);
    }
    
    
    @GetMapping("/distributors/details/by-filters")
    public ResponseEntity<Map<String, Object>> getAllDistributorsWithDetailsAndSeperatedFilter(
    		@RequestParam(defaultValue = "0") int page,
    	    @RequestParam(defaultValue = "10") int size,
    	    @RequestParam(required = false) String productName,
    	    @RequestParam(required = false) String categoryName,
    	    @RequestParam(required = false) String invoiceNumber,
    	    @RequestParam(required = false) Optional<String> invoiceSearch,
    	    @RequestParam(required = false) Optional<String> distributor,
    	    @RequestParam(required = false) Optional<String> salespersonName,
    	    @RequestParam(required = false) Optional<String> query,
    	    @RequestParam(required = false) Optional<Integer> year,
    	    @RequestParam(required = false) Optional<String> month,
    	    @RequestParam String interval,
    	    @RequestParam(required = false) Optional<String> customFromDate,
    	    @RequestParam(required = false) Optional<String> customToDate) 
 {

        Pageable pageable = PageRequest.of(page, size);
        Page<Map<String, Object>> mappedDataPage;

        mappedDataPage = invoiceService.findAllDistributorsWithDetailsAndSeperatedFilters(productName, categoryName,invoiceNumber,query,year,month,interval,distributor,salespersonName,invoiceSearch,customFromDate,customToDate,pageable);

        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> invoices = mappedDataPage.getContent();
        response.put("DistributorDetails", invoices);
        response.put("currentPage", mappedDataPage.getNumber());
        response.put("totalItems", mappedDataPage.getTotalElements());
        response.put("totalPages", mappedDataPage.getTotalPages());
        return ResponseEntity.ok().body(response);
    }
    

}