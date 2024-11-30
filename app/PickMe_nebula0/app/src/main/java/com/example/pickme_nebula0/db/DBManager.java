package com.example.pickme_nebula0.db;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.event.Event;
import com.example.pickme_nebula0.facility.Facility;
import com.example.pickme_nebula0.notification.Notification;
import com.example.pickme_nebula0.qr.QRCodeGenerator;
import com.example.pickme_nebula0.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Class encompassing database access and modification
 *
 * @author Stephine Yearley
 * @see Event,User,Facility
 */
public class DBManager {
    // USERS COLLECTION
    public String usersCollection = "Users";
    // Sub-collections of Users
    public String registeredEventsCollection = "RegisteredEvents";
    public String organizerEventsCollection = "OrganizerEvents";
    // EVENTS COLLECTION
    public String eventsCollection = "Events";
    // Sub-collections of Event
    public String eventRegistrantsCollection = "EventRegistrants";
    public String eventStatusKey = "status";
    // FACILITIES COLLECTION
    public String facilitiesCollection = "Facilities";
    // NOTIFICATIONS COLLECTION
    public String notificationCollection = "Notifications";
    // Sub-collection of Notifications
    public String userNotifsCollection = "userNotifs";

    private final QRCodeGenerator qrCodeManager;
    public final FirebaseFirestore db;


    /**
     * Enum describing status of an entrant
     * Waitlisted = user is registered but has not "won the lottery"
     * Selected = user has "won the lottery" but has not accepted the invite
     * Confirmed = user has "won the lottery" and has accepted the invite
     * Cancelled = user "won the lottery" but took too long to accept, got cancelled by organizer
     */
    public enum RegistrantStatus{
        WAITLISTED, SELECTED, CONFIRMED, CANCELLED;
    }


    /**
     * Constructor, instantiates the default FirebaseFirestore instance
     */
    public DBManager() {

        db = FirebaseFirestore.getInstance();
        qrCodeManager = new QRCodeGenerator();
    }

// ------------- \ Function Interfaces / -----------------------------------------------------------

    /**
     * Interface defining a callback function with signature "void foo()"
     * Pass callback into a function that takes a Void2VoidCallback using "bar(this::foo)"
     * Run callback function using foo.run();
     */
    public interface Void2VoidCallback {
        void run();
    }

    /**
     * Interface defining a callback function with signature "void foo(Object obj)"
     * Pass callback into a function that takes a Obj2VoidCallback using "bar(this::foo)"
     * Run callback function using foo.run(obj)
     * NOTE: Function of type Obj2VoidCallback should cast to required object type, if necessary
     */
    public interface Obj2VoidCallback {
        void run(Object u);
    }

    /**
     * Interface defining a function that converts a DocumentSnapshot into a relevant class Instance
     * A valid DocumentConverter has signature "Object myDocConverter(DocumentSnapshot doc)"
     */
    public interface DocumentConverter {
        Object convert(DocumentSnapshot doc);
    }

    /**
     * Interface defining a callback to be performed on a document in the results of a query.
     * A valid ProcessQueryDocCallback has signature "void myForEachDocCallback(QueryDocumentSnapshot)"
     */
    public interface ProcessQueryDocCallback {
        void process(QueryDocumentSnapshot queryDocumentSnapshot);
    }
// ------------- / Function Interfaces \ -----------------------------------------------------------


// -------------------- \ Users / ------------------------------------------------------------------

    /**
     * Checks if user with given deviceID exists in Users collection of database
     *
     * @param deviceID deviceID of user we are searching for
     * @param registeredCallback function called if user exists in database
     * @param unregisteredVoid2VoidCallback function called if user does not exist in database
     */
    public void checkUserRegistration(String deviceID, Void2VoidCallback registeredCallback, Void2VoidCallback unregisteredVoid2VoidCallback) {
        checkExistenceOfDocument(usersCollection, deviceID, registeredCallback, unregisteredVoid2VoidCallback);
    }

    /**
     * Checks if user is already in db, if yes update their profile, if not, create new
     * @param user User object describing user
     * @param onSuccess function to run if successful
     */
    public void addUpdateUserProfile(User user, Void2VoidCallback onSuccess) {
        checkExistenceOfDocument(usersCollection,user.getUserID(),()->updateUser(user,onSuccess),()->createNewUser(user,onSuccess));
    }

