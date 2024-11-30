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
 * Array adapter for displaying list of users
 */
public class UserArrayAdapter extends ArrayAdapter<User> {

    private ImageView profilePicImgView;

    public UserArrayAdapter(Context context, int textViewResourceId, ArrayList<User> events){
        super(context,textViewResourceId,events);
    }

    // convert each User into a View
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
