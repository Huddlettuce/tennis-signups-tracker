package com.hopedale.hdalepark;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class ParticipantService {

    private static final Logger logger = LoggerFactory.getLogger(ParticipantService.class);
    private static final String COLLECTION_NAME = "participants";

    @Autowired
    private Firestore dbFirestore;

    public List<Participant> getAllParticipants() throws ExecutionException, InterruptedException {
        List<Participant> participantsList = new ArrayList<>();

        ApiFuture<QuerySnapshot> future = dbFirestore.collection(COLLECTION_NAME)
                .orderBy("parentName", Query.Direction.ASCENDING)
                .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            Participant participant = document.toObject(Participant.class);
            participant.setId(document.getId());
            participantsList.add(participant);
        }

        logger.info("Retrieved {} participants from Firestore.", participantsList.size());
        return participantsList;
    }

    public Participant addParticipant(Participant participant) throws ExecutionException, InterruptedException {
        String session = participant.getLessonSession();

        ApiFuture<QuerySnapshot> future = dbFirestore.collection(COLLECTION_NAME)
                .whereEqualTo("lessonSession", session)
                .get();

        List<QueryDocumentSnapshot> existingParticipants = future.get().getDocuments();

        if (existingParticipants.size() >= 12) {
            throw new IllegalStateException("This session is full. Maximum capacity reached.");
        }

        // Build the Firestore document manually to avoid mismatches
        Map<String, Object> docData = new HashMap<>();
        docData.put("parentName", participant.getParentName());
        docData.put("parentEmail", participant.getParentEmail());
        docData.put("childName", participant.getChildName());
        docData.put("contact", participant.getContact());
        docData.put("lessonType", participant.getLessonType());
        docData.put("lessonSession", participant.getLessonSession());

        ApiFuture<DocumentReference> addedDoc = dbFirestore.collection(COLLECTION_NAME).add(docData);
        DocumentReference addedDocRef = addedDoc.get();
        participant.setId(addedDocRef.getId());

        logger.info("Added participant to session '{}', ID: {}", session, participant.getId());

        return participant;
    }

    public boolean deleteParticipant(String id) {
        try {
            logger.info("Attempting to delete participant with ID: {}", id);
            ApiFuture<WriteResult> writeResult = dbFirestore.collection(COLLECTION_NAME).document(id).delete();
            writeResult.get(); // Block until deletion completes
            logger.info("Successfully deleted participant with ID: {} (or it didn't exist).", id);
            return true;
        } catch (Exception e) {
            logger.error("Error deleting participant with ID: {}", id, e);
            return false;
        }
    }
}
