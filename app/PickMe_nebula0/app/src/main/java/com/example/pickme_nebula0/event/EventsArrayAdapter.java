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

/**
 * Array adapter used for updating and displaying instances of Event
 *  */
public class EventsArrayAdapter extends ArrayAdapter<Event> {

    public EventsArrayAdapter(Context context,int textViewResourceId, ArrayList<Event> events){
        super(context,textViewResourceId,events);
    }

    // convert each Event into a View
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_event, parent, false);
        }
        else {
            view = convertView;
        }

        Event event = getItem(position);
        TextView eventName = view.findViewById(R.id.event_name_text_view);
        eventName.setText(event.getEventName());

        return view;
    };
}
