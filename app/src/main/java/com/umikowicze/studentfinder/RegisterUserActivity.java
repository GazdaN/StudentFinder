package com.umikowicze.studentfinder;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RegisterUserActivity extends AppCompatActivity {

    private static final String TAG = "mno";

    private EditText inputEmail;
    private String email;
    private EditText inputPassword;
    private String password;
    private String photoUrl = null;
    private EditText inputNickName;
    private String nickName;
    private boolean macierze_area;
    private boolean java_area;
    private boolean matlab_area;
    private boolean routing_area;
    private DatabaseReference mRootReference;
    private Button registerButton;
    private ImageButton mPhotoPickerButton;
    private static final int GALLERY_PICK = 1;

    //Firebase instance variables
    private FirebaseAuth auth;
    private DatabaseReference mDataBase;

    List<String> areaList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        auth = FirebaseAuth.getInstance();


        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.passwordd);
        inputNickName = (EditText) findViewById(R.id.nickName);
        registerButton = (Button) findViewById(R.id.register_button);
        mPhotoPickerButton = (ImageButton) findViewById(R.id.photo_select);
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                email = inputEmail.getText().toString().trim();
                password = inputPassword.getText().toString().trim();
                nickName = inputNickName.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(nickName)) {
                    Toast.makeText(getApplicationContext(), "Enter nickname!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (photoUrl == null) {
                    photoUrl = "https://firebasestorage.googleapis.com/v0/b/studentfinder-3e472.appspot.com/o/users%2Fuser.png?alt=media&token=fef38b18-4480-4917-83b5-4ab64b9cd1bb";
                }


                //onCheckboxClicked(view);
                registerUser(email, photoUrl, password, nickName);

                //onCheckboxClicked();


                //Toast.makeText(RegisterUserActivity.this, "You have been registered.", Toast.LENGTH_LONG).show();
            }
        });


    }

    private void registerUser(String mEmail, String mphotoUrl, String mPassword, String mNickName) {

       final String _mNickName = mNickName;
       final String _mPhotoUrl = mphotoUrl;

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = auth.getCurrentUser();
                            String CurrentUserID = user.getUid();
                             Helper helper = new Helper(_mNickName, _mPhotoUrl, (long)1.0 , (long)1.0, "false" );

                            mRootReference = FirebaseDatabase.getInstance().getReference();
                            mRootReference.child("Helpers").child(CurrentUserID).child("location").setValue(helper.getLocation());
                            mRootReference.child("Helpers").child(CurrentUserID).child("name").setValue(helper.getName());
                            mRootReference.child("Helpers").child(CurrentUserID).child("photoUrl").setValue(helper.getPhotoUrl());
                            mRootReference.child("Helpers").child(CurrentUserID).child("ratings").setValue(helper.getRating());
                            mRootReference.child("Helpers").child(CurrentUserID).child("stars").setValue(helper.getStars());

                            for(String area : areaList){
                                HelpOffer helpOffer = new HelpOffer(area, CurrentUserID);
                                mRootReference.child("HelpOffers").push().setValue(helpOffer);
                            }

                            //mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            Intent mainIntent = new Intent(RegisterUserActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        } else {
                            // If registering in fails
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterUserActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // updateUI(null);
                        }

                    }
                });


    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();



        switch (view.getId()) {
            case R.id.macierze_checkbox:
                if (checked)
                {
                    macierze_area = true;
                    areaList.add("Macierze");
                    }
                else
                    macierze_area = false;
                break;
            case R.id.java_checkbox:
                if (checked)
                {
                    java_area = true;
                    areaList.add("Java");}
                else
                    java_area = false;
                break;

            case R.id.matlab_checkbox:
                if (checked)
                {matlab_area = true;
                    areaList.add("Matlab");}
                else
                    matlab_area = false;
                break;
            case R.id.routing_checkbox:
                if (checked)
                {routing_area = true;
                    areaList.add("Routing");}
                else
                    routing_area = false;
                break;

        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            final Uri imageUri = data.getData();

            //Refrence to store photo file
            FirebaseStorage mFirebaseDatabase = FirebaseStorage.getInstance();
            StorageReference mStorageReference = mFirebaseDatabase.getReference();
            StorageReference photoRef = mStorageReference.child("users").child(UUID.randomUUID().toString() + ".jpg");

            //Uploading photo file to Firebase storage

            photoRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {
                        photoUrl = task.getResult().getDownloadUrl().toString();
                    }
                }
            });
        }
    }
}



