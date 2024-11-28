package com.example.pickme_nebula0.notification;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.event.EventDetailUserActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity that lets all users view the messages they've received.
 * These messages appear as android notification if the user has notifications enabled.
 *
 * @author Stephine Yearley
 */
public class MessageViewActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    public DBManager dbManager;
    private ArrayList<Notification> notifs;
    private ListView notifsList;
    private NotificationArrayAdapter notifAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_view);

        db = FirebaseFirestore.getInstance();

        dbManager = new DBManager();

        final Button backBtn = findViewById(R.id.button_mv_back);

        notifs = new ArrayList<Notification>();
        notifsList = findViewById(R.id.listView_mv_messages);
        notifAdapter = new NotificationArrayAdapter(this,R.id.element_list_notif, notifs);
        notifsList.setAdapter(notifAdapter);

        notifsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                // On-click, go the EventDetailUserActivity
                Notification clickedNotif = (Notification) adapterView.getItemAtPosition(pos);

                Intent intent = new Intent(MessageViewActivity.this, EventDetailUserActivity.class);
                intent.putExtra("eventID",clickedNotif.getEventID());
                startActivity(intent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    finish();
            }
        });


    }

    @Override
    public void onResume(){
        super.onResume();
        loadMessages();
    }

    /**
     * Updates the list of messages with all messages stored for this user in the data base
     */
    private void loadMessages(){
        notifs.clear();
        db.collection("Notifications").document(DeviceManager.getDeviceId()).collection("userNotifs")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // get a notification
                            Notification n = document.toObject(Notification.class);

                            if (dbManager.notificationShouldExist(n)) {
                                notifs.add(n);
                            }
                        }
                        notifAdapter.notifyDataSetChanged();
                    }
                });
    }
}
