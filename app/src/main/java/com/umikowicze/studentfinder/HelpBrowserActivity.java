package com.umikowicze.studentfinder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
    private Button button;
    private String lastSearch;

    private FirebaseDatabase mFirebaseReference;
    private DatabaseReference mHelpersDatabaseReference;

    public HelpBrowserActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_browser);

        mFirebaseReference = FirebaseDatabase.getInstance();
        mHelpersDatabaseReference = mFirebaseReference.getReference();

        areas = new ArrayList<>();
        areas.addAll(Arrays.asList(getResources().getStringArray(R.array.areas)));

        areasAdapter = new ArrayAdapter<>(HelpBrowserActivity.this, android.R.layout.simple_dropdown_item_1line, areas);

        searchField = (AutoCompleteTextView) findViewById(R.id.searchField);
        searchField.setAdapter(areasAdapter);

        final List<Helper> helperList = new ArrayList<Helper>();
        helperAdapter = new HelperAdapter(this, R.layout.item_helper, helperList);

        helpersListView = findViewById(R.id.helpersListView);
        helpersListView.setAdapter(helperAdapter);

        button = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helperAdapter.clear();
                String area = searchField.getText().toString();
                lastSearch = searchField.getText().toString();
                Query query = mHelpersDatabaseReference.child("HelpOffers").orderByChild("area").equalTo(area);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot dss : dataSnapshot.getChildren()) {
                                HelpOffer helpOffer = dss.getValue(HelpOffer.class);
                                Query query2 = mHelpersDatabaseReference.child("Helpers").orderByKey().equalTo(helpOffer.getUserid());
                                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        for (DataSnapshot dss2 : dataSnapshot.getChildren()) {
                                            Helper helper = dss2.getValue(Helper.class);
                                            helperAdapter.add(helper);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                        else
                            Toast.makeText(HelpBrowserActivity.this, "Brak użytkowników oferujących pomoc w tym zakresie.", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        final Context context = this;
        helpersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Helper helper = (Helper) adapterView.getItemAtPosition(i);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Pomoc w: " + lastSearch);
                builder.setMessage("Czy chcesz poprosić użytkownika " + helper.getName() + " o pomoc?");

                builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(HelpBrowserActivity.this, "Poprosiłeś o pomoc, ale jeszcze nie dodaliśmy tej funkcjonalności.", Toast.LENGTH_LONG).show();
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
            }
        });
    }
}
