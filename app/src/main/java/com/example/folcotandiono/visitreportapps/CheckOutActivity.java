package com.example.folcotandiono.visitreportapps;

import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CheckOutActivity extends AppCompatActivity {
    private FirebaseAuth checkOutAuth;
    private FirebaseAuth.AuthStateListener checkOutAuthListener;
    private FirebaseDatabase checkOutDatabase;

    private Calendar checkOutCalendar;
    private String circleName;
    private String date;

    private Toolbar checkOutToolbar;
    private RecyclerView checkOutRecyclerView;
    private RecyclerView.Adapter checkOutAdapter;
    private RecyclerView.LayoutManager checkOutLayoutManager;
    private Parcelable recyclerViewState;

    private ArrayList<String> address = new ArrayList<String>();
    private ArrayList<String> placeName = new ArrayList<String>();
    private ArrayList<LatLng> latlng = new ArrayList<LatLng>();
    private ArrayList<String> checkIn = new ArrayList<String>();
    private ArrayList<String> checkOut = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            startActivity(new Intent(CheckOutActivity.this, LoginActivity.class));
        }

        initView();
        initObject();
        initListener();
        initRecyclerView();
    }

    private void initRecyclerView() {
        checkOutDatabase.getReference("User").child(checkOutAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final int pos = Integer.valueOf(dataSnapshot.child("checkIn").child("pos").getValue().toString());
                circleName = (String) dataSnapshot.child("checkIn").child("circle").getValue();
                date = (String) dataSnapshot.child("checkIn").child("date").getValue();

                checkOutDatabase.getReference("VisitPlan").child(checkOutAuth.getCurrentUser().getUid()).child(circleName).child(date).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("address").getValue() != null) {
//                    address = new ArrayList<String>();
//                    for (DataSnapshot snapshot : dataSnapshot.child("address").getChildren()) {
//                        address.add((String) snapshot.getValue());
//                    }
                            address = (ArrayList<String>) dataSnapshot.child("address").getValue();
                        }
                        else address = new ArrayList<String>();

                        if (dataSnapshot.child("placeName").getValue() != null) {
//                    placeName = new ArrayList<String>();
//                    for (DataSnapshot snapshot : dataSnapshot.child("placeName").getChildren()) {
//                        placeName.add((String) snapshot.getValue());
//                    }
                            placeName = (ArrayList<String>) dataSnapshot.child("placeName").getValue();
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
//                    checkIn = new ArrayList<String>();
//                    for (DataSnapshot snapshot : dataSnapshot.child("checkIn").getChildren()) {
//                        checkIn.add((String) snapshot.getValue());
//                    }
                            checkIn = (ArrayList<String>) dataSnapshot.child("checkIn").getValue();
                        }
                        else checkIn = new ArrayList<String>();

                        if (dataSnapshot.child("checkOut").getValue() != null) {
//                    checkOut = new ArrayList<String>();
//                    for (DataSnapshot snapshot : dataSnapshot.child("checkOut").getChildren()) {
//                        checkOut.add((String) snapshot.getValue());
//                    }
                            checkOut = (ArrayList<String>) dataSnapshot.child("checkOut").getValue();
                        }
                        else checkOut = new ArrayList<String>();

                        if (address.size() == placeName.size() && placeName.size() == latlng.size() && latlng.size() == checkIn.size() && checkIn.size() == checkOut.size()) {
                            recyclerViewState = checkOutRecyclerView.getLayoutManager().onSaveInstanceState();

                            checkOutAdapter = new CheckOutAdapter(placeName, address, checkOut, circleName, date, latlng, pos);
//                    checkOutAdapter = new CheckOutAdapter();
                            checkOutRecyclerView.setAdapter(checkOutAdapter);

                            checkOutRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initListener() {
    }

    private void initObject() {
        checkOutCalendar = Calendar.getInstance();
        checkOutAuth = FirebaseAuth.getInstance();

        checkOutAuthListener = new FirebaseAuth.AuthStateListener() {
            public static final String TAG = "CheckOutActivity";

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

        checkOutDatabase = FirebaseDatabase.getInstance();

        checkOutRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        checkOutLayoutManager = new LinearLayoutManager(this);
        checkOutRecyclerView.setLayoutManager(checkOutLayoutManager);

        setSupportActionBar(checkOutToolbar);
        getSupportActionBar().setTitle("Check Out");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initView() {
        checkOutToolbar = (Toolbar) findViewById(R.id.checkOutToolbar);

        checkOutRecyclerView = (RecyclerView) findViewById(R.id.checkOutRecyclerView);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        checkOutAuth.addAuthStateListener(checkOutAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (checkOutAuthListener != null) {
            checkOutAuth.removeAuthStateListener(checkOutAuthListener);
        }
    }

}
