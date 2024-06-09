package com.sas.SalesAnalysisSystem.service.invoice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
		
import org.hibernate.type.descriptor.java.DoubleJavaType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import com.sas.SalesAnalysisSystem.dto.DistributorDetails;
import com.sas.SalesAnalysisSystem.exception.ResourceNotFoundException;
import com.sas.SalesAnalysisSystem.models.Category;
import com.sas.SalesAnalysisSystem.models.Distributor;
import com.sas.SalesAnalysisSystem.models.Eway;
import com.sas.SalesAnalysisSystem.models.Invoice;
import com.sas.SalesAnalysisSystem.models.Product;
import com.sas.SalesAnalysisSystem.models.ProductDetails;
import com.sas.SalesAnalysisSystem.models.Salesperson;
import com.sas.SalesAnalysisSystem.repository.DistributorRepository;
import com.sas.SalesAnalysisSystem.repository.EwayRepository;
import com.sas.SalesAnalysisSystem.repository.InvoiceRepository;
import com.sas.SalesAnalysisSystem.repository.ProductDetailsRepository;
import com.sas.SalesAnalysisSystem.repository.ProductRepository;
import com.sas.SalesAnalysisSystem.repository.SalespersonRepository;

import io.micrometer.observation.annotation.Observed;
import jakarta.transaction.Transactional;

@Service
public class InvoiceServiceImpl implements InvoiceService {

	private final InvoiceRepository invoiceRepository;
	private final DistributorRepository distributorRepository;
	private final ProductRepository productRepository;
	private final ProductDetailsRepository productDetailsRepository;
	private final EwayRepository ewayRepository;
	private final ResourceLoader resourceLoader;

	@Autowired
	public InvoiceServiceImpl(InvoiceRepository invoiceRepository, ProductRepository productRepository,
			DistributorRepository distributorRepository, ProductDetailsRepository productDetailsRepository,
			EwayRepository ewayRepository, ResourceLoader resourceLoader) {
		this.invoiceRepository = invoiceRepository;
		this.productRepository = productRepository;
		this.distributorRepository = distributorRepository;
		this.productDetailsRepository = productDetailsRepository;
		this.ewayRepository = ewayRepository;
		this.resourceLoader = resourceLoader;

	}

	@Override
	public Invoice createInvoice(Invoice invoice) {

		if (invoice.getTotalQuantity_Nos() <= 0) {
			invoice.setTotalQuantity_Nos(1);
		}
		if (invoice.getTotalQuantity_Doz() <= 0) {
			invoice.setTotalQuantity_Doz(1);
		}

		if (invoice.getAmount() <= 0.0) {
			invoice.setAmount(0.0);
		}
		Distributor tempDistributor = invoice.getDistributor();
		Optional<Distributor> foundDisOptional = distributorRepository.findById(tempDistributor.getId());

		if (foundDisOptional.isPresent()) {
			Distributor distributor = foundDisOptional.get();
			invoice.setDistributor(distributor);
			return invoiceRepository.save(invoice);
		} else {
			throw new ResourceNotFoundException("Distributor Id not found");
		}
	}

	@Override
    public void toggleStatus(Long id)
    {
        Optional<Invoice> invoiceDb = invoiceRepository.findById(id);
        if (invoiceDb.isPresent()) {
        	Invoice invoice=invoiceDb.get();
        	boolean newStatus = !invoice.getIsReceived(); 

            invoice.setIsReceived(newStatus);
            invoiceRepository.save(invoice);
            }
    else {
            throw new ResourceNotFoundException("Record not found with id: " + id);
        }
      }

	@Override
	public Invoice getInvoiceById(Long id) {
		Optional<Invoice> optionalInvoice = invoiceRepository.findById(id);
		if (optionalInvoice.isPresent()) {
			return optionalInvoice.get();
		} else {
			throw new ResourceNotFoundException("Invoice not found with invoice number: " + id);
		}
	}
	
	@Override
	public Page<Invoice> searchInvoicesByReceivedFlag(Boolean isReceived,String searchQuery, Pageable pageable) {
	    return invoiceRepository.findByIsReceivedAndInvoiceNumberContainingIgnoreCaseOrDistributor_DistributorProfile_AgencyNameContainingIgnoreCaseOrDistributor_DistributorProfile_CityContainingIgnoreCaseOrDistributor_DistributorProfile_RegionContainingIgnoreCaseOrDistributor_DistributorProfile_ZoneContainingIgnoreCase(
	    		isReceived, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, pageable);
	}
	
	@Override
	public Page<Invoice> searchInvoices(String searchQuery, Pageable pageable) {
	    return invoiceRepository.findByInvoiceNumberContainingIgnoreCaseOrDistributor_DistributorProfile_AgencyNameContainingIgnoreCaseOrDistributor_DistributorProfile_CityContainingIgnoreCaseOrDistributor_DistributorProfile_RegionContainingIgnoreCaseOrDistributor_DistributorProfile_ZoneContainingIgnoreCase(
	    		searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, pageable);
	}


	  @Override
	    public Page<Invoice> findByTotalAmountWithFlag(Boolean isReceived, String searchQuery, Pageable pageable) {
	        return invoiceRepository.findByIsReceivedAndTotalAmount( isReceived,Double.parseDouble(searchQuery), pageable);
	    }
	  
