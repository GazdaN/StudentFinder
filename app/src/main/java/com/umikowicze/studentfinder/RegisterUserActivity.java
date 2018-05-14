package com.umikowicze.studentfinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterUserActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputNickName;
    private Button registerButton;
    private boolean mathematics_area;
    private boolean physics_area;
    private boolean coding_area;
    private boolean electronics_area;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        auth = FirebaseAuth.getInstance();
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.passwordd);
        inputNickName = (EditText) findViewById(R.id.nickName);
        registerButton = (Button) findViewById(R.id.register_button);

        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String nickName = inputNickName.getText().toString().trim();

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

                //onCheckboxClicked();




                //Toast.makeText(RegisterUserActivity.this, "You have been registered.", Toast.LENGTH_LONG).show();
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
}
