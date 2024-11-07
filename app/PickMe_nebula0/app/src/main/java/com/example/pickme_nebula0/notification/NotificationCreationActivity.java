package com.example.pickme_nebula0.notification;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.SharedDialogue;
import com.example.pickme_nebula0.db.DBManager;

public class NotificationCreationActivity extends AppCompatActivity {
    DBManager dbManager;
    EditText subjectLineField;
    EditText messageField;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_create);

        dbManager = new DBManager();

        subjectLineField = findViewById(R.id.editText_nc_subjectLine);
        messageField = findViewById(R.id.editText_nc_message);

        String eventID = getIntent().getStringExtra("eventID");
        if (eventID == null || eventID.isEmpty()) {
            Toast.makeText(this, "Invalid Event ID.", Toast.LENGTH_SHORT).show();
            finish();
        }

        Button cancelBtn = findViewById(R.id.button_nc_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });

        Button msgAllBtn = findViewById(R.id.button_nc_notifAll);
        msgAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = subjectLineField.getText().toString();
                String msg = messageField.getText().toString();

                String warn = validateNotifInfo(subject,msg);
                if(!warn.isBlank()){
                    SharedDialogue.showInvalidDataAlert(warn,NotificationCreationActivity.this);
                    return;
                }

                dbManager.notifyAllEntrants(subject,msg,eventID);
                Toast.makeText(NotificationCreationActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();

                finish();
            }
        });





    }

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
