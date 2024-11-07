package com.example.pickme_nebula0.organizer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.organizer.adapters.EnrolledAdapter;
import com.example.pickme_nebula0.user.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class OrganizerEnrolledFragment extends Fragment {
    private FirebaseFirestore db;
    ArrayList<User> enrolledUsers = new ArrayList<User>();
    private EnrolledAdapter adapter;

    public OrganizerEnrolledFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_enrolled, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.enrolled_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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
        // TODO: TaekwanY
        // backend implementation
    }

    private void loadUserData(String userID) {
        //TODO: TaekwanY
    }
}
