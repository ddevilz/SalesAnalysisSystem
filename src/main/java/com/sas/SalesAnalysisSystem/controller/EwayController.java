package com.sas.SalesAnalysisSystem.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sas.SalesAnalysisSystem.exception.CustomErrorResponse;
import com.sas.SalesAnalysisSystem.exception.ResourceNotFoundException;
import com.sas.SalesAnalysisSystem.models.Distributor;
import com.sas.SalesAnalysisSystem.models.Eway;
import com.sas.SalesAnalysisSystem.models.Invoice;
import com.sas.SalesAnalysisSystem.service.eway.EwayService;
import com.sas.SalesAnalysisSystem.service.eway.EwayServiceImpl;
import com.sas.SalesAnalysisSystem.service.invoice.InvoiceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/eways")
public class EwayController {
	
	    private final EwayService ewayService;
	    private final InvoiceService invoiceService;
	    
	    @Autowired
	    public EwayController(EwayService ewayService, InvoiceService invoiceService) {
			this.ewayService = ewayService;
			this.invoiceService = invoiceService;
		}

		@GetMapping("/all")
	    public ResponseEntity<Object> getAllEways() {
	        try {
	            List<Eway> eways = ewayService.getAllEways();
	            return ResponseEntity.ok().body(eways);
	        } catch (ResourceNotFoundException e) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Eways found");
	        }
	    }

	    @GetMapping("/invoice/{id}")
	    public ResponseEntity<Object> getEwayByInvoiceId(@PathVariable("id") Long id) {
	        try {
	        	Eway eway = ewayService.findEwayWithInvoiceId(id);
	            return ResponseEntity.ok().body(eway);
	        } catch (ResourceNotFoundException e) {
	            CustomErrorResponse errorResponse = new CustomErrorResponse();
	            errorResponse.setTimestamp(LocalDateTime.now());
	            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
	            errorResponse.setError("No Eway Found for this id - " + id);
	            errorResponse.setMessage(e.getMessage());
	            errorResponse.setPath("/api/v1/eways/all/" + id);
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	        }
	    }
	    @GetMapping("/{id}")
	    public ResponseEntity<Object> getEwayById(@PathVariable("id") Long id) {
	        try {
	            Eway eway = ewayService.getEwayById(id);
	            return ResponseEntity.ok().body(eway);
	        } catch (ResourceNotFoundException e) {
	            CustomErrorResponse errorResponse = new CustomErrorResponse();
	            errorResponse.setTimestamp(LocalDateTime.now());
	            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
	            errorResponse.setError("No Eway Found for this id - " + id);
	            errorResponse.setMessage(e.getMessage());
	            errorResponse.setPath("/api/v1/eways/all/" + id);
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	        }
	    }

	    @PostMapping("/add-eway")
	    public ResponseEntity<Object> createEway(
	            @RequestParam("invoiceId") Long invoiceId,
	            @RequestParam("ewayDocNumber") String ewayDocNumber,
	            @RequestParam("eWayBillNo") String eWayBillNo,
	            @RequestParam("eWayMode") String eWayMode,
	            @RequestParam("eWayApproxDistance") String eWayApproxDistance,
	            @RequestParam("eWayValidUpto") String eWayValidUpto,
	            @RequestParam("eWaySupplyType") String eWaySupplyType,
	            @RequestParam("eWayTransactionType") String eWayTransactionType,
	            @RequestParam("eWayTransactionId") String eWayTransactionId,
	            @RequestParam("eWayGSTIN") String eWayGSTIN,
	            @RequestParam("eWayfrom") String eWayfrom,
	            @RequestParam("eWayTo") String eWayTo,
	            @RequestParam("eWayDistpatchFrom") String eWayDistpatchFrom,
	            @RequestParam("eWayShipTo") String eWayShipTo,
	            @RequestParam("ewaytaxAmount") String ewaytaxAmount,
	            @RequestParam("ewaytaxRate") String ewaytaxRate,
	            @RequestParam("ewayTransportationID") String ewayTransportationID,
	            @RequestParam("ewayVechileNo") String ewayVechileNo,
	            @RequestParam("ewayVehicleFrom") String ewayVehicleFrom
	    ) {
	        try {
	            Invoice existingInvoice = invoiceService.getInvoiceById(invoiceId);
	            if (existingInvoice == null) {
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invoice not found");
	            }
	            Eway eway = new Eway();
	            eway.setInvoice(existingInvoice);
	            eway.setEwayDocNumber(ewayDocNumber);
	            eway.seteWayBillNo(eWayBillNo);
	            eway.seteWayMode(eWayMode);
	            eway.seteWayApproxDistance(eWayApproxDistance);
	            eway.seteWayValidUpto(eWayValidUpto);
	            eway.seteWaySupplyType(eWaySupplyType);
	            eway.seteWayTransactionType(eWayTransactionType);
	            eway.seteWayTransactionId(eWayTransactionId);
	            eway.seteWayGSTIN(eWayGSTIN);
	            eway.seteWayfrom(eWayfrom);
	            eway.seteWayTo(eWayTo);
	            eway.seteWayDistpatchFrom(eWayDistpatchFrom);
	            eway.seteWayShipTo(eWayShipTo);
	            eway.setEwaytaxAmount(ewaytaxAmount);
	            eway.setEwaytaxRate(ewaytaxRate);
	            eway.setEwayTransportationID(ewayTransportationID);
	            eway.setEwayVechileNo(ewayVechileNo);
	            eway.setEwayVehicleFrom(ewayVehicleFrom);
	            
	            Eway createdEway = ewayService.createEway(eway);
	            return new ResponseEntity<>(createdEway, HttpStatus.CREATED);
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	        }
	    }

	    @PutMapping("/update-eway/{id}")
	    public ResponseEntity<Object> updateEway(
	            @PathVariable("id") Long id,
	            @RequestParam(value = "ewayDocNumber", required = false) String ewayDocNumber,
	            @RequestParam(value = "eWayBillNo", required = false) String eWayBillNo,
	            @RequestParam(value = "eWayApproxDistance", required = false) String eWayApproxDistance,
	            @RequestParam(value = "eWayValidUpto", required = false) String eWayValidUpto,
	            @RequestParam(value = "eWaySupplyType", required = false) String eWaySupplyType,
	            @RequestParam(value = "eWayTransactionType", required = false) String eWayTransactionType,
	            @RequestParam(value = "eWayGSTIN", required = false) String eWayGSTIN,
	            @RequestParam(value = "eWayfrom", required = false) String eWayfrom,
	            @RequestParam(value = "eWayTo", required = false) String eWayTo,
	            @RequestParam(value = "eWayDistpatchFrom", required = false) String eWayDistpatchFrom,
	            @RequestParam(value = "eWayShipTo", required = false) String eWayShipTo,
	            @RequestParam(value = "ewaytaxAmount", required = false) String ewaytaxAmount,
	            @RequestParam(value = "ewaytaxRate", required = false) String ewaytaxRate,
	            @RequestParam(value = "ewayTransportationID", required = false) String ewayTransportationID,
	            @RequestParam(value = "ewayVechileNo", required = false) String ewayVechileNo,
	            @RequestParam(value = "ewayVehicleFrom", required = false) String ewayVehicleFrom
	    ) {
	        try {
	            Eway existingEway = ewayService.getEwayById(id);
	            if (existingEway != null) {
	                if (ewayDocNumber != null) {
	                    existingEway.setEwayDocNumber(ewayDocNumber);
	                }
	                if (eWayBillNo != null) {
	                    existingEway.seteWayBillNo(eWayBillNo);
	                }
	                if (eWayApproxDistance != null) {
	                    existingEway.seteWayApproxDistance(eWayApproxDistance);
	                }
	                if (eWayValidUpto != null) {
	                    existingEway.seteWayValidUpto(eWayValidUpto);
	                }
	                if (eWaySupplyType != null) {
	                    existingEway.seteWaySupplyType(eWaySupplyType);
	                }
	                if (eWayTransactionType != null) {
	                    existingEway.seteWayTransactionType(eWayTransactionType);
	                }
	                if (eWayGSTIN != null) {
	                    existingEway.seteWayGSTIN(eWayGSTIN);
	                }
	                if (eWayfrom != null) {
	                    existingEway.seteWayfrom(eWayfrom);
	                }
	                if (eWayTo != null) {
	                    existingEway.seteWayTo(eWayTo);
	                }
	                if (eWayDistpatchFrom != null) {
	                    existingEway.seteWayDistpatchFrom(eWayDistpatchFrom);
	                }
	                if (eWayShipTo != null) {
	                    existingEway.seteWayShipTo(eWayShipTo);
	                }
	                if (ewaytaxAmount != null) {
	                    existingEway.setEwaytaxAmount(ewaytaxAmount);
	                }
	                if (ewaytaxRate != null) {
	                    existingEway.setEwaytaxRate(ewaytaxRate);
	                }
	                if (ewayTransportationID != null) {
	                    existingEway.setEwayTransportationID(ewayTransportationID);
	                }
	                if (ewayVechileNo != null) {
	                    existingEway.setEwayVechileNo(ewayVechileNo);
	                }
	                if (ewayVehicleFrom != null) {
	                    existingEway.setEwayVehicleFrom(ewayVehicleFrom);
	                }
	                
	                Eway updatedEway = ewayService.updateEway(id, existingEway);
	                return ResponseEntity.ok().body(updatedEway);
	            } else {
	                throw new ResourceNotFoundException("Eway not found with id: " + id);
	            }
	        } catch (ResourceNotFoundException e) {
	            CustomErrorResponse errorResponse = new CustomErrorResponse();
	            errorResponse.setTimestamp(LocalDateTime.now());
	            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
	            errorResponse.setError("No Eway Found for this id - " + id);
	            errorResponse.setMessage(e.getMessage());
	            errorResponse.setPath("/api/v1/eways/all/" + id);
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	        }
	    }


	    @DeleteMapping("/delete-eway")
	    public ResponseEntity<Object> deleteEway(@RequestParam("id") Long id) {
	        try {
	            ewayService.deleteEway(id);
	            return ResponseEntity.ok("Eway successfully deleted.");
	        } catch (ResourceNotFoundException e) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Eway not found with id: " + id);
	        }
	    }
	}
