package com.hopedale.hdalepark;

public class Participant {

    private String id; // Firestore uses String IDs
    private String parentName;
    private String parentEmail;
    private String childName;
    private String lessonType;
    private String lessonSession;
    private String contact;

    // Default constructor required for Firestore
    public Participant() {}

    public Participant(String parentName, String parentEmail, String childName, String lessonType, String lessonSession) {
        this.parentName = parentName;
        this.parentEmail = parentEmail;
        this.childName = childName;
        this.lessonType = lessonType;
        this.lessonSession = lessonSession;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getContact() {
        return contact;
    }
    public void setContact(String contact) {
        this.contact = contact;
    }
    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getLessonType() {
        return lessonType;
    }

    public void setLessonType(String lessonType) {
        this.lessonType = lessonType;
    }

    public String getLessonSession() {
        return lessonSession;
    }

    public void setLessonSession(String lessonSession) {
        this.lessonSession = lessonSession;
    }

    @Override
    public String toString() {
        return "Participant{" +
                "id='" + id + '\'' +
                ", parentName='" + parentName + '\'' +
                ", parentEmail='" + parentEmail + '\'' +
                ", childName='" + childName + '\'' +
                ", lessonType='" + lessonType + '\'' +
                ", lessonSession='" + lessonSession + '\'' +
                '}';
    }
}
