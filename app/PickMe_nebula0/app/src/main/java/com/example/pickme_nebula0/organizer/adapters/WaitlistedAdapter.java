package com.example.pickme_nebula0.organizer.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.user.User;
import com.example.pickme_nebula0.user.activities.UserDetailActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Array adapter for viewing waitlisted entrants
 */
public class WaitlistedAdapter extends RecyclerView.Adapter<WaitlistedAdapter.WaitlistedViewHolder> {
    private final ArrayList<User> waitlistedUsers;
    private final Context context;
    private final String eventID;

    public WaitlistedAdapter(Context context, ArrayList<User> waitlistedUsers, String eventID) {
        this.context = context;
        this.waitlistedUsers = waitlistedUsers;
        this.eventID = eventID;
    }

    @NonNull
    @Override
    public WaitlistedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewTypes) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return new WaitlistedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WaitlistedViewHolder holder, int position) {
        User user = waitlistedUsers.get(position);
        Log.d("User", user.toString());

        holder.userNameTextView.setText(user.getName());

        String userID = user.getUserID();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("profilePics/" + userID);

        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get()
                    .load(uri)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.error_image)
                    .into(holder.profileImageView);
        }).addOnFailureListener(exception -> {
            // Handle any errors
            holder.profileImageView.setImageResource(R.drawable.error_image);
            Log.e("ImageLoadError", "Failed to load image for userID: " + userID, exception);
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserDetailActivity.class);
            intent.putExtra("userID", user.getUserID());
            intent.putExtra("organizer", true);
            intent.putExtra("eventID", eventID);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return waitlistedUsers.size();
    }

    static class WaitlistedViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        ImageView profileImageView;

        public WaitlistedViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.user_name_text_view);
            profileImageView = itemView.findViewById(R.id.imageView);
        }
    }
}
