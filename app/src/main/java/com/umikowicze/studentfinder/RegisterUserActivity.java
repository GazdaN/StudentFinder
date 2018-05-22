package com.umikowicze.studentfinder;

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

import java.util.HashMap;
import java.util.Map;

public class RegisterUserActivity extends AppCompatActivity {

    private static final String TAG = "mno";
    private static final int RC_PHOTO_PICKER = 2;

    private EditText inputEmail;
    private String email;
    private EditText inputPassword;
    private String password;
    private String photoUrl;
    private EditText inputNickName;
    private String nickName;
    private boolean mathematics_area;
    private boolean physics_area;
    private boolean coding_area;
    private boolean electronics_area;
    private DatabaseReference mRootReference;
    private Button registerButton;
    private ImageButton mPhotoPickerButton;

    //Firebase instance variables
    private FirebaseAuth auth;
    private DatabaseReference mDataBase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        auth = FirebaseAuth.getInstance();


        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.passwordd);
        inputNickName = (EditText) findViewById(R.id.nickName);
        registerButton = (Button) findViewById(R.id.register_button);
//        mPhotoPickerButton = (ImageButton) findViewById(R.id.photo_select);
//
//        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/jpeg");
//                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
//                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
//            }
//        });

        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                email = inputEmail.getText().toString().trim();
                password = inputPassword.getText().toString().trim();
                nickName = inputNickName.getText().toString().trim();
                photoUrl = "photoURL";
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

        mphotoUrl = "fddsfsdf";
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
            case R.id.mathematics_checkbox:
                if (checked)
                    mathematics_area = true;
                else
                    mathematics_area = false;
                break;
            case R.id.physics_checkbox:
                if (checked)
                    physics_area = true;
                else
                    physics_area = false;
                break;

            case R.id.coding_checkbox:
                if (checked)
                    coding_area = true;
                else
                    coding_area = false;
                break;
            case R.id.electronics_checkbox:
                if (checked)
                    electronics_area = true;
                else
                    electronics_area = false;
                break;

        }
    }


//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
//            final Uri selectedIamgeUri = data.getData();
//
//            //Refrence to store photo file
//            StorageReference photoRef = mProfilePhotoRefrence.child(selectedIamgeUri.getLastPathSegment());
//
//            //Uploading photo file to Firebase storage
//
//
//            photoRef.putFile(selectedIamgeUri).addOnSuccessListener
//                    (this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
//                            //TODO: Wyswietlanie zdjecia profilowego
//                            //mPhotoPickerButton.setImageResource();
//                        }
//                    });
//
//
//        }


    }



