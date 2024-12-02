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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * RecyclerView adapter for displaying a list of selected entrants.
 *
 * This adapter binds data for each selected entrant to a custom layout (`item_user`)
 * and handles user interactions, such as clicking on an item to view user details.
 *
 * @see User
 * @see UserDetailActivity
 * @see RecyclerView.Adapter
 */
public class SelectedAdapter extends RecyclerView.Adapter<SelectedAdapter.SelectedViewHolder> {
    private final ArrayList<User> selectedUsers;
    private final Context context;
    private final String eventID;

    /**
     * Constructs a `SelectedAdapter` with the provided context, list of selected users, and event ID.
     *
     * @param context       the context in which the adapter is used
     * @param selectedUsers the list of users who are selected for the event
     * @param eventID       the ID of the event associated with the selected users
     */
    public SelectedAdapter(Context context, ArrayList<User> selectedUsers, String eventID) {
        this.context = context;
        this.selectedUsers = selectedUsers;
        this.eventID = eventID;
    }

    /**
     * Creates a new `ViewHolder` for displaying a user item.
     *
     * Inflates the `item_user` layout and initializes the `SelectedViewHolder`.
     *
     * @param parent   the parent view group
     * @param viewTypes the view type of the new view
     * @return a new `SelectedViewHolder` instance
     */
    @NonNull
    @Override
    public SelectedViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewTypes) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new SelectedViewHolder(view);
    }

    /**
     * Binds data for a specific user to the provided `ViewHolder`.
     *
     * Sets the user's name, loads their profile picture from Firebase Storage, and sets up
     * a click listener to navigate to the `UserDetailActivity` with the user's ID and event ID.
     *
     * @param holder   the `SelectedViewHolder` containing the UI components for the user item
     * @param position the position of the user in the list
     */
    @Override
    public void onBindViewHolder(@NonNull SelectedViewHolder holder, int position) {
        User user = selectedUsers.get(position);
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
            intent.putExtra("eventID", eventID);
            context.startActivity(intent);
        });
    }

    /**
     * Returns the total number of selected users in the list.
     *
     * @return the size of the `selectedUsers` list
     */
    @Override
    public int getItemCount() {
        return selectedUsers.size();
    }

    /**
     * ViewHolder for displaying user information in the RecyclerView.
     *
     * Contains UI components for displaying the user's name and profile picture.
     */
    static class SelectedViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        ImageView profileImageView;

        /**
         * Constructs a `SelectedViewHolder` and initializes its UI components.
         *
         * @param itemView the root view of the item layout
         */
        public SelectedViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.user_name_text_view);
            profileImageView = itemView.findViewById(R.id.imageView);

        }
    }
}