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
import com.example.pickme_nebula0.organizer.adapters.EnrolledAdapter;
import com.example.pickme_nebula0.user.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Fragment for displaying a list of confirmed (enrolled) entrants for a specific event.
 *
 * This fragment initializes a RecyclerView with an `EnrolledAdapter` to display
 * confirmed users and dynamically loads data from the database.
 *
 * @see EnrolledAdapter
 * @see User
 * @see Fragment
 */
public class OrganizerConfirmedFragment extends Fragment {
    private DBManager dbManager = new DBManager();
    ArrayList<User> enrolledUsers = new ArrayList<User>();
    private EnrolledAdapter adapter;
    String eventID;

    /**
     * Default constructor for `OrganizerConfirmedFragment`.
     *
     * Required for fragment instantiation.
     */
    public OrganizerConfirmedFragment() { }

    /**
     * Creates and initializes the view for the fragment.
     *
     * Sets up the RecyclerView with an `EnrolledAdapter` to display confirmed entrants
     * and retrieves the event ID from the parent activity's intent.
     *
     * @param inflater           the LayoutInflater object used to inflate views
     * @param container          the parent view that this fragment's UI is attached to
     * @param savedInstanceState the previously saved state of the fragment, if any
     * @return the initialized view for the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_enrolled, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.enrolled_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventID = getActivity().getIntent().getStringExtra("eventID");
        adapter = new EnrolledAdapter(getContext(), enrolledUsers);
        recyclerView.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        return view;
    }

    /**
     * Invoked when the fragment becomes visible to the user.
     *
     * Calls `loadEnrolledUsers` to populate the RecyclerView with updated data.
     */
    @Override
    public void onResume() {
        super.onResume();
        loadEnrolledUsers();
    }

    /**
     * Loads the list of confirmed (enrolled) users from the database and updates the RecyclerView.
     *
     * Clears the current list of enrolled users, retrieves updated data based on the event ID
     * and registrant status, and dynamically updates the adapter to reflect the new data.
     */
    @SuppressLint("NotifyDataSetChanged")
    private void loadEnrolledUsers() {
        enrolledUsers.clear();
        dbManager.loadUsersRegisteredInEvent(eventID, DBManager.RegistrantStatus.CONFIRMED, "OrganizerEnrolledFragment",
                (userObj) -> {
                    User user = (User) userObj;
                    enrolledUsers.add(user);
                    adapter.notifyDataSetChanged();
                });

    }
}