package com.example.pickme_nebula0.organizer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.user.User;

import java.util.ArrayList;

public class EnrolledAdapter extends RecyclerView.Adapter<EnrolledAdapter.EnrolledViewHolder>{
    private ArrayList<User> enrolledUsers;
    private Context context;

    public EnrolledAdapter(Context context, ArrayList<User> enrolledUsers) {
        this.context = context;
        this.enrolledUsers = enrolledUsers;
    }

    @NonNull
    @Override
    public EnrolledViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewTypes) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new EnrolledViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EnrolledViewHolder holder, int position) {
        User user = enrolledUsers.get(position);
        holder.userNameTextView.setText(user.getName());
    }

    @Override
    public int getItemCount() {
        return enrolledUsers.size();
    }

    static class EnrolledViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;

        public EnrolledViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.user_name_text_view);
        }
    }
}
