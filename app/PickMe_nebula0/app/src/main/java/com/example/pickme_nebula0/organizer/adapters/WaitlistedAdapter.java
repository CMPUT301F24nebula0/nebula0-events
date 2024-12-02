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
 * RecyclerView adapter for displaying a list of waitlisted entrants.
 *
 * This adapter binds data for each waitlisted entrant to a custom layout (`item_user`)
 * and handles user interactions, such as clicking on an item to view user details.
 *
 * @see User
 * @see UserDetailActivity
 * @see RecyclerView.Adapter
 */
public class WaitlistedAdapter extends RecyclerView.Adapter<WaitlistedAdapter.WaitlistedViewHolder> {
    private final ArrayList<User> waitlistedUsers;
    private final Context context;
    private final String eventID;

    /**
     * Constructs a `WaitlistedAdapter` with the provided context, list of waitlisted users, and event ID.
     *
     * @param context         the context in which the adapter is used
     * @param waitlistedUsers the list of users who are waitlisted for the event
     * @param eventID         the ID of the event associated with the waitlisted users
     */
    public WaitlistedAdapter(Context context, ArrayList<User> waitlistedUsers, String eventID) {
        this.context = context;
        this.waitlistedUsers = waitlistedUsers;
        this.eventID = eventID;
    }

    /**
     * Creates a new `ViewHolder` for displaying a user item.
     *
     * Inflates the `item_user` layout and initializes the `WaitlistedViewHolder`.
     *
     * @param parent   the parent view group
     * @param viewTypes the view type of the new view
     * @return a new `WaitlistedViewHolder` instance
     */
    @NonNull
    @Override
    public WaitlistedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewTypes) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return new WaitlistedViewHolder(view);
    }

    /**
     * Binds data for a specific user to the provided `ViewHolder`.
     *
     * Sets the user's name, loads their profile picture from Firebase Storage, and sets up
     * a click listener to navigate to the `UserDetailActivity` with the user's ID, organizer flag, and event ID.
     *
     * @param holder   the `WaitlistedViewHolder` containing the UI components for the user item
     * @param position the position of the user in the list
     */
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

    /**
     * Returns the total number of waitlisted users in the list.
     *
     * @return the size of the `waitlistedUsers` list
     */
    @Override
    public int getItemCount() {
        return waitlistedUsers.size();
    }

    /**
     * ViewHolder for displaying user information in the RecyclerView.
     *
     * Contains UI components for displaying the user's name and profile picture.
     */
    static class WaitlistedViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        ImageView profileImageView;

        /**
         * Constructs a `WaitlistedViewHolder` and initializes its UI components.
         *
         * @param itemView the root view of the item layout
         */
        public WaitlistedViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.user_name_text_view);
            profileImageView = itemView.findViewById(R.id.imageView);
        }
    }
}
