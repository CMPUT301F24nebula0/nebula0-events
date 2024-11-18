package com.example.pickme_nebula0.db;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FBStorageManager {

    private static StorageReference storageRef = FirebaseStorage.getInstance().getReference();
}
