package com.example.pickme_nebula0.db;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.Toast;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.SharedDialogue;
import com.example.pickme_nebula0.user.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;

public class FBStorageManager {
    public interface Uri2VoidCallback {
        void run(Uri uri);
    }

    private static final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private static final DBManager dbManager = new DBManager();

    public static void uploadProfilePic(String filePath, String userID, Context context){
        Uri uri = Uri.fromFile(new File(filePath));
        StorageReference newFileRef = storageRef.child("profilePics/"+userID);

        Toast failureToast = Toast.makeText(context,"Could not update profile picture",Toast.LENGTH_LONG);
        UploadTask uploadTask = newFileRef.putFile(uri);
                uploadTask.addOnFailureListener(e -> {
                            SharedDialogue.showInvalidDataAlert("Could not update profile picture",context);
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

    public static void retrieveProfilePicUri(String userID, Uri2VoidCallback onSuccessCallback, DBManager.Void2VoidCallback onFailureCallback){
            dbManager.getUser(DeviceManager.getDeviceId(), userObj -> {
                User user = (User) userObj;
                Uri uri = Uri.parse(user.getProfilePic());
                onSuccessCallback.run(uri);
            }, onFailureCallback);
    }


}
