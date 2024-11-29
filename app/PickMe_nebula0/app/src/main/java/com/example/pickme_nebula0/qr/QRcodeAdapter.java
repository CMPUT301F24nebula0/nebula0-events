package com.example.pickme_nebula0.qr;

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

/**
 * Array adapter for displaying QR code
 */
public class QRcodeAdapter extends ArrayAdapter<Event> {

    public QRcodeAdapter(Context context, int textViewResourceId, ArrayList<Event> events){
        super(context,textViewResourceId, events);

    }
    // convert each User into a View
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_qrcode, parent, false);
        }
        else {
            view = convertView;
        }
        Event event= getItem(position);
        String hashcode=event.getQrCodeData();
        String eventName = event.getEventName();
        TextView QRcode = view.findViewById(R.id.qrcodehash_text_view);
        if (hashcode=="null"){
            QRcode.setText("null");
        }
        else {
            QRcode.setText(eventName);
        }
        return view;
    };
}

