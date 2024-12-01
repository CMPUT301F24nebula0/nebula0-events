package com.example.pickme_nebula0.organizer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.organizer.adapters.WaitlistedAdapter;
import com.example.pickme_nebula0.user.User;
import com.example.pickme_nebula0.db.DBManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Fragment for displaying a list of waitlisted entrants for a specific event.
 *
 * This fragment initializes a RecyclerView with a `WaitlistedAdapter` to display waitlisted users
 * and dynamically loads data from the database based on the event ID and user status.
 *
 * @see WaitlistedAdapter
 * @see User
 * @see Fragment
 */
public class OrganizerWaitlistedFragment extends Fragment {
    private FirebaseFirestore db;
    private DBManager dbManager = new DBManager();
    ArrayList<User> waitlistedUsers = new ArrayList<User>();
    private WaitlistedAdapter adapter;
    String eventID;

    /**
     * Constructs a `OrganizerWaitlistedFragment` with the given event ID.
     *
     * @param eventID the ID of the event for which waitlisted users will be displayed
     */
    public OrganizerWaitlistedFragment(String eventID) {
        this.eventID = eventID;
    }

    /**
     * Creates and initializes the view for the fragment.
     *
     * Sets up the RecyclerView with a `WaitlistedAdapter` to display waitlisted entrants.
     *
     * @param inflater           the LayoutInflater object used to inflate views
     * @param container          the parent view that this fragment's UI is attached to
     * @param savedInstanceState the previously saved state of the fragment, if any
     * @return the initialized view for the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_waitlisted, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.waitlisted_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new WaitlistedAdapter(getContext(), waitlistedUsers, this.eventID);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        return view;
    }

    /**
     * Invoked when the fragment becomes visible to the user.
     *
     * Calls `loadWaitlistedUsers` to populate the RecyclerView with updated data.
     */
    @Override
    public void onResume() {
        super.onResume();
        loadWaitlistedUsers();
    }

    /**
     * Loads the list of waitlisted users from the database and updates the RecyclerView.
     *
     * Clears the current list of waitlisted users, retrieves updated data based on the event ID
     * and registrant status, and dynamically updates the adapter to reflect the new data.
     *
     * This method fetches users with the `WAITLISTED` status from the database.
     */
    private void loadWaitlistedUsers() {
        waitlistedUsers.clear();
        dbManager.loadUsersRegisteredInEvent(eventID, DBManager.RegistrantStatus.WAITLISTED, "OrganizerWaitlistedFragment",
                (userObj) -> {
                    User user = (User) userObj;
                    waitlistedUsers.add(user);
                    adapter.notifyDataSetChanged();
                });

    }
}