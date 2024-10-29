package com.example.pickme_nebula0;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DBManager {
    private FirebaseFirestore db;

    public DBManager (){
        db = FirebaseFirestore.getInstance();
    }

    public interface Void2VoidCallback {
        void run();  // Define the method to be used as a callback
    }

    public interface User2VoidCallback{
        void run(User u);
    }


    /**
     * Checks for user with this device ID in the database
     * If no such user exists, launches UserInfoActivity
     * If user already exists, launches HomePageActivity
     */
    public void checkUserRegistration(String deviceID, Void2VoidCallback registeredCallback, Void2VoidCallback unregisteredVoid2VoidCallback){
        checkExistenceOfDocument("User",deviceID,registeredCallback, unregisteredVoid2VoidCallback);
    }

    public void addUpdateUserProfile(User user){
        DocumentReference docRef = db.collection("User").document(user.getUserID());

        // Check if the document exists
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) { // Create or overwrite user info
                DocumentSnapshot document = task.getResult();

                Map<String, Object> userData = new HashMap<>();
                userData.put("name", user.getName());
                userData.put("email", user.getEmail());
                userData.put("phone", user.getPhoneNumber());
                userData.put("profilePic", user.getProfilePicture());
                userData.put("notificationsEnabled",user.notifEnabled());

                docRef.set(userData)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("Firestore","User profile created/updated successfully.");
                        })
                        .addOnFailureListener(e -> {
                           Log.d("Firestore","Error creating/updated user profile: " + e.getMessage());
                        });
            } else {
                // Failed to check document existence
                Log.d("Firestore","Failed to create/update user profile: " + task.getException().getMessage());
            }
        });
    }

    /**
     * Performs onSuccessCallback() on User profile if successfully retrieved, else performs onFailureCallback
     *
     * @param deviceID deviceID of user of interest (key for db)
     * @param onSuccessCallback action performed on user if their profile is found in database
     * @param onFailureCallback action performed if user not found in database
     */
    public void getUser(String deviceID,User2VoidCallback onSuccessCallback, Void2VoidCallback onFailureCallback){
        DocumentReference docRef = db.collection("User").document(deviceID);
        String operationDescription = String.format("getUser for [%s]",deviceID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String name,email,phone;
                    Boolean notifEnabled;
                    if (document.exists()) {
                        Log.d("Firestore", operationDescription + " succeeded - user FOUND");
                        // TODO: create User Object with fields from DB
                        try {
                            name = document.getString("name");
                            email = document.getString("email");
                            phone = document.getString("phone");
                            notifEnabled = document.getBoolean("notificationsEnabled");
                            // TODO: add get profile picture URL
                        }
                        catch (Exception e){
                            Log.d("Firestore", operationDescription + " failed - user found, failed to extract required fields with error: " +e.getMessage());
                            return;
                        }

                        User userToReturn = new User(deviceID,name,email,phone,notifEnabled);
                        onSuccessCallback.run(userToReturn);
                    } else {
                        // Document does not exist
                        Log.d("Firestore", operationDescription + " failed - user NOT FOUND");
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

    private void checkExistenceOfDocument(String collectionName, String documentID, Void2VoidCallback foundCallback, Void2VoidCallback unfoundCallback){
        DocumentReference docRef = db.collection(collectionName).document(documentID);
        String operationDescription = String.format("checkExistenceOfDocument for [%s,%s]",collectionName,documentID);

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
}
