package com.example.folcotandiono.visitreportapps;

import android.content.Intent;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class AddVisitPlanActivity extends AppCompatActivity {

    private FirebaseAuth addVisitPlanAuth;
    private FirebaseAuth.AuthStateListener addVisitPlanAuthListener;
    private FirebaseDatabase addVisitPlanDatabase;

    private Toolbar addVisitPlanToolbar;
    private RecyclerView addVisitPlanRecyclerView;
    private RecyclerView.Adapter addVisitPlanAdapter;
    private RecyclerView.LayoutManager addVisitPlanLayoutManager;
    private Parcelable recyclerViewState;

    private ArrayList<String> placeName = new ArrayList<String>();
    private ArrayList<String> address = new ArrayList<String>();
    private ArrayList<LatLng> latlng = new ArrayList<LatLng>();
    private ArrayList<String> checkIn = new ArrayList<String>();
    private ArrayList<String> checkOut = new ArrayList<String>();
    private String circleName;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_visit_plan);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            startActivity(new Intent(AddVisitPlanActivity.this, LoginActivity.class));
        }

        initView();
        initObject();
        initListener();
        initRecyclerView();
    }

    private void initRecyclerView() {
        addVisitPlanDatabase.getReference("Customer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

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
        addVisitPlanAuth = FirebaseAuth.getInstance();

        addVisitPlanAuthListener = new FirebaseAuth.AuthStateListener() {
            public static final String TAG = "AddVisitPlanActivity";

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

        addVisitPlanDatabase = FirebaseDatabase.getInstance();

        addVisitPlanRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        addVisitPlanLayoutManager = new LinearLayoutManager(this);
        addVisitPlanRecyclerView.setLayoutManager(addVisitPlanLayoutManager);

        setSupportActionBar(addVisitPlanToolbar);
        getSupportActionBar().setTitle("Customer");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (getIntent().getSerializableExtra("placeName") != null) this.placeName = (ArrayList<String>) getIntent().getSerializableExtra("placeName");
        if (getIntent().getSerializableExtra("address") != null) this.address = (ArrayList<String>) getIntent().getSerializableExtra("address");
        if (getIntent().getSerializableExtra("latlng") != null) this.latlng = (ArrayList<LatLng>) getIntent().getSerializableExtra("latlng");
        if (getIntent().getSerializableExtra("checkIn") != null) this.checkIn = (ArrayList<String>) getIntent().getSerializableExtra("checkIn");
        if (getIntent().getSerializableExtra("checkOut") != null) this.checkOut = (ArrayList<String>) getIntent().getSerializableExtra("checkOut");
        this.circleName = getIntent().getStringExtra("circleName");
        this.date = getIntent().getStringExtra("date");

        addVisitPlanDatabase.getReference("Customer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> placeNameAdapter = new ArrayList<>();
                ArrayList<String> addressAdapter = new ArrayList<>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    placeNameAdapter.add(dataSnapshot1.getKey());
                    addressAdapter.add((String) dataSnapshot1.child("address").getValue());

                    addVisitPlanAdapter = new AddVisitPlanAdapter(placeName, address, latlng, checkIn, checkOut, circleName, date, placeNameAdapter, addressAdapter);
                    addVisitPlanRecyclerView.setAdapter(addVisitPlanAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initView() {
        addVisitPlanToolbar = findViewById(R.id.addVisitPlanToolbar);
        addVisitPlanRecyclerView = findViewById(R.id.addVisitPlanRecyclerView);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        addVisitPlanAuth.addAuthStateListener(addVisitPlanAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (addVisitPlanAuthListener != null) {
            addVisitPlanAuth.removeAuthStateListener(addVisitPlanAuthListener);
        }
    }
}
