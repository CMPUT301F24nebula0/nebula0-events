package com.example.pickme_nebula0.event;

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
 * EventsArrayAdapter
 *
 * A custom adapter for displaying `Event` objects in a list or grid view.
 *
 * Key Features:
 * - Converts an `Event` object into a `View` for display.
 * - Utilizes the `item_event` layout to render event details.
 *
 * Dependencies:
 * - `Event` class: Represents the event objects.
 * - `item_event.xml`: The layout resource for rendering each event.
 *
 * Constructor Parameters:
 * - `context`: The context in which the adapter is being used.
 * - `textViewResourceId`: The resource ID of the layout file.
 * - `events`: The list of `Event` objects to display.
 *
 * Usage:
 * - Initialize the adapter with a list of events and set it to a ListView or GridView.
 */
public class EventsArrayAdapter extends ArrayAdapter<Event> {
    /**
     * Constructor for the `EventsArrayAdapter`.
     *
     * @param context The context in which the adapter is being used.
     * @param textViewResourceId The resource ID of the layout file for each item.
     * @param events The list of `Event` objects to display in the adapter.
     */
    public EventsArrayAdapter(Context context,int textViewResourceId, ArrayList<Event> events){
        super(context,textViewResourceId,events);
    }

    /**
     * Converts an `Event` object into a `View` for display in the list or grid.
     *
     * - Inflates the `item_event` layout if the `convertView` is null.
     * - Sets the event name in the corresponding `TextView`.
     *
     * @param position The position of the item within the adapter's data set.
     * @param convertView The recycled view to populate, or null if a new view needs to be created.
     * @param parent The parent view group that this view will be attached to.
     * @return The `View` corresponding to the data at the specified position.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView == null ? LayoutInflater.from(getContext()).inflate(R.layout.item_event, parent, false) : convertView;
        Event event = getItem(position);
        TextView eventName = view.findViewById(R.id.event_name_text_view);
        eventName.setText(event.getEventName());

        return view;
    };
}
