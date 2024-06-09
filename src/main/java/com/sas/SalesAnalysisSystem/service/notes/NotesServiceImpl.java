package com.sas.SalesAnalysisSystem.service.notes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sas.SalesAnalysisSystem.exception.ResourceNotFoundException;
import com.sas.SalesAnalysisSystem.models.Invoice;
import com.sas.SalesAnalysisSystem.models.Notes;
import com.sas.SalesAnalysisSystem.repository.NotesRepository;

@Service
public class NotesServiceImpl implements NotesService {
	
	private final NotesRepository notesRepository;
	
	@Autowired
	public NotesServiceImpl(NotesRepository notesRepository) {
		this.notesRepository = notesRepository;
	}

	@Override
	public List<Notes> getAll() {
		try {
			List<Notes> notes = notesRepository.findAll();
			return notes;
		} catch (Exception e) {
			throw new RuntimeException("Error occurred while fetching all notes: " + e.getMessage());
		}
	}
	
	 @Override
	    public Notes create(Notes notes) {
	        try {
	            return notesRepository.save(notes);
	        } catch (Exception e) {
	            throw new RuntimeException("Error occurred while creating notes: " + e.getMessage());
	        }
	    }

	    @Override
	    public Notes update(Long id, Notes updatedNotes) {
	        try {
	            Optional<Notes> optionalNotes = notesRepository.findById(id);
	            if (optionalNotes.isPresent()) {
	                Notes existingNotes = optionalNotes.get();
	                existingNotes.setTitle(updatedNotes.getTitle() != null ? updatedNotes.getTitle() : existingNotes.getTitle());
	                existingNotes.setContent(updatedNotes.getContent() != null ? updatedNotes.getContent() : existingNotes.getContent());
	                existingNotes.setTheme(updatedNotes.getTheme() != null ? updatedNotes.getTheme() : existingNotes.getTheme());
	                existingNotes.setDate(updatedNotes.getDate() != null ? updatedNotes.getDate() : existingNotes.getDate());
	                return notesRepository.save(existingNotes);
	            } else {
	                throw new ResourceNotFoundException("Notes with ID " + id + " not found.");
	            }
	        } catch (Exception e) {
	            throw new RuntimeException("Error occurred while updating notes with ID " + id + ": " + e.getMessage());
	        }
	    }

	    @Override
	    public void delete(Long id) {
	        try {
	            if (notesRepository.existsById(id)) {
	                notesRepository.deleteById(id);
	            } else {
	                throw new ResourceNotFoundException("Notes with ID " + id + " not found.");
	            }
	        } catch (Exception e) {
	            throw new RuntimeException("Error occurred while deleting notes with ID " + id + ": " + e.getMessage());
	        }
	    }

		@Override
		public List<Notes> findByDate(String dateString) {
			LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
			return notesRepository.findByDate(date);
		}
	
}
