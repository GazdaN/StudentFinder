package com.umikowicze.studentfinder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


//WelcomPage for students - place where you can login or make a new account
public class StartActivity extends AppCompatActivity {

    private Button mRegisterButton;
    private Button mLoginButton;
    private TextInputEditText mEmailInput;
    private TextInputEditText mPasswdInput;
    private FirebaseAuth mAuth;
    private ProgressDialog mLoginProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mAuth = FirebaseAuth.getInstance();

        mLoginProgress = new ProgressDialog(this);

        mRegisterButton = findViewById(R.id.registerButton);
        mLoginButton = findViewById(R.id.loginButton);
        mEmailInput =  findViewById(R.id.emailTextLayout);
        mPasswdInput = findViewById(R.id.passwordTextLayout);


        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmailInput.getText().toString();
                String password = mPasswdInput.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))
                {

                    mLoginProgress.setTitle("Logging in");
                    mLoginProgress.setMessage("Please wait.");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    loginUser(email, password);
                }

            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Here will be switch for ReqistrationPage
                Toast.makeText(getApplicationContext(), "It will be here in future" , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser(String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    mLoginProgress.dismiss();

                    Intent mainIntent = new Intent(StartActivity.this, MainActivity.class);
                    //It clears all previous tasks -> when you go back form main screen you will close app, you won't come back to WelcomeScreen.
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();
                }
                else
                {
                    mLoginProgress.hide();
                    Toast.makeText(StartActivity.this, "Cannot Sign in. Please check your credentials and try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
