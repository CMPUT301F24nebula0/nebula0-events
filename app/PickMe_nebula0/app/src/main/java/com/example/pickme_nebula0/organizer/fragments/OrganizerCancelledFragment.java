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
import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.organizer.adapters.CancelledAdapter;
import com.example.pickme_nebula0.user.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OrganizerCancelledFragment extends Fragment {
    private FirebaseFirestore db;
    private DBManager dbManager = new DBManager();
    ArrayList<User> cancelledUsers = new ArrayList<User>();
    private CancelledAdapter adapter;
    String eventID;

    public OrganizerCancelledFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_cancelled, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.cancelled_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CancelledAdapter(getContext(), cancelledUsers);
        recyclerView.setAdapter(adapter);
        eventID = getActivity().getIntent().getStringExtra("eventID");
        db = FirebaseFirestore.getInstance();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCancelledUsers();
    }

    private void loadCancelledUsers() {
        cancelledUsers.clear();
        dbManager.loadUsersRegisteredInEvent(eventID, DBManager.RegistrantStatus.CANCELED, "OrganizerCancelledFragment",
                (userObj) -> {
                    User user = (User) userObj;
                    cancelledUsers.add(user);
                    adapter.notifyDataSetChanged();
                });

//
//        Log.d("OrganizerCancelledFragment",
//                String.format("Fetching cancelled users from path: %s/%s/%s where %s==%s",
//                        DBManager.eventsCollection,
//                        eventID,
//                        DBManager.eventRegistrantsCollection,
//                        DBManager.eventStatusKey,
//                        DBManager.RegistrantStatus.getStatus(DBManager.RegistrantStatus.CANCELED)));
//
//        db.collection(DBManager.eventsCollection)
//                .document(eventID)
//                .collection(DBManager.eventRegistrantsCollection)
//                .whereEqualTo(DBManager.eventStatusKey, DBManager.RegistrantStatus.getStatus(DBManager.RegistrantStatus.CANCELED))
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        List<DocumentSnapshot> registrantDocs = task.getResult().getDocuments();
//                        if (!registrantDocs.isEmpty()) {
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
//                                                    // Manually set userID from document ID
//                                                    user.setUserID(userDoc.getId());
//
//                                                    user.setStatus(status); // Set the status from eventRegistrants
//                                                    cancelledUsers.add(user);
//                                                    adapter.notifyDataSetChanged();
//
//                                                    Log.d("OrganizerCancelledFragment", "Fetched User: " + user.toString());
//                                                }
//                                            } else {
//                                                Log.w("OrganizerCancelledFragment", "No such user with ID: " + registrantID);
//                                            }
//                                        })
//                                        .addOnFailureListener(e -> {
//                                            Log.e("OrganizerCancelledFragment", "Error fetching user with ID: " + registrantID, e);
//                                        });
//                            }
//                        } else {
//                            Log.d("OrganizerCancelledFragment", "No cancelled users found for eventID: " + eventID);
//                        }
//                    } else {
//                        Log.e("OrganizerCancelledFragment", "Error getting cancelled users", task.getException());
//                    }
//                });
    }
}