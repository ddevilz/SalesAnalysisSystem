package com.sas.SalesAnalysisSystem.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Utils {
	
	
	public LocalDateTime getMonthlyFromDate(Optional<Integer> year, Optional<String> month) {
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
    
	public LocalDateTime getMonthlyToDate(Integer year, String monthName) {
        LocalDate endOfMonth = LocalDate.of(year, Month.valueOf(monthName.toUpperCase()), 1).with(TemporalAdjusters.lastDayOfMonth());
        return endOfMonth.atStartOfDay();
    }
    
	public LocalDateTime getAnnuallyFromDate(Optional<Integer> year) {
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

	public LocalDateTime getAnnuallyToDate(Optional<Integer> year) {
        LocalDate startOfYear = getAnnuallyFromDate(year).toLocalDate();
        int endYear = startOfYear.plusYears(1).getYear();
        LocalDate endOfYear = LocalDate.of(endYear, Month.MARCH, 31);
        return endOfYear.atTime(LocalTime.MAX);
    }

}
