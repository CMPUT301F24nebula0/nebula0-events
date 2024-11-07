package com.example.pickme_nebula0.organizer.fragments;

import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OrganizerWaitlistedFragment extends Fragment {
    private FirebaseFirestore db;
    private DBManager dbManager = new DBManager();
    ArrayList<User> waitlistedUsers = new ArrayList<User>();
    private WaitlistedAdapter adapter;
    String eventID;

    public OrganizerWaitlistedFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_waitlisted, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.waitlisted_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new WaitlistedAdapter(getContext(), waitlistedUsers);
        recyclerView.setAdapter(adapter);
        eventID = getActivity().getIntent().getStringExtra("eventID");

        db = FirebaseFirestore.getInstance();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWaitlistedUsers();
    }

    private void loadWaitlistedUsers() {
        waitlistedUsers.clear();
        dbManager.loadUsersRegisteredInEvent(eventID, DBManager.RegistrantStatus.WAITLISTED, "OrganizerWaitlistedFragment",
                (userObj) -> {
                    User user = (User) userObj;
                    waitlistedUsers.add(user);
                    adapter.notifyDataSetChanged();
                });
//
//        db.collection(DBManager.eventsCollection)
//                .document(eventID)
//                .collection(DBManager.eventRegistrantsCollection)
//                .whereEqualTo(DBManager.eventStatusKey, DBManager.RegistrantStatus.getStatus(DBManager.RegistrantStatus.WAITLISTED))
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        List<DocumentSnapshot> registrantDocs = task.getResult().getDocuments();
//                        if (!registrantDocs.isEmpty()) {
//                            // Iterate through each registrant document
//                            for (DocumentSnapshot registrantDoc : registrantDocs) {
//                                String registrantID = registrantDoc.getId();
//                                String status = registrantDoc.getString(DBManager.eventStatusKey);
//
//                                // Fetch the complete User details
//                                db.collection(DBManager.usersCollection)
//                                        .document(registrantID)
//                                        .get()
//                                        .addOnSuccessListener(userDoc -> {
//                                            if (userDoc.exists()) {
//                                                User user = userDoc.toObject(User.class);
//                                                if (user != null) {
//                                                    // **Manually set userID from document ID**
//                                                    user.setUserID(userDoc.getId());
//
//                                                    user.setStatus(status); // Set the status from EventRegistrants
//                                                    waitlistedUsers.add(user);
//                                                    adapter.notifyDataSetChanged();
//
//                                                    Log.d("OrganizerWaitlistedFragment", "Fetched User: " + user.toString());
//                                                }
//                                            } else {
//                                                Log.w("OrganizerWaitlistedFragment", "No such user with ID: " + registrantID);
//                                            }
//                                        })
//                                        .addOnFailureListener(e -> {
//                                            Log.e("OrganizerWaitlistedFragment", "Error fetching user with ID: " + registrantID, e);
//                                        });
//                            }
//                        } else {
//                            Log.d("OrganizerWaitlistedFragment", "No waitlisted users found for eventID: " + eventID);
//                        }
//                    } else {
//                        Log.e("OrganizerWaitlistedFragment", "Error getting waitlisted users", task.getException());
//                    }
//                });
    }
}