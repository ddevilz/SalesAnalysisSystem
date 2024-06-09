package com.sas.SalesAnalysisSystem.service.salesperson;

import com.sas.SalesAnalysisSystem.models.Salesperson;

import java.util.List;
public interface SalespersonService {

    Salesperson getSalespersonById(Long salespersonId);


	void toggleStatusSalesperson(Long id);

	Long countAllActiveSalesperson();

	Long countAllInActiveSalesperson();

	List<Salesperson> getAllActiveSalespersons();

	List<Salesperson> getAllInActiveSalespersons();

	List<String> getAllDistributorProfiles(int page, int size);

	List<String> findAllSalespersonNames(String query, int page, int size);


	Salesperson createSalesperson(String name, String contactNumber, String email, Boolean isActive,
			List<Long> distributorIds);

	List<Object[]> getAllDistributorProfilesWithId(int page, int size);

	Salesperson updateSalesperson(Long id, String name, String contactNumber, String email, Boolean isActive);

	String updateSalespersonDistributor(Long salespersonId, List<Long> distributorIds);

}
