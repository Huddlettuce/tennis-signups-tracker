package com.hopedale.hdalepark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/participants") // Base path for all endpoints in this controller
public class ParticipantController {

    private static final Logger logger = LoggerFactory.getLogger(ParticipantController.class);

    @Autowired // Spring automatically injects an instance of ParticipantService
    private ParticipantService participantService;

    // GET /api/participants - Fetches all participants
    @GetMapping
    public ResponseEntity<List<Participant>> getAllParticipants() {
        try {
            List<Participant> participants = participantService.getAllParticipants();
            return ResponseEntity.ok(participants);
        } catch (Exception e) {
            logger.error("Error retrieving participants from Firestore", e);
            // Return an empty list or appropriate error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // POST /api/participants - Adds a new participant
    @PostMapping
    public ResponseEntity<?> addParticipant(@RequestBody Participant participant) {
       if (participant.getParentName() == null || participant.getParentName().trim().isEmpty() ||
            participant.getChildName() == null || participant.getChildName().trim().isEmpty() ||
            participant.getLessonSession() == null || participant.getLessonSession().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("{\"error\": \"Parent name, child name, and session are required\"}");
}


        try {
            Participant savedParticipant = participantService.addParticipant(participant);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedParticipant);
        } catch (IllegalStateException e) {
            // Handle full session
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            logger.error("Error adding participant to Firestore", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Failed to save participant\"}");
        }
    }


    // DELETE /api/participants/{id} - Deletes a participant by ID (ID is now String)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipant(@PathVariable String id) {
        try {
            boolean deleted = participantService.deleteParticipant(id);
            if (deleted) {
                 return ResponseEntity.ok().build(); // Return 200 OK (even if not found, delete is idempotent)
            } else {
                // This case now indicates an internal error during deletion
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            // Catch any other unexpected exceptions during delete attempt
            logger.error("Unexpected error deleting participant with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}