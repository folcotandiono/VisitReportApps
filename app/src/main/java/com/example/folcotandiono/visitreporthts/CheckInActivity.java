package com.example.folcotandiono.visitreporthts;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CheckInActivity extends AppCompatActivity {

    private FirebaseAuth checkInAuth;
    private FirebaseAuth.AuthStateListener checkInAuthListener;
    private FirebaseDatabase checkInDatabase;

    private Calendar checkInCalendar;
    private String circleName;
    private String date;

    private Toolbar checkInToolbar;
    private RecyclerView checkInRecyclerView;
    private RecyclerView.Adapter checkInAdapter;
    private RecyclerView.LayoutManager checkInLayoutManager;

    private ArrayList<String> address = new ArrayList<String>();
    private ArrayList<String> placeName = new ArrayList<String>();
    private ArrayList<LatLng> latlng = new ArrayList<LatLng>();
    private ArrayList<String> checkIn = new ArrayList<String>();
    private ArrayList<String> checkOut = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            startActivity(new Intent(CheckInActivity.this, LoginActivity.class));
        }

        initView();
        initObject();
        initListener();
        initRecyclerView();
    }

    private void initRecyclerView() {
        checkInDatabase.getReference("VisitPlan").child(checkInAuth.getCurrentUser().getUid()).child(circleName).child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("address").getValue() != null) {
                    address = new ArrayList<String>();
                    for (DataSnapshot snapshot : dataSnapshot.child("address").getChildren()) {
                        address.add((String) snapshot.getValue());
                    }
                }
                else address = new ArrayList<String>();

                if (dataSnapshot.child("placeName").getValue() != null) {
                    placeName = new ArrayList<String>();
                    for (DataSnapshot snapshot : dataSnapshot.child("placeName").getChildren()) {
                        placeName.add((String) snapshot.getValue());
                    }
                }
                else placeName = new ArrayList<String>();

                if (dataSnapshot.child("latlng").getValue() != null) {
                    latlng = new ArrayList<LatLng>();
                    for (DataSnapshot snapshot : dataSnapshot.child("latlng").getChildren()) {
                        latlng.add(new LatLng((double) snapshot.child("latitude").getValue(), (double) snapshot.child("longitude").getValue()));
                }
                }
                else latlng = new ArrayList<LatLng>();

                if (dataSnapshot.child("checkIn").getValue() != null) {
                    checkIn = new ArrayList<String>();
                    for (DataSnapshot snapshot : dataSnapshot.child("checkIn").getChildren()) {
                        checkIn.add((String) snapshot.getValue());
                    }
                }
                else checkIn = new ArrayList<String>();

                if (dataSnapshot.child("checkOut").getValue() != null) {
                    checkOut = new ArrayList<String>();
                    for (DataSnapshot snapshot : dataSnapshot.child("checkOut").getChildren()) {
                        checkOut.add((String) snapshot.getValue());
                    }
                }
                else checkOut = new ArrayList<String>();

                if (address.size() == placeName.size() && placeName.size() == latlng.size() && latlng.size() == checkIn.size() && checkIn.size() == checkOut.size()) {
                    checkInAdapter = new CheckInAdapter(placeName, address, checkIn, circleName, date, latlng);
                    checkInRecyclerView.setAdapter(checkInAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initListener() {
    }

    private void initObject() {
        checkInCalendar = Calendar.getInstance();
        checkInAuth = FirebaseAuth.getInstance();

        checkInAuthListener = new FirebaseAuth.AuthStateListener() {
            public static final String TAG = "CheckInActivity";

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        checkInDatabase = FirebaseDatabase.getInstance();

        checkInRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        checkInLayoutManager = new LinearLayoutManager(this);
        checkInRecyclerView.setLayoutManager(checkInLayoutManager);

        setSupportActionBar(checkInToolbar);
        getSupportActionBar().setTitle("Check In");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        circleName = getIntent().getStringExtra("circleName").toString();

        String formatTanggal = "dd-MM-yyyy";
        final SimpleDateFormat sdf = new SimpleDateFormat(formatTanggal);
        date = sdf.format(checkInCalendar.getTime());
    }

    private void initView() {
        checkInToolbar = (Toolbar) findViewById(R.id.checkInToolbar);

        checkInRecyclerView = (RecyclerView) findViewById(R.id.checkInRecyclerView);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        checkInAuth.addAuthStateListener(checkInAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (checkInAuthListener != null) {
            checkInAuth.removeAuthStateListener(checkInAuthListener);
        }
    }

}
