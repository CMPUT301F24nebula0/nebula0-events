package com.example.pickme_nebula0.notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pickme_nebula0.R;

import java.util.ArrayList;

public class NotificationArrayAdapter extends ArrayAdapter<Notification> {

    public NotificationArrayAdapter(Context context,int textViewResourceId, ArrayList<Notification> notifs){
        super(context,textViewResourceId,notifs);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_notif_layout, parent, false);
        }
        else {
            view = convertView;
        }

        Notification notif = getItem(position);
        TextView notifTitle = view.findViewById(R.id.textView_notif_title);
        TextView notifBody = view.findViewById(R.id.textView_notif_body);

        notifTitle.setText(notif.getTitle());
        notifBody.setText(notif.getMessage());

        // TODO On click, go to even view if applicable


        return view;
    };

}
