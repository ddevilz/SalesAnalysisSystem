package com.sas.SalesAnalysisSystem.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sas.SalesAnalysisSystem.exception.CustomErrorResponse;
import com.sas.SalesAnalysisSystem.exception.ResourceNotFoundException;
import com.sas.SalesAnalysisSystem.models.Notes;
import com.sas.SalesAnalysisSystem.service.notes.NotesService;

@RestController
@RequestMapping("/api/v1/notes")
public class NotesController {
	
	private final NotesService notesService;
	
	@Autowired
	public NotesController(NotesService notesService) {
		this.notesService = notesService;
	}
	
	 @GetMapping("/all")
	    public ResponseEntity<Object> getAllNotes() {
	        try {
	            List<Notes> notes = notesService.getAll();
	            return ResponseEntity.ok().body(notes);
	        } catch (ResourceNotFoundException e) {
	            CustomErrorResponse errorResponse = new CustomErrorResponse();
	            errorResponse.setTimestamp(LocalDateTime.now());
	            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
	            errorResponse.setMessage(e.getMessage());
	            errorResponse.setPath("/api/v1/notes/all/");
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	        }
	    }
	 
		 @GetMapping("/findByDate")
		    public ResponseEntity<Object> findByDate(@RequestParam("date") String dateString) {
		        try {
		            List<Notes> notes = notesService.findByDate(dateString);
		            return ResponseEntity.ok().body(notes);
		        } catch (ResourceNotFoundException e) {
		            CustomErrorResponse errorResponse = new CustomErrorResponse();
		            errorResponse.setTimestamp(LocalDateTime.now());
		            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
		            errorResponse.setMessage(e.getMessage());
		            errorResponse.setPath("/api/v1/notes/findByDate?date=" + dateString);
		            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
		        }
		    }

	    @PostMapping("/create")
	    public ResponseEntity<Object> createNotes(@RequestBody Notes notes) {
	        try {
	            Notes createdNotes = notesService.create(notes);
	            return ResponseEntity.status(HttpStatus.CREATED).body(createdNotes);
	        } catch (Exception e) {
	            CustomErrorResponse errorResponse = new CustomErrorResponse();
	            errorResponse.setTimestamp(LocalDateTime.now());
	            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
	            errorResponse.setMessage("Failed to create notes: " + e.getMessage());
	            errorResponse.setPath("/api/v1/notes/create");
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	        }
	    }

	    @PutMapping("/update/{id}")
	    public ResponseEntity<Object> updateNotes(@PathVariable Long id, @RequestBody Notes updatedNotes) {
	        try {
	            Notes notes = notesService.update(id, updatedNotes);
	            return ResponseEntity.ok().body(notes);
	        } catch (ResourceNotFoundException e) {
	            CustomErrorResponse errorResponse = new CustomErrorResponse();
	            errorResponse.setTimestamp(LocalDateTime.now());
	            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
	            errorResponse.setMessage("Notes with ID " + id + " not found.");
	            errorResponse.setPath("/api/v1/notes/update/" + id);
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	        } catch (Exception e) {
	            CustomErrorResponse errorResponse = new CustomErrorResponse();
	            errorResponse.setTimestamp(LocalDateTime.now());
	            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
	            errorResponse.setMessage("Failed to update notes: " + e.getMessage());
	            errorResponse.setPath("/api/v1/notes/update/" + id);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	        }
	    }

	    @DeleteMapping("/delete/{id}")
	    public ResponseEntity<Object> deleteNotes(@PathVariable Long id) {
	        try {
	            notesService.delete(id);
	            return ResponseEntity.ok().body("Notes with ID " + id + " has been deleted successfully.");
	        } catch (ResourceNotFoundException e) {
	            CustomErrorResponse errorResponse = new CustomErrorResponse();
	            errorResponse.setTimestamp(LocalDateTime.now());
	            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
	            errorResponse.setMessage("Notes with ID " + id + " not found.");
	            errorResponse.setPath("/api/v1/notes/delete/" + id);
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	        } catch (Exception e) {
	            CustomErrorResponse errorResponse = new CustomErrorResponse();
	            errorResponse.setTimestamp(LocalDateTime.now());
	            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
	            errorResponse.setMessage("Failed to delete notes: " + e.getMessage());
	            errorResponse.setPath("/api/v1/notes/delete/" + id);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	        }
	    }
	
	
}
