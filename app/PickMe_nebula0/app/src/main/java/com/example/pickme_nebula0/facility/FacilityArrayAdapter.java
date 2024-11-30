package com.example.pickme_nebula0.facility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pickme_nebula0.R;

import java.util.ArrayList;

/**
 * Array adapter for displaying facilities in list view
 */
public class FacilityArrayAdapter extends ArrayAdapter<Facility> {

    public FacilityArrayAdapter(Context context,int i ,ArrayList<Facility> facilities){
        super(context,i,facilities);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_facility, parent, false);
        }
        else {
            view = convertView;
        }

        Facility facility = getItem(position);
        TextView facilityName = view.findViewById(R.id.facility_name_text_view);

        facilityName.setText(facility.getName());

        return view;
    };
}
