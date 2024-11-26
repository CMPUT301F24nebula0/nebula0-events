package com.example.pickme_nebula0.qr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.event.Event;
import com.example.pickme_nebula0.user.User;

import java.util.ArrayList;

public class ImageAdapter extends ArrayAdapter<Event> {

    public ImageAdapter (Context context, int textViewResourceId, ArrayList<Event> events){
        super(context,textViewResourceId, events);

    }
    // convert each User into a View
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_image, parent, false);
        }
        else {
            view = convertView;
        }
        Event event= getItem(position);
        TextView eventName = view.findViewById(R.id.image_text_view);

        assert event != null;
        eventName.setText(event.getEventName());
        return view;
    };
}

