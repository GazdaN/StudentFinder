package com.umikowicze.studentfinder;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;

    public MessageAdapter(List<Messages> mMessageList)
    {
        this.mMessageList = mMessageList;
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

        if(fromUser.equals(currentUserId))
        {
            holder.messageText.setBackgroundResource(R.drawable.message_text_backround_sender);
            holder.messageText.setTextColor(Color.BLACK);
            holder.mProfileImage.setVisibility(View.INVISIBLE);
        }
        else
        {
            holder.messageText.setBackgroundResource(R.drawable.message_text_background);
            holder.messageText.setTextColor(Color.WHITE);

        }

        if(messageType.equals("text")) {
            holder.messageText.setText(messages.getMessage());
            holder.mMessageImage.setVisibility(View.INVISIBLE);
        }else
        {
            holder.messageText.setVisibility(View.INVISIBLE);
            Picasso.get().load(messages.getMessage()).placeholder(R.drawable.star24).into(holder.mMessageImage);

        }

     }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView messageText;
        public CircleImageView mProfileImage;
        public ImageView mMessageImage;

        public MessageViewHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.messageTextLayout);
            mProfileImage = itemView.findViewById(R.id.messageProfileImageLayout);
            mMessageImage = itemView.findViewById(R.id.imageViewLayout);

        }
    }
}
