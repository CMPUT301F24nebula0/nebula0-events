package com.example.pickme_nebula0.user.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.PickVisualMediaRequest;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.SharedDialogue;
import com.example.pickme_nebula0.db.FBStorageManager;
import com.example.pickme_nebula0.user.User;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * UserInfoActivity
 *
 * This activity allows users to register, update their profile information, or navigate to manage their facility information.
 *
 * Key Features:
 * - Register or update user profile information (name, email, phone number, notification preferences).
 * - Manage profile picture (upload, auto-generate, or remove).
 * - Navigate to FacilityInfoActivity for additional management options.
 * - Includes validation for user inputs.
 *
 * Author: Stephine Yearley
 *
 * @see User
 * @see DBManager
 * @see FBStorageManager
 */
public class UserInfoActivity extends AppCompatActivity {
    private String deviceID;
    private DBManager dbManager;

    // UI Elements
    TextView headerTextView;
    EditText nameField;
    EditText emailField;
    EditText phoneField;
    CheckBox enableNotifBox;
    Button confirmButton;
    Button cancelButton;
    Button facilityButton;
    ImageView profilePicImageView;
    Button changeProfilePicButton;
    Button removeProfilePicButton;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    Uri profilePicUri = null;

    private void conditionalPicUpload(String name, boolean newUser){
        if (profilePicUri != null){
            FBStorageManager.uploadProfilePic(profilePicUri,deviceID,UserInfoActivity.this);
        } else if (newUser) {
            FBStorageManager.uploadProfilePic(genProfilePic(this,name),deviceID,UserInfoActivity.this);
        }
    }

    /**
     * Initializes the activity.
     *
     * - Sets up UI components and event listeners for user actions.
     * - Handles different layouts for new users and returning users.
     * - Registers a media picker for profile picture selection.
     *
     * @param savedInstanceState The saved instance state for restoring the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        dbManager = new DBManager();

        deviceID = DeviceManager.getDeviceId();
        boolean newUser = getIntent().getBooleanExtra("newUser",false);

        // Registers a photo picker activity launcher in single-select mode.
        pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        profilePicUri = uri;
                        renderProfilePicture(uri);
                    }
                });

        headerTextView = findViewById(R.id.textViewUserInfoHeader);
        nameField = findViewById(R.id.editTextUserInfoName);
        emailField = findViewById(R.id.editTextUserInfoEmail);
        phoneField = findViewById(R.id.editTextUserInfoPhone);
        enableNotifBox = findViewById(R.id.checkBoxUserInfoNotifEnabled);
        confirmButton= findViewById(R.id.buttonUserInfoConfirm);
        cancelButton = findViewById(R.id.buttonUserInfoCancel);
        facilityButton = findViewById(R.id.buttonUserInfoManageFacility);
        profilePicImageView = findViewById(R.id.imageViewProfilePic);
        changeProfilePicButton = findViewById(R.id.buttonChangePicture);
        removeProfilePicButton = findViewById(R.id.buttonRemovePicture);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do NOT update db with new info
                finish();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameField.getText().toString();
                String email = emailField.getText().toString();
                String phone = phoneField.getText().toString().replaceAll("[^0-9]", "");
                boolean notifEnabled = enableNotifBox.isChecked();

                String warning = validateUserInfo(name,email,phone);
                if (!warning.isBlank()){
                    // user had entered invalid data, warn them about it and don't update DB
                    SharedDialogue.showInvalidDataAlert(warning,UserInfoActivity.this);
                    return;
                }

                User u = new User(deviceID,name,email,phone,notifEnabled);
                dbManager.addUpdateUserProfile(u,()->{
                    conditionalPicUpload(name,newUser);});
                finish();
            }
        });

        facilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this, FacilityInfoActivity.class);
                startActivity(intent);
                finish();
            }
        });

        changeProfilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        removeProfilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAutoProfilePic();
            }
        });

        if (newUser){ // new user's require a slightly different screen layout
            headerTextView.setText(R.string.user_info_header_first_time);
            cancelButton.setVisibility(View.GONE);
            facilityButton.setVisibility(View.GONE);
            enableNotifBox.setChecked(true);
        } else{ // returning users need to see their stored info, read it from DB
            headerTextView.setText(R.string.user_info_header_returning);
            dbManager.getUser(deviceID,this::populateFieldsFromDB,this::failedToPopulateFieldsFromDB);
        }

    }

    /**
     * Callback to populate the screen's fields
     *
     * @param user instance of user containing data used to populate the fields on screen
     */
    private void populateFieldsFromDB(Object user){
        User castedUser = (User) user;
        nameField.setText(castedUser.getName());
        emailField.setText(castedUser.getEmail());
        if(castedUser.getPhone() != null){
            phoneField.setText(castedUser.getPhone());
        }
        enableNotifBox.setChecked(castedUser.getNotificationsEnabled());
        FBStorageManager.retrieveProfilePicUri(deviceID,this::renderProfilePicture,()->{});
    }