	  @Override
	    public Page<Invoice> findByGSTNoWithFlag(Boolean isReceived, String searchQuery, Pageable pageable) {
	        return invoiceRepository.findByIsReceivedAndDistributor_DistributorProfile_GstNo(isReceived,searchQuery, pageable);
	    }
	  @Override
	    public Page<Invoice> findByTotalAmount(String searchQuery, Pageable pageable) {
	        return invoiceRepository.findByTotalAmount(Double.parseDouble(searchQuery), pageable);
	    }
	  
	  @Override
	    public Page<Invoice> findByGSTNo( String searchQuery, Pageable pageable) {
	        return invoiceRepository.findByDistributor_DistributorProfile_GstNo(searchQuery, pageable);
	    }
	
	@Override
	public Page<Invoice> getAllInvoices(Pageable page) {
	    Page<Invoice> invoices = invoiceRepository.findAllByOrderByInvoiceDateDesc(page);
	    if (invoices.isEmpty()) {
	        throw new ResourceNotFoundException("No invoices found");
	    }
	    return invoices;
	}

	@Override
	public Page<Invoice> getAllInvoicesByIsReceived(Boolean isReceived,Pageable page) {
	    Page<Invoice> invoices = invoiceRepository.findAllByIsReceivedOrderByInvoiceDateDesc(isReceived,page);
	    if (invoices.isEmpty()) {
	        throw new ResourceNotFoundException("No invoices found");
	    }
	    return invoices;
	}

	
	@Override
	public List<Invoice> getAllInvoices() {
		List<Invoice> invoices = invoiceRepository.findAll();
		if (invoices.isEmpty()) {
			throw new ResourceNotFoundException("No invoices found");
		}
		return invoices;
	}

	@Override
	public List<String> findAllInvoiceNumbers() {
		return invoiceRepository.findAllInvoiceNumber();
	}

	@Override
	public List<String> findAllIRNs() {
		return invoiceRepository.findAllIRN();
	}

	@Override
	public Invoice updateInvoice(Long id, Invoice updatedInvoice) {
		Optional<Invoice> invoiceDb = invoiceRepository.findById(id);

		if (invoiceDb.isPresent()) {
			Invoice invoiceUpdate = invoiceDb.get();
			invoiceUpdate.setInvoiceNumber(updatedInvoice.getInvoiceNumber());
			invoiceUpdate.setIRN(updatedInvoice.getIRN());
			invoiceUpdate.setAckNo(updatedInvoice.getAckNo());
			invoiceUpdate.setDispatchedThrough(updatedInvoice.getDispatchedThrough());
			invoiceUpdate.setDestination(updatedInvoice.getDestination());
			invoiceUpdate.setVechicleNo(updatedInvoice.getVechicleNo());
			invoiceUpdate.setCgst(updatedInvoice.getCgst());
			invoiceUpdate.setSgst(updatedInvoice.getSgst());
			invoiceUpdate.setTotalAmount(updatedInvoice.getTotalAmount());
			invoiceUpdate.setPurchaseNumber(updatedInvoice.getPurchaseNumber());
			invoiceUpdate.setDeliveryDate(updatedInvoice.getDeliveryDate());
			invoiceUpdate.setSupplierName(updatedInvoice.getSupplierName());
			invoiceUpdate.setDiscountPercentage(updatedInvoice.getDiscountPercentage());
			invoiceUpdate.setDiscountPrice(updatedInvoice.getDiscountPrice());
			invoiceUpdate.setTotalQuantity_Nos(updatedInvoice.getTotalQuantity_Nos());
			invoiceUpdate.setTotalQuantity_Doz(updatedInvoice.getTotalQuantity_Doz());
			invoiceUpdate.setAmount(updatedInvoice.getAmount());
			invoiceUpdate.setTermsOfDelivery(updatedInvoice.getTermsOfDelivery());
			invoiceUpdate.setDistributor(updatedInvoice.getDistributor());
			invoiceUpdate.setProductQuantityMap(updatedInvoice.getProductQuantityMap());
			invoiceUpdate.setIsActive(updatedInvoice.getIsActive());
			invoiceUpdate.setSalespersonId(updatedInvoice.getSalespersonId());
			invoiceUpdate.setInvoiceDate(updatedInvoice.getInvoiceDate());
			return invoiceRepository.save(invoiceUpdate);
		} else {
			throw new ResourceNotFoundException("Record not found with id: " + id);
		}
	}

	@Override
	public void deleteInvoice(Long id) {
		Optional<Invoice> optionalInvoice = invoiceRepository.findById(id);
		if (optionalInvoice.isPresent()) {
			Invoice invoice = optionalInvoice.get();
			Long invoiceId = invoice.getId();

			invoiceRepository.deleteById(id);

			Optional<Eway> optionalEway = ewayRepository.findByInvoiceId(invoiceId);
			if (optionalEway.isPresent()) {
				Eway eway = optionalEway.get();
				ewayRepository.deleteById(eway.getId());
			} else {
				System.out.println("No Eway found for invoice ID: " + invoiceId);
			}
		} else {
			throw new ResourceNotFoundException("Invoice not found with invoice number: " + id);
		}
	}

