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
 * RecyclerView adapter for displaying a list of ongoing events.
 *
 * This adapter binds data for each ongoing event to a custom layout (`item_event`)
 * and handles user interactions, such as clicking on an item to view event details.
 *
 * @see Event
 * @see EventDetailActivity
 * @see RecyclerView.Adapter
 */
public class OngoingEventsAdapter extends RecyclerView.Adapter<OngoingEventsAdapter.OngoingEventViewHolder> {
    private ArrayList<Event> ongoingEvents;
    private Context context;

    /**
     * Constructs an `OngoingEventsAdapter` with the provided context and list of ongoing events.
     *
     * @param context       the context in which the adapter is used
     * @param ongoingEvents the list of ongoing events to display
     */
    public OngoingEventsAdapter(Context context, ArrayList<Event> ongoingEvents) {
        this.context = context;
        this.ongoingEvents = ongoingEvents;
    }

    /**
     * Creates a new `ViewHolder` for displaying an event item.
     *
     * Inflates the `item_event` layout and initializes the `OngoingEventViewHolder`.
     *
     * @param parent    the parent view group
     * @param viewType  the view type of the new view
     * @return a new `OngoingEventViewHolder` instance
     */
    @NonNull
    @Override
    public OngoingEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()). inflate(R.layout.item_event, parent, false);
        return new OngoingEventViewHolder(view);
    }

    /**
     * Binds data for a specific event to the provided `ViewHolder`.
     *
     * Sets the event's name in the corresponding `TextView` and attaches a click listener
     * to navigate to the `EventDetailActivity` with the event's ID.
     *
     * @param holder   the `OngoingEventViewHolder` containing the UI components for the event item
     * @param position the position of the event in the list
     */
    @Override
    public void onBindViewHolder(@NonNull OngoingEventViewHolder holder, int position) {
        Event event = ongoingEvents.get(position);
        holder.eventNameTextView.setText(event.getEventName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailActivity.class);
            intent.putExtra("eventID", event.getEventID());
            context.startActivity(intent);
        });
    }

    /**
     * Returns the total number of ongoing events in the list.
     *
     * @return the size of the `ongoingEvents` list
     */
    @Override
    public int getItemCount() {
        return ongoingEvents.size();
    }

    /**
     * ViewHolder for displaying event information in the RecyclerView.
     *
     * Contains a UI component for displaying the event's name.
     */
    static class OngoingEventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;

        /**
         * Constructs an `OngoingEventViewHolder` and initializes its UI components.
         *
         * @param itemView the root view of the item layout
         */
        public OngoingEventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.event_name_text_view);
        }
    }
}