    /**
     *  Callback called if we fail to populate ths user info screen for an existing user
     */
    private void failedToPopulateFieldsFromDB(){
        SharedDialogue.showInvalidDataAlert("Fields could not be populated from DB, the data shown may not match what is in the DB",UserInfoActivity.this);
    }

    /**
     * Checks whether given fields values are valid.
     *
     * @param name name to validate
     * @param email email to validate
     * @param phone phone number to validate
     * @return a blank string if all is valid, else returns a warning string containing information of invalidities
     */
    public static String validateUserInfo(String name, String email, String phone){
        String warning = "";
        // verify name is non-numeric
        if (name.matches(".*\\d.*")){
            warning = "Name cannot contain numbers";
        }
        // verify email address has proper format
        if (!email.matches(".+@.+\\..+")){
            String errString = "Email must be in format like name@mail.com";
            if (warning.isBlank()){
                warning = errString;
            } else{
                warning += "\n\n" + errString;
            }
        }
        if(name.isBlank() || email.isBlank()){
            String errString = "Must provide both Name and Email";
            if (warning.isBlank()){
                warning = errString;
            } else{
                warning += "\n\n" + errString;
            }
        }
        // verify that, if provided, is 10 numbers (e.g. xxx-xxx-xxxx)
        if (!phone.isEmpty() && phone.length() != 10){
            String errString = "Phone number must be 10 digits";
            if (warning.isBlank()){
                warning = errString;
            } else{
                warning += "\n\n" + errString;
            }
        }

        return warning;
    }

    /**
     * Renders the profile picture on screen
     * @param uri
     */
    public void renderProfilePicture(Uri uri){
        try {
            Picasso.get()
                    .load(uri)
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .into(profilePicImageView);
        } catch(Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Opens the image picker so user can choose profile picture
     */
    private void openImagePicker() {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    /**
     * Sets the local profile pic to an autogenerated one, profile pic doesn't get updated until user hits "confirm" button
     */
    private void setAutoProfilePic(){
        Toast.makeText(this,"Replacing image with auto",Toast.LENGTH_LONG).show();

        Uri generatedImageUri = genProfilePic(this,nameField.getText().toString());
        if (generatedImageUri == null){
            Toast.makeText(this,"Failed to auto generate profile picture",Toast.LENGTH_SHORT).show();
            return;
        }
        profilePicUri = generatedImageUri;
        renderProfilePicture(generatedImageUri);
    }


    /**
     * Generates image uri from bitmap image. Based on code from: https://stackoverflow.com/a/26060004
     * @param context
     * @param inImage image to create a uri for
     * @return Uri of image
     */
    public static Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private static ArrayList<Integer> colors = new ArrayList<>(Arrays.asList(
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.YELLOW,
            Color.CYAN,
            Color.LTGRAY,
            Color.MAGENTA
    ));

    /**
     * Generates an profile picture with a random background color and the user's first initial
     * based on code generated from OpenAI's chatGPT4 based on prompt "in android studio using java, I want to create a bitmap image of the first letter of a given string on a background of a random color"
     * @param context
     * @param name
     * @return
     */
    public static Uri genProfilePic(Context context,String name){
        if (name == null || name.isEmpty()) return null;

        String firstLetter = name.substring(0, 1).toUpperCase();

        int color = colors.get(new Random().nextInt(colors.size()));

        // Set up paint
        Paint paint = new Paint();
        paint.setTextSize(100); // Adjust the size as needed
        paint.setColor(Color.WHITE); // Text color
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);

        // Measure the size of the letter
        Rect bounds = new Rect();
        paint.getTextBounds(firstLetter, 0, firstLetter.length(), bounds);

        // Create a square bitmap
        int size = Math.max(bounds.width(), bounds.height()) + 50; // Add some padding
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        // Draw the background and letter
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(color); // Fill the background with the random color
        canvas.drawText(firstLetter, size / 2f, (size / 2f) - bounds.exactCenterY(), paint);

        return getImageUri(context,bitmap);
    }


}
