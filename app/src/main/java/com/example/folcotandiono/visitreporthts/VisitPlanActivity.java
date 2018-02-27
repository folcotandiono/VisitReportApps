package com.example.folcotandiono.visitreporthts;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
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

public class VisitPlanActivity extends AppCompatActivity {

    private FirebaseAuth visitPlanAuth;
    private FirebaseAuth.AuthStateListener visitPlanAuthListener;
    private FirebaseDatabase visitPlanDatabase;

    private Calendar visitPlanCalendar;
    private String circleName;

    private Toolbar visitPlanToolbar;
    private Button visitPlanChooseDate;
    private TextView visitPlanDate;
    private Button visitPlanAddPlace;
    private RecyclerView visitPlanRecyclerView;
    private RecyclerView.Adapter visitPlanAdapter;
    private RecyclerView.LayoutManager visitPlanLayoutManager;
    private PlacePicker.IntentBuilder visitPlanPlacePickerIntentBuilder;
    private Parcelable recyclerViewState;

    int PLACE_PICKER_REQUEST = 1;

    private ArrayList<String> placeName = new ArrayList<String>();
    private ArrayList<String> address = new ArrayList<String>();
    private ArrayList<LatLng> latlng = new ArrayList<LatLng>();
    private ArrayList<String> checkIn = new ArrayList<String>();
    private ArrayList<String> checkOut = new ArrayList<String>();

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_plan);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            startActivity(new Intent(VisitPlanActivity.this, LoginActivity.class));
        }
        
        initView();
        initObject();
        initListener();
    }

    private void initListener() {
        visitPlanChooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(VisitPlanActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        visitPlanCalendar.set(Calendar.YEAR, year);
                        visitPlanCalendar.set(Calendar.MONTH, month);
                        visitPlanCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String formatTanggal = "dd-MM-yyyy";
                        final SimpleDateFormat sdf = new SimpleDateFormat(formatTanggal);
                        visitPlanDate.setText(sdf.format(visitPlanCalendar.getTime()));

                        visitPlanDatabase.getReference("VisitPlan").child(uid).child(circleName).child(visitPlanDate.getText().toString()).addValueEventListener(new ValueEventListener() {
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
                                    recyclerViewState = visitPlanRecyclerView.getLayoutManager().onSaveInstanceState();

                                    visitPlanAdapter = new VisitPlanAdapter(placeName, address, latlng, sdf.format(visitPlanCalendar.getTime()), circleName, checkIn, checkOut, uid);
                                    visitPlanRecyclerView.setAdapter(visitPlanAdapter);

                                    visitPlanRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                },
                        visitPlanCalendar.get(Calendar.YEAR), visitPlanCalendar.get(Calendar.MONTH),
                        visitPlanCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        visitPlanAddPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (visitPlanDate.getText().toString().isEmpty()) {
                    Toast.makeText(VisitPlanActivity.this, "Date should not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    startActivityForResult(visitPlanPlacePickerIntentBuilder.build(VisitPlanActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                final Place place = PlacePicker.getPlace(data, this);

                placeName.add(place.getName().toString());

                ArrayList<String> addresses = new ArrayList<>();
                addresses.add(place.getAddress().toString());
                address.add(addresses.get(0));

                latlng.add(place.getLatLng());
                checkIn.add("");
                checkOut.add("");

                visitPlanDatabase.getReference("VisitPlan").child(visitPlanAuth.getCurrentUser().getUid()).child(circleName).child(visitPlanDate.getText().toString()).child("placeName").setValue(placeName);
                visitPlanDatabase.getReference("VisitPlan").child(visitPlanAuth.getCurrentUser().getUid()).child(circleName).child(visitPlanDate.getText().toString()).child("address").setValue(address);
                visitPlanDatabase.getReference("VisitPlan").child(visitPlanAuth.getCurrentUser().getUid()).child(circleName).child(visitPlanDate.getText().toString()).child("latlng").setValue(latlng);
                visitPlanDatabase.getReference("VisitPlan").child(visitPlanAuth.getCurrentUser().getUid()).child(circleName).child(visitPlanDate.getText().toString()).child("checkIn").setValue(checkIn);
                visitPlanDatabase.getReference("VisitPlan").child(visitPlanAuth.getCurrentUser().getUid()).child(circleName).child(visitPlanDate.getText().toString()).child("checkOut").setValue(checkOut);

                Toast.makeText(this, "Place is added", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initObject() {
        visitPlanCalendar = Calendar.getInstance();
        visitPlanAuth = FirebaseAuth.getInstance();

        visitPlanAuthListener = new FirebaseAuth.AuthStateListener() {
            public static final String TAG = "VisitPlanActivity";

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

        visitPlanDatabase = FirebaseDatabase.getInstance();
        // use a linear layout manager
        visitPlanLayoutManager = new LinearLayoutManager(this);
        visitPlanRecyclerView.setLayoutManager(visitPlanLayoutManager);

        setSupportActionBar(visitPlanToolbar);
        getSupportActionBar().setTitle("Visit Plan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        visitPlanPlacePickerIntentBuilder = new PlacePicker.IntentBuilder();

        circleName = getIntent().getStringExtra("circleName").toString();

        uid = getIntent().getStringExtra("uid").toString();

        if (!uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            visitPlanAddPlace.setVisibility(View.GONE);
        }
    }

    private void initView() {
        visitPlanChooseDate = (Button) findViewById(R.id.visitPlanChooseDate);
        visitPlanDate = (TextView) findViewById(R.id.visitPlanDate);
        visitPlanAddPlace = (Button) findViewById(R.id.visitPlanAddPlace);

        visitPlanRecyclerView = (RecyclerView) findViewById(R.id.visitPlanRecyclerView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        visitPlanRecyclerView.setHasFixedSize(true);

        visitPlanToolbar = (Toolbar) findViewById(R.id.visitPlanToolbar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        visitPlanAuth.addAuthStateListener(visitPlanAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (visitPlanAuthListener != null) {
            visitPlanAuth.removeAuthStateListener(visitPlanAuthListener);
        }
    }
}