	@Transactional
	public void addProductsToInvoice(Long invoiceId, Long productId, ProductDetails productDetails) {
		Optional<Invoice> optionalInvoice = invoiceRepository.findById(invoiceId);
		Optional<Product> optionalProduct = productRepository.findById(productId);

		Product product = optionalProduct.get();
		if (optionalInvoice.isPresent() && optionalProduct.isPresent() && product.getIsActive() == true) {
			Invoice invoice = optionalInvoice.get();
			if (product.getHSNSAC() != productDetails.getHsnSac()) {
				double hsn = productDetails.getHsnSac();
				product.setHSNSAC(hsn);
				product = productRepository.save(product);

			}
			Map<Product, Integer> productQuantityMap = invoice.getProductQuantityMap();

			if (productQuantityMap.containsKey(product)) {
				int existingQuantity = productQuantityMap.get(product);
				productQuantityMap.put(product, existingQuantity + productDetails.getQuantity());
			} else {
				productQuantityMap.put(product, productDetails.getQuantity());
			}
			int totalQuantityNos = invoice.getTotalQuantity_Nos() + productDetails.getQuantity();
			int totalQuantityDoz = invoice.getTotalQuantity_Doz() + (productDetails.getQuantity() / 12);

			invoice.setAmount(invoice.getAmount() + productDetails.getTotalAmountWithTax());
			invoice.setTotalAmount(invoice.getTotalAmount() + productDetails.getTotalAmountWithoutTaxDiscount());

			Double cgstAmount = productDetails.getCgstAmount();
			invoice.setCgst(cgstAmount != null ? cgstAmount : 0.0);
			Double igstAmount = productDetails.getIgstAmount();
			invoice.setIgst(igstAmount != null ? igstAmount : 0.0);
			Double sgstAmount = productDetails.getSgstAmount();
			invoice.setSgst(sgstAmount != null ? sgstAmount : 0.0);
			Double discountedAmount = productDetails.getDiscountAmount();
			invoice.setDiscount(discountedAmount != null ? discountedAmount : 0.0);

			invoice.setTotalQuantity_Nos(totalQuantityNos);
			invoice.setTotalQuantity_Doz(totalQuantityDoz);

			productDetailsRepository.save(productDetails);

			Map<Product, ProductDetails> productGstMap = invoice.getProductGst();
			productGstMap.put(product, productDetails);
			invoice.setProductGst(productGstMap);

			invoiceRepository.save(invoice);
		} else {
			throw new IllegalArgumentException("Invoice or Product not found");
		}
	}
	
	@Override
	@Transactional
	public void removeProductFromInvoice(Long invoiceId, Long productId) {
	    Optional<Invoice> optionalInvoice = invoiceRepository.findById(invoiceId);
	    if (optionalInvoice.isPresent()) {
	        Invoice invoice = optionalInvoice.get();

	        Optional<Product> optionalProduct = productRepository.findById(productId);
	        if (optionalProduct.isPresent()) {
	            Product product = optionalProduct.get();

	            Map<Product, Integer> productQuantityMap = invoice.getProductQuantityMap();
	            if (productQuantityMap.containsKey(product)) {
	                productQuantityMap.remove(product);
	                
	                Map<Product, ProductDetails> productGstMap = invoice.getProductGst();
	                if (productGstMap.containsKey(product)) {
	                    ProductDetails productDetails = productGstMap.get(product);
	                    productGstMap.remove(product);
	                }
	                invoiceRepository.save(invoice);
	            } else {
	                throw new IllegalArgumentException("Product not found in the invoice");
	            }
	        } else {
	            throw new IllegalArgumentException("Product not found");
	        }
	    } else {
	        throw new IllegalArgumentException("Invoice not found");
	    }
	}


	@Override
	@Transactional
	public void addProductToInvoice(Long invoiceId, Map<Long, ProductDetails> productQuantities) {
		for (Map.Entry<Long, ProductDetails> entry : productQuantities.entrySet()) {
			Long productId = entry.getKey();
			ProductDetails quantity = entry.getValue();
			addProductsToInvoice(invoiceId, productId, quantity);
		}
	}

	@Override
	public void updateProductToInvoice(Long invoiceId, Map<Long, ProductDetails> productQuantities) {
		Optional<Invoice> optionalInvoice = invoiceRepository.findById(invoiceId);

		if (optionalInvoice.isPresent()) {
			Invoice invoice = optionalInvoice.get();

			for (Map.Entry<Long, ProductDetails> entry : productQuantities.entrySet()) {
				Long productId = entry.getKey();
				ProductDetails productDetails = entry.getValue();

				Optional<Product> optionalProduct = productRepository.findById(productId);

				Product product = optionalProduct.get();
				if (optionalProduct.isPresent() && product.getIsActive() == true) {
					if (product.getHSNSAC() != productDetails.getHsnSac()) {
						double hsn = productDetails.getHsnSac();
						product.setHSNSAC(hsn);
						product = productRepository.save(product);
					}
					double productPrice = product.getPrice() * productDetails.getQuantity();

					Map<Product, Integer> productQuantityMap = invoice.getProductQuantityMap();
					Map<Product, ProductDetails> newproductGstMap = invoice.getProductGst();

					if (productQuantityMap.containsKey(product)) {
						int existingQuantity = productQuantityMap.get(product);
						int diff = productDetails.getQuantity() - existingQuantity;

						productQuantityMap.put(product, productDetails.getQuantity());
						newproductGstMap.put(product, productDetails);

						// Update total quantity and csgt-sgst || igst
						int totalQuantityNos = invoice.getTotalQuantity_Nos() + diff;
						int totalQuantityDoz = invoice.getTotalQuantity_Doz() + (diff / 12);
						invoice.setTotalQuantity_Nos(totalQuantityNos);
						invoice.setTotalQuantity_Doz(totalQuantityDoz);

						// Update total amount
						double diffPrice = productPrice - (product.getPrice() * existingQuantity);
						invoice.setTotalAmount(
								invoice.getAmount() + productDetails.getTotalAmountWithoutTax() - diffPrice);
						invoice.setAmount(invoice.getAmount() + productDetails.getTotalAmountWithTax() - diffPrice);

						invoice.setProductGst(newproductGstMap);

						productDetailsRepository.save(productDetails); // Save ProductDetails if needed
					} else {
						throw new IllegalArgumentException("Product not found in invoice");
					}
				} else {
					throw new IllegalArgumentException("Product not found");
				}
			}

			invoiceRepository.save(invoice);
		} else {
			throw new IllegalArgumentException("Invoice not found");
		}
	}