    /**
     * Adds a given user to the database.
     * If this user (same user.deviceID) already exists, their data will be overwritten.
     *
     * @param user instance of User to be added to the Users collection of the database
     * @param onSuccess Callback called when user successfully added
     */
    private void createNewUser(User user, Void2VoidCallback onSuccess){
        Map<String, Object> userData = new HashMap<>();
        userData.put("userID",user.getUserID());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("phone", user.getPhone());
        userData.put("profilePic", user.getProfilePic());
        userData.put("notificationsEnabled", user.getNotificationsEnabled());
        userData.put("admin", false);

        addUpdateDocument(db.collection(usersCollection),user.getUserID(),userData,onSuccess);
    }

    /**
     * Updates a user's profile in the database.
     *
     * @param user user we are updating, holds new info
     * @param onSuccess callback to run if we updated profile successfully
     */
    private void updateUser(User user, Void2VoidCallback onSuccess){
        DocumentReference docRef = db.collection(usersCollection).document(user.getUserID());
        updateField(docRef,"name",user.getName());
        updateField(docRef,"email",user.getEmail());
        updateField(docRef,"phone",user.getPhone());
        updateField(docRef,"notificationsEnabled",user.getNotificationsEnabled());
        onSuccess.run();
    }

    /**
     * Performs onSuccessCallback() on User profile if successfully retrieved, else performs onFailureCallback
     *
     * @param deviceID          deviceID of user of interest (key for db)
     * @param onSuccessCallback action performed on user if their profile is found in database
     * @param onFailureCallback action performed if user not found in database
     */
    public void getUser(String deviceID, Obj2VoidCallback onSuccessCallback,Void2VoidCallback onFailureCallback) {
        DocumentReference userDocRef = db.collection(usersCollection).document(deviceID);
        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                     User user = document.toObject(User.class);
                     onSuccessCallback.run(user);
                }
            }
            else{
                onFailureCallback.run();
            }
        });
    }

    /**
     * Retrieves the status string of given user for a given event.
     * If successful, call onSuccessCallback with the status string as the argument.
     * In status cannot be retrieved, calls onFailureCallback.
     *
     * @param deviceID deviceID of user we are trying to retrieve event registration status of
     * @param eventID event of interest
     * @param onSuccessCallback action performed with status string if it is successfully retrieved
     * @param onFailureCallback action performed if we cannot retrieve status
     */
    public void getUserStatusString(String deviceID,String eventID, Obj2VoidCallback onSuccessCallback, Void2VoidCallback onFailureCallback){
        db.collection(usersCollection).document(deviceID).collection(registeredEventsCollection).document(eventID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String status = document.getString("status");
                            onSuccessCallback.run(status);
                        } else{
                            onFailureCallback.run();
                        }
                    }
                    else{
                        onFailureCallback.run();
                    }
                });
    }

    /**
     * Deletes the user associated with a given userID
     * Removes user from all events they signed up for (Entrant)
     * Deletes all events created by this user (Organizer)
     * Deletes facility associated with this user if applicable (Organizer)
     * Deletes user from Users collection
     *
     * @param userID ID of user to delete
     */
    public void deleteUser(String userID,Void2VoidCallback onSuccess){
        DocumentReference userDoc = db.collection(usersCollection).document(userID);

        // Remove User from all events they signed up for
        CollectionReference registeredEventsCol = userDoc.collection(registeredEventsCollection);
        registeredEventsCol.get()
                        .addOnSuccessListener(querySnapshot-> {
                            for (DocumentSnapshot doc : querySnapshot.getDocuments()){
                                // Remove User from all events they signed up for
                                String eventID = doc.getId();
                                removeRegistrantFromEvent(eventID,userID);
                            }

                            // Remove any events user created (if an organizer)
                            CollectionReference createdEventsCol = userDoc.collection(organizerEventsCollection);
                            createdEventsCol.get()
                                    .addOnSuccessListener(querySnapshot2-> {
                                        for (DocumentSnapshot doc : querySnapshot2.getDocuments()){
                                            // Remove User from all events they signed up for
                                            String eventID = doc.getId();
                                            deleteEvent(eventID);
                                        }

                                        // Remove facility associated with that user (if an organizer)
                                        performIfFieldPopulated(userDoc,"facilityID",this::deleteFacility,()->{});

                                        // Remove User from Users
                                        removeDocument(userDoc);
                                        onSuccess.run();
                                    });
                        });
    }
