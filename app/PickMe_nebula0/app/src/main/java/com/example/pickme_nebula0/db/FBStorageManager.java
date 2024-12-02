package com.example.pickme_nebula0.db;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.pickme_nebula0.event.Event;
import com.example.pickme_nebula0.event.EventManager;
import com.example.pickme_nebula0.user.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Class for managing accesses and modifications of Firebase Storage (images)
 *
 * @author Stephine
 * @author Evan
 */
public class FBStorageManager {

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

    /**
     * Uploads a profile picture for a user to Firebase Storage and updates the user's profilePic field in Firestore.
     *
     * @param uri     the Uri of the image file to upload
     * @param userID  the ID of the user whose profile picture is being uploaded
     * @param context the context from which the method is called, used for displaying Toast messages
     */
    public static void uploadProfilePic(Uri uri,String userID,Context context){
        StorageReference newFileRef = storageRef.child(profilePicImageDirectory+userID);

        Toast failureToast = Toast.makeText(context,"Could not update profile picture",Toast.LENGTH_LONG);
        UploadTask uploadTask = newFileRef.putFile(uri);
        uploadTask.addOnFailureListener(e -> {
                    failureToast.show();
                })
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(context, "File Uploaded", Toast.LENGTH_SHORT).show();
                    newFileRef.getDownloadUrl().addOnSuccessListener(accessUri -> {

                        DBManager dbManager = new DBManager();
                        DocumentReference userDocRef = dbManager.db.collection("Users").document(userID);
                        dbManager.updateField(userDocRef,profilePicFieldName,accessUri);
                    }).addOnFailureListener(exception -> {
                        failureToast.show();
                    });
                });
    }

    /**
     * Uploads a poster image for an event to Firebase Storage and updates the event's poster field in Firestore.
     *
     * @param uri      the Uri of the image file to upload
     * @param eventID  the ID of the event whose poster is being uploaded
     * @param context  the context from which the method is called, used for displaying Toast messages
     */
    public static void uploadPoster(Uri uri,String eventID, Context context){
        StorageReference newFileRef = storageRef.child(eventPosterImageDirectory+eventID);

        Toast failureToast = Toast.makeText(context,"Could not upload poster",Toast.LENGTH_LONG);
        UploadTask uploadTask = newFileRef.putFile(uri);
        uploadTask.addOnFailureListener(e -> {
                    failureToast.show();
                })
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(context, "File Uploaded", Toast.LENGTH_SHORT).show();
                    newFileRef.getDownloadUrl().addOnSuccessListener(accessUri -> {

                        DBManager dbManager = new DBManager();
                        DocumentReference userDocRef = dbManager.db.collection("Events").document(eventID);
                        dbManager.updateField(userDocRef,"poster",accessUri);
                    }).addOnFailureListener(exception -> {
                        failureToast.show();
                    });
                });
    }

    /**
     * Retrieves the Uri of a user's profile picture from Firestore.
     *
     * @param userID             the ID of the user whose profile picture Uri is being retrieved
     * @param onSuccessCallback  the callback function to run with the Uri upon successful retrieval
     * @param onFailureCallback  the callback function to run if the retrieval fails
     */
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

    /**
     * Retrieves the Uri of an event's poster from Firestore.
     *
     * @param eventID            the ID of the event whose poster Uri is being retrieved
     * @param onSuccessCallback  the callback function to run with the Uri upon successful retrieval
     * @param onFailureCallback  the callback function to run if the retrieval fails
     */
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
     * @param eventID ID of event we want to delete poster of
     * @param onSuccessCallback function to run on success
     * @param onFailureCallback function to run on failure
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

    /**
     * Assumes event poster path is here: eventPosters/{eventID}
     * There is no method for checking if a StorageReference exists,
     * so a workaround is to try downloading the URL
     * and assume a failure means the reference doesn't exist.
     * @param eventID ID of event we are checking for poster of
     * @param posterFoundCallback function run if poster found, takes Uri of poster
     * @param posterNotFoundCallback function run if poster not found, takes string
     */
    public static void eventPosterExists(String eventID, Uri2VoidCallback posterFoundCallback, EventManager.String2VoidCallback posterNotFoundCallback) {
        String storagePath = eventPosterImageDirectory+eventID;
        imageExists(storagePath, posterFoundCallback, posterNotFoundCallback);
    }


    /**
     * Checks if an image exists in Firebase Storage at the specified path.
     *
     * @param storagePath             the path in Firebase Storage where the image is expected to be located
     * @param posterFoundCallback     the function to run with the Uri if the image is found
     * @param posterNotFoundCallback  the function to run with an error message if the image is not found
     */
    public static void imageExists(String storagePath, Uri2VoidCallback posterFoundCallback, EventManager.String2VoidCallback posterNotFoundCallback) {

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

    }