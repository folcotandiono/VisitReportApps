package com.example.folcotandiono.visitreporthts;

import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.folcotandiono.visitreporthts.HomeActivity.stopLocationUpdates;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth settingsAuth;
    private FirebaseAuth.AuthStateListener settingsAuthListener;
    private FirebaseDatabase settingsDatabase;

    private Toolbar settingsToolbar;
    private EditText settingsName;
    private TextView settingsEmail;
    private TextView settingsCircleOptions;
    private TextView settingsChangePassword;
    private TextView settingsLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
        }

        initView();
        initObject();
        initListener();
    }

    private void initListener() {
        settingsCircleOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, CircleOptionsActivity.class);
                startActivity(intent);
            }
        });
        settingsChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
        settingsLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                settingsAuth.signOut();
                stopLocationUpdates();
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settingsSave) {
            String name = settingsName.getText().toString();

            if (name.isEmpty()) {
                Toast.makeText(SettingsActivity.this, "Name is empty", Toast.LENGTH_SHORT).show();
                return false;
            }

            settingsDatabase.getReference("User").child(settingsAuth.getCurrentUser().getUid()).child("name").setValue(name);
            Toast.makeText(SettingsActivity.this, "Settings saved", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initObject() {
        settingsAuth = FirebaseAuth.getInstance();

        settingsAuthListener = new FirebaseAuth.AuthStateListener() {
            public static final String TAG = "Settings";

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

        settingsDatabase = FirebaseDatabase.getInstance();

        settingsDatabase.getReference("User").child(settingsAuth.getCurrentUser().getUid()).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                settingsEmail.setText((CharSequence) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        settingsToolbar = (Toolbar) findViewById(R.id.settingsToolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        putName();
    }

    private void putName() {
        settingsDatabase.getReference("User").child(settingsAuth.getCurrentUser().getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                settingsName.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initView() {
        settingsName = (EditText) findViewById(R.id.settingsName);

        settingsEmail = (TextView) findViewById(R.id.settingsEmail);

        settingsCircleOptions = (TextView) findViewById(R.id.settingsCircleOptions);

        settingsChangePassword = (TextView) findViewById(R.id.settingsChangePassword);

        settingsLogOut = (TextView) findViewById(R.id.settingsLogOut);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        settingsAuth.addAuthStateListener(settingsAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (settingsAuthListener != null) {
            settingsAuth.removeAuthStateListener(settingsAuthListener);
        }
    }

    @Override
    protected void onResume() {
        putName();
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