// -------------------- / Users \ ------------------------------------------------------------------

// -------------------- \ Notifications / ----------------------------------------------------------

    /**
     * Generates a notification for entrants of given event that have given status
     *
     * @param title title of notification/message
     * @param message body of notification/message
     * @param eventID event this notification is associated with
     * @param status only entrants with this status are notified
     */
    public void notifyEntrantsOfStatus(String title, String message,String eventID, RegistrantStatus status){
        CollectionReference registrantsCollection =
                db.collection(eventsCollection).document(eventID).collection(eventRegistrantsCollection);

        iterateOverCollection(registrantsCollection,
                (qds)->{if(qds.getString("status").equals(status.toString())){
                    createNotification(title,message,qds.getId(),eventID);}
        });
    }

    /**
     * Generates a notification for all entrants in the event associate with given eventID
     *
     * @param title subject line displayed in notification
     * @param message message body displayed in notification
     * @param eventID id of event associated with the notification
     */
    public void notifyAllEntrants(String title, String message, String eventID){
        CollectionReference registrantsCollection = db.collection(eventsCollection).document(eventID).collection(eventRegistrantsCollection);
        iterateOverCollection(registrantsCollection,(qds)-> {createNotification(title,message,qds.getId(),eventID);});
    }

    /**
     * Create a notification for a given user.
     *
     * @param title subject line displayed in notification
     * @param message message body displayed in notification
     * @param userID deviceID of user we are sending the notification to
     * @param eventID ID of event associated with notification
     */
    public void createNotification(String title, String message, String userID, String eventID){
        CollectionReference userNotifCollection = db.collection(notificationCollection).document(userID).collection(userNotifsCollection);
        String notifID = createIDForDocumentIn(userNotifCollection);
        Timestamp timestamp = new Timestamp(new Date());

        Notification notif = new Notification(title,message,userID,eventID,timestamp,notifID);

       userNotifCollection
               .document(notif.getNotificationID()) // Using eventId as document ID
               .set(notif)
               .addOnSuccessListener(aVoid -> {
                   // Successfully uploaded
                   Log.d("Firestore", "DocumentSnapshot successfully written!");
               })
               .addOnFailureListener(e -> {
                   // Handle failure
                   Log.w("Firestore", "Error writing document", e);
               });
}

// -------------------- / Notifications \ ---------------------------------------------------------


