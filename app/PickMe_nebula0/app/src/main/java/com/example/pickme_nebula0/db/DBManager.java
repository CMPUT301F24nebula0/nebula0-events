package com.example.pickme_nebula0.db;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pickme_nebula0.event.Event;
import com.example.pickme_nebula0.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DBManager {
    public String usersCollection = "Users";
    public String eventsCollection = "Events";
    public String eventRegistrantsCollection = "EventRegistrants";
    public String registeredEventsCollection = "registeredEvents";
    public String organizerEventsCollection = "organizerEvents";

    public enum RegistrantStatus{
        WAITLISTED, SELECTED, CONFIRMED, CANCELED
    }
    // Waitlisted (user is registered but has not "won the lottery"
    // Selected (user has "won the lottery" but has not accepted the invite
    // Confirmed (user has "won the lottery" and has accepted the invite
    // Canceled (user "won the lottery" but took too long to accept, got canceled by organizer

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

    public interface ForEachDocumentCallback {
        void process(QueryDocumentSnapshot queryDocumentSnapshot);
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
        eventData.put("eventID", event.getEventID());
        eventData.put("organizerID", event.getOrganizerID());
        eventData.put("eventName", event.getEventName());
        eventData.put("eventDescription", event.getEventDescription());
        eventData.put("eventDate", event.getEventDate());
        eventData.put("geolocationRequired", event.getGeolocationRequired());
        eventData.put("geolocationRequirement", event.getGeolocationMaxDistance());
        eventData.put("waitlistCapacityRequired", event.getWaitlistCapacityRequired());
        eventData.put("waitlistCapacity", event.getWaitlistCapacity());
        eventData.put("createdDateTime", new Date());
        eventData.put("numberOfAttendees", event.getEventCapacity());

        // Create document
        addUpdateDocument(eventsCollection,event.getEventID(),eventData);
    }

    public void addRegistrantToWaitlist(String eventID, String registrantID){
        Map<String, Object> data = new HashMap<>();
        data.put("status", RegistrantStatus.WAITLISTED);

        // In Events, update event to have waitlisted registrant
        CollectionReference eventRegColRef = db.collection(eventsCollection).document(eventID).collection(eventRegistrantsCollection);
        createDocument(eventRegColRef,registrantID,data);

        // In Users, update user to have event
        CollectionReference userEventsColRef = db.collection(usersCollection).document(eventID).collection(registeredEventsCollection);
        createDocument(userEventsColRef,eventID,data);
    }

    public void removeRegistrantFromEvent(String eventID,String registrantID){
        // Remove registrant from events
        DocumentReference regInEventDocRef = getDocOfRegistrantInEvent(eventID,registrantID);
        removeDocument(regInEventDocRef);

        // Remove event from registrant
        DocumentReference eveInRegDocRef = getDocOfEventInRegistrant(eventID,registrantID);
        removeDocument(eveInRegDocRef);
    }

    public void setRegistrantStatus(String eventID,String registrantID,RegistrantStatus registrantStatus){
        // Set status within event
        DocumentReference regInEventDocRef = getDocOfRegistrantInEvent(eventID,registrantID);
        updateField(regInEventDocRef,"status", registrantStatus);

        // Set status within registrant
        DocumentReference eveInRegDocRef = getDocOfEventInRegistrant(eventID,registrantID);
        updateField(eveInRegDocRef,"status",registrantStatus);
    }

    public  void updateEvent(Event event){
        // For every field in database, update with event.getField(),
        // Can probably amalgamate add and update
        throw new RuntimeException("NOT IMPLEMENTED");
    }

    public void getEvent(String eventID,Obj2VoidCallback onSuccessCallback,
                         Void2VoidCallback onFailureCallback){
        throw new RuntimeException("NOT IMPLEMENTED");
    }

    public void deleteEvent(Event event){
        // Remove Event from all users who signed up
        DocumentReference eventDoc = db.collection(eventsCollection).document(event.getEventID());
        CollectionReference collectionOfEventRegistrants = eventDoc.collection(eventRegistrantsCollection);
        iterateOverCollection(collectionOfEventRegistrants, this::removeEventFromRegistrant);

        // Remove Event from organizer
        String orgID = event.getOrganizerID();
        removeDocument(db.collection(usersCollection).document(orgID).collection(organizerEventsCollection).document(event.getEventID()));

        // Remove Event from Events
        removeDocument(eventDoc);
    }

    private void removeEventFromRegistrant(DocumentSnapshot eventRegistrantDoc){
        String registrantID = eventRegistrantDoc.getId();
        String eventID = eventRegistrantDoc.getReference().getParent().getId();

        removeDocument(getDocOfEventInRegistrant(eventID,registrantID));
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
//                            Toast.makeText(OrganizerCreateEventActivity.this, "Event data saved successfully", Toast.LENGTH_SHORT).show())
                            // TODO add these toast messages to key operations
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
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
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

    private void iterateOverCollection(CollectionReference collectionRef, ForEachDocumentCallback forEachDocumentCallback){
        String operationDescription = String.format("iterateOverCollection [%s]", collectionRef.getId());
        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        forEachDocumentCallback.process(document);
                    }
                } else {
                    Log.d("Firestore", operationDescription + " failed - "+task.getException());

                }
            }
        });
    }

    private void removeDocument(DocumentReference doc){
        String operationDescription = String.format("removeDocument [C-%s, D-%s]",doc.getParent().getId(), doc.getId());

        doc.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Firestore", operationDescription + " succeeded");
                        } else {
                            Log.d("Firestore", operationDescription + " failed: " + task.getException());
                        }
                    }
                });
    }

    private void updateField(DocumentReference doc,String fieldName, Object newVal){
        String operationDescription = String.format("updateField [C-%s,D-%s, F-%s]",doc.getParent().getId(),doc.getId(),fieldName);
        doc.update(fieldName,newVal);
    }

    private void createDocument(CollectionReference colRef, String docID, Map<String, Object> data){
        String operationDescription = String.format("createDocument [C-%s, D-%s]",colRef.getId(), docID);

        colRef.document(docID).set(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Firestore", operationDescription + " succeeded");
                        } else {
                            Log.d("Firestore", operationDescription + " failed: " + task.getException());
                        }
                    }
                });
    }

    private DocumentReference getDocOfRegistrantInEvent(String eventID, String registrantID){
        return db.collection(eventsCollection).document(eventID).collection(eventRegistrantsCollection).document(registrantID);
    }

    private DocumentReference getDocOfEventInRegistrant(String eventID, String registrantID){
        return db.collection(usersCollection).document(registrantID).collection(registeredEventsCollection).document(eventID);
    }


// ----------------- \ Abstracted Helpers / --------------------------------------------------------
}