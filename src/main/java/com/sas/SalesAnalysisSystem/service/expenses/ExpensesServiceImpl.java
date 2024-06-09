package com.sas.SalesAnalysisSystem.service.expenses;

import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sas.SalesAnalysisSystem.exception.ResourceNotFoundException;
import com.sas.SalesAnalysisSystem.models.Expenses;
import com.sas.SalesAnalysisSystem.models.Salesperson;
import com.sas.SalesAnalysisSystem.repository.ExpensesRepository;
import com.sas.SalesAnalysisSystem.repository.SalespersonRepository;

@Service
public class ExpensesServiceImpl implements ExpensesService{
	
	@Autowired
	private final ExpensesRepository expensesRepository;
	private final SalespersonRepository salespersonRepository;

	public ExpensesServiceImpl(ExpensesRepository expensesRepository,SalespersonRepository salespersonRepository) {
		this.expensesRepository = expensesRepository;
		this.salespersonRepository = salespersonRepository;
	}
	
	@Override
    public Expenses getExpensesById(Long expensesId) {
        Optional<Expenses> expensesDb = expensesRepository.findById(expensesId);
        if (expensesDb.isEmpty()) {
            throw new ResourceNotFoundException("Record not found with id: " + expensesId);
        }
        return expensesDb.get();
    }

    @Override
    public List<Expenses> getAllExpenses() {
        List<Expenses> expensesList = expensesRepository.findAll();
        if (expensesList.isEmpty()) {
            throw new ResourceNotFoundException("No expenses found.");
        }
        return expensesList;
    }

    @Override
    public void deleteExpenses(Long expensesId) {
        Optional<Expenses> expensesDb = expensesRepository.findById(expensesId);
        if (expensesDb.isPresent()) {
            expensesRepository.delete(expensesDb.get());
        } else {
            throw new ResourceNotFoundException("Record not found with id: " + expensesId);
        }
    }
    
    @Override
    public Expenses createExpenses(Long id,Double salary, Double incentive, Double miscellaneous,LocalDateTime date) {
        Expenses expenses = new Expenses();
        Optional<Salesperson> salespersonn=salespersonRepository.findById(id);
        if(!salespersonn.isPresent()) {
        	throw new ResourceNotFoundException("salesperson not found with id: " + id);
        }
        Salesperson salesperson = salespersonn.get();
        expenses.setSalesperson(salesperson.getId());
        expenses.setSalary(salary);
        expenses.setIncentive(incentive);
        expenses.setMiscellaneous(miscellaneous);
        expenses.setExpenseDate(date);
        return expensesRepository.save(expenses);
    }

    @Override
    public Expenses updateExpenses(Long id, Double salary, Double incentive, Double miscellaneous,LocalDateTime date) {
        Optional<Expenses> optionalExpenses = expensesRepository.findById(id);
        if (optionalExpenses.isPresent()) {
            Expenses expenses = optionalExpenses.get();
            expenses.setSalary(salary != null ? salary : expenses.getSalary());
            expenses.setIncentive(incentive != null ? incentive : expenses.getIncentive());
            expenses.setMiscellaneous(miscellaneous != null ? miscellaneous : expenses.getMiscellaneous());
            expenses.setExpenseDate(date != null ? date : expenses.getExpenseDate() );
            return expensesRepository.save(expenses);
        } else {
            throw new ResourceNotFoundException("Record not found with id: " + id);
        }
    }
    
    @Override
    public List<Map<String, Object>> findBySalesperson(Long salespersonId) {
        List<Object[]> resultList = expensesRepository.findBySalesperson(salespersonId);
        List<Map<String, Object>> expensesList = new ArrayList<>();

        if (resultList != null && !resultList.isEmpty()) {
            for (Object[] row : resultList) {
                LocalDateTime createDate = (LocalDateTime) row[0];
                LocalDateTime updateDate = (LocalDateTime) row[1];
                double incentive = (double) row[2];
                double miscellaneous = (double) row[3];
                double salary = (double) row[4];

                Map<String, Object> expensesMap = new HashMap<>();
                expensesMap.put("createDate", createDate.toString()); 
                expensesMap.put("updateDate", updateDate.toString()); 
                expensesMap.put("incentive", incentive);
                expensesMap.put("miscellaneous", miscellaneous);
                expensesMap.put("salary", salary);

                expensesList.add(expensesMap);
            }
        }

        return expensesList;
    }



}
