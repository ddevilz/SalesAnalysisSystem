package com.sas.SalesAnalysisSystem.service.distributor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.sas.SalesAnalysisSystem.models.Distributor;
import com.sas.SalesAnalysisSystem.models.DistributorProfile;
import com.sas.SalesAnalysisSystem.models.DistributorSalespersonDTO;
import com.sas.SalesAnalysisSystem.models.Product;

public interface DistributorService {
	

    Distributor getDistributorById(Long id);

    List<Product> getProductsByDistributorId(Long distributorId);

	void addProductToDistributor(Long distributorId, Long productId);

	void addProductsToDistributor(Long distributorId, List<Long> productIds);
	

	DistributorProfile createDistributor(Long distributorId);


	List<Distributor> getAllInActiveDistributors();
	List<Distributor> getAllActiveDistributors();

	void toggleStatusDistributor(Long id);

	List<Map<String, Object>> getTotalSalesByDistributors(LocalDate localDate, LocalDate localDate2, Boolean status);

	List<Distributor> getDistributorsBySalespersonId(Long salespersonId);

	List<Distributor> findDistributorsWithNoSalesperson();

	Distributor updateSalesperson(Long distributorId, Long salespersonId);

	List<DistributorSalespersonDTO> getDistributorSalespersonDetails();


}
