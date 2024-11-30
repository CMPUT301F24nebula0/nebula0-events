package com.example.pickme_nebula0.db;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.SharedDialogue;
import com.example.pickme_nebula0.admin.activities.EventDetailAdminActivity;
import com.example.pickme_nebula0.event.Event;
import com.example.pickme_nebula0.event.EventManager;
import com.example.pickme_nebula0.user.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;

public class FBStorageManager {

    public static final String firebaseStorageImageBucket = "gs://pickme-c2fb3.firebasestorage.app";
    public static final String eventPosterFieldName = "poster";
    public static final String eventPosterImageDirectory = "eventPosters/";

    public static final String profilePicFieldName = "profilePic";
    public static final String profilePicImageDirectory = "profilePics/";

    private static final String storage_tag = "FBStorageManager";

    public interface Uri2VoidCallback {
        void run(Uri uri);
    }

    private static final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private static final DBManager dbManager = new DBManager();

    public static void uploadProfilePic(Uri uri,String userID,Context context){
        StorageReference newFileRef = storageRef.child("profilePics/"+userID);

        Toast failureToast = Toast.makeText(context,"Could not update profile picture",Toast.LENGTH_LONG);
        UploadTask uploadTask = newFileRef.putFile(uri);
        uploadTask.addOnFailureListener(e -> {
                    failureToast.show();
                })
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(context, "File uploaded :)", Toast.LENGTH_SHORT).show();
                    newFileRef.getDownloadUrl().addOnSuccessListener(accessUri -> {
                        // Add to db - TODO turn this into a DB manager function
                        DBManager dbManager = new DBManager();
                        DocumentReference userDocRef = dbManager.db.collection("Users").document(userID);
                        dbManager.updateField(userDocRef,"profilePic",accessUri);
                    }).addOnFailureListener(exception -> {
                        failureToast.show();
                    });
                });
    }

    public static void uploadPoster(Uri uri,String eventID, Context context){
        StorageReference newFileRef = storageRef.child("eventPosters/"+eventID);

        Toast failureToast = Toast.makeText(context,"Could not upload poster",Toast.LENGTH_LONG);
        UploadTask uploadTask = newFileRef.putFile(uri);
        uploadTask.addOnFailureListener(e -> {
                    failureToast.show();
                })
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(context, "File uploaded :)", Toast.LENGTH_SHORT).show();
                    newFileRef.getDownloadUrl().addOnSuccessListener(accessUri -> {
                        // Add to db - TODO turn this into a DB manager function
                        DBManager dbManager = new DBManager();
                        DocumentReference userDocRef = dbManager.db.collection("Events").document(eventID);
                        dbManager.updateField(userDocRef,"poster",accessUri);
                    }).addOnFailureListener(exception -> {
                        failureToast.show();
                    });
                });
    }

    public static void uploadProfilePic(String filePath, String userID, Context context){
        Uri uri = Uri.fromFile(new File(filePath));
        uploadProfilePic(uri,userID,context);
    }

    public static void retrieveProfilePicUri(String userID, Uri2VoidCallback onSuccessCallback, DBManager.Void2VoidCallback onFailureCallback){
            dbManager.getUser(userID, userObj -> {
                User user = (User) userObj;
                if (user.getProfilePic() != null){
                    Uri uri = Uri.parse(user.getProfilePic());
                    onSuccessCallback.run(uri);
                }else{
                    onFailureCallback.run();
                }

            }, onFailureCallback);
    }

    public static void retrievePosterUri(String eventID, Uri2VoidCallback onSuccessCallback, DBManager.Void2VoidCallback onFailureCallback){
        dbManager.getEvent(eventID, eventObj -> {
            Event event = (Event) eventObj;
            if (event.getPoster() != null){
                Uri uri = Uri.parse(event.getPoster());
                onSuccessCallback.run(uri);
            }else{
                onFailureCallback.run();
            }

        }, onFailureCallback);
    }


    /**
     * Removes event poster from Firebase Storage.
     * Updating event docs should be done from another function,
     * and is implemented in EventManager.
     *
     * @param eventID
     * @param onSuccessCallback
     * @param onFailureCallback
     * @see EventManager
     */
    public static void deleteEventPosterFromStorage(String eventID, DBManager.Void2VoidCallback onSuccessCallback, DBManager.Void2VoidCallback onFailureCallback) {

        eventPosterExists(eventID, (uri) -> {
            // assumes lastPathSegment will return the storage path
            String storagePath = uri.getLastPathSegment();
            if (storagePath == null || storagePath.isEmpty()) {
                onFailureCallback.run();
            }

            Log.d(storage_tag, "Delete event poster at the following path: "+storagePath);

            StorageReference imageRef = storageRef.child(storagePath);
            imageRef.delete().addOnSuccessListener(aVoid -> {
                onSuccessCallback.run();
            }).addOnFailureListener(exception -> {
                onFailureCallback.run();
            });

        }, (error_message) -> {onFailureCallback.run();});
    }

    // utility functions

    /**
     * Assumes event poster path is here: eventPosters/{eventID}
     * There is no method for checking if a StorageReference exists,
     * so a workaround is to try downloading the URL
     * and assume a failure means the reference doesn't exist.
     * @param eventID
     * @param posterFoundCallback
     * @param posterNotFoundCallback
     */
    public static void eventPosterExists(String eventID, Uri2VoidCallback posterFoundCallback, EventManager.String2VoidCallback posterNotFoundCallback) {

        // TODO: replace path with consistent formatting
        String storagePath = "eventPosters/"+eventID;
        imageExists(storagePath, posterFoundCallback, posterNotFoundCallback);
    }

    public static void profilePicExists(String userID, Uri2VoidCallback picFoundCallback, EventManager.String2VoidCallback picNotFoundCallback) {

        String storagePath = "profilePics/" + userID;
        imageExists(storagePath, picFoundCallback, picNotFoundCallback);
    }

    public static void imageExists(String storagePath, Uri2VoidCallback posterFoundCallback, EventManager.String2VoidCallback posterNotFoundCallback) {

        // TODO: replace path with consistent formatting
        StorageReference imageRef = storageRef.child(storagePath);

        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Log.d(storage_tag, uri.toString());
                posterFoundCallback.run(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d(storage_tag, "no uri found at "+storagePath);
                posterNotFoundCallback.run("Could not get download URL");
            }
        });
    }

    /**
     * To standardize format of image paths, for uploading user profile pic
     * and event poster.
     * Adds .jpg file extension and names image based on eventID or userID.
     *
     * @param imagesDirectory
     * @param objectID
     * @return
     */
    public static String formatImagePath(String imagesDirectory, String objectID) {
        assert imagesDirectory.endsWith("/");
        return String.format("%s%s.jpg", imagesDirectory, objectID);
    }

    }
