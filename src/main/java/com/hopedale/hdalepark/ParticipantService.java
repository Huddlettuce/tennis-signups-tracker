package com.hopedale.hdalepark;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ParticipantService {

    private static final Logger logger = LoggerFactory.getLogger(ParticipantService.class);
    private static final String COLLECTION_NAME = "participants";

    @Autowired
    private Firestore dbFirestore;

    public List<Participant> getAllParticipants() throws ExecutionException, InterruptedException {
        List<Participant> participantsList = new ArrayList<>();
        // Order by name ascending
        ApiFuture<QuerySnapshot> future = dbFirestore.collection(COLLECTION_NAME).orderBy("name", Query.Direction.ASCENDING).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            Participant participant = document.toObject(Participant.class);
            participant.setId(document.getId()); // Set the document ID
            participantsList.add(participant);
        }
        logger.info("Retrieved {} participants from Firestore.", participantsList.size());
        return participantsList;
    }

    public Participant addParticipant(Participant participant) throws ExecutionException, InterruptedException {
        // Firestore automatically generates an ID if one isn't provided
        ApiFuture<DocumentReference> future = dbFirestore.collection(COLLECTION_NAME).add(participant);
        DocumentReference addedDocRef = future.get();
        participant.setId(addedDocRef.getId()); // Set the generated ID back on the object
        logger.info("Added participant with ID: {}", participant.getId());
        return participant;
    }

    public boolean deleteParticipant(String id) {
        try {
            logger.info("Attempting to delete participant with ID: {}", id);
            ApiFuture<WriteResult> writeResult = dbFirestore.collection(COLLECTION_NAME).document(id).delete();
            // delete() doesn't throw an error if the document doesn't exist.
            // We can check the result or just assume success if no exception.
            writeResult.get(); // Wait for operation to complete
            logger.info("Successfully deleted participant with ID: {} (or it didn't exist).", id);
            return true; // Indicate success (or non-existence)
        } catch (Exception e) {
            logger.error("Error deleting participant with ID: {}", id, e);
            return false; // Indicate failure
        }
    }
} 