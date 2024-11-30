package com.example.pickme_nebula0.qr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.event.Event;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Array adapter for displaying images
 */
public class ImageAdapter extends ArrayAdapter<Event> {

    public ImageAdapter (Context context, int textViewResourceId, ArrayList<Event> events){
        super(context,textViewResourceId, events);

    }
    // convert each User into a View
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_image, parent, false);
        }
        else {
            view = convertView;
        }

        Event event= getItem(position);
        TextView imgURI = view.findViewById(R.id.image_text_view);
        ImageView imgView = view.findViewById(R.id.image_view_of_event);

        // display if image cannot be loaded
        imgURI.setVisibility(View.GONE);
        assert event != null;

        loadImage(event.getPoster(), imgView, () -> {
            imgURI.setVisibility(View.GONE);
        }, () -> {
            imgURI.setText(event.getEventName());
            imgURI.setVisibility(View.VISIBLE);
        });

        return view;
    };


    /**
     * Loads an image into the view
     * @param img_uri the uri for the image we want to load
     * @param imgView image view we want to load the image into
     * @param onSuccessCallback function to call if successful
     * @param onFailureCallback function to call if unsuccessful
     */
    private void loadImage(String img_uri, ImageView imgView, DBManager.Void2VoidCallback onSuccessCallback, DBManager.Void2VoidCallback onFailureCallback) {
        if (img_uri != null && !img_uri.isEmpty()) {
            Picasso.get()
                    .load(img_uri)
                    .placeholder(R.drawable.ic_profile_placeholder) // placeholder while loading
                    .error(R.drawable.ic_profile_placeholder)   // image to show on error
                    .into(imgView, new Callback() {
                        @Override
                        public void onSuccess() {
                            onSuccessCallback.run();
                        }

                        @Override
                        public void onError(Exception e) {
                            onFailureCallback.run();
                        }
                    });
        } else {
            onFailureCallback.run();
        }
    }
}

