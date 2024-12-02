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
 * Array adapter for displaying QR code data.
 *
 * This adapter is responsible for converting a list of `Event` objects into
 * views that display their QR code data or event name.
 *
 * @see Event
 */
public class QRcodeAdapter extends ArrayAdapter<Event> {

    /**
     * Constructs a new QRcodeAdapter.
     *
     * @param context The current context, used to inflate the layout file.
     * @param textViewResourceId The resource ID for a layout file containing a TextView to use when instantiating views.
     * @param events The list of `Event` objects to be displayed.
     */
    public QRcodeAdapter(Context context, int textViewResourceId, ArrayList<Event> events){
        super(context,textViewResourceId, events);

    }

    /**
     * Converts an `Event` object into a view for display in the adapter.
     *
     * This method inflates a layout for each `Event` and populates it with the
     * event's QR code data or its name if no QR code data is available.
     *
     * @param position The position of the `Event` in the list.
     * @param convertView The recycled view to populate, or null if a new view needs to be created.
     * @param parent The parent view group that this view will be attached to.
     * @return A view corresponding to the data at the specified position.
     */
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

