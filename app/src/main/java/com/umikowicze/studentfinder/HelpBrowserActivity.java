package com.umikowicze.studentfinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HelpBrowserActivity extends AppCompatActivity{

    private ArrayList<String> areas;
    private ArrayAdapter<String> areasAdapter;
    private AutoCompleteTextView searchField;
    private ListView helpersListView;
    private HelperAdapter helperAdapter;

    private FirebaseDatabase mFirebaseReference;
    private DatabaseReference mHelpersDatabaseReference;

    public HelpBrowserActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_browser);

        areas = new ArrayList<>();
        areas.addAll(Arrays.asList(getResources().getStringArray(R.array.areas)));

        areasAdapter = new ArrayAdapter<>(HelpBrowserActivity.this, android.R.layout.simple_dropdown_item_1line, areas);

        searchField = (AutoCompleteTextView) findViewById(R.id.searchField);
        searchField.setAdapter(areasAdapter);

        List<Helper> helperList = new ArrayList<Helper>();
        helperAdapter = new HelperAdapter(this, R.layout.item_helper, helperList);

        helpersListView = findViewById(R.id.helpersListView);
        helpersListView.setAdapter(helperAdapter);

        mFirebaseReference = FirebaseDatabase.getInstance();
        mHelpersDatabaseReference = mFirebaseReference.getReference().child("Helpers");

        mHelpersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dss : dataSnapshot.getChildren()) {
                    Helper helper = dss.getValue(Helper.class);
                    helperAdapter.add(helper);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
