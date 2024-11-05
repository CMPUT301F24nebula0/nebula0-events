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

import java.util.ArrayList;

public class PastEventsAdapter extends RecyclerView.Adapter<PastEventsAdapter.PastEventViewHolder> {
    private ArrayList<Event> pastEvents;
    private Context context;

    public PastEventsAdapter(Context context, ArrayList<Event> pastEvents) {
        this.context = context;
        this.pastEvents = pastEvents;
    }

    @NonNull
    @Override
    public PastEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_past_event, parent, false);
        return new PastEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PastEventViewHolder holder, int position) {
        Event event = pastEvents.get(position);
        holder.eventNameTextView.setText(event.getEventName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailActivity.class);
            intent.putExtra("eventName", event.getEventName());
            context.startActivity(intent);
        });
    }

    @Override public int getItemCount() {
        return pastEvents.size();
    }

    static class PastEventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;

        public PastEventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.event_name_text_view);
        }
    }
}
