package com.example.pickme_nebula0.user;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.db.FBStorageManager;
import com.example.pickme_nebula0.event.Event;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

/**
 * UserArrayAdapter
 *
 * Custom ArrayAdapter for displaying a list of `User` objects in a `ListView`.
 *
 * Key Features:
 * - Maps each `User` object to a corresponding view (`item_user`) in the list.
 * - Dynamically loads and displays user profile pictures using Picasso.
 * - Provides fallback options with placeholder images for missing or invalid profile pictures.
 *
 * Dependencies:
 * - Picasso for image loading and rendering.
 * - `FBStorageManager` for handling profile picture URIs.
 *
 * Use Cases:
 * - Populating a list view with user details in apps that manage users.
 *
 * @see User
 * @see com.squareup.picasso.Picasso
 */
public class UserArrayAdapter extends ArrayAdapter<User> {

    private ImageView profilePicImgView;

    /**
     * Constructs a `UserArrayAdapter` to handle and display a list of `User` objects.
     *
     * @param context The context of the activity or fragment where the adapter is used.
     * @param textViewResourceId The resource ID of the layout to use for each item in the list.
     * @param events The list of `User` objects to be displayed.
     */
    public UserArrayAdapter(Context context, int textViewResourceId, ArrayList<User> events){
        super(context,textViewResourceId,events);
    }

    /**
     * Converts a `User` object into a `View` to be displayed in the `ListView`.
     *
     * - Inflates the `item_user` layout if no reusable view is provided (`convertView` is null).
     * - Sets the user's name in the `TextView` component.
     * - Dynamically loads and displays the user's profile picture using Picasso.
     *
     * @param position The position of the item in the data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent view group that this view will be attached to.
     * @return A `View` representing the `User` at the specified position.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }
        else {
            view = convertView;
        }

        User user = getItem(position);

        TextView userName = view.findViewById(R.id.user_name_text_view);

        profilePicImgView = view.findViewById(R.id.imageView);

        userName.setText(user.getName());

        if(user.getProfilePic() != null){
            renderProfilePicture(Uri.parse(user.getProfilePic()));
        }

        return view;
    };

    /**
     * Renders a user's profile picture into the `ImageView` component.
     *
     * - Uses Picasso to load the image from a given URI.
     * - Applies a placeholder image while loading.
     * - Displays an error image if the URI is invalid or fails to load.
     *
     * @param uri The URI of the profile picture to load.
     */
    public void renderProfilePicture(Uri uri){
        try {
            Picasso.get()
                    .load(uri)
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .into(profilePicImgView);
        } catch(Exception e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
