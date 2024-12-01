package com.example.pickme_nebula0.organizer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.organizer.adapters.SelectedAdapter;
import com.example.pickme_nebula0.user.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Fragment for displaying a list of selected entrants for a specific event.
 *
 * This fragment initializes a RecyclerView with a `SelectedAdapter` to display selected users
 * and dynamically loads data from the database based on the event ID and user status.
 *
 * @see SelectedAdapter
 * @see User
 * @see Fragment
 */
public class OrganizerSelectedFragment extends Fragment {
    private FirebaseFirestore db;
    private DBManager dbManager = new DBManager();
    ArrayList<User> selectedUsers = new ArrayList<User>();
    private SelectedAdapter adapter;
    String eventID;

    /**
     * Default constructor for `OrganizerSelectedFragment`.
     *
     * Required for fragment instantiation.
     */
    public OrganizerSelectedFragment() { }

    /**
     * Creates and initializes the view for the fragment.
     *
     * Sets up the RecyclerView with a `SelectedAdapter` to display selected entrants
     * and retrieves the event ID from the parent activity's intent.
     *
     * @param inflater           the LayoutInflater object used to inflate views
     * @param container          the parent view that this fragment's UI is attached to
     * @param savedInstanceState the previously saved state of the fragment, if any
     * @return the initialized view for the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_selected, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.selected_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        eventID = getActivity().getIntent().getStringExtra("eventID");
        adapter = new SelectedAdapter(getContext(), selectedUsers, eventID);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        return view;
    }

    /**
     * Invoked when the fragment becomes visible to the user.
     *
     * Calls `loadSelectedUsers` to populate the RecyclerView with updated data.
     */
    @Override
    public void onResume() {
        super.onResume();
        loadSelectedUsers();
    }

    /**
     * Loads the list of selected users from the database and updates the RecyclerView.
     *
     * Clears the current list of selected users, retrieves updated data based on the event ID
     * and registrant status, and dynamically updates the adapter to reflect the new data.
     *
     * This method fetches users with the `SELECTED` status from the database.
     */
    private void loadSelectedUsers() {
        selectedUsers.clear();
        dbManager.loadUsersRegisteredInEvent(eventID, DBManager.RegistrantStatus.SELECTED, "OrganizerSelectedFragment",
                (userObj) -> {
                    User user = (User) userObj;
                    selectedUsers.add(user);
                    adapter.notifyDataSetChanged();
                });
        adapter.notifyDataSetChanged();
    }
}