package com.example.pickme_nebula0;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pickme_nebula0.db.FBStorageManager;
import com.squareup.picasso.Picasso;

/**
 * SharedDialogue
 *
 * A utility class that provides shared functionality for creating and displaying dialog boxes.
 * This class helps minimize code duplication by centralizing the logic for reusable dialog-related features.
 *
 * Features:
 * - Displaying alerts for invalid data.
 * - Displaying event posters with optional fallback handling for missing images.
 *
 * Dependencies:
 * - Picasso for image loading and rendering.
 * - FBStorageManager for retrieving Firebase Storage resources.
 *
 * Author: Stephine Yearley
 */
public class SharedDialogue {

    /**
     * Displays an alert dialog with a warning message.
     *
     * This method is used to notify the user when invalid data is detected, providing a standardized
     * way to show error messages across activities.
     *
     * @param message The message describing the issue to display in the alert dialog.
     * @param context The context of the activity in which the alert dialog should be displayed.
     */
    public static void showInvalidDataAlert(String message, Context context) {
        // Create an AlertDialog to show invalid data error
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Whoops!");
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    /**
     * Displays a dialog with an event poster image fetched using the event ID.
     *
     * This version retrieves the poster's URI from Firebase Storage using the `FBStorageManager` class.
     * If the poster is unavailable, it falls back to a placeholder image and displays a message indicating
     * that no poster exists for the event.
     *
     * @param context The context in which the dialog should be displayed.
     * @param eventID The ID of the event for which the poster should be retrieved.
     */
    public static void displayPosterPopup(Context context, String eventID) {
        // Create a Dialog
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_poster_viewer);

        // Find the ImageView and Button
        ImageView imageView = dialog.findViewById(R.id.imageViewPoster);
        Button closeButton = dialog.findViewById(R.id.buttonPosterViewClose);
        TextView noPosterText = dialog.findViewById(R.id.textViewNoPoster);
        noPosterText.setText("");

        // Set the image resource
        FBStorageManager.retrievePosterUri(eventID,(uri)->{
            Picasso.get()
                    .load(uri)
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.no_poster_placeholder)
                    .error(R.drawable.error_image)
                    .into(imageView);
        },()->{
            Picasso.get()
                    .load(R.drawable.no_poster_placeholder)
                    .into(imageView);
            noPosterText.setText("this event has no poster");});

        // Set close button action
        closeButton.setOnClickListener(v -> dialog.dismiss());

        // Show the dialog
        dialog.show();
    }

    /**
     * Displays a dialog with an event poster image, using a directly provided URI.
     *
     * If the URI is null, the dialog will display a message indicating that no poster exists for the event.
     *
     * @param context The context in which the dialog should be displayed.
     * @param uri The URI of the event poster to display.
     */
    public static void displayPosterPopup(Context context, Uri uri) {
        // Create a Dialog
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_poster_viewer);

        // Find the ImageView and Button
        ImageView imageView = dialog.findViewById(R.id.imageViewPoster);
        Button closeButton = dialog.findViewById(R.id.buttonPosterViewClose);
        TextView noPosterText = dialog.findViewById(R.id.textViewNoPoster);
        noPosterText.setText("");

        if (uri != null){
            Picasso.get()
                    .load(uri)
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .into(imageView);
        } else{
            noPosterText.setText("This event has no poster.");
        }

        // Set close button action
        closeButton.setOnClickListener(v -> dialog.dismiss());

        // Show the dialog
        dialog.show();
    }
}
