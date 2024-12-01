package com.example.pickme_nebula0.organizer.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.event.Event;
import com.example.pickme_nebula0.event.EventDetailActivity;

import java.util.ArrayList;

/**
 * RecyclerView adapter for displaying a list of past events.
 *
 * This adapter binds data for each past event to a custom layout (`item_event`)
 * and handles user interactions, such as clicking on an item to view event details.
 *
 * @see Event
 * @see EventDetailActivity
 * @see RecyclerView.Adapter
 */
public class PastEventsAdapter extends RecyclerView.Adapter<PastEventsAdapter.PastEventViewHolder> {
    private ArrayList<Event> pastEvents;
    private Context context;
    /**
     * Constructs a `PastEventsAdapter` with the provided context and list of past events.
     *
     * @param context    the context in which the adapter is used
     * @param pastEvents the list of past events to display
     */
    public PastEventsAdapter(Context context, ArrayList<Event> pastEvents) {
        this.context = context;
        this.pastEvents = pastEvents;
    }

    /**
     * Creates a new `ViewHolder` for displaying an event item.
     *
     * Inflates the `item_event` layout and initializes the `PastEventViewHolder`.
     *
     * @param parent   the parent view group
     * @param viewType the view type of the new view
     * @return a new `PastEventViewHolder` instance
     */
    @NonNull
    @Override
    public PastEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new PastEventViewHolder(view);
    }

    /**
     * Binds data for a specific event to the provided `ViewHolder`.
     *
     * Sets the event's name in the corresponding `TextView` and attaches a click listener
     * to navigate to the `EventDetailActivity` with the event's ID.
     *
     * @param holder   the `PastEventViewHolder` containing the UI components for the event item
     * @param position the position of the event in the list
     */
    @Override
    public void onBindViewHolder(@NonNull PastEventViewHolder holder, int position) {
        Event event = pastEvents.get(position);
        holder.eventNameTextView.setText(event.getEventName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailActivity.class);
            intent.putExtra("eventID", event.getEventID());
            context.startActivity(intent);
        });
    }

    /**
     * Returns the total number of past events in the list.
     *
     * @return the size of the `pastEvents` list
     */
    @Override
    public int getItemCount() {
        return pastEvents.size();
    }

    /**
     * ViewHolder for displaying event information in the RecyclerView.
     *
     * Contains a UI component for displaying the event's name.
     */
    static class PastEventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;

        /**
         * Constructs a `PastEventViewHolder` and initializes its UI components.
         *
         * @param itemView the root view of the item layout
         */
        public PastEventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.event_name_text_view);
        }
    }
}
