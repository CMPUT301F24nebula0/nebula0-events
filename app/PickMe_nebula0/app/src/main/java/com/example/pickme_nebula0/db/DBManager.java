package com.example.pickme_nebula0.db;

import android.util.Log;

import com.example.pickme_nebula0.event.Event;
import com.example.pickme_nebula0.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DBManager {
    public String usersCollection = "Users";
    public String eventsCollection = "Events";

    private FirebaseFirestore db;

    public DBManager() {
        db = FirebaseFirestore.getInstance();
    }

// ------------- \ Function Interfaces / -----------------------------------------------------------
    public interface Void2VoidCallback {
        void run();  // Define the method to be used as a callback
    }

    public interface Obj2VoidCallback {
        void run(Object u);
    }

    public interface DocumentConverter {
        Object convert(DocumentSnapshot doc);
    }
// ------------- / Function Interfaces \ -----------------------------------------------------------


// -------------------- \ Users / ------------------------------------------------------------------
    /**
     * Checks for user with this device ID in the database
     * If no such user exists, launches UserInfoActivity
     * If user already exists, launches HomePageActivity
     */
    public void checkUserRegistration(String deviceID, Void2VoidCallback registeredCallback, Void2VoidCallback unregisteredVoid2VoidCallback) {
        checkExistenceOfDocument(usersCollection, deviceID, registeredCallback, unregisteredVoid2VoidCallback);
    }

    public void addUpdateUserProfile(User user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("phone", user.getPhoneNumber());
        userData.put("profilePic", user.getProfilePicture());
        userData.put("notificationsEnabled", user.notifEnabled());
        // TODO need to do this with events lists and facility profile too - may be better to split addUserProfile and update user profile into two functions

        addUpdateDocument(usersCollection,user.getUserID(),userData);
    }

    /**
     * Performs onSuccessCallback() on User profile if successfully retrieved, else performs onFailureCallback
     *
     * @param deviceID          deviceID of user of interest (key for db)
     * @param onSuccessCallback action performed on user if their profile is found in database
     * @param onFailureCallback action performed if user not found in database
     */
    public void getUser(String deviceID, Obj2VoidCallback onSuccessCallback,Void2VoidCallback onFailureCallback) {
        getDocumentAsObject(usersCollection,deviceID,this::userConverter,onSuccessCallback,onFailureCallback);
    }

    private Object userConverter(DocumentSnapshot document){
        String name = document.getString("name");
        String email = document.getString("email");
        String phone = document.getString("phone");
        Boolean notifEnabled = document.getBoolean("notificationsEnabled");

        return new User(document.getId(), name, email, phone, notifEnabled);
    }

// -------------------- / Users \ ------------------------------------------------------------------


// -------------------- \ Events / -----------------------------------------------------------------
    public void addEvent(Event event){
         // Populate fields with data from object
        Map<String, Object> eventData = new HashMap<>();
        //eventData.put("organizerID",event.getOrgID());
        // TODO - populate remaining fields

        // Create document
        addUpdateDocument(eventsCollection,event.getEventID(),eventData);
    }

    public  void updateEvent(Event event){
        // For every field in database, update with event.getField(),
        // Can probably amalgamate add and update
    }

    public void getEvent(String eventID,Obj2VoidCallback onSuccessCallback,
                         Void2VoidCallback onFailureCallback){

    }

    public void deleteEvent(Event event){
        // Remove Event from all users

        // Remove Event from organizer

        // Remove Event from Events
    }
// -------------------- / Events \ -----------------------------------------------------------------


// ----------------- \ Abstracted Helpers / --------------------------------------------------------
    private void checkExistenceOfDocument(String collectionName, String documentID,
                                          Void2VoidCallback foundCallback,
                                          Void2VoidCallback unfoundCallback) {
        DocumentReference docRef = db.collection(collectionName).document(documentID);
        String operationDescription = String.format("checkExistenceOfDocument for [%s,%s]", collectionName, documentID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Firestore", operationDescription + " succeeded with status FOUND");
                        foundCallback.run();
                    } else {
                        Log.d("Firestore", operationDescription + " succeeded with status NOT FOUND");
                        unfoundCallback.run();
                    }
                } else {
                    Log.d("Firestore", operationDescription + " failed with error: " + task.getException());
                    unfoundCallback.run();
                }
            }
        });
    }

    private void addUpdateDocument(String collectionName, String documentID,
                                   Map<String, Object> data){
        String operationDescription = String.format("addUpdateDocument for [%s,%s]", collectionName, documentID);

        DocumentReference docRef = db.collection(collectionName).document(documentID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) { // we can retrieve (or create) the document
                DocumentSnapshot document = task.getResult();

                docRef.set(data)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("Firestore", operationDescription + " succeeded");
                        })
                        .addOnFailureListener(e -> {
                            Log.d("Firestore", operationDescription + "found/created doc but failed to set with error:" + e.getMessage());
                        });
            } else {
                // Failed to check document existence
                Log.d("Firestore", operationDescription + "failed with error: " + task.getException().getMessage());
            }
        });
    }

    public String createIDForDocumentIn(String collectionName){
        return FirebaseFirestore.getInstance().collection(collectionName).document().getId();
    }

    private void getDocumentAsObject(String collectionName, String documentID,
                                     DocumentConverter documentConverter,
                                     Obj2VoidCallback onSuccessCallback,
                                     Void2VoidCallback onFailureCallback){
        DocumentReference docRef = db.collection(collectionName).document(documentID);
        String operationDescription = String.format("getDocumentAsObject for [%s,%s]", collectionName, documentID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String name, email, phone;
                    Boolean notifEnabled;
                    if (document.exists()) {
                        Object objToReturn;
                        try {
                            objToReturn = documentConverter.convert(document);
                        } catch (Exception e) {
                            Log.d("Firestore", operationDescription + " failed - document found but failed to extract required fields with error: " + e.getMessage());
                            return;
                        }
                        Log.d("Firestore", operationDescription + " succeeded");
                        onSuccessCallback.run(objToReturn);
                    } else {
                        // Document does not exist
                        Log.d("Firestore", operationDescription + " failed - document does not exist");
                        onFailureCallback.run();
                    }
                } else {
                    // Failed to get the document
                    Log.d("Firestore", operationDescription + " failed with error: " + task.getException());
                    onFailureCallback.run();
                }
            }
        });
    }
// ----------------- \ Abstracted Helpers / --------------------------------------------------------
}