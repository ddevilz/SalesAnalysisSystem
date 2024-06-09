package com.sas.SalesAnalysisSystem.service.eway;

import java.util.List;
import java.util.Optional;

import com.sas.SalesAnalysisSystem.models.Eway;

public interface EwayService {

	Eway createEway(Eway eway);

	Eway updateEway(Long id, Eway eway);

	List<Eway> getAllEways();

	Eway getEwayById(Long ewayId);

	void deleteEway(Long ewayId);

	Eway findEwayWithInvoiceId(Long id);

}
