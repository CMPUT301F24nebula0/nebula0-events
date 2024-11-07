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
import com.example.pickme_nebula0.organizer.adapters.SelectedAdapter;
import com.example.pickme_nebula0.user.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OrganizerSelectedFragment extends Fragment {
    private FirebaseFirestore db;
    ArrayList<User> selectedUsers = new ArrayList<User>();
    private SelectedAdapter adapter;
    String eventID;

    public OrganizerSelectedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_selected, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.selected_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SelectedAdapter(getContext(), selectedUsers);
        recyclerView.setAdapter(adapter);
        eventID = getActivity().getIntent().getStringExtra("eventID");

        db = FirebaseFirestore.getInstance();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSelectedUsers();
    }

    private void loadSelectedUsers() {
        selectedUsers.clear();
        db.collection("Events")
                .document(eventID)
                .collection("EventRegistrants")
                .whereEqualTo("status", "SELECTED")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> registrantDocs = task.getResult().getDocuments();
                        if (!registrantDocs.isEmpty()) {
                            for (DocumentSnapshot registrantDoc : registrantDocs) {
                                String registrantID = registrantDoc.getId();
                                String status = registrantDoc.getString("status");

                                // Fetch the complete User details
                                db.collection("Users")
                                        .document(registrantID)
                                        .get()
                                        .addOnSuccessListener(userDoc -> {
                                            if (userDoc.exists()) {
                                                User user = userDoc.toObject(User.class);
                                                if (user != null) {
                                                    // Manually set userID from document ID
                                                    user.setUserID(userDoc.getId());

                                                    user.setStatus(status); // Set the status from eventRegistrants
                                                    selectedUsers.add(user);
                                                    adapter.notifyDataSetChanged();

                                                    Log.d("OrganizerSelectedFragment", "Fetched User: " + user.toString());
                                                }
                                            } else {
                                                Log.w("OrganizerSelectedFragment", "No such user with ID: " + registrantID);
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("OrganizerSelectedFragment", "Error fetching user with ID: " + registrantID, e);
                                        });
                            }
                        } else {
                            Log.d("OrganizerSelectedFragment", "No selected users found for eventID: " + eventID);
                        }
                    } else {
                        Log.e("OrganizerSelectedFragment", "Error getting selected users", task.getException());
                    }
                });
    }
}