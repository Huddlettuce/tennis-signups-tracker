# Hopedale Parks Tennis Sign-up Tracker (Java/Spring Boot + Firestore)

This web application helps track sign-ups for tennis lessons (adults and children) for the Hopedale Parks Summer Program.

It uses a Java Spring Boot backend connected to **Google Cloud Firestore** (a NoSQL document database) to store participant data.
The frontend uses React (via CDN/Babel Standalone for simplicity).

## Features

*   Add new participants (Name, Lesson Type, optional Contact Info).
*   View the list of signed-up participants, ordered by name (on a separate admin page).
*   Remove participants from the list (from the admin page).
*   Data is stored persistently and scalably in Google Cloud Firestore.
*   Simple REST API backend.
*   Basic React frontend for sign-ups, simple HTML/JS admin view.

## Prerequisites

*   **Java Development Kit (JDK):** Version 17 or later installed.
*   **Apache Maven:** Version 3.6 or later installed.
*   **Google Cloud / Firebase Account:** Needed to create a Firestore database.
*   **Firebase Service Account Key:** A JSON key file downloaded from your Firebase project.

## Firebase Setup (One-time)

1.  Create a project in the [Firebase Console](https://console.firebase.google.com/).
2.  Inside your project, enable the **Firestore Database**. Choose "Start in production mode" (we control access via the backend service account).
3.  Go to Project Settings -> Service accounts.
4.  Generate a **new private key** (JSON file) and download it.
5.  **Save this JSON file securely.** Do not commit it to your Git repository.

## Setup and Running Locally

1.  **Get the Code:** Clone the repository or download the source code.
2.  **Configure Service Account Key:**
    *   Find the downloaded service account key JSON file (e.g., `firebase-service-account-key.json`).
    *   Open the `src/main/resources/application.properties` file.
    *   Set the `app.firebase.service-account-key-path` property to the **full, absolute path** of your downloaded JSON key file. Use forward slashes `/` in the path, even on Windows.
        ```properties
        # Example for Windows:
        app.firebase.service-account-key-path=C:/Users/your_username/path/to/firebase-service-account-key.json
        # Example for macOS/Linux:
        # app.firebase.service-account-key-path=/Users/your_username/path/to/firebase-service-account-key.json
        ```
3.  **Navigate to Project Directory:** Open a terminal/command prompt and `cd` into the project's root directory (containing `pom.xml`).
4.  **Build the Application:** `mvn clean install` (Downloads dependencies, including Firebase Admin SDK).
5.  **Run the Application:**
    *   `mvn spring-boot:run`
    *   OR `java -jar target/hdalepark-0.0.1-SNAPSHOT.jar`

## Accessing the Application

*   **Sign-up Page:** `http://localhost:8080/`
*   **Admin Page:** `http://localhost:8080/admin.html`

## Project Structure

*   `pom.xml`: Maven build configuration (includes `firebase-admin`).
*   `src/main/java/com/hopedale/hdalepark/`: Contains the Java backend source code.
    *   `HdaleParkApplication.java`: Main Spring Boot application class.
    *   `Participant.java`: POJO representing a participant (no JPA annotations).
    *   `FirebaseConfig.java`: Initializes Firebase Admin SDK.
    *   `ParticipantService.java`: Handles interaction with Firestore.
    *   `ParticipantController.java`: Spring REST controller.
*   `src/main/resources/`: Contains configuration and static files.
    *   `application.properties`: Includes path to Firebase service account key.
    *   `static/`: Frontend files.
        *   `index.html`, `App.js`, `admin.html`, `admin.js`, `style.css`
*   `target/`: Compiled code and JAR file.

## Frontend Notes (React via CDN)

*   Uses CDN links for React/ReactDOM/Babel. Not recommended for production due to performance.
*   A proper Node.js build step should be added for deployment.

## Deployment (Further Steps)

*   **Credentials:** Do NOT hardcode the service key path in `application.properties` for deployment. Instead, remove the property and set the `GOOGLE_APPLICATION_CREDENTIALS` environment variable on your deployment platform to the *path where you securely upload the key file* on the server. The `FirebaseConfig.java` code prioritizes this environment variable.
*   **Platform:** Deploy the JAR file to a PaaS (Render, Fly.io, App Engine, etc.) or a VPS.
*   **Frontend Build:** Implement a Node.js build process for the React frontend.
*   **Security:** Secure the `/admin.html` page with authentication.