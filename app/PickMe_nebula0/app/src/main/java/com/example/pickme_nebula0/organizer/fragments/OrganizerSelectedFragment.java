package com.example.pickme_nebula0.organizer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.organizer.adapters.SelectedAdapter;
import com.example.pickme_nebula0.user.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class OrganizerSelectedFragment extends Fragment {
    private FirebaseFirestore db;
    ArrayList<User> selectedUsers = new ArrayList<User>();
    private SelectedAdapter adapter;

    public OrganizerSelectedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_selected,container, false);
        RecyclerView recyclerView = view.findViewById(R.id.selected_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SelectedAdapter(getContext(), selectedUsers);
        recyclerView.setAdapter(adapter);

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
        // TODO: TaekwanY
        // backend implementation
    }

    private void loadUserData(String userID) {
        //TODO: TaekwanY
    }
}