// -------------------- \ Events / -----------------------------------------------------------------

    /**
     * Adds new event document to Events collection given an Event instance.
     * If Event exists already (same eventID), overwrites entry in database.
     *
     * @param event instance of event containing information to be added to database Events collection
     */
    public void addEvent(Context context,Event event){
        addEvent(context,event,null);
    }

    public void addEvent(Context context, Event event, Uri uri){
        String eventID = event.getEventID();

        // Populate fields with data from object
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventID",eventID);
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

        QRCodeGenerator qrCodeGenerator = new QRCodeGenerator();
        String qrCodeURI  = qrCodeGenerator.generateQRCodeURI(eventID);
        String hashedQRCode = qrCodeGenerator.generateSHA256Hash(qrCodeURI);

        eventData.put("hashedQRCode", hashedQRCode);

        // Create document
        if(uri != null){
            addUpdateDocument(db.collection(eventsCollection), eventID, eventData,()->{FBStorageManager.uploadPoster(uri,eventID,context);});
        } else{
            addUpdateDocument(db.collection(eventsCollection), eventID, eventData);
        }
        // Add this event to the organizer's list of created events
        CollectionReference orgsCreatedEventsCol = db.collection(usersCollection).document(event.getOrganizerID()).collection(organizerEventsCollection);
        Map<String,Object> orgEventData = new HashMap<>();
        orgEventData.put("status", "OPEN");
        addUpdateDocument(orgsCreatedEventsCol,eventID,orgEventData);
    }

    /**
     * Removes a given registrant from a given event.
     * Updates event document in Events collection
     * Updates user document in Users collection
     *
     * @param eventID eventID of event we are removing registrant from
     * @param registrantID userID of registrant we are removing from event
     */
    public void removeRegistrantFromEvent(String eventID,String registrantID){
        // Remove registrant from events
        DocumentReference regInEventDocRef = getDocOfRegistrantInEvent(eventID,registrantID);
        removeDocument(regInEventDocRef);

        // Remove event from registrant
        DocumentReference eveInRegDocRef = getDocOfEventInRegistrant(eventID,registrantID);
        removeDocument(eveInRegDocRef);
    }

    /**
     * Sets the status of a given registrant for a given event to a given status.
     * Updates the event document sub-collection
     * Updates the user document sub-collection
     *
     * @param eventID eventID of event to modify user registration for
     * @param registrantID userID of registrant we are modifying registration status of
     * @param registrantStatus new status to set user registration status to
     */
    public void setRegistrantStatus(String eventID,String registrantID,RegistrantStatus registrantStatus){
        // Set status within event
        DocumentReference regInEventDocRef = getDocOfRegistrantInEvent(eventID,registrantID);
        updateField(regInEventDocRef,"status", registrantStatus);

        // Set status within registrant
        DocumentReference eveInRegDocRef = getDocOfEventInRegistrant(eventID,registrantID);
        updateField(eveInRegDocRef,"status",registrantStatus);
    }

    /**
     * Updates the database entry for an event with the information held in given event instance
     *
     * @param event event with updated values, must have same eventID as event you want to update
     */
    public void updateEvent(Event event){
        DocumentReference eventDocRef = db.collection(eventsCollection).document(event.getEventID());

        updateField(eventDocRef,"organizerID", event.getOrganizerID());
        updateField(eventDocRef,"eventName", event.getEventName());
        updateField(eventDocRef,"eventDescription", event.getEventDescription());
        updateField(eventDocRef,"eventDate", event.getEventDate());
        updateField(eventDocRef,"geolocationRequired", event.getGeolocationRequired());
        updateField(eventDocRef,"geolocationRequirement", event.getGeolocationMaxDistance());
        updateField(eventDocRef,"waitlistCapacityRequired", event.getWaitlistCapacityRequired());
        updateField(eventDocRef,"waitlistCapacity", event.getWaitlistCapacity());
        updateField(eventDocRef,"numberOfAttendees", event.getEventCapacity());
        updateField(eventDocRef,"qrCodeData", event.getQrCodeData());
    }

    /**
     * Gets event associated with a given eventID as an Event object.
     * If successful in retrieving event, performs onSuccessCallback on event object.
     * If unsuccessful, perform onFailureCallback.
     *
     * @param eventID eventID of event to retrieve and perform onSuccessCallback on
     * @param onSuccessCallback operation to perform on retrieved Event object
     * @param onFailureCallback operation to perform if event cannot be retrieved
     */
    public void getEvent(String eventID,Obj2VoidCallback onSuccessCallback,
                         Void2VoidCallback onFailureCallback){
        DocumentReference eventDocRef = db.collection(eventsCollection).document(eventID);
        eventDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Event event = document.toObject(Event.class);
                    onSuccessCallback.run(event);
                }
            }
            else{
                onFailureCallback.run();
            }
        });
    }

    /**
     * Removes the event associated with the given eventID from the database.
     * Removes the event from all users who had registered for it
     * Removes the event from the organizers list of events
     * Removes the event from the Events collection
     *
     * @param eventID eventID of event to be removed
     */
    public void deleteEvent(String eventID){

        DocumentReference eventDoc = db.collection(eventsCollection).document(eventID);
        CollectionReference collectionOfEventRegistrants = eventDoc.collection(eventRegistrantsCollection);

        eventDoc
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Event e =  task.getResult().toObject(Event.class);

                        // Remove Event from organizer
                        removeEventFromOrganizer(e.getOrganizerID(),eventID);

                        // Remove Event from all users who signed up, including removing notifications
                        iterateOverCollection(collectionOfEventRegistrants, (regDoc)->{removeEventFromRegistrant(regDoc.getId(),eventID);});

                        // Remove Event from Events
                        removeDocument(eventDoc);
                    }
                });


    }

    /**
     * Removes a given event from a given organizer's list of organized events
     * @param organizerID ID of organizer of interest
     * @param eventID ID of event of interest
     */
    private void removeEventFromOrganizer(String organizerID, String eventID){
        DocumentReference documentReference = db.collection(usersCollection).document(organizerID).collection(organizerEventsCollection).document(eventID);
        removeDocument(documentReference);
    }

    /**
     * Removes the given registeredEvent document from the user given a eventRegistrant document.
     *
     * @param registrantID userID of registrant to remove from event
     * @param eventID eventID of event we are removing the registrant from
     */
    private void removeEventFromRegistrant(String registrantID,String eventID){
        removeEventNotificationsFromRegistrant(registrantID,eventID);
        removeDocument(getDocOfEventInRegistrant(eventID,registrantID));
    }

    private void removeEventNotificationsFromRegistrant(String registrantID, String eventID){
        CollectionReference allUserNotifs =  db.collection(notificationCollection).document(registrantID).collection(userNotifsCollection);
        allUserNotifs.whereEqualTo("eventID",eventID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Loop through the results and delete each document
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            allUserNotifs.document(document.getId()).delete()
                                    .addOnSuccessListener(aVoid ->
                                            Log.d("Firestore", "Document successfully deleted!")
                                    )
                                    .addOnFailureListener(e ->
                                            Log.w("Firestore", "Error deleting document", e)
                                    );
                        }
                    } else {
                        Log.w("Firestore", "Error getting documents", task.getException());
                    }
                });
    }

