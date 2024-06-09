package com.sas.SalesAnalysisSystem.service.notes;

import java.util.List;

import com.sas.SalesAnalysisSystem.models.Invoice;
import com.sas.SalesAnalysisSystem.models.Notes;

public interface NotesService {

	List<Notes> getAll();

	Notes create(Notes notes);

	Notes update(Long id, Notes updatedNotes);

	void delete(Long id);

	List<Notes> findByDate(String dateString);

}
