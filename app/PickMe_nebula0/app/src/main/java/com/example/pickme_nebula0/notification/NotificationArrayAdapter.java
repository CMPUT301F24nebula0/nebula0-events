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

/**
 * Array adapter used for updating and displaying instances of Notification
 *
 * @author Stephine Yearley
 */
public class NotificationArrayAdapter extends ArrayAdapter<Notification> {

    /**
     * Constructs a new `NotificationArrayAdapter` for displaying notifications.
     *
     * @param context           the context in which the adapter is used
     * @param textViewResourceId the resource ID of the text view to use
     * @param notifs            the list of notifications to be displayed
     */
    public NotificationArrayAdapter(Context context,int textViewResourceId, ArrayList<Notification> notifs){
        super(context,textViewResourceId,notifs);
    }

    /**
     * Provides a view for each notification in the ListView.
     *
     * Inflates a custom layout for displaying a notification if no reusable view is available.
     * Sets the title and message of the notification in the respective text views.
     *
     * @param position    the position of the notification in the list
     * @param convertView the recycled view to reuse, if available
     * @param parent      the parent view group that this view will be attached to
     * @return the view corresponding to the notification at the specified position
     */
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

        return view;
    };

}