// -------------------- / Events \ -----------------------------------------------------------------

    /**
     * Ensures all users registered in an event are fetched before calling onSuccessCallback.
     * Passes list of Users to the callback (ArrayList<User> as an Object class.)
     * Used for sampling users.
     * @param eventID eventID
     * @param status status
     * @param onSuccessCallback onSuccessCallback
     */
    public void loadAllUsersRegisteredInEvent(String eventID, RegistrantStatus status, DBManager.Obj2VoidCallback onSuccessCallback) {
        Query waitlistedUsersQuery = db.collection(eventsCollection)
                .document(eventID)
                .collection(eventRegistrantsCollection)
                .whereEqualTo(eventStatusKey, status);

        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Future<User>> futures = new ArrayList<>();

        waitlistedUsersQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<User> waitlistedUsers = Collections.synchronizedList(new ArrayList<>());

                for (QueryDocumentSnapshot registeredUserDoc : task.getResult()) {
                    String userID = registeredUserDoc.getId();

                    // submit task to the executor for each event
                    Future<User> future = executor.submit(() -> {
                        final CompletableFuture<User> userFuture = new CompletableFuture<>();

                        // fetch actual event asynchronously
                        getUser(userID, (userFetched) -> {
                            userFuture.complete((User) userFetched);
                        }, () -> {
                            Log.d("Firestore", "Could not get registered user " + userID);
                            userFuture.completeExceptionally(new Exception("Failed to fetch waitlisted user"));
                        });

                        return userFuture.get();
                    });

                    futures.add(future);
                }

                // wait for all users to be fetched
                executor.submit(() -> {
                    try {
                        for (Future<User> future : futures) {
                            User user = future.get();  // blocks until user is fetched
                            waitlistedUsers.add(user);
                        }
                    } catch (Exception e) {
                        Log.d("Firestore", "Error while fetching users: " + e.getMessage());
                    } finally {
                        synchronized (waitlistedUsers) {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                onSuccessCallback.run((List<User>) waitlistedUsers);
                                Log.d("Firestore", "waitlisted users callback was invoked with size: " + waitlistedUsers.size());
                            });
                        }
                        executor.shutdown();
                        try {
                            if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                                executor.shutdownNow();
                            }
                        } catch (InterruptedException e) {
                            executor.shutdownNow();
                        }
                    }
                });
            } else {
                Log.d("Firestore", "Error querying users from EventRegistrants: " + task.getException());
            }
        });
    }

    /**
     * Fetches all registered users of an Event who match the given status.
     * Note that onSuccessCallback is run for every user fetched.
     * This was adapted from Organizer Fragments.
     * @param eventID ID of event of interest
     * @param status  Status of interest
     * @param onSuccessCallback The loaded user object is passed as a parameter to this callback function.
     *
     */
    public void loadUsersRegisteredInEvent(String eventID, RegistrantStatus status, Obj2VoidCallback onSuccessCallback) {
        loadUsersRegisteredInEvent(eventID, status, "Firestore", onSuccessCallback);
    }

    /**
     * Runs a callback function on each user loaded for an event's registered user list.
     * Used for loading users to display in fragment.
     * @param eventID ID of event of interest
     * @param status Status of interest, only users of this status are loaded
     * @param loggingTag Tag used for logging
     * @param onSuccessCallback Function called on each user registered in the event
     */
    public void loadUsersRegisteredInEvent(String eventID, RegistrantStatus status, String loggingTag, Obj2VoidCallback onSuccessCallback) {

        Query registrantsMatchingStatus = db.collection(eventsCollection)
                .document(eventID)
                .collection(eventRegistrantsCollection)
                .whereEqualTo(eventStatusKey, status.toString());

        registrantsMatchingStatus.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // fetched documents for all registrants in Event
                        List<DocumentSnapshot> registrantDocs = task.getResult().getDocuments();
                        if (!registrantDocs.isEmpty()) {
                            for (DocumentSnapshot registrantDoc : registrantDocs) {
                                // run onSuccessCallback for each fetched user
                                String registrantID = registrantDoc.getId();
                                String registrantStatus = registrantDoc.getString(eventStatusKey);

                                // FETCH COMPLETE USER DETAILS
                                // userObj is GUARANTEED to return a non null object
                                // see getUser and userConverter
                                // userID will always be set as document ID
                                getUser(registrantID, (userObj) -> {
                                    User user = (User) userObj;
                                    user.setStatus(registrantStatus); // Set the status from EventRegistrants

                                    onSuccessCallback.run(user);

                                }, () -> Log.e(loggingTag, "Error fetching user from userID: " + registrantID));

                            }
                        } else {
                            Log.d(loggingTag, "No enrolled users found for eventID: " + eventID);
                        }
                    } else {
                        Log.e(loggingTag, "Error getting enrolled users", task.getException());
                    }
                });
    }

