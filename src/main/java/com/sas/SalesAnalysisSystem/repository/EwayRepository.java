package com.sas.SalesAnalysisSystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sas.SalesAnalysisSystem.models.Eway;

public interface EwayRepository extends JpaRepository<Eway, Long>{

	Optional<Eway> findByInvoiceId(Long invoiceId);

}
