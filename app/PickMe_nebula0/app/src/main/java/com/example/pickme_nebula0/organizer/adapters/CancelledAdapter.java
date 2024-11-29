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
import com.example.pickme_nebula0.user.User;
import com.example.pickme_nebula0.user.activities.UserDetailActivity;

import java.util.ArrayList;

/**
 * Array Adapter for viewing cancelled entrants
 */
public class CancelledAdapter extends RecyclerView.Adapter<CancelledAdapter.CancelledViewHolder> {
    private ArrayList<User> cancelledUsers;
    private Context context;

    public CancelledAdapter(Context context, ArrayList<User> cancelledUsers) {
        this.context = context;
        this.cancelledUsers = cancelledUsers;
    }

    @NonNull
    @Override
    public CancelledViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewTypes) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new CancelledViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CancelledViewHolder holder, int position) {
        User user = cancelledUsers.get(position);
        holder.userNameTextView.setText(user.getName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserDetailActivity.class);
            intent.putExtra("userID", user.getUserID());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return cancelledUsers.size();
    }

    static class CancelledViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;

        public CancelledViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.user_name_text_view);
        }
    }
}