package com.example.pickme_nebula0.event;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.example.pickme_nebula0.R;

import java.util.ArrayList;

public class WaitlistEventAdapter extends ArrayAdapter<Event> {

    private CardView eventCardView;
    private LinearLayout expandableLayout;

    public WaitlistEventAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_list_event, parent, false);
        }
        else {
            view = convertView;
        }

        Event event = getItem(position);
        TextView eventName = view.findViewById(R.id.event_name);
        TextView eventDescription = view.findViewById(R.id.event_description);

        eventName.setText(event.getEventName());
        eventDescription.setText(event.getEventDescription());

        Log.d("ListEventCreation", "Calling ListEventFragment");

        // Get references to the CardView and the hidden LinearLayout
        this.eventCardView = view.findViewById(R.id.event_card_view);
        this.expandableLayout = view.findViewById(R.id.event_hidden_view);

        // Set an OnClickListener on the CardView
        Log.d("ListEventFragment", "Setting OnClickListener for CardView");
        eventCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ListEventFragment", "CardView clicked");
                // Toggle the visibility of the expandable content
                if (expandableLayout.getVisibility() == View.GONE) {
                    // Show the expandable content
                    expandableLayout.setVisibility(View.VISIBLE);
                } else {
                    // Hide the expandable content
                    expandableLayout.setVisibility(View.GONE);
                }
            }
        });

        return view;
    };
}
