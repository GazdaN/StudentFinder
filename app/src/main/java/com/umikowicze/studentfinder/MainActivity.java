package com.umikowicze.studentfinder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private long lastDateTime;

    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "help_request_notification_channel";

    private String requesterName = null;

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

        initializeNewHelpRequestListener();
    }

    private void initializeNewHelpRequestListener() {
        Calendar nowDate = Calendar.getInstance();
        lastDateTime = nowDate.getTimeInMillis();

        mFirebaseReference = null;
        mDatabaseReference = null;

        mFirebaseReference = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseReference.getReference();
        Query query = mDatabaseReference.child("HelpRequests");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HelpRequest helpRequest = dataSnapshot.getValue(HelpRequest.class);
                if (helpRequest.getHelperid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) &&
                    helpRequest.getStatus().equals("Sent")) {

                    Calendar requestDate = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm aa");
                    try {
                        requestDate.setTime(sdf.parse(helpRequest.getDatetime()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (requestDate.getTimeInMillis() > lastDateTime) {
                        createNewHelpRequestNotification(helpRequest);
                        Calendar nowDate = Calendar.getInstance();
                        lastDateTime = nowDate.getTimeInMillis();
                    }
                }
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

    private void createNewHelpRequestNotification(HelpRequest helpRequest) {
        Query query = mDatabaseReference.child("Helpers").orderByKey().equalTo(helpRequest.getRequesterid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dss : dataSnapshot.getChildren()) {
                        final Helper helper = dss.getValue(Helper.class);
                        requesterName = helper.getName();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Intent notificationIntent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,notificationIntent, 0);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("New help request sent to user notification.");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(requesterName)
                .setContentText("Prośba o pomoc w obszarze: " + helpRequest.getArea())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
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
