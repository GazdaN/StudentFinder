package com.umikowicze.studentfinder;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {


    private RecyclerView mConvList;
    private DatabaseReference mConvDatabase;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mHelpersDatabase;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private View mMainView;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

      mMainView = inflater.inflate(R.layout.fragment_chat, container, false);
      mConvList = mMainView.findViewById(R.id.convList);
      mAuth = FirebaseAuth.getInstance();
      mCurrentUserId = mAuth.getCurrentUser().getUid();

      mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat");

      mConvDatabase.keepSynced(true);
      mHelpersDatabase = FirebaseDatabase.getInstance().getReference().child("Helpers");
      mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("Messages").child(mCurrentUserId);
      mHelpersDatabase.keepSynced(true);

      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
      linearLayoutManager.setReverseLayout(true);
      linearLayoutManager.setStackFromEnd(true);

      mConvList.setHasFixedSize(true);
      mConvList.setLayoutManager(linearLayoutManager);

      return mMainView;
    }
    @Override
    public void onStart() {
        super.onStart();


        Query conversationQuery = mConvDatabase.orderByChild("timestamp");

        FirebaseRecyclerAdapter<Conv, ConvViewHolder> firebaseConvAdapter = new FirebaseRecyclerAdapter<Conv, ConvViewHolder>(
                Conv.class,
                R.layout.single_user_layout,
                ConvViewHolder.class,
                conversationQuery
        )
        {
            @Override
            protected void populateViewHolder(final ConvViewHolder convViewHolder, final Conv conv, int i) {



                final String list_user_id = getRef(i).getKey();

                Query lastMessageQuery = mMessageDatabase.child(list_user_id).limitToLast(1);

                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        String data = dataSnapshot.child("message").getValue().toString();
                        convViewHolder.setMessage(data, conv.isSeen());

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                mHelpersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();

                        convViewHolder.setName(userName);

                        convViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {


                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                chatIntent.putExtra("user_id", list_user_id);
                                startActivity(chatIntent);

                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mConvList.setAdapter(firebaseConvAdapter);

    }

    public static class ConvViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ConvViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setMessage(String message, boolean isSeen){

            TextView userLastMessage = (TextView) mView.findViewById(R.id.lastMessage);
            userLastMessage.setText(message);

            if(!isSeen){
                userLastMessage.setTypeface(userLastMessage.getTypeface(), Typeface.BOLD);
            } else {
                userLastMessage.setTypeface(userLastMessage.getTypeface(), Typeface.NORMAL);
            }

        }

        public void setName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.userName);
            userNameView.setText(name);

        }


    }



}