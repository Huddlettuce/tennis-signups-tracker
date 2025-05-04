package com.hopedale.hdalepark;

// Remove JPA imports
// import jakarta.persistence.*;

// Remove JPA annotations like @Entity, @Table, @Id, @GeneratedValue, @Column
public class Participant {

    private String id; // Firestore uses String IDs
    private String name;
    private String lessonType;
    private String contact;

    // --- Constructors ---

    // Default constructor needed for Firestore deserialization
    public Participant() {
    }

    public Participant(String name, String lessonType, String contact) {
        this.name = name;
        this.lessonType = lessonType;
        this.contact = contact;
    }

    // --- Getters and Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLessonType() {
        return lessonType;
    }

    public void setLessonType(String lessonType) {
        this.lessonType = lessonType;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    // --- (Optional) toString, equals, hashCode ---
    @Override
    public String toString() {
        return "Participant{" +
               "id='" + id + '\'' + // Updated for String id
               ", name='" + name + '\'' +
               ", lessonType='" + lessonType + '\'' +
               ", contact='" + contact + '\'' +
               '}';
    }
} 