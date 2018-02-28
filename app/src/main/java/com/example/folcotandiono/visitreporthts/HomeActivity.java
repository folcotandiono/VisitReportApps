package com.example.folcotandiono.visitreporthts;

import android.*;
import android.Manifest;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout homeDrawerLayout;
    private NavigationView homeNavigationView;
    private ListView listView;
    private ActionBarDrawerToggle toggle;
    private Toolbar homeToolbar;
    private Menu homeMenu;
    private SubMenu homeSubMenu;
    private RecyclerView homeRecyclerView;
    private RecyclerView.Adapter homeAdapter;
    private RecyclerView.LayoutManager homeLayoutManager;

    private FirebaseAuth homeAuth;
    private FirebaseAuth.AuthStateListener homeAuthListener;
    private FirebaseDatabase homeDatabase;

    private GoogleMap mMap;
    private static FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private static LocationCallback mLocationCallback;

    private Boolean circleChosen;
    private String circleChosenStr;
    private AddressResultReceiver mResultReceiver;
    private Location mLastLocation;
    private Parcelable recyclerViewState;

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            String mAddressOutput = resultData.getString("address");

            // Show a toast message if an address was found.
            if (resultCode == 0) {
                homeDatabase.getReference("User").child(homeAuth.getCurrentUser().getUid()).child("address").setValue(mAddressOutput);
            }

        }
    }

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
        initMyCircles();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_checkin, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initObject() {
        mResultReceiver = new AddressResultReceiver(new Handler());

        circleChosen = false;

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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.homeMapFragment);
        mapFragment.getMapAsync(this);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        homeRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        homeLayoutManager = new LinearLayoutManager(this);
        homeRecyclerView.setLayoutManager(homeLayoutManager);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location temp = null;
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    temp = location;
                }
                if (temp != null) {
                    mMap.clear();
                    homeDatabase.getReference("User").child(homeAuth.getCurrentUser().getUid()).child("lat").setValue(temp.getLatitude());
                    homeDatabase.getReference("User").child(homeAuth.getCurrentUser().getUid()).child("lng").setValue(temp.getLongitude());

                    if (Geocoder.isPresent()) {

                        mLastLocation = new Location("");
                        mLastLocation.setLatitude(temp.getLatitude());
                        mLastLocation.setLongitude(temp.getLongitude());

                        startIntentService();
                    }

                    if (circleChosen) {
                        homeDatabase.getReference("Circle").child(circleChosenStr).child("id").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final ArrayList<String> name = new ArrayList<String>();
                                final ArrayList<String> address = new ArrayList<String>();
                                recyclerViewState = homeRecyclerView.getLayoutManager().onSaveInstanceState();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    homeDatabase.getReference("User").child((String) snapshot.getValue()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            double lat = (double) dataSnapshot.child("lat").getValue();
                                            double lng = (double) dataSnapshot.child("lng").getValue();
                                            mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(lat, lng))
                                                    .title((String) dataSnapshot.child("name").getValue()));

                                            name.add((String) dataSnapshot.child("name").getValue());
                                            address.add((String) dataSnapshot.child("address").getValue());

                                            homeAdapter = new HomeAdapter(name, address);
                                            homeRecyclerView.setAdapter(homeAdapter);
                                            homeRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }

//                                homeAdapter = new HomeAdapter(name, address);
//                                homeRecyclerView.setAdapter(homeAdapter);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                }
            }
        };

        homeNavigationView.setNavigationItemSelectedListener(this);

        homeMenu = homeNavigationView.getMenu();
        homeSubMenu = homeMenu.addSubMenu("My Circles");
    }

    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra("receiver", mResultReceiver);
        intent.putExtra("location", mLastLocation);
        startService(intent);
    }

    private void initMyCircles() {
        homeDatabase.getReference("User").child(homeAuth.getCurrentUser().getUid())
                .child("circle").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                homeSubMenu.clear();
                int cnt = Menu.FIRST;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    homeSubMenu.add(0, cnt++, Menu.NONE, (CharSequence) snapshot.getValue());
                }
                homeNavigationView.setNavigationItemSelectedListener(HomeActivity.this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                        }
                    }
                });
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void initView() {
        homeToolbar = (Toolbar) findViewById(R.id.nav_action);
        setSupportActionBar(homeToolbar);

        homeDrawerLayout = (DrawerLayout) findViewById(R.id.homeDrawerLayout);
        toggle = new ActionBarDrawerToggle(this, homeDrawerLayout, R.string.open, R.string.close);
        homeDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        homeNavigationView = (NavigationView) findViewById(R.id.homeNavigationView);

        homeRecyclerView = (RecyclerView) findViewById(R.id.homeRecyclerView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == R.id.menu_checkin) {
            if (circleChosen) {
                Intent intent = new Intent(HomeActivity.this, CheckInActivity.class);
                intent.putExtra("circleName", circleChosenStr);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Choose Circle", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (item.getItemId() == R.id.menu_checkout) {
            if (circleChosen) {
                Intent intent = new Intent(HomeActivity.this, CheckOutActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Choose Circle", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();

        if (circleChosen) {
            homeDatabase.getReference("User").child(homeAuth.getCurrentUser().getUid()).child("circle").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<String> circle = new ArrayList<>();
                    if (dataSnapshot.getValue() != null)
                        circle = (ArrayList<String>) dataSnapshot.getValue();

                    Boolean ada = false;

                    for (String circleStr : circle) {
                        if (circleStr.equals(circleChosenStr)) {
                            ada = true;
                            break;
                        }
                    }

                    if (!ada) {
                        circleChosen = false;
                        circleChosenStr = "";
                        getSupportActionBar().setTitle("VisitReportHTS");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        stopLocationUpdates();
    }

    public static void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        if (item.getItemId() == R.id.drawerMenuCreateCycle) {
//            finish();
            Intent intent = new Intent(HomeActivity.this, CreateCircleActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.drawerMenuVisitPlan) {
            if (circleChosen) {
                Intent intent = new Intent(HomeActivity.this, VisitPlanActivity.class);
                intent.putExtra("circleName", circleChosenStr);
                intent.putExtra("uid", homeAuth.getCurrentUser().getUid());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Choose Circle", Toast.LENGTH_SHORT).show();
            }
        } else if(item.getItemId() == R.id.drawerMenuCustomer) {
            Intent intent = new Intent(HomeActivity.this, CustomerActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.drawerMenuLogOut) {
            finish();
            homeAuth.signOut();
            stopLocationUpdates();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (item.getItemId() == R.id.drawerMenuJoinCycle) {
            Intent intent = new Intent(HomeActivity.this, JoinCircleActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.drawerMenuSettings) {
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else {
            homeDatabase.getReference("User").child(homeAuth.getCurrentUser().getUid())
                    .child("circle").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int cnt = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (item.getItemId() == homeSubMenu.getItem(cnt++).getItemId()) {
                            circleChosen = true;
                            circleChosenStr = (String) homeSubMenu.getItem(cnt - 1).getTitle();
                            getSupportActionBar().setTitle(homeSubMenu.getItem(cnt - 1).getTitle());
                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        homeDrawerLayout.closeDrawers();

        return false;
    }
}
