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
 * ArrayAdapter for displaying event images.
 *
 * This adapter is used to bind a list of `Event` objects to a view, displaying each event's
 * poster image and optionally the event name if the image fails to load.
 *
 * @see Event
 * @see ArrayAdapter
 */
public class ImageAdapter extends ArrayAdapter<Event> {

    /**
     * Constructs an `ImageAdapter` for displaying event images.
     *
     * @param context the context in which the adapter is used
     * @param textViewResourceId the resource ID for a layout file containing a TextView
     * @param events the list of `Event` objects to display
     */
    public ImageAdapter (Context context, int textViewResourceId, ArrayList<Event> events){
        super(context,textViewResourceId, events);

    }

    /**
     * Converts each `Event` into a `View` for display in a list or grid.
     *
     * This method inflates the layout for each item, loads the event's poster image into an `ImageView`,
     * and displays the event name if the image fails to load.
     *
     * @param position the position of the item within the adapter's data set
     * @param convertView the old view to reuse, if possible
     * @param parent the parent view that this view will be attached to
     * @return a `View` representing the event
     */
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
     * Loads an image from a URI into an `ImageView`.
     *
     * Uses the Picasso library to load the image. If the image is successfully loaded,
     * the provided success callback is executed. If the image fails to load,
     * the failure callback is executed.
     *
     * @param img_uri the URI of the image to load
     * @param imgView the `ImageView` into which the image should be loaded
     * @param onSuccessCallback the callback to execute if the image loads successfully
     * @param onFailureCallback the callback to execute if the image fails to load
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

