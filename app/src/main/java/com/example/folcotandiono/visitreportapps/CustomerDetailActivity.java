package com.example.folcotandiono.visitreportapps;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class CustomerDetailActivity extends AppCompatActivity {

    private FirebaseAuth customerDetailAuth;
    private FirebaseAuth.AuthStateListener customerDetailAuthListener;
    private FirebaseDatabase customerDetailDatabase;
    private FirebaseStorage customerDetailStorage;

    private Toolbar customerDetailToolbar;
    private TextView customerDetailPlaceName;
    private TextView customerDetailAddress;
    private ImageView customerDetailPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            startActivity(new Intent(CustomerDetailActivity.this, LoginActivity.class));
        }

        initView();
        initObject();
        initListener();
    }

    private void initListener() {
    }

    private void initObject() {
        customerDetailAuth = FirebaseAuth.getInstance();

        customerDetailAuthListener = new FirebaseAuth.AuthStateListener() {
            public static final String TAG = "CustomerDetailActivity";

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

        customerDetailDatabase = FirebaseDatabase.getInstance();

        setSupportActionBar(customerDetailToolbar);
        getSupportActionBar().setTitle("Customer Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        customerDetailDatabase.getReference("Customer").child(getIntent().getStringExtra("placeName")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                customerDetailPlaceName.setText(getIntent().getStringExtra("placeName"));
                customerDetailAddress.setText(dataSnapshot.child("address").getValue().toString());

                String url = dataSnapshot.child("url").getValue().toString();
                // Reference to an image file in Firebase Storage
                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);

                // Load the image using Glide
                Glide.with(CustomerDetailActivity.this)
                        .using(new FirebaseImageLoader())
                        .load(storageReference)
                        .into(customerDetailPhoto);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initView() {
        customerDetailToolbar = findViewById(R.id.customerDetailToolbar);
        customerDetailPlaceName = findViewById(R.id.customerDetailPlaceName);
        customerDetailAddress = findViewById(R.id.customerDetailAddress);
        customerDetailPhoto = findViewById(R.id.customerDetailPhoto);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        customerDetailAuth.addAuthStateListener(customerDetailAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (customerDetailAuthListener != null) {
            customerDetailAuth.removeAuthStateListener(customerDetailAuthListener);
        }
    }
}
