package com.umikowicze.studentfinder;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatActivity extends AppCompatActivity {

    private String mChatUserId;
    private Toolbar mChatToolbar;
    private DatabaseReference mRootReference;
    private  ActionBar actionBar;
    private String mChatUsername;
    private TextView mDisplayName;
    private EditText mNewMessageText;
    private ImageButton mAddStuffButton;
    private ImageButton mSendMessageButton;
    private CircleImageView mcircleImageView;
    private FirebaseAuth mAuth;
    private  String mCurrentUserID;
    //Stuff for receiving messages
    private RecyclerView mMessageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatUserId = getIntent().getStringExtra("user_id");
        mChatToolbar = findViewById(R.id.chat_app_bar);

        setSupportActionBar(mChatToolbar);
        actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);


        mRootReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserID = mAuth.getCurrentUser().getUid();

        mRootReference.child("Helpers").child(mChatUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mChatUsername = dataSnapshot.child("name").getValue().toString();
                mDisplayName.setText(mChatUsername);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.chat_custom_bar, null);

        actionBar.setCustomView(actionBarView);
        mcircleImageView = findViewById(R.id.customBarImage);
        mDisplayName = findViewById(R.id.displayNameCustomBar);
        mNewMessageText = findViewById(R.id.newMessageEditTextView);
        mAddStuffButton = findViewById(R.id.addStuffImageButton);
        mSendMessageButton = findViewById(R.id.sendMessageButton);
        mMessageList = findViewById(R.id.messagesList);

        mRootReference.child("Chat").child(mCurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(mChatUserId))
                {

                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/"+mCurrentUserID+"/"+mChatUserId,chatAddMap);
                    chatUserMap.put("Chat/"+mChatUserId+"/"+mCurrentUserID,chatAddMap);

                    mRootReference.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError != null)
                            {
                                Log.d("CHAT_LOG",databaseError.getMessage().toString());
                            }

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessageMethod();

            }
        });

        mAddStuffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    //Sending new message
    private void sendMessageMethod() {

        String message = mNewMessageText.getText().toString();

        if(!message.isEmpty())
        {
            //Strings for HashMaps
            String currentUserRef = "Messages/"+mCurrentUserID+"/"+mChatUserId;
            String chatUserRef = "Messages/"+mChatUserId+"/"+mCurrentUserID;

            //PushId is needed identify messages
            DatabaseReference userMessagePush = mRootReference.child("Messages").child(mCurrentUserID).child(mChatUserId).push();
            String pushId = userMessagePush.getKey();

            //New hasMap in database - for message
            Map messageMap = new HashMap();
            messageMap.put("message",message );
            messageMap.put("seen",false);
            messageMap.put("type","text");
            messageMap.put("time",ServerValue.TIMESTAMP);

            Map messageUserMap = new HashMap();
            messageUserMap.put(currentUserRef + "/" + pushId, messageMap);
            messageUserMap.put(chatUserRef+"/"+pushId,messageMap);

            mRootReference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError != null)
                    {
                        Log.d("CHAT_LOG",databaseError.getMessage().toString());
                    }
                }
            });


        }

    }
}
