package com.example.folcotandiono.visitreporthts;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomerActivity extends AppCompatActivity {

    private FirebaseAuth customerAuth;
    private FirebaseAuth.AuthStateListener customerAuthListener;
    private FirebaseDatabase customerDatabase;
    private FirebaseStorage customerStorage;

    private Toolbar customerToolbar;
    private EditText customerFind;
    private RecyclerView customerRecyclerView;
    private RecyclerView.Adapter customerAdapter;
    private RecyclerView.LayoutManager customerLayoutManager;
    private Parcelable recyclerViewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            startActivity(new Intent(CustomerActivity.this, LoginActivity.class));
        }

        initView();
        initObject();
        initListener();
        initRecyclerView();
    }

    private void initRecyclerView() {

        customerDatabase.getReference("Customer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> placeName = new ArrayList<String>();
                final ArrayList<String> address = new ArrayList<String>();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    placeName.add(dataSnapshot1.getKey());
                    address.add((String) dataSnapshot1.child("address").getValue());

                    customerAdapter = new CustomerAdapter(placeName, address);
                    customerRecyclerView.setAdapter(customerAdapter);
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
        customerAuth = FirebaseAuth.getInstance();

        customerAuthListener = new FirebaseAuth.AuthStateListener() {
            public static final String TAG = "CustomerActivity";

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

        customerDatabase = FirebaseDatabase.getInstance();
        customerStorage = FirebaseStorage.getInstance();

        customerRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        customerLayoutManager = new LinearLayoutManager(this);
        customerRecyclerView.setLayoutManager(customerLayoutManager);

        setSupportActionBar(customerToolbar);
        getSupportActionBar().setTitle("Customer");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initView() {
        customerToolbar = (Toolbar) findViewById(R.id.customerToolbar);
        customerRecyclerView = findViewById(R.id.customerRecyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_customer, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.customerAdd) {
            Intent intent = new Intent(this, AddCustomerActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        customerAuth.addAuthStateListener(customerAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (customerAuthListener != null) {
            customerAuth.removeAuthStateListener(customerAuthListener);
        }
    }
}