// ----------------- \ Facilities / --------------------------------------------------------

    /**
     * Adds or updates a facility document in the database.
     * Modifiers Facilities collection and if new facility, document of organizer in Users collection
     *
     * @param facility instance of Facility containing attributes to store in database
     */
    public void addUpdateFacility(Facility facility){
        String userID = DeviceManager.getDeviceId();
        performIfFieldPopulated(db.collection(usersCollection).document(userID),"facilityID",(facilityID)-> updateOldFacility(facilityID,facility),()->createNewFacility(facility));
    }

    /**
     * Updates the document in Facilities collection associated with the given facilityID to contain values held by given facility instance
     *
     * @param facilityID facilityID of facility to update
     * @param facility instance of Facility class containing attributes to update database with
     */
    public void updateOldFacility(Object facilityID,Facility facility){
        // update facilities collection
        DocumentReference docRefFacilities = db.collection(facilitiesCollection).document(facilityID.toString());
        updateField(docRefFacilities,"name",facility.getName());
        updateField(docRefFacilities,"address",facility.getAddress());
      }

    /**
     * Creates a new document in Facilities collection
     * Updates organizers user profile to contain facilityID field
     *
     * @param facility instance of Facility class containing attributes to upload to the database
     */
    public void createNewFacility(Facility facility){
        // create new document in facilities collection
        String facilityID = createIDForDocumentIn(facilitiesCollection);
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("facilityID",facilityID);
        facilityData.put("name", facility.getName());
        facilityData.put("address", facility.getAddress());
        facilityData.put("organizerID",facility.getOrganizerID());
        addUpdateDocument(facilitiesCollection,facilityID,facilityData);

        // update user in users collection
        updateField(db.collection(usersCollection).document(DeviceManager.getDeviceId()),"facilityID",facilityID);
    }

    /**
     * Attempts to retrieve an instance of Facility class for a given organizer's userID.
     * If the user is not an organizer (i.e. does not have a facilityID), does nothing.
     * If the organizer has an invalid facilityID, runs onFailureCallback.
     * If facility was successfully retrieved, runs onSuccessCallback.
     *
     * @param organizerID deviceID (aka userID) of organizer we are trying to retrieve facility info for
     * @param onSuccessCallback function to run on Facility instance if we successfully retrieve it
     * @param onFailureCallback function to run if we fail to retrieve the facility
     */
    public void getFacility(String organizerID, Obj2VoidCallback onSuccessCallback, Void2VoidCallback onFailureCallback){
        DocumentReference userDocRef = db.collection(usersCollection).document(organizerID);
        performIfFieldPopulated(userDocRef,"facilityID",(facilityID) -> facilityPopulatedCallback(facilityID, onSuccessCallback, onFailureCallback),()->{return;});
    }

    /**
     * Defines callbacks to be performed on the facilityID value
     * Attempts to generate instance of Facility class to perform populatedSuccessCallback on
     *
     * @param facilityID facilityID retrieved from populated facilityID field, castable to string
     * @param populatedSuccessCallback function that operates on an instance of Facility, called if successful in retrieving facility document and generating Facility instance
     * @param populatedFailureCallback function called if unable to generate Facility instance for given facilityID
     */
    public void facilityPopulatedCallback(Object facilityID, Obj2VoidCallback populatedSuccessCallback,Void2VoidCallback populatedFailureCallback){
        getDocumentAsObject(facilitiesCollection,facilityID.toString(),this::facilityConverter,populatedSuccessCallback,populatedFailureCallback);
    }

    /**
     * Given a document snapshot of a facility document, generates an instance of Facility
     *
     * @param document DocumentSnapshot of facility we want to create an instance of Facility for
     * @return returns a new Facility instance containing the values held in the document
     */
    private Object facilityConverter(DocumentSnapshot document){
        String name = document.getString("name");
        String adr = document.getString("address");
        String orgID = document.getString("organizerID");

        return new Facility(document.getId(),orgID,name,adr);
    }

    /**
     * Given a facilityID, deletes this facility.
     * Deletes all events that are held at this facility and removes it from the organizer's profile
     * Deletes the facility from the facilities collection.
     *
     * @param facilityID facilityID of the facility we want to delete
     */
    public void deleteFacility(String facilityID,Void2VoidCallback onSuccess){
        DocumentReference facilityDocRef = db.collection(facilitiesCollection).document(facilityID);

        facilityDocRef.get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        String organizerID = task.getResult().getString("organizerID");

                        if (organizerID == null){
                            return;
                        }

                        // delete all events at this facility
                        db.collection("Users").document(organizerID).collection(organizerEventsCollection).get()
                                .addOnCompleteListener(task2->{
                                    if(task2.isSuccessful()){
                                        for(QueryDocumentSnapshot qds : task2.getResult()){
                                            deleteEvent(qds.getId());
                                        }
                                    }
                                    // delete facility
                                    removeDocument(facilityDocRef);

                                    // remove facility ID from organizer
                                    updateField(db.collection(usersCollection).document(organizerID),"facilityID",null);
                                    onSuccess.run();
                                });
                    }
                }
                );
    }

    /**
     * Given the facility ID as a string delete the facility
     * @param facilityID ID of facility to be deleted
     */
    public void deleteFacility(String facilityID){
        deleteFacility(facilityID, ()->{});
    }

    /**
     * Given the facilityID string as an object, casts the object to string and deletes the facility
     *
     * @param facilityID Object containing facilityID
     */
    private void deleteFacility(Object facilityID) {
        deleteFacility(facilityID.toString());
    }

