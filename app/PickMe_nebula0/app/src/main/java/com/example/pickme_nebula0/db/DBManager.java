package com.example.pickme_nebula0.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.PickMeApplication;
import com.example.pickme_nebula0.SharedDialogue;
import com.example.pickme_nebula0.event.Event;
import com.example.pickme_nebula0.facility.Facility;
import com.example.pickme_nebula0.qr.QRCodeManager;
import com.example.pickme_nebula0.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import android.widget.Toast;
import com.example.pickme_nebula0.organizer.activities.OrganizerCreateEventActivity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DBManager {

    public String usersCollection = "Users";
    // Subcollections of Users
    public String registeredEventsCollection = "registeredEvents";
    public String organizerEventsCollection = "organizerEvents";

    public String eventsCollection = "Events";
    public String eventRegistrantsCollection = "EventRegistrants";

    public String facilitiesCollection = "Facilities";

    private QRCodeManager qrCodeManager;



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
        qrCodeManager = new QRCodeManager();
    }

// ------------- \ Function Interfaces / -----------------------------------------------------------
    public interface Void2VoidCallback {
        void run();  // Define the method to be used as a callback
    }

    public interface Obj2VoidCallback {
        void run(Object u);
    }

    public interface DocRetriever{
        DocumentReference retrieve(String docID);
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
        checkExistenceOfDocument(usersCollection,user.getUserID(),()->updateUser(user),()->createNewUser(user));
    }

    private void createNewUser(User user){
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("phone", user.getPhoneNumber());
        userData.put("profilePic", user.getProfilePicture());
        userData.put("notificationsEnabled", user.notifEnabled());
        userData.put("admin", false);

        addUpdateDocument(usersCollection,user.getUserID(),userData);
    }

    private void updateUser(User user){
        DocumentReference docRef = db.collection(usersCollection).document(user.getUserID());
        updateField(docRef,"name",user.getName());
        updateField(docRef,"email",user.getEmail());
        updateField(docRef,"phone",user.getPhoneNumber());
        updateField(docRef,"profilePic",user.getProfilePicture());
        updateField(docRef,"notificationsEnabled",user.notifEnabled());
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

    public void deleteUser(String userID){
        DocumentReference userDoc = db.collection(usersCollection).document(userID);

        // Remove User from all events they signed up for
        CollectionReference registeredEventsCol = userDoc.collection(registeredEventsCollection);
        iterateOverCollection(registeredEventsCol,(qds)->fromEventDocInRegisteredCollectionRemoveUser(qds,userID));

        // Remove any events user created (if were an organizer)
        CollectionReference createdEventsCol = userDoc.collection(organizerEventsCollection);
        iterateOverCollection(createdEventsCol,(qds)->{deleteEvent(qds.getId());});

        // Remove User from Users
        removeDocument(userDoc);
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

        String qrUri = qrCodeManager.generateQRCodeURI(event.getEventID());

        Bitmap qrBitmap = qrCodeManager.generateQRCodeBitmap(qrUri);

        String qrBase64 = qrCodeManager.bitmapToBase64(qrBitmap);

        eventData.put("qrCodeData", qrBase64);
        // Create document
        addUpdateDocument(eventsCollection, event.getEventID(), eventData);
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

    public void fromEventDocInRegisteredCollectionRemoveUser(QueryDocumentSnapshot registeredEventQDS, String userID){
        String eventID = registeredEventQDS.getId();
        removeRegistrantFromEvent(eventID, userID);
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
        getDocumentAsObject(eventsCollection,eventID,this::eventConverter,onSuccessCallback,onFailureCallback);
    }

    public void deleteEvent(String eventID){
        // Remove Event from all users who signed up
        DocumentReference eventDoc = db.collection(eventsCollection).document(eventID);
        CollectionReference collectionOfEventRegistrants = eventDoc.collection(eventRegistrantsCollection);
        iterateOverCollection(collectionOfEventRegistrants, this::removeEventFromRegistrant);

        // Remove Event from organizer
        getDocumentAsObject(eventsCollection,eventID,this::eventConverter,this::removeEventFromOrganizer,()->{});

        // Remove Event from Events
        removeDocument(eventDoc);
    }

    private void removeEventFromOrganizer(Object event){
        Event castedEvent = (Event) event;
        removeDocument(db.collection(usersCollection).document(castedEvent.getOrganizerID()).collection(organizerEventsCollection).document(castedEvent.getEventID()));
    }

    private void removeEventFromRegistrant(DocumentSnapshot eventRegistrantDoc){
        String registrantID = eventRegistrantDoc.getId();
        String eventID = eventRegistrantDoc.getReference().getParent().getId();

        removeDocument(getDocOfEventInRegistrant(eventID,registrantID));
    }

    private Object eventConverter(DocumentSnapshot document){
        String eventID = document.getId();
        String organizerID = document.getString("organizerID");
        String name = document.getString("eventName");
        String desc = document.getString("eventDescription");
        Date date = document.getDate("eventDate");
        Boolean geolocationRequired = document.getBoolean("geolocationRequired");
        // Retrieve numeric fields as Long and convert to Integer
        Long geolocRequirementLong = document.getLong("geolocationRequirement");
        Integer geolocRequirement = (geolocRequirementLong != null) ? geolocRequirementLong.intValue() : null;

        Long numberOfAttendeesLong = document.getLong("numberOfAttendees");
        Integer numberOfAttendees = (numberOfAttendeesLong != null) ? numberOfAttendeesLong.intValue() : null;

        Boolean waitistCapcityReq = document.getBoolean("waitlistCapacityRequired");
        Long waitlistCapacityLong = document.getLong("waitlistCapacity");
        Integer waitlistCapacity = (waitlistCapacityLong != null) ? waitlistCapacityLong.intValue() : null;

        String qrCodeData = document.getString("qrCodeData");

        Event event = new Event(eventID, organizerID, name, desc, date, geolocationRequired, geolocRequirement, waitistCapcityReq , waitlistCapacity, numberOfAttendees);
        event.setQrCodeData(qrCodeData);
        return event;
    }

    public Bitmap getQRCodeBitmap(String eventID) {
        DocumentReference docRef = db.collection(eventsCollection).document(eventID);
        final Bitmap[] qrBitmap = {null};

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                        String qrBase64 = document.getString("qrCodeData");
                        if (qrBase64 != null) {
                            byte[] decodedBytes = Base64.decode(qrBase64, Base64.DEFAULT);
                            qrBitmap[0] = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    }
                }
            }
        });
        return qrBitmap[0];
    }

