package com.example.pickme_nebula0;

import android.app.AlertDialog;
import android.content.Context;

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
}
