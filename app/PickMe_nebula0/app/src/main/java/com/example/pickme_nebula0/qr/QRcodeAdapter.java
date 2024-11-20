package com.example.pickme_nebula0.qr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.user.User;

import java.util.ArrayList;

public class QRcodeAdapter {

    public UserArrayAdapter(Context context, int textViewResourceId, ArrayList<String> QRcodes){
        super(context,textViewResourceId,QRcodes);

    }
    // convert each User into a View
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_QRcode, parent, false);
        }
        else {
            view = convertView;
        }

        User user = getItem(position);
        TextView userName = view.findViewById(R.id.QRcode_image);
        userName.setText(user.getName());

        return view;
    };
}