	@Override
	public List<Invoice> findInvoicesBetweenDates(LocalDate startDate, LocalDate endDate) {
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
		return invoiceRepository.findInvoicesByInvoiceDateBetween(startDateTime, endDateTime);
	}



	@Override
	public Map<String, Double> getTotalSumOfInvoicesByMonth() {
		Map<String, Double> totalSumByMonth = new HashMap<>();

		LocalDate currentDate = LocalDate.now();
		int currentYear = currentDate.getYear();
		int currentMonth = currentDate.getMonthValue();

		LocalDate startFinancialYear;
		if (currentMonth < 4) {
			startFinancialYear = LocalDate.of(currentYear - 1, 4, 1);
			currentDate = LocalDate.of(currentYear, 3, 31);
		} else {
			startFinancialYear = LocalDate.of(currentYear, 4, 1);
			currentDate = LocalDate.of(currentYear + 1, 3, 31);
		}

		LocalDate startDate = startFinancialYear;
		LocalDate endDate = currentDate;
		System.out.println((startDate.toString() + " and " + endDate.toString()));
		while (!startDate.isAfter(endDate)) {
			LocalDateTime startOfMonth = YearMonth.from(startDate).atDay(1).atStartOfDay();
			LocalDateTime endOfMonth = YearMonth.from(startDate).atEndOfMonth().atTime(LocalTime.MAX);
			// System.out.println((startOfMonth.toString() + " and " +
			// endOfMonth.toString()));
			List<Invoice> invoices = invoiceRepository.findByInvoiceDateBetween(startOfMonth, endOfMonth);

			double totalSum = invoices.stream().mapToDouble(Invoice::getTotalAmount).sum();

			totalSumByMonth.put(YearMonth.from(startDate).toString(), totalSum);

			startDate = startDate.plusMonths(1);
		}

		return totalSumByMonth;
	}

	@Override
	public Map<String, Double> getTotalSumOfInvoicesByMonth(LocalDate customStartDate, LocalDate customEndDate) {
		TreeMap<YearMonth, Double> totalSumByMonth = new TreeMap<>();

		LocalDate startDate = customStartDate;
		LocalDate endDate = customEndDate;

		while (!startDate.isAfter(endDate)) {
			YearMonth currentYearMonth = YearMonth.from(startDate);
			LocalDateTime startOfMonth = currentYearMonth.atDay(1).atStartOfDay();
			LocalDateTime endOfMonth = currentYearMonth.atEndOfMonth().atTime(LocalTime.MAX);

			List<Invoice> invoices = invoiceRepository.findByInvoiceDateBetween(startOfMonth, endOfMonth);

			double totalSum = invoices.stream().mapToDouble(Invoice::getTotalAmount).sum();

			totalSumByMonth.put(currentYearMonth, totalSum);

			startDate = startDate.plusMonths(1);
		}

		Map<String, Double> sortedMap = totalSumByMonth.entrySet().stream()
				.collect(Collectors.toMap(entry -> entry.getKey().toString(), Map.Entry::getValue,
						(oldValue, newValue) -> oldValue, LinkedHashMap::new));

		return sortedMap;
	}

	@Override
	public Map<String, Double> getTotalSumOfInvoicesByFinancialYear() {
		Map<String, Double> totalSumByFinancialYear = new HashMap<>();

		LocalDate currentDate = LocalDate.now();
		int currentYear = currentDate.getYear();
		int currentMonth = currentDate.getMonthValue();

		int startYear = currentMonth >= 4 ? currentYear : currentYear - 1;

		for (int year = startYear; year >= startYear - 2; year--) {
			LocalDateTime startOfFinancialYear = YearMonth.of(year, 4).atDay(1).atStartOfDay();
			LocalDateTime endOfFinancialYear = YearMonth.of(year + 1, 3).atEndOfMonth().atTime(LocalTime.MAX);

			System.out.println("Financial Year: " + year + "-" + (year + 1));
			System.out.println("Start Date: " + startOfFinancialYear);
			System.out.println("End Date: " + endOfFinancialYear);

			List<Invoice> invoices = invoiceRepository.findByInvoiceDateBetween(startOfFinancialYear,
					endOfFinancialYear);

			System.out.println("Number of invoices in the financial year: " + invoices.size());

			double totalSum = invoices.stream().mapToDouble(Invoice::getTotalAmount).sum();

			System.out.println("Total sum of invoices in the financial year: " + totalSum);

			String financialYear = year + "-" + (year + 1);

			totalSumByFinancialYear.put(financialYear, totalSum);
		}

		return totalSumByFinancialYear;
	}

	@Override
	public List<Object[]> findTop5ProductsByInvoiceId(Long invoiceId) {
		return invoiceRepository.findTop5ProductsByInvoiceId(invoiceId, null, null);
	}

