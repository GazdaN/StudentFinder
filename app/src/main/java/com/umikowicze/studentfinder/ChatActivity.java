package com.umikowicze.studentfinder;

import android.app.Application;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatActivity extends AppCompatActivity {

    private String mChatUserId;
    private Toolbar mChatToolbar;
    private DatabaseReference mRootReference;
    private ActionBar actionBar;
    private String mChatUsername;
    private TextView mDisplayName;
    private EditText mNewMessageText;
    private ImageButton mAddStuffButton;
    private ImageButton mSendMessageButton;
    private CircleImageView mImageView;
    private FirebaseAuth mAuth;
    private String mCurrentUserID;
    //Stuff for receiving messages
    private RecyclerView mMessageList;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mMessageAdapter;
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;
    private SwipeRefreshLayout mRefreshMessages;
    private int mitemPos = 0;
    private String mLastKey = "";
    private String mPreviousKey = "";
    private static final int GALLERY_PICK = 1;
    private StorageReference mImageStorage;
    private ProgressDialog mProgressDialog;
    private ImageView endRequestButton;
    private String requestID;
    private boolean isCurrentUserHelper;
    private float currentStars = 0;
    private float currentRatings = 0;
    private boolean isRated = false;
    private long userRating = 0;
    private long difference = 0;

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
                String mChatUserPhoto = dataSnapshot.child("photoUrl").getValue().toString();
                mDisplayName.setText(mChatUsername);
                Glide.with(getBaseContext()).load(mChatUserPhoto).into(mImageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.chat_custom_bar, null);

        actionBar.setCustomView(actionBarView);
        mImageView = findViewById(R.id.customBarImage);
        mDisplayName = findViewById(R.id.displayNameCustomBar);
        mNewMessageText = findViewById(R.id.newMessageEditTextView);
        mAddStuffButton = findViewById(R.id.addStuffImageButton);
        mSendMessageButton = findViewById(R.id.sendMessageButton);

        endRequestButton = findViewById(R.id.endRequestButton);
        // Check who is helper and who is requester in chat
        mRootReference.child("HelpRequests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dss : dataSnapshot.getChildren()) {
                        if (dss.child("helperid").getValue().toString().equals(mCurrentUserID)
                                && dss.child("requesterid").getValue().toString().equals(mChatUserId)) {
                            endRequestButton.setVisibility(View.INVISIBLE);
                            endRequestButton.setClickable(false);
                            isCurrentUserHelper = true;
                            requestID = dss.getKey();
                        }
                        if (dss.child("requesterid").getValue().toString().equals(mCurrentUserID)
                                && dss.child("helperid").getValue().toString().equals(mChatUserId)) {

                            mRootReference.child("Ratings").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot dss : dataSnapshot.getChildren()) {
                                            if (dss.exists())
                                                if (dss.child("helperID").getValue().equals(mChatUserId)
                                                        && dss.child("requesterID").getValue().equals(mCurrentUserID)) {
                                                    isRated = true;
                                                    userRating = (long) dss.child("rating").getValue();
                                                }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            endRequestButton.setVisibility(View.VISIBLE);
                            endRequestButton.setClickable(true);
                            isCurrentUserHelper = false;
                            requestID = dss.getKey();
                            }
                        }
                    }
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        endRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context context = view.getContext();
                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                builder.setTitle("Jak oceniasz otrzymaną pomoc?");

                LayoutInflater inflater = getLayoutInflater();
                View v = inflater.inflate(R.layout.rating_bar, null);
                builder.setView(v);
                final RatingBar rating = (RatingBar)v.findViewById(R.id.ratingbar);

                if (isRated)
                    rating.setRating((float) userRating);

                builder.setPositiveButton("Oceń!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final long stars = (long) rating.getRating();

                        if (!isRated) {
                            Query query = mRootReference.child("Helpers").child(mChatUserId);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        currentRatings = Float.valueOf(dataSnapshot.child("ratings").getValue().toString());
                                        currentStars = Float.valueOf(dataSnapshot.child("stars").getValue().toString());

                                        currentStars += stars;
                                        currentRatings++;

                                        mRootReference.child("Helpers").child(mChatUserId).child("ratings").setValue(currentRatings);
                                        mRootReference.child("Helpers").child(mChatUserId).child("stars").setValue(currentStars);

                                        Rating newRating = new Rating(mChatUserId, mCurrentUserID, stars);
                                        mRootReference.child("Ratings").push().setValue(newRating);
                                        isRated = true;
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        else {
                            mRootReference.child("Ratings").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot dss : dataSnapshot.getChildren()) {
                                            if (dss.child("helperID").getValue().equals(mChatUserId)
                                                    && dss.child("requesterID").getValue().equals(mCurrentUserID)) {
                                                difference = stars - userRating;
                                                userRating = stars;
                                                String key = dss.getKey();
                                                mRootReference.child("Ratings").child(key).child("rating").setValue(stars);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            Query query = mRootReference.child("Helpers").child(mChatUserId);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        currentStars = (long) dataSnapshot.child("stars").getValue();
                                        currentStars += difference;
                                        mRootReference.child("Helpers").child(mChatUserId).child("stars").setValue(currentStars);
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }

                    }
                });

                builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

                /*
                builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // TUTAJ RATING

                        mRootReference.child("HelpRequests").child(requestID).removeValue();
                        mRootReference.child("Chat").child(mChatUserId).child(mCurrentUserID).removeValue();
                        mRootReference.child("Chat").child(mCurrentUserID).child(mChatUserId).removeValue();

                        Intent mainActivityIntent = new Intent(context, MainActivity.class);
                        startActivity(mainActivityIntent);
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
                */
            }
        });

        //Stuff for receiving messages

        mMessageAdapter = new MessageAdapter(messagesList,mCurrentUserID, mChatUserId);
        mMessageList = findViewById(R.id.messagesList);
        mLinearLayout = new LinearLayoutManager(this);
        mMessageList.setHasFixedSize(true);
        mMessageList.setLayoutManager(mLinearLayout);
        mMessageList.setAdapter(mMessageAdapter);
        mRefreshMessages = findViewById(R.id.messageSwipeLayour);

        mImageStorage = FirebaseStorage.getInstance().getReference();

        loadMessagesMethod();

        mRootReference.child("Chat").child(mCurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(mChatUserId)) {

                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + mCurrentUserID + "/" + mChatUserId, chatAddMap);
                    chatUserMap.put("Chat/" + mChatUserId + "/" + mCurrentUserID, chatAddMap);

                    mRootReference.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError != null) {
                                Log.d("CHAT_LOG", databaseError.getMessage().toString());
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

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK)
        {
            Uri imageUri = data.getData();

            final String currentUserRef = "Messages/"+mCurrentUserID+"/"+mChatUserId;
            final String chatUserRef = "Messages/"+mChatUserId+"/"+mCurrentUserID;

            DatabaseReference userMessagePush = mRootReference.child("Messages").child(mCurrentUserID).child(mChatUserId).push();

            final String pushId = userMessagePush.getKey();

            StorageReference filePath = mImageStorage.child("Message_images").child(pushId + ".jpg");

            filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {
                        String downloadUrl = task.getResult().getDownloadUrl().toString();

                        Map messageMap = new HashMap();

                        messageMap.put("message", downloadUrl);
                        messageMap.put("seen", false);
                        messageMap.put("type", "image");
                        messageMap.put("from", mCurrentUserID);
                        messageMap.put("time", ServerValue.TIMESTAMP);

                        Map messageUserMap = new HashMap();
                        messageUserMap.put(currentUserRef + "/" + pushId, messageMap);
                        messageUserMap.put(chatUserRef + "/" + pushId, messageMap);

                        mNewMessageText.setText("");

                        mRootReference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    Log.d("CHAT_LOG", databaseError.getMessage().toString());
                                }
                            }
                        });

                    }
                }
            });

        }
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
