package com.umikowicze.studentfinder;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mMessageAdapter;
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;
    private SwipeRefreshLayout mRefreshMessages;
    private int mitemPos = 0;
    private String mLastKey =  "";
    private String mPreviousKey = "";

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

        //Stuff for receiving messages

        mMessageAdapter = new MessageAdapter(messagesList);
        mMessageList = findViewById(R.id.messagesList);
        mLinearLayout = new LinearLayoutManager(this);
        mMessageList.setHasFixedSize(true);
        mMessageList.setLayoutManager(mLinearLayout);
        mMessageList.setAdapter(mMessageAdapter);
        mRefreshMessages = findViewById(R.id.messageSwipeLayour);

        loadMessagesMethod();

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

        mRefreshMessages.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mCurrentPage++;
                mitemPos = 0;
                loadMoreMessagesMethod();
            }
        });
    }

    private void loadMoreMessagesMethod(){

        DatabaseReference messageRef =  mRootReference.child("Messages").child(mCurrentUserID).child(mChatUserId);
        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //Data form DataSnapshot will be provided to the set methods in Messages class
                Messages messsage = dataSnapshot.getValue(Messages.class);
                String messageKey = dataSnapshot.getKey();

                if(!mPreviousKey.equals(messageKey) )
                {
                    messagesList.add(mitemPos++, messsage);
                }
                else
                {
                    mPreviousKey = mLastKey;
                }

                if(mitemPos==1)
                {
                    mLastKey = messageKey;
                }

                mMessageAdapter.notifyDataSetChanged();
                mRefreshMessages.setRefreshing(false);
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


    }

    private void loadMessagesMethod() {

        DatabaseReference messageRef =  mRootReference.child("Messages").child(mCurrentUserID).child(mChatUserId);

        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //Data form DataSnapshot will be provided to the set methods in Messages class
                Messages messsage = dataSnapshot.getValue(Messages.class);
                mitemPos++;

                if(mitemPos==1){
                    mLastKey = dataSnapshot.getKey();
                    mPreviousKey = dataSnapshot.getKey();
                }

                messagesList.add(messsage);
                mMessageAdapter.notifyDataSetChanged();

                mMessageList.scrollToPosition(messagesList.size()-1);

                mRefreshMessages.setRefreshing(false);
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
            messageMap.put("from", mCurrentUserID);

            mNewMessageText.setText("");

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
