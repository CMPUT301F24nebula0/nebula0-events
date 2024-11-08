package com.example.pickme_nebula0.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.event.Event;

import java.util.ArrayList;

public class UserArrayAdapter extends ArrayAdapter<User> {

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
        userName.setText(user.getName());

        return view;
    };
}