	@Override
	public List<Object[]> findTopSellingProducts(String interval, Optional<Integer> year, Optional<String> monthName,
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
							"Both fromDate and toDate must be provided for customDate interval.");
				}
				break;
			default:
				throw new IllegalArgumentException("Invalid interval provided.");
		}

		System.out.println(toDate + " And " + fromDate);
		return getTopSellingProducts(fromDate, toDate);
	}

	@Override
	public List<Map<String, Object>> totalInvoiceAmountAndExpenses(Optional<Long> salespersonId, String interval,
			Optional<Integer> year, Optional<String> monthName, Optional<String> customFromDate,
			Optional<String> customToDate, Boolean status) {
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
							"Both fromDate and toDate must be provided for customDate interval.");
				}
				break;
			default:
				throw new IllegalArgumentException("Invalid interval provided.");
		}
		List<Object[]> responseData;

		if (salespersonId.isPresent()) {
			responseData = invoiceRepository.totalInvoiceAmountAndExpenses(salespersonId.get(), fromDate, toDate,
					status);
		} else {
			responseData = invoiceRepository.totalInvoiceAmountAndExpenses(null, fromDate, toDate, status);
		}

		List<Map<String, Object>> processedData = processInvoiceData(responseData);

		return processedData;

	}

	private List<Map<String, Object>> processInvoiceData(List<Object[]> responseData) {
		List<Map<String, Object>> processedData = new ArrayList<>();

		for (Object[] entry : responseData) {
			Map<String, Object> processedEntry = new HashMap<>();
			processedEntry.put("salespersonName", entry[0] != null ? entry[0] : "Not assigned");
			processedEntry.put("TotalAmount", entry[1] != null ? entry[1] : 0);
			processedEntry.put("incentive", entry[2] != null ? entry[2] : 0);
			processedEntry.put("Salary", entry[3] != null ? entry[3] : 0);
			processedEntry.put("mis", entry[4] != null ? entry[4] : 0);
			processedData.add(processedEntry);
		}

		return processedData;
	}

	@Override
	public List<Object[]> findLeastSellingProducts(String interval, Optional<Integer> year, Optional<String> monthName,
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

		return getLeastSellingProducts(fromDate, toDate);
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

	public List<Object[]> getTopSellingProducts(LocalDateTime fromDate, LocalDateTime toDate) {
		return invoiceRepository.findTop5SellingProducts(fromDate, toDate);
	}

	private List<Object[]> getLeastSellingProducts(LocalDateTime fromDate, LocalDateTime toDate) {
		return invoiceRepository.findLeast5SellingProducts(fromDate, toDate);
	}

	public List<Object[]> findTopSellingProductsByCity(String city, Optional<Integer> year,
			Optional<String> monthName, String interval, Optional<String> customFromDate,
			Optional<String> customToDate) {
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
		List<Object[]> topProducts = invoiceRepository.findTop5SellingProductsByCity(city, fromDate, toDate);
		return topProducts;
	}

	public List<Object[]> findTopSellingProductsByRegion(String region, Optional<Integer> year,
			Optional<String> monthName, String interval, Optional<String> customFromDate,
			Optional<String> customToDate) {
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

		List<Object[]> topProducts = invoiceRepository.findTop5SellingProductsByRegion(region, fromDate, toDate);

		return topProducts;
	}

	public List<Object[]> findTopSellingProductsByZone(String zone, Optional<Integer> year,
			Optional<String> monthName, String interval, Optional<String> customFromDate,
			Optional<String> customToDate) {
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

		List<Object[]> topProducts = invoiceRepository.findTop5SellingProductsByZone(zone, fromDate, toDate);
		return topProducts;
	}

	@Override
	public List<Object[]> findLeastSellingProductsByCity(String city, Optional<Integer> year,
			Optional<String> monthName, String interval, Optional<String> customFromDate,
			Optional<String> customToDate) {
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

		List<Object[]> leastProducts = invoiceRepository.findLeast5SellingProductsByCity(city, fromDate, toDate);
		return leastProducts;
	}

	@Override
	public List<Object[]> findLeastSellingProductsByRegion(String region, Optional<Integer> year,
			Optional<String> monthName, String interval, Optional<String> customFromDate,
			Optional<String> customToDate) {
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

		List<Object[]> leastProducts = invoiceRepository.findLeast5SellingProductsByRegion(region, fromDate, toDate);
		return leastProducts;
	}

	@Override
	public List<Object[]> findLeastSellingProductsByZone(String zone, Optional<Integer> year,
			Optional<String> monthName, String interval, Optional<String> customFromDate,
			Optional<String> customToDate) {
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

		List<Object[]> leastProducts = invoiceRepository.findLeast5SellingProductsByZone(zone, fromDate, toDate);
		return leastProducts;
	}

	@Override
	public List<Object[]> findTop5SellingProductsByCategory(Optional<Integer> year,
			Optional<String> monthName, String interval, Optional<String> customFromDate,
			Optional<String> customToDate) {
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

		List<Object[]> topProducts = invoiceRepository.findTop5SellingCategories(fromDate, toDate);
		return topProducts;
	}

	@Override
	public List<Object[]> findTop5LeastProductsByCategory(Optional<Integer> year,
			Optional<String> monthName, String interval, Optional<String> customFromDate,
			Optional<String> customToDate) {
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

		List<Object[]> topProducts = invoiceRepository.findTop5LeastCategories(fromDate, toDate);
		return topProducts;
	}

	@Override
	public List<Object[]> findLeastSellingCategoriesByCityRegionAndZone(Optional<String> city, Optional<String> region,
			Optional<String> zone, Optional<Integer> year,
			Optional<String> monthName, String interval, Optional<String> customFromDate,
			Optional<String> customToDate) {

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

		List<Object[]> leastCategories = invoiceRepository.findLeast5SellingCategoriesByCityRegionAndZone(
				city.orElse(null),
				region.orElse(null),
				zone.orElse(null),
				fromDate,
				toDate);
		return leastCategories;
	}

	@Override
	public List<Object[]> findMostSellingCategoriesByCityRegionAndZone(Optional<String> city, Optional<String> region,
			Optional<String> zone, Optional<Integer> year,
			Optional<String> monthName, String interval, Optional<String> customFromDate,
			Optional<String> customToDate) {

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

		List<Object[]> leastCategories = invoiceRepository.findTop5SellingCategoriesByCityRegionAndZone(
				city.orElse(null),
				region.orElse(null),
				zone.orElse(null),
				fromDate,
				toDate);
		return leastCategories;
	}

	@Override
	public List<Category> findAllProductsInInvoices() {
		return invoiceRepository.findAllProductsInInvoices();
	}

	@Override
	public List<Object[]> getInvoiceProductDetails(Long invoiceId) {
		System.out.println(invoiceId);
		return invoiceRepository.getInvoiceProductDetails(invoiceId);
	}
	@Override
	public List<Object[]> findProductsSoldToDistributorThisMonthAndSameMonthLastYear(Long distributorId) {
		LocalDate now = LocalDate.now();
		int currentMonth = now.getMonthValue();
		int currentYear = now.getYear();
		int lastYear = currentYear - 1;
		return invoiceRepository.findProductsSoldToDistributorThisMonthAndSameMonthLastYear(
			distributorId, currentMonth, currentYear, currentMonth, lastYear);
	    }
	
	public LocalDateTime findInitialEntryInvoiceDate() {
        return invoiceRepository.findInitialEntryInvoiceDate();
    }
	
	@Override
	 public Page<DistributorDetails> findInvoicesByInvoiceAge(int days, boolean status,String search , Pageable page) {
		    LocalDateTime startDate = null;
		    LocalDateTime initialEntryInvoiceDate = findInitialEntryInvoiceDate();
		    LocalDateTime endDate = LocalDate.now().atTime(LocalTime.MAX);
		    if (days <= 0) {
	            throw new IllegalArgumentException("Days must be greater than 0.");
	        }
		    else if (days > 90) {
		        startDate = initialEntryInvoiceDate;
		        endDate = LocalDate.now().minusDays(90).atTime(LocalTime.MAX);
		    }
		    else if (days == 30) {
	            startDate = LocalDate.now().minusDays(30).atStartOfDay();
	            endDate = LocalDate.now().minusDays(0).atTime(LocalTime.MAX);
	        } else if(days==45){
	        	System.out.println(days);
	            startDate = LocalDate.now().minusDays(45).atStartOfDay();
	            endDate = LocalDate.now().minusDays(30).atTime(LocalTime.MAX);
	        	System.out.println(startDate+ " " +endDate);
	        }else if(days==60) {
	            startDate = LocalDate.now().minusDays(60).atStartOfDay();
	            endDate = LocalDate.now().minusDays(45).atTime(LocalTime.MAX);
	        }
	        else if(days==90) {
	            startDate = LocalDate.now().minusDays(90).atStartOfDay();
	            endDate = LocalDate.now().minusDays(60).atTime(LocalTime.MAX); 
	        } 
		    System.out.println(startDate+ "  " + endDate);
	        
	        return invoiceRepository.findInvoicesByDateRange(startDate, endDate , status, search, page);
	    }
	
		@Override
		public Page<Map<String, Object>> findAllDistributorsWithDetails(
		    Long productId,
		    Long categoryId,
		    String invoiceNumber,
		    String searchQuery,
		    Optional<Integer> year,
			Optional<String> monthName, String interval, Optional<String> customFromDate,
			Optional<String> customToDate,
		    Pageable page) {
			
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
			
		    Page<Object[]> results = invoiceRepository.findAllDistributorsWithDetails(productId, categoryId, invoiceNumber,searchQuery, fromDate,toDate,page);
		    return mapResultsToPage(results);
		}


	@Override
	public Page<Map<String, Object>> findByDistributorAgencyName(String searchQuery, Pageable pageable) {
		
		
	    Page<Object[]> results = invoiceRepository.findByDistributor_DistributorProfile_agencyName(searchQuery, pageable);
	    return mapResultsToPage(results);
	}

	private Page<Map<String, Object>> mapResultsToPage(Page<Object[]> results) {
	    List<Map<String, Object>> mappedDataList = new ArrayList<>();

	    for (Object[] row : results) {
	        Map<String, Object> mappedData = new HashMap<>();

	        mappedData.put("agencyname", row[0]);
	        mappedData.put("id", row[1]);
	        mappedData.put("salespersonName", row[2]);
	        mappedData.put("city", row[3]);
	        mappedData.put("region", row[4]);
	        mappedData.put("zone", row[5]);

	        List<String> products = Arrays.asList(((String) row[6]).split(","));
	        List<String> categories = Arrays.asList(((String) row[7]).split(","));
	        List<String> invoices = Arrays.asList(((String) row[8]).split(","));

	        mappedData.put("products", products);
	        mappedData.put("categories", categories);
	        mappedData.put("invoices", invoices);
	        mappedDataList.add(mappedData);
	    }

	    return new PageImpl<>(mappedDataList, results.getPageable(), results.getTotalElements());
	}
	
	

	@Override
	public List<Map<String, Object>> findAllDistributorsWithDetailsByDistributorId(Long distributorId, Optional<Integer> year,
	        Optional<String> monthName, String interval, Optional<String> customFromDate,
	        Optional<String> customToDate) {
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
	    System.out.println(fromDate + " " + toDate);
	    List<Object[]> results = invoiceRepository.findAllDistributorsWithDetailsByDistributorId(distributorId, fromDate, toDate);

	    List<Map<String, Object>> mappedDataList = new ArrayList<>();

	    for (Object[] row : results) {
	        Map<String, Object> mappedData = new HashMap<>();

	        mappedData.put("agencyname", row[0]);
	        mappedData.put("contactperson", row[1]);
	        mappedData.put("address", row[2]);
	        mappedData.put("gstNo", row[3]);
	        mappedData.put("panNo", row[4]);
	        mappedData.put("city", row[5]);
	        mappedData.put("zone", row[6]);
	        mappedData.put("region", row[7]);

	        List<String> products = Arrays.asList(((String) row[8]).split(","));
	        List<String> categories = Arrays.asList(((String) row[9]).split(","));
	        List<String> invoices = Arrays.asList(((String) row[10]).split(","));
	        
	        Comparator<String> dateComparator = (s1, s2) -> {
	            LocalDate date1 = LocalDate.parse(s1.split(":")[2].trim(), DateTimeFormatter.ofPattern("dd-MMMM-yy"));
	            LocalDate date2 = LocalDate.parse(s2.split(":")[2].trim(),DateTimeFormatter.ofPattern("dd-MMMM-yy"));
	            return date1.compareTo(date2);
	        };
	        Comparator<String> dateComparator2 = (s1, s2) -> {
	            LocalDate date1 = LocalDate.parse(s1.split(":")[1].trim(), DateTimeFormatter.ofPattern("dd-MMMM-yy"));
	            LocalDate date2 = LocalDate.parse(s2.split(":")[1].trim(),DateTimeFormatter.ofPattern("dd-MMMM-yy"));
	            return date1.compareTo(date2);
	        };

	        Collections.sort(products, dateComparator);
	        Collections.sort(categories, dateComparator2);
	        Collections.sort(invoices, dateComparator);

	        mappedData.put("products", products);
	        mappedData.put("categories", categories);
	        mappedData.put("invoices", invoices);
	        mappedDataList.add(mappedData);
	    }

	       return mappedDataList;
	}
	// Service method to find details of distributors by their IDs
	@Override
	public Page<Map<String, Object>> findAllDistributorsWithDetailsByDistributorIds(List<Long> distributorIds, Pageable page) {
	    Page<Object[]> results = invoiceRepository.findAllDistributorsWithDetailsByDistributorIds(distributorIds, page);
	    List<Map<String, Object>> mappedDataList = new ArrayList<>();

	    for (Object[] row : results) {
	        Map<String, Object> mappedData = new HashMap<>();
	        mappedData.put("agencyname", row[0]);
	        mappedData.put("contactperson", row[1]);
	        mappedData.put("city", row[2]);
	        mappedData.put("region", row[3]);

	        List<String> products = Arrays.asList(((String) row[4]).split(","));
	        List<String> categories = Arrays.asList(((String) row[5]).split(","));
	        List<String> invoices = Arrays.asList(((String) row[6]).split(","));

	        mappedData.put("products", products);
	        mappedData.put("categories", categories);
	        mappedData.put("invoices", invoices);
	        mappedDataList.add(mappedData);
	    }

	    return new PageImpl<>(mappedDataList, results.getPageable(), results.getTotalElements());
	}
	
//	public byte[] generatePdfForDistributor(Long distributorId, LocalDateTime startDate, LocalDateTime endDate) throws Exception {
//        List<Object[]> distributorDetails = invoiceRepository.findAllDistributorsWithDetailsByDistributorIdPdf(distributorId, startDate, endDate);
//                String html = loadHtmlTemplate("classpath:templates/invoiceTemplate.html");
//        String filledHtml = fillTemplateWithData(html, distributorDetails,); 
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        PdfRendererBuilder builder = new PdfRendererBuilder();
//        builder.useFastMode();
//        builder.withHtmlContent(filledHtml, null);
//        builder.toStream(os);
//        builder.run();
//        
//        return os.toByteArray();
//    }
	
	@Override
	public byte[] generatePdfForDistributorWithInterval(Long distributorId,Optional<Integer> year,
	        Optional<String> monthName, String interval, Optional<String> customFromDate,
	        Optional<String> customToDate) throws Exception {
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
	    List<Object[]> distributorDetails = invoiceRepository.findAllDistributorsWithDetailsByDistributorIdPdf(distributorId, fromDate, toDate);
	    
	    if (distributorDetails.isEmpty()) {
	        throw new ResourceNotFoundException("No details found for distributor ID : "+ distributorId + " on this interval " );
	    }
	    
	    String html = loadHtmlTemplate("classpath:templates/invoiceTemplate.html");
	    String filledHtml = fillTemplateWithData(html, distributorDetails, fromDate,toDate); 
	    ByteArrayOutputStream os = new ByteArrayOutputStream();
	    PdfRendererBuilder builder = new PdfRendererBuilder();
	    builder.useFastMode();
	    builder.withHtmlContent(filledHtml, null);
	    builder.toStream(os);
	    builder.run();
	    
	    return os.toByteArray();
	}


    private String loadHtmlTemplate(String path) throws Exception {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resourceLoader.getResource(path).getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
    

    public String fillTemplateWithData(String html, List<Object[]> data, LocalDateTime fromDate, LocalDateTime toDate) {
        if (data.isEmpty()) {
            throw new IllegalArgumentException("No data found for the given distributor and date range");
        }

        Object[] distributorData = data.get(0);
        LocalDate fromDate2 = fromDate.toLocalDate();
        LocalDate toDate2 = toDate.toLocalDate();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy");
        String formattedFromDate = fromDate2.format(formatter);
        String formattedToDate = toDate2.format(formatter);

        html = replacePlaceholder(html, "${agencyName}", (String) distributorData[0]);
        html = replacePlaceholder(html, "${contactPerson}", (String) distributorData[1]);
        html = replacePlaceholder(html, "${address}", (String) distributorData[2]);
        html = replacePlaceholder(html, "${gstNo}", (String) distributorData[3]);
        html = replacePlaceholder(html, "${panNo}", (String) distributorData[4]);
        html = replacePlaceholder(html, "${city}", (String) distributorData[5]);
        html = replacePlaceholder(html, "${zone}", (String) distributorData[6]);
        html = replacePlaceholder(html, "${region}", (String) distributorData[7]);
        html = replacePlaceholder(html, "${fromDate}", formattedFromDate);
        html = replacePlaceholder(html, "${toDate}", formattedToDate);

        String productNamesConcatenated = (String) distributorData[8];
        List<String> productNamesList = Arrays.asList(productNamesConcatenated.split(",")).stream()
                .sorted(Comparator.comparing(s -> s.split(" : ")[2])) // Sort by the date part
                .collect(Collectors.toList());
        StringBuilder productRows = new StringBuilder();
        for (String productName : productNamesList) {
            String[] parts = productName.split(" : ");
            if (parts.length == 3) {
                productRows.append("<tr class='bg-white border-b'><td class='px-6 py-3 b w text-center'>")
                        .append(parts[2])
                        .append("</td><td class='px-6 py-3 b '>")
                        .append(parts[0])
                        .append("</td><td class='px-6 py-3 b w text-center'>")
                        .append(parts[1])
                        .append("</td></tr>");
            }
        }
        html = replacePlaceholder(html, "${productRows}", productRows.toString());

        String categoryDetailsConcatenated = (String) distributorData[9];
        List<String> categoryDetailsList = Arrays.asList(categoryDetailsConcatenated.split(",")).stream()
                .sorted(Comparator.comparing(s -> s.split(" : ")[1])) // Sort by the date part
                .collect(Collectors.toList());
        StringBuilder categoryRows = new StringBuilder();
        for (String categoryDetail : categoryDetailsList) {
            String[] parts = categoryDetail.split(" : ");
            if (parts.length == 3) {
                categoryRows.append("<tr class='bg-white border-b'><td class='px-6 py-3 b w text-center'>")
                        .append(parts[1])
                        .append("</td><td class='px-6 py-3 b '>")
                        .append(parts[0])
                        .append("</td><td class='px-6 py-3 b w text-center'>")
                        .append(parts[2])
                        .append("</td></tr>");
            }
        }
        html = replacePlaceholder(html, "${categoryRows}", categoryRows.toString());

        String invoiceNumbersConcatenated = (String) distributorData[10];
        List<String> invoiceNumbersList = Arrays.asList(invoiceNumbersConcatenated.split(",")).stream()
                .sorted(Comparator.comparing(s -> s.split(" : ")[2])) // Sort by the date part
                .collect(Collectors.toList());
        StringBuilder invoiceNumbers = new StringBuilder();
        double totalAmount = 0.0;
        for (String invoiceNumber : invoiceNumbersList) {
            String[] parts = invoiceNumber.split(" : ");
            if (parts.length == 3) {
                try {
                    double amount = Double.parseDouble(parts[1]);
                    totalAmount += amount;
                    invoiceNumbers.append("<tr class='bg-white border-b'><td class='px-6 py-3 b w text-center'>")
                            .append(parts[2])
                            .append("</td><td class='px-6 py-3 b '>")
                            .append(parts[0])
                            .append("</td><td class='px-6 py-3 b w text-center'>")
                            .append(String.format("%.2f", amount))
                            .append("</td></tr>");
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        html = replacePlaceholder(html, "${invoiceNumbers}", invoiceNumbers.toString());
        html = replacePlaceholder(html, "${totalAmount}", String.format("%.2f", totalAmount));

        return html;
    }



    private String replacePlaceholder(String html, String placeholder, String value) {
        if (value != null) {
        	// Encode '&' character to '&amp;' before replacing placeholder
            value = value.replace("&", "&amp;");
            return html.replace(placeholder, value);
        } else {
            return html.replace(placeholder, "N/A");
        }
    }
    
    
    @Override
    public Page<Map<String, Object>> findAllDistributorsWithDetailsAndSeperatedFilters(
            String productName,
            String categoryName,
            String invoiceNumber,
            Optional<String> searchQuery,
            Optional<Integer> year,
            Optional<String> monthName,
            String interval,
            Optional<String> distributor,
            Optional<String> salespersonName,
            Optional<String> invoiceSearch,
            Optional<String> customFromDate, 
            Optional<String> customToDate,
            Pageable page) {
    	

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
        System.out.println(searchQuery);
        Page<Object[]> results = invoiceRepository.findAllDistributorsWithDetailsAndSeperatedFilters(
                productName,
                categoryName,
                invoiceNumber,
                distributor.orElse(null),
                salespersonName.orElse(null),
                searchQuery.orElse(null),
                invoiceSearch.orElse(null),
                fromDate,
                toDate,
                page);
        System.out.println(results);
        return mapResultsToPage(results);
    }

    
    
    
    
    
    



    

}
