package com.umikowicze.studentfinder;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class LessonRequestFragment extends Fragment {

    private FirebaseDatabase mFirebaseReference;
    private DatabaseReference mDatabaseReference;
    private ListView requestListView;
    private HelpRequestAdapter helpRequestAdapter;
    private HashMap<String, String> requestersIDs;
    private HashMap<String, String> requestsKeys;


    public LessonRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lesson_request, container, false);

        mFirebaseReference = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseReference.getReference();

        requestersIDs = new HashMap<String, String>();
        requestsKeys = new HashMap<String, String>();

        final List<HelpRequest> helpRequestList = new ArrayList<HelpRequest>();
        helpRequestAdapter = new HelpRequestAdapter(getActivity(), R.layout.item_help_request, helpRequestList);

        requestListView = view.findViewById(R.id.requestListView);
        requestListView.setAdapter(helpRequestAdapter);

        getRequestsToProcess();

        requestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int position = i;
                final HelpRequest helpRequest = (HelpRequest) adapterView.getItemAtPosition(i);

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                builder.setTitle("Pomoc w: " + helpRequest.getArea());
                builder.setMessage("Czy chcesz pomóc użytkownikowi " + requestersIDs.get(Integer.toString(position)) + "?");

                builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        mDatabaseReference.child("HelpRequests").child(requestsKeys.get(Integer.toString(position)))
                                .child("status").setValue("Active");
                        getRequestsToProcess();

                        Intent chatIntent = new Intent(getContext(),ChatActivity.class);
                        chatIntent.putExtra("user_id",helpRequest.getRequesterid());
                        startActivity(chatIntent);
                        dialog.dismiss();


                    }
                });

                builder.setNegativeButton("Nie", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        return view;
    }

    public void getRequestsToProcess() {
        helpRequestAdapter.clear();
        Query query = mDatabaseReference.child("HelpRequests");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dss : dataSnapshot.getChildren()) {
                        HelpRequest helpRequest = dss.getValue(HelpRequest.class);
                        if (helpRequest.getHelperid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) &&
                                helpRequest.getStatus().equals("Sent")) {
                            helpRequestAdapter.add(helpRequest);
                            requestsKeys.put(Integer.toString(helpRequestAdapter.getCount() - 1), dss.getKey());
                            Query query2 = mDatabaseReference.child("Helpers").orderByKey().equalTo(helpRequest.getRequesterid());
                            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot dss2 : dataSnapshot.getChildren()) {
                                            final Helper helper = dss2.getValue(Helper.class);
                                            requestersIDs.put(Integer.toString(helpRequestAdapter.getCount() - 1), helper.getName());
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
