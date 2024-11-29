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
 * Array adapter for viewing ongoing events
 */
public class OngoingEventsAdapter extends RecyclerView.Adapter<OngoingEventsAdapter.OngoingEventViewHolder> {
    private ArrayList<Event> ongoingEvents;
    private Context context;

    public OngoingEventsAdapter(Context context, ArrayList<Event> ongoingEvents) {
        this.context = context;
        this.ongoingEvents = ongoingEvents;
    }

    @NonNull
    @Override
    public OngoingEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()). inflate(R.layout.item_event, parent, false);
        return new OngoingEventViewHolder(view);
    }

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

    @Override
    public int getItemCount() {
        return ongoingEvents.size();
    }

    static class OngoingEventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;

        public OngoingEventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.event_name_text_view);
        }
    }
}
