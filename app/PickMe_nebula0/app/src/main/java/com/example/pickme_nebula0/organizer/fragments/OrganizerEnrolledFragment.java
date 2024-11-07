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
import com.example.pickme_nebula0.organizer.adapters.EnrolledAdapter;
import com.example.pickme_nebula0.user.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OrganizerEnrolledFragment extends Fragment {
    private FirebaseFirestore db;
    private DBManager dbManager = new DBManager();
    ArrayList<User> enrolledUsers = new ArrayList<User>();
    private EnrolledAdapter adapter;
    String eventID;

    public OrganizerEnrolledFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_enrolled, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.enrolled_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventID = getActivity().getIntent().getStringExtra("eventID");
        adapter = new EnrolledAdapter(getContext(), enrolledUsers);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadEnrolledUsers();
    }

    private void loadEnrolledUsers() {
        enrolledUsers.clear();
        dbManager.loadUsersRegisteredInEvent(eventID, DBManager.RegistrantStatus.CONFIRMED, "OrganizerEnrolledFragment",
                (userObj) -> {
                    User user = (User) userObj;
                    enrolledUsers.add(user);
                    adapter.notifyDataSetChanged();
                });
//
//        Log.d("OrganizerEnrolledFragment",
//                String.format("Fetching waitlisted users from path: %s/%s/%s where %s==%s",
//                        DBManager.eventsCollection,
//                        eventID,
//                        DBManager.eventRegistrantsCollection,
//                        DBManager.eventStatusKey,
//                        DBManager.RegistrantStatus.getStatus(DBManager.RegistrantStatus.WAITLISTED)));
//
//        db.collection("Events")
//                .document(eventID)
//                .collection("eventRegistrants")
//                .whereEqualTo("status", "ENROLLED")
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        List<DocumentSnapshot> registrantDocs = task.getResult().getDocuments();
//                        if (!registrantDocs.isEmpty()) {
//                            for (DocumentSnapshot registrantDoc : registrantDocs) {
//                                String registrantID = registrantDoc.getId();
//                                String status = registrantDoc.getString("status");
//
//                                // Fetch the complete User details
//                                db.collection("Users")
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
//                                                    enrolledUsers.add(user);
//                                                    adapter.notifyDataSetChanged();
//
//                                                    Log.d("OrganizerEnrolledFragment", "Fetched User: " + user.toString());
//                                                }
//                                            } else {
//                                                Log.w("OrganizerEnrolledFragment", "No such user with ID: " + registrantID);
//                                            }
//                                        })
//                                        .addOnFailureListener(e -> {
//                                            Log.e("OrganizerEnrolledFragment", "Error fetching user with ID: " + registrantID, e);
//                                        });
//                            }
//                        } else {
//                            Log.d("OrganizerEnrolledFragment", "No enrolled users found for eventID: " + eventID);
//                        }
//                    } else {
//                        Log.e("OrganizerEnrolledFragment", "Error getting enrolled users", task.getException());
//                    }
//                });
    }
}