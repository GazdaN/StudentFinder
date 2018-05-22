package com.umikowicze.studentfinder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference mRootReference;
    private String mCurrentUser;
    private String mimageUrlCurrentUser;
    private String mimageUrlChatUser;
    private String mChatUser;

    public MessageAdapter(List<Messages> mMessageList, String currentUser, String chatUser)
    {
        this.mMessageList = mMessageList;
        mRootReference = FirebaseDatabase.getInstance().getReference();

        mRootReference.child("Helpers").child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mimageUrlCurrentUser = dataSnapshot.child("photoUrl").getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRootReference.child("Helpers").child(chatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mimageUrlChatUser = dataSnapshot.child("photoUrl").getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false);

        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {

        mAuth = FirebaseAuth.getInstance();

        String currentUserId = mAuth.getCurrentUser().getUid();
        Messages messages = mMessageList.get(position);

        String fromUser = messages.getFrom();
        String messageType = messages.getType();



        if(messageType.equals("text")) {
            holder.messageText.setText(messages.getMessage());
            holder.mMessageImage.setVisibility(View.INVISIBLE);

            if(fromUser.equals(currentUserId))
            {
                holder.messageText.setBackgroundResource(R.drawable.message_text_backround_sender);
                holder.messageText.setTextColor(Color.BLACK);
                Glide.with(holder.mProfileImage.getContext()).load(mimageUrlCurrentUser).into(holder.mProfileImage);


            }
            else
            {
                holder.messageText.setBackgroundResource(R.drawable.message_text_background);
                holder.messageText.setTextColor(Color.WHITE);
                Glide.with(holder.mProfileImage.getContext()).load(mimageUrlChatUser).into(holder.mProfileImage);
            }

        }else
        {
            if(fromUser.equals(currentUserId))
        {
            holder.messageText.setVisibility(View.INVISIBLE);
            Picasso.get().load(messages.getMessage()).placeholder(R.drawable.star24).into(holder.mMessageImage);
            Glide.with(holder.mProfileImage.getContext()).load(mimageUrlCurrentUser).into(holder.mProfileImage);
        }
        else
        {
            holder.messageText.setVisibility(View.INVISIBLE);
            Picasso.get().load(messages.getMessage()).placeholder(R.drawable.star24).into(holder.mMessageImage);
            Glide.with(holder.mProfileImage.getContext()).load(mimageUrlChatUser).into(holder.mProfileImage);
        }



        }

     }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView messageText;
        public ImageView mProfileImage;
        public ImageView mMessageImage;

        public MessageViewHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.messageTextLayout);
            mProfileImage = itemView.findViewById(R.id.messageProfileImageLayout);
            mMessageImage = itemView.findViewById(R.id.imageViewLayout);

        }
    }
}
