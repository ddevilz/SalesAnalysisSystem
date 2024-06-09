package com.sas.SalesAnalysisSystem.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sas.SalesAnalysisSystem.models.Notes;

@Repository
public interface NotesRepository extends JpaRepository<Notes, Long> {
	List<Notes> findByDate(LocalDate date);
}
