package com.sas.SalesAnalysisSystem.controller;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.sas.SalesAnalysisSystem.exception.CustomErrorResponse;
import com.sas.SalesAnalysisSystem.exception.ResourceNotFoundException;
import com.sas.SalesAnalysisSystem.models.Expenses;
import com.sas.SalesAnalysisSystem.service.expenses.ExpensesService;

@RestController
@RequestMapping("/api/v1/expenses")
public class ExpensesController {

    @Autowired
    private ExpensesService expensesService;

    @GetMapping("/all")
    public ResponseEntity<Object> getAllExpenses() {
        try {
            List<Expenses> expenses = expensesService.getAllExpenses();
            return ResponseEntity.ok().body(expenses);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No expenses found");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getExpensesById(@PathVariable("id") Long id) {
        try {
            Expenses expenses = expensesService.getExpensesById(id);
            return ResponseEntity.ok().body(expenses);
        } catch (ResourceNotFoundException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("No expenses Found for this id - " + id);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setPath("/api/v1/expenses/id/" + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Expenses> createExpenses(@RequestParam("salespersonId") Long salespersonId,
            @RequestParam("salary") Double salary,
            @RequestParam("incentive") Double incentive,
            @RequestParam("miscellaneous") Double miscellaneous,
            @RequestParam("date") String dateString) {
    	LocalDateTime date = LocalDateTime.parse(dateString +"T"+ LocalTime.now());
        Expenses createdExpenses = expensesService.createExpenses(salespersonId, salary, incentive, miscellaneous,date);
        return new ResponseEntity<>(createdExpenses, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateExpenses(@PathVariable("id") Long id,
            @RequestParam(value = "salary", required = false) Double salary,
            @RequestParam(value = "incentive", required = false) Double incentive,
            @RequestParam(value = "miscellaneous", required = false) Double miscellaneous,
            @RequestParam(value = "date", required = false) String dateString) {
        try {
        	if (dateString != null) {
        		LocalDateTime date = LocalDateTime.parse(dateString +"T"+ LocalTime.now());
        		Expenses updatedExpenses = expensesService.updateExpenses(id, salary, incentive, miscellaneous,date);
			}
        	Expenses updatedExpenses = expensesService.updateExpenses(id, salary, incentive, miscellaneous, null);
            return ResponseEntity.ok().body(updatedExpenses);

        } catch (MethodArgumentTypeMismatchException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("No expenses found for this id - " + id);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setPath("/api/v1/expenses/update/" + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteExpenses(@PathVariable("id") Long id) {
        try {
            expensesService.deleteExpenses(id);
            return ResponseEntity.ok("Expenses successfully deleted.");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Expenses not found with id: " + id);
        }
    }

    @GetMapping("salesperson/{salespersonId}")
    public ResponseEntity<Object> getExpensesBySalesperson(@PathVariable Long salespersonId) {
        try {
            List<Map<String, Object>> expensesMap = expensesService.findBySalesperson(salespersonId);
            return ResponseEntity.ok(expensesMap);
        } catch (ResourceNotFoundException e) {
            CustomErrorResponse errorResponse = new CustomErrorResponse();
            errorResponse.setTimestamp(LocalDateTime.now());
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("No expenses found for this id - " + salespersonId);
            errorResponse.setMessage(e.getMessage());
            errorResponse.setPath("/api/v1/expenses/update/" + salespersonId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

}
