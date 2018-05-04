package com.umikowicze.studentfinder;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class LessonRequestFragment extends Fragment {

    private FirebaseDatabase mFirebaseReference;
    private DatabaseReference mDatabaseReference;
    private ListView requestListView;
    private HelpRequestAdapter helpRequestAdapter;


    public LessonRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lesson_request, container, false);

        mFirebaseReference = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseReference.getReference();

        final List<HelpRequest> helpRequestList = new ArrayList<HelpRequest>();
        helpRequestAdapter = new HelpRequestAdapter(getActivity(), R.layout.item_help_request, helpRequestList);

        requestListView = view.findViewById(R.id.requestListView);
        requestListView.setAdapter(helpRequestAdapter);

        Query query = mDatabaseReference.child("HelpRequest");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dss : dataSnapshot.getChildren()) {
                        HelpRequest helpRequest = dss.getValue(HelpRequest.class);
                        if (helpRequest.getHelperid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) &&
                                helpRequest.getStatus().equals("Sent")) {
                            helpRequestAdapter.add(helpRequest);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

}
