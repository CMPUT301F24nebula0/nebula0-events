package com.example.pickme_nebula0.event;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme_nebula0.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDetailActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView eventDetailsTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        final Button backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { getOnBackPressedDispatcher().onBackPressed(); }
        });

        eventDetailsTextView = findViewById(R.id.event_details_text_view);

        String eventID = getIntent().getStringExtra("eventID");
        db = FirebaseFirestore.getInstance();

        db.collection("Events").document(eventID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Event event = documentSnapshot.toObject(Event.class);
                if (event != null) {
                    eventDetailsTextView.setText("Event Name: " + event.getEventName());
                }
            }
        });
    }
}