// -------------------- / Facilities \ -----------------------------------------------------------------


// ----------------- \ Abstracted Helpers / --------------------------------------------------------

    /**
     * Checks for existence of given document in given collection.
     * Performs foundCallback if it exists.
     * Performs unfoundCallback if it does not exist
     *
     * @param collectionName name of collection potentially containing the document of interest
     * @param documentID id of the document of interest
     * @param foundCallback function called if document found
     * @param unfoundCallback function called if document not found
     */
    private void checkExistenceOfDocument(String collectionName, String documentID,
                                          Void2VoidCallback foundCallback,
                                          Void2VoidCallback unfoundCallback) {
        DocumentReference docRef = db.collection(collectionName).document(documentID);
        String operationDescription = String.format("checkExistenceOfDocument for [%s,%s]", collectionName, documentID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
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

    public void doIfAdmin(String userID, Void2VoidCallback callback){
        DocumentReference userDoc = db.collection(usersCollection).document(userID);
        performIfFieldPopulated(userDoc,"admin",(fieldVal)->
        {Boolean isAdmin = (Boolean) fieldVal;
            if(isAdmin){
            callback.run();}
        }, ()->{});
    }

    public void doIfOrganizer(String userID, Void2VoidCallback callback){
        DocumentReference userDoc = db.collection(usersCollection).document(userID);
        performIfFieldPopulated(userDoc,"facilityID",(fieldVal)->{
            if(fieldVal == null){
                return;
            }
            if(fieldVal.toString().isBlank()){
                return;
            }
            callback.run();
            }, ()->{});
    }

    /**
     * Attempts to perform given function on field value if that field is populated
     *
     * @param docRef reference to the document containing the field of interest
     * @param fieldName name of the field of interest
     * @param populatedCallback callback called on the value of the field of interest if it is populated
     * @param unpopulatedCallback callback called if the field of interest is not populated
     */
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

    /**
     * Attempts to add/update via overwrite a document with given ID in a given collection with given data
     *
     * @param colRef collection where we want to add or update the document
     * @param documentID ID of document we want to add or update
     * @param data hashmap of data to be added or used to overwrite old data
     */
    private void addUpdateDocument(CollectionReference colRef, String documentID,
                                   Map<String, Object> data,Void2VoidCallback onSuccess){
        String operationDescription = String.format("addUpdateDocument for [%s,%s]", colRef.getId(), documentID);

        DocumentReference docRef = colRef.document(documentID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) { // we can retrieve (or create) the document
                docRef.set(data)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("Firestore", operationDescription + " succeeded");
                            onSuccess.run();
                        })
                        .addOnFailureListener(e -> {
                            Log.d("Firestore", operationDescription + "found/created doc but failed to set with error:" + e.getMessage());
                        });
            } else {
                // Failed to check document existence
                Log.d("Firestore", operationDescription + "failed with error: " + Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    /**
     * Attempts to add/update via overwrite a document with given ID in a given collection with given data
     *
     * @param collectionName name of collection where we want to add or update the document
     * @param documentID ID of document we want to add or update
     * @param data hashmap of data to be added or used to overwrite old data
     */
    private void addUpdateDocument(String collectionName, String documentID, Map<String,Object> data){
        addUpdateDocument(db.collection(collectionName),documentID,data,()->{});
    }

    private void addUpdateDocument(CollectionReference collection,String docID,Map<String,Object> data){
        addUpdateDocument(collection,docID,data,()->{});
    }

    /**
     * Generates unique ID for a document in a given collection
     *
     * @param colRef reference to collection we are generating IDs within
     * @return an ID string unique within the given collection
     */
    public String createIDForDocumentIn(CollectionReference colRef){
        return colRef.document().getId();
    }

    /**
     * Generates unique ID for a document in a given collection
     *
     * @param colName name of collection we are generating IDs within
     * @return an ID string unique within the given collection
     */
    public String createIDForDocumentIn(String colName){
        return createIDForDocumentIn(db.collection(colName));
    }

    /**
     * Attempts to convert a document to an object castable to the appropriate type and perform specified action on this object.
     *
     * @param collectionName name of collection containing document of interest
     * @param documentID documentID of document of interest
     * @param documentConverter function which converts from document to object of appropriate type
     * @param onSuccessCallback operation performed on object castable to appropriate type
     * @param onFailureCallback function called if unable to retrieve document
     */
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

    /**
     * Iterates over an entire collection of documents, performing the specified callback on each document
     *
     * @param collectionRef collection to iterate over all documents of
     * @param forEachDocumentCallback action to perform on each document
     */
    private void iterateOverCollection(CollectionReference collectionRef, ProcessQueryDocCallback forEachDocumentCallback){
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

    /**
     * Removes a given document from the database
     *
     * @param doc document to remove
     */
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

    /**
     * Updates a given field in a given document to a given value
     *
     * @param doc document in which we want to update a field
     * @param fieldName name of field which we want to update
     * @param newVal value which we want to set this field to
     */
    public void updateField(DocumentReference doc,String fieldName, Object newVal){
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

    /**
     * Returns the DocumentReference for a registrant in the event's list of registered users
     *
     * @param eventID eventID of desired event
     * @param registrantID userID of a desired registrant
     * @return DocumentReference to the document of this registrant in the event's sub-collection
     */
    private DocumentReference getDocOfRegistrantInEvent(String eventID, String registrantID){
        return db.collection(eventsCollection).document(eventID).collection(eventRegistrantsCollection).document(registrantID);
    }

    /**
     * Returns the DocumentReference for a given event under a given registrants list of registered events
     *
     * @param eventID eventID of desired event
     * @param registrantID userID of a desired registrant
     * @return DocumentReference to the document of this event in the user's sub-collection
     */
    private DocumentReference getDocOfEventInRegistrant(String eventID, String registrantID){
        return db.collection(usersCollection).document(registrantID).collection(registeredEventsCollection).document(eventID);
    }

// ----------------- \ Abstracted Helpers / --------------------------------------------------------
}