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

public class SelectedAdapter extends RecyclerView.Adapter<SelectedAdapter.SelectedViewHolder> {
    private final ArrayList<User> selectedUsers;
    private final Context context;

    public SelectedAdapter(Context context, ArrayList<User> selectedUsers) {
        this.context = context;
        this.selectedUsers = selectedUsers;
    }

    @NonNull
    @Override
    public SelectedViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewTypes) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new SelectedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedViewHolder holder, int position) {
        User user = selectedUsers.get(position);
        holder.userNameTextView.setText(user.getName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserDetailActivity.class);
            intent.putExtra("userID", user.getUserID());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return selectedUsers.size();
    }

    static class SelectedViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;

        public SelectedViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.user_name_text_view);
        }
    }
}