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
 * Class for minimizing code duplication by providing dialogue box creation shared by multiple activities
 *
 * @author Stephine Yearley
 */
public class SharedDialogue {

    /**
     * Creates a dialogue box displaying a warning message.
     *
     * @param message message to display, describes the issue
     * @param context context of the activity in which to display the dialogue box.
     */
    public static void showInvalidDataAlert(String message, Context context) {
        // Create an AlertDialog to show invalid data error
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Whoops!");
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

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
