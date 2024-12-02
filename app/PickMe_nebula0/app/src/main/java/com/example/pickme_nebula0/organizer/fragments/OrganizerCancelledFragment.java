package com.example.pickme_nebula0.organizer.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.organizer.adapters.CancelledAdapter;
import com.example.pickme_nebula0.user.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Fragment for displaying a list of cancelled entrants for a specific event.
 *
 * This fragment initializes a RecyclerView with a `CancelledAdapter` to display
 * cancelled users and dynamically loads data from the database.
 *
 * @see CancelledAdapter
 * @see User
 * @see Fragment
 */
public class OrganizerCancelledFragment extends Fragment {
    private final DBManager dbManager = new DBManager();
    ArrayList<User> cancelledUsers = new ArrayList<User>();
    private CancelledAdapter adapter;
    String eventID;

    /**
     * Default constructor for `OrganizerCancelledFragment`.
     *
     * Required for fragment instantiation.
     */
    public OrganizerCancelledFragment() { }

    /**
     * Creates and initializes the view for the fragment.
     *
     * Sets up the RecyclerView with a `CancelledAdapter` and retrieves the event ID
     * from the parent activity's intent.
     *
     * @param inflater           the LayoutInflater object used to inflate views
     * @param container          the parent view that this fragment's UI is attached to
     * @param savedInstanceState the previously saved state of the fragment, if any
     * @return the initialized view for the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_cancelled, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.cancelled_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CancelledAdapter(getContext(), cancelledUsers);
        recyclerView.setAdapter(adapter);
        eventID = getActivity().getIntent().getStringExtra("eventID");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        return view;
    }

    /**
     * Invoked when the fragment becomes visible to the user.
     *
     * Calls `loadCancelledUsers` to populate the RecyclerView with updated data.
     */
    @Override
    public void onResume() {
        super.onResume();
        loadCancelledUsers();
    }

    /**
     * Loads the list of cancelled users from the database and updates the RecyclerView.
     *
     * Clears the current list of cancelled users, retrieves updated data based on the event ID
     * and registrant status, and dynamically updates the adapter to reflect the new data.
     */
    @SuppressLint("NotifyDataSetChanged")
    private void loadCancelledUsers() {
        cancelledUsers.clear();
        dbManager.loadUsersRegisteredInEvent(eventID, DBManager.RegistrantStatus.CANCELLED, "OrganizerCancelledFragment",
                (userObj) -> {
                    User user = (User) userObj;
                    cancelledUsers.add(user);
                    adapter.notifyDataSetChanged();
                });

    }
}