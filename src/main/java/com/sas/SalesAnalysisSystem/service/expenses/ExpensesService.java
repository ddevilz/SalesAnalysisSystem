package com.sas.SalesAnalysisSystem.service.expenses;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.sas.SalesAnalysisSystem.models.Expenses;

@Service
public interface ExpensesService {

	Expenses getExpensesById(Long expensesId);

	List<Expenses> getAllExpenses();

	void deleteExpenses(Long expensesId);


	Expenses updateExpenses(Long id, Double salary, Double incentive, Double miscellaneous, LocalDateTime dateString);

	List<Map<String, Object>> findBySalesperson(Long salespersonId);


	Expenses createExpenses(Long salespersonId, Double salary, Double incentive, Double miscellaneous,
			LocalDateTime dateString);


}
