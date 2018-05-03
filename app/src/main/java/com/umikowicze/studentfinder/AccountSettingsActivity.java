package com.umikowicze.studentfinder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;


public class AccountSettingsActivity extends AppCompatActivity {

    private Button changeDisplayName;
    private Button mChangeImageButton;
    //For taking stuff from DB
    private DatabaseReference mDatabase;
    //Current user
    private FirebaseUser mCurrentUser;
    private String mName;
    private TextView mDisplayNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        mDisplayNameTextView = findViewById(R.id.displayNameTextView);
        changeDisplayName = findViewById(R.id.changeDisplayNameButton);
        mChangeImageButton = findViewById(R.id.changeImageButton);

        //Get User ID
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserUid = mCurrentUser.getUid();

        //Get DB reference
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Helpers").child(currentUserUid);
        mDatabase.keepSynced(true);
        //Listener to get information stored in DB
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mName = dataSnapshot.child("name").getValue().toString();
                mDisplayNameTextView.setText(mName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        changeDisplayName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if we want to pass some information to next activity we use intent.putExtra and we receive it by getIntent().getStringExtra...
                Toast.makeText(AccountSettingsActivity.this, "Feature of changing name will be available in future", Toast.LENGTH_LONG).show();
            }
        });

        mChangeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AccountSettingsActivity.this, "Feature of changing image will be available in future", Toast.LENGTH_LONG).show();
            }
        });

    }
}
