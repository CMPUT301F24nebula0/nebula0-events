package com.example.pickme_nebula0.notification;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MessageViewActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ArrayList<Notification> notifs;
    private ListView notifsList;
    private NotificationArrayAdapter notifAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_view);

        db = FirebaseFirestore.getInstance();

        final Button backBtn = findViewById(R.id.button_mv_back);

        notifs = new ArrayList<Notification>();
        notifsList = findViewById(R.id.listView_mv_messages);
        notifAdapter = new NotificationArrayAdapter(this,R.id.element_list_notif, notifs);
        notifsList.setAdapter(notifAdapter);

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

    private void loadMessages(){
        notifs.clear();
        db.collection("Notifications").document(DeviceManager.getDeviceId()).collection("userNotifs")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Notification n = document.toObject(Notification.class);

                            notifs.add(n);
                        }
                        notifAdapter.notifyDataSetChanged();
                    }
                });
    }
}
