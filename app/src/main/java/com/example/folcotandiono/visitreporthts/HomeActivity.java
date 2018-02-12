package com.example.folcotandiono.visitreporthts;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private DrawerLayout homeDrawerLayout;
    private NavigationView homeNavigationView;
    private ListView listView;
    private ActionBarDrawerToggle toggle;
    private Toolbar homeToolbar;
    private GoogleMap homeGoogleMap;

    private FirebaseAuth homeAuth;
    private FirebaseAuth.AuthStateListener homeAuthListener;
    private FirebaseDatabase homeDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        }

        initView();
        initObject();

        homeDatabase.getReference("User").orderByChild("role").equalTo(0).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                User user = dataSnapshot.getValue(User.class);
                Log.d("HomeActivity", user.getEmail());
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

            // ...
        });

        if (googleServicesAvailable()) {
            initMap();
        }
    }

    private void initObject() {
        homeAuth = FirebaseAuth.getInstance();
        homeAuthListener = new FirebaseAuth.AuthStateListener() {
            public static final String TAG = "HomeActivity";

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
        homeDatabase = FirebaseDatabase.getInstance();
    }

    // https://www.youtube.com/watch?v=lchyOhPREh4
    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.homeMapFragment);
        mapFragment.getMapAsync(this);
    }

    private void initView() {
        homeToolbar = (Toolbar) findViewById(R.id.nav_action);
        setSupportActionBar(homeToolbar);

        homeDrawerLayout = (DrawerLayout) findViewById(R.id.homeDrawerLayout);
        toggle = new ActionBarDrawerToggle(this, homeDrawerLayout, R.string.open, R.string.close);
        homeDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // https://www.youtube.com/watch?v=lchyOhPREh4
    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();

        int isAvailable = api.isGooglePlayServicesAvailable(this);

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        }
        else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        }
        else {
            Toast.makeText(this, "Can't connect to play services", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        homeGoogleMap = googleMap;
    }

    @Override
    public void onStart() {
        super.onStart();
        homeAuth.addAuthStateListener(homeAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (homeAuthListener != null) {
            homeAuth.removeAuthStateListener(homeAuthListener);
        }
    }
}
