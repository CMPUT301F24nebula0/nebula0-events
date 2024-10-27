package com.example.pickme_nebula0;

import android.app.AlertDialog;
import android.content.Context;

public class SharedDialogue {

    /**
     * Creates a dialogue box displaying a warning message.
     *
     * @param message message to display, describes the issue
     * @param cntxt context of the activity in which to display the dialogue box.
     */
    public static void showInvalidDataAlert(String message, Context cntxt) {
        // Create an AlertDialog to show invalid data error
        AlertDialog.Builder builder = new AlertDialog.Builder(cntxt);
        builder.setTitle("Whoops!");
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