// -------------------- / Events \ -----------------------------------------------------------------

// ----------------- \ Facilities / --------------------------------------------------------
    public void addUpdateFacility(Facility facility){
        String userID = DeviceManager.getDeviceId();
        performIfFieldPopulated(db.collection(usersCollection).document(userID),"facilityID",(facilityID)-> updateOldFacility(facilityID,facility),()->createNewFacility(facility));
    }

    public void updateOldFacility(Object facilityID,Facility facility){
        // update facilities collection
        DocumentReference docRefFacilities = db.collection(facilitiesCollection).document(facilityID.toString());
        updateField(docRefFacilities,"name",facility.getFacilityName());
        updateField(docRefFacilities,"address",facility.getFacilityAddress());
      }

    public void createNewFacility(Facility facility){
        // create new document in facilities collection
        String facilityID = createIDForDocumentIn(facilitiesCollection);
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("name", facility.getFacilityName());
        facilityData.put("address", facility.getFacilityAddress());
        facilityData.put("organizerID",facility.getOrganizerID());
        addUpdateDocument(facilitiesCollection,facilityID,facilityData);

        // update user in users collection
        updateField(db.collection(usersCollection).document(DeviceManager.getDeviceId()),"facilityID",facilityID);
    }

    public void getFacility(String userID, Obj2VoidCallback onPopulated, Void2VoidCallback onFailureCallback){
        DocumentReference userDocRef = db.collection(usersCollection).document(userID);
        performIfFieldPopulated(userDocRef,"facilityID",(facilityID) -> onSuc(facilityID, onPopulated, onFailureCallback),()->{return;});
    }

    public void onSuc(Object facilityID, Obj2VoidCallback suc2,Void2VoidCallback fail){
        getDocumentAsObject(facilitiesCollection,facilityID.toString(),this::facilityConverter,suc2,fail);
    }

    private Object facilityConverter(DocumentSnapshot document){
        String name = document.getString("name");
        String adr = document.getString("address");
        String orgID = document.getString("organizerID");

        return new Facility(document.getId(),orgID,name,adr);
    }

    public void deleteFacility(String facilityID){
        DocumentReference facilityDocRef = db.collection(facilitiesCollection).document(facilityID);

        // Delete all the events occurring at this facility e.g. all events for that organizer, and remove the facility from the organize
        getDocumentAsObject(facilitiesCollection,facilityID,this::facilityConverter,this::removeEventsAtFacilityAndRemoveFromOrganizer,()->{});

        // Remove it from Facilities Collection
        removeDocument(facilityDocRef);
    }

    private void removeEventsAtFacilityAndRemoveFromOrganizer(Object facility){
        Facility castedFacility = (Facility) facility;
        String organizerID = castedFacility.getOrganizerID();
        CollectionReference createdEventsCol = db.collection(usersCollection).document(organizerID).collection(organizerEventsCollection);
        iterateOverCollection(createdEventsCol,(qds)->{deleteEvent(qds.getId());});

        updateField(db.collection(usersCollection).document(organizerID),"facilityID",null);
    }


// -------------------- / Facilities \ -----------------------------------------------------------------

// ----------------- \ TODO / --------------------------------------------------------
    public void deleteQRCode(){
        throw new RuntimeException("NOT IMPLEMENTED");
    }

    public void deleteImage(){
        throw new RuntimeException("NOT IMPLEMENTED");
    }

// ----------------- / TODO \ --------------------------------------------------------


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


    private void performIfFieldPopulated(DocumentReference docRef, String fieldName, Obj2VoidCallback populatedCallback, Void2VoidCallback unpopulatedCallback){
        String operationDescription = String.format("checkFieldPopulated [C-%s, D-%s, F-%s]: ",docRef.getParent().getId(),docRef.getId(), fieldName);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Check if the field exists and is non-null
                    if (document.contains(fieldName) && document.get(fieldName) != null) {

                        Object fieldValue = document.get(fieldName);
                        populatedCallback.run(fieldValue);
                        Log.d("Firestore", operationDescription + "field is populated");
                    } else {
                        Log.d("Firestore", operationDescription + "field DNE or NULL");
                        unpopulatedCallback.run();
                    }
                } else {
                    Log.d("Firestore", operationDescription + "document DNE");
                }
            } else {
                Log.e("Firestore", operationDescription + "Failed to retrieve document", task.getException());
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
        doc.update(fieldName,newVal).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("Firestore", operationDescription + " succeeded");
                } else {
                    Log.d("Firestore", operationDescription + " failed: " + task.getException());
                }
            }
        });;
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