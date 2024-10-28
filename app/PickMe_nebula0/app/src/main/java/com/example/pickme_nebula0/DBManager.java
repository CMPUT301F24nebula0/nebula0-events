package com.example.pickme_nebula0;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DBManager {
    private FirebaseFirestore db;

    public DBManager (){
        db = FirebaseFirestore.getInstance();
    }

    public interface Callback {
        void run();  // Define the method to be used as a callback
    }


    /**
     * Checks for user with this device ID in the database
     * If no such user exists, launches UserInfoActivity
     * If user already exists, launches HomePageActivity
     */
    public void checkUserRegistration(String deviceID,Callback registeredCallback,Callback unregisteredCallback){
        checkExistenceOfDocument("User",deviceID,registeredCallback,unregisteredCallback);
    }

    private void checkExistenceOfDocument(String collectionName,String documentID, Callback foundCallback,Callback unfoundCallback){
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
                }
            }
        });
    }
}
