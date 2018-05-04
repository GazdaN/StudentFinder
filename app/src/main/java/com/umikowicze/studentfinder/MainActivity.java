package com.umikowicze.studentfinder;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private TabLayout mTabLayout;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseReference;
    private ListView requestListView;
    private HelpRequestAdapter helpRequestAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = findViewById(R.id.main_page_actionbar);
        //Tabs
        mViewPager = findViewById(R.id.viewTabPager);
        //Initiation of tabs
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);

        mTabLayout = findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
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

        else if(item.getItemId() == R.id.mainMenuSearchForHelpButton)
        {
            Intent intent = new Intent(MainActivity.this, HelpBrowserActivity.class);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.settingsActivityButton)
        {
            Intent settingsIntent = new Intent(MainActivity.this, AccountSettingsActivity.class);
            startActivity(settingsIntent);
        }

        return true;
    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }
}
