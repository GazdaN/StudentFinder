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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class HelpBrowserActivity extends AppCompatActivity{

    private ArrayList<String> areas;
    private ArrayAdapter<String> areasAdapter;
    private HashMap<String, String> helpOffersUserIDs;
    private AutoCompleteTextView searchField;
    private ListView helpersListView;
    private HelperAdapter helperAdapter;
    private Button button;
    private String lastSearch;

    private FirebaseDatabase mFirebaseReference;
    private DatabaseReference mDatabaseReference;

    public HelpBrowserActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_browser);

        mFirebaseReference = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseReference.getReference();

        areas = new ArrayList<>();
        areas.addAll(Arrays.asList(getResources().getStringArray(R.array.areas)));

        areasAdapter = new ArrayAdapter<>(HelpBrowserActivity.this, android.R.layout.simple_dropdown_item_1line, areas);

        searchField = (AutoCompleteTextView) findViewById(R.id.searchField);
        searchField.setAdapter(areasAdapter);

        final List<Helper> helperList = new ArrayList<Helper>();
        helperAdapter = new HelperAdapter(this, R.layout.item_helper, helperList);

        helpersListView = findViewById(R.id.helpersListView);
        helpersListView.setAdapter(helperAdapter);

        helpOffersUserIDs = new HashMap<String, String>();

        button = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helperAdapter.clear();
                String area = searchField.getText().toString();
                lastSearch = searchField.getText().toString();
                Query query = mDatabaseReference.child("HelpOffers").orderByChild("area").equalTo(area);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot dss : dataSnapshot.getChildren()) {
                                final HelpOffer helpOffer = dss.getValue(HelpOffer.class);
                                Query query2 = mDatabaseReference.child("Helpers").orderByKey().equalTo(helpOffer.getUserid());
                                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        for (DataSnapshot dss2 : dataSnapshot.getChildren()) {
                                            Helper helper = dss2.getValue(Helper.class);
                                            helperAdapter.add(helper);
                                            helpOffersUserIDs.put(Integer.toString(helperAdapter.getCount() - 1), helpOffer.getUserid());
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
                final int position = i;
                Helper helper = (Helper) adapterView.getItemAtPosition(i);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Pomoc w: " + lastSearch);
                builder.setMessage("Czy chcesz poprosić użytkownika " + helper.getName() + " o pomoc?");

                builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        askForHelp(lastSearch, helpOffersUserIDs.get(Integer.toString(position)), FirebaseAuth.getInstance().getCurrentUser().getUid());
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

    private void askForHelp(String area, String helperid, String requesterid) {
        final String area1 = area;
        final String requesterid1 = requesterid;
        final String helperid1 = helperid;
        Query query = mDatabaseReference.child("HelpRequests");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    int counter = 0;
                    for (DataSnapshot dss : dataSnapshot.getChildren()) {
                        final HelpRequest helpRequest = dss.getValue(HelpRequest.class);
                        if (helpRequest.getArea().equals(area1) && helpRequest.getHelperid().equals(helperid1) && helpRequest.getRequesterid().equals(requesterid1)) {
                            counter++;
                            if (helpRequest.getStatus().equals("Sent"))
                                Toast.makeText(HelpBrowserActivity.this, "Wysłałeś już taką prośbę o pomoc. Musisz poczekać na zaakceptowanie jej przez drugą osobę.", Toast.LENGTH_LONG).show();
                            else if (helpRequest.getStatus().equals("Active"))
                                Toast.makeText(HelpBrowserActivity.this, "Ta osoba udziela Ci już pomocy w tym obszarze. Jeżeli chcesz poprosić o kolejną, musisz zamknąć poprzednią prośbę.", Toast.LENGTH_LONG).show();
                        }
                    }
                    if (counter == 0) {
                        final String date = DateFormat.getDateInstance().format(new Date());
                        HelpRequest newHelpRequest = new HelpRequest(area1, helperid1, requesterid1, "Sent", date);
                        mDatabaseReference.child("HelpRequests").push().setValue(newHelpRequest);
                        Toast.makeText(HelpBrowserActivity.this, "Wysłano prośbę o pomoc.", Toast.LENGTH_LONG).show();
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
