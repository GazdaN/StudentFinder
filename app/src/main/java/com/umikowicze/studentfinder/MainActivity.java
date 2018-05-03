package com.umikowicze.studentfinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = findViewById(R.id.main_page_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("StudentFinder");
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //It will be null if user is not signed in -> it will switch screen to WelcomeScreen (startActivity)
        if(currentUser == null)
        {
            sendToStart();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       super.onCreateOptionsMenu(menu);

       getMenuInflater().inflate(R.menu.main_menu, menu);
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.mainMenuLogutButton)
        {
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }

        return true;
    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }
}
