package com.example.pickme_nebula0.notification;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.SharedDialogue;
import com.example.pickme_nebula0.db.DBManager;

/**
 * Activity for organizers to create and send notifications/messages to entrants of a given event.
 *
 * This activity allows organizers to send messages to all entrants or filter them by specific statuses
 * (e.g., waitlisted, confirmed). Includes input validation and user feedback via Toast messages.
 *
 * @see Notification
 * @see MessageViewActivity
 * @see SharedDialogue
 *
 * @author Stephine Yearley
 */
public class NotificationCreationActivity extends AppCompatActivity {
    DBManager dbManager;
    EditText subjectLineField;
    EditText messageField;
    String eventID;

    /**
     * Initializes the activity, sets up input fields and buttons, and configures click listeners.
     *
     * Listens for button clicks to send notifications to entrants based on their registration status
     * or to cancel and exit the activity. Retrieves the `eventID` from the intent and validates it.
     *
     * @param savedInstanceState the previously saved state of the activity, if any
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_create);

        dbManager = new DBManager();

        subjectLineField = findViewById(R.id.editText_nc_subjectLine);
        messageField = findViewById(R.id.editText_nc_message);

        Button msgAllBtn = findViewById(R.id.button_nc_notifAll);
        Button msgWaitlistedBtn = findViewById(R.id.button_nc_notifWaitlist);
        Button msgConfirmedBtn = findViewById(R.id.button_nc_notifConfirmed);
        Button msgUnconfirmedBtn = findViewById(R.id.button_nc_notifUnconfirmed);
        Button msgCancelledBtn = findViewById(R.id.button_nc_notfiCancelled);
        Button cancelBtn = findViewById(R.id.button_nc_cancel);

        eventID = getIntent().getStringExtra("eventID");
        if (eventID == null || eventID.isEmpty()) {
            Toast.makeText(this, "Invalid Event ID.", Toast.LENGTH_SHORT).show();
            finish();
        }

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });

        msgAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFieldsAndAttemptNotification(null)){
                    finish();
                }
            }
        });

        msgUnconfirmedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getFieldsAndAttemptNotification(DBManager.RegistrantStatus.SELECTED)){
                    finish();
                }

            }
        });

        msgConfirmedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getFieldsAndAttemptNotification(DBManager.RegistrantStatus.CONFIRMED)){
                    finish();
                }
            }
        });

        msgCancelledBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getFieldsAndAttemptNotification(DBManager.RegistrantStatus.CANCELLED)){
                    finish();

                }
            }
        });

        msgWaitlistedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getFieldsAndAttemptNotification(DBManager.RegistrantStatus.WAITLISTED)){
                    finish();
                }
            }
        });
    }

    /**
     * Attempts to send the entered message to all entrants of given status.
     * Retrieves the entered values in the subject line and message fields.
     * If valid, attempts to send message and returns true. If invalid,
     * shows invalid data alert and returns false.
     *
     * @param status status of entrants to send message to, null if all entrants
     * @return True if we attempted to send message, False if fields were invalid
     */
    private boolean getFieldsAndAttemptNotification(DBManager.RegistrantStatus status){
        String subject = subjectLineField.getText().toString();
        String msg = messageField.getText().toString();

        String warn = validateNotifInfo(subject,msg);
        if(!warn.isBlank()){
            SharedDialogue.showInvalidDataAlert(warn,NotificationCreationActivity.this);
            return false;
        }

        if(status == null){
            dbManager.notifyAllEntrants(subject,msg,eventID);
            Toast.makeText(NotificationCreationActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();

        } else{
            dbManager.notifyEntrantsOfStatus(subject,msg,eventID,status);
            Toast.makeText(NotificationCreationActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    /**
     * Validate given subject line and message body.
     *
     * @param subject subject line or title of the message/notification
     * @param message body of the message/notification
     * @return a string containing warnings of invalidities, blank if valid
     */
    public static String validateNotifInfo(String subject, String message){
        String warn = "";

        if(subject.isBlank()){
            warn += "subject line cannot be blank\n";
        }
        if(message.isBlank()){
            warn += "message cannot be blank";
        }

        return  warn;
    }

}
