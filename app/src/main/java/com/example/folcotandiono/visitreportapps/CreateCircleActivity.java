package com.example.folcotandiono.visitreportapps;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class CreateCircleActivity extends AppCompatActivity {

    private Toolbar createCircleToolbar;
    private EditText createCircleName;
    private Button createCircleButton;

    private FirebaseAuth createCircleAuth;
    private FirebaseAuth.AuthStateListener createCircleAuthListener;
    private FirebaseDatabase createCircleDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_circle);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            startActivity(new Intent(CreateCircleActivity.this, LoginActivity.class));
        }

        initView();
        initObject();
        initListener();
    }

    private void initObject() {
        createCircleAuth = FirebaseAuth.getInstance();
        createCircleAuthListener = new FirebaseAuth.AuthStateListener() {
            public static final String TAG = "CreateCircleActivity";

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
        createCircleDatabase = FirebaseDatabase.getInstance();
    }

    // https://stackoverflow.com/questions/20536566/creating-a-random-string-with-a-z-and-0-9-in-java
    private String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 6) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    private void initListener() {
        createCircleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = createCircleName.getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(CreateCircleActivity.this, "Circle name is empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                createCircleDatabase.getReference("Circle").child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            Toast.makeText(CreateCircleActivity.this, "Circle already exists", Toast.LENGTH_SHORT).show();

                        } else {

                            createCircleDatabase.getReference("Circle").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final Boolean[] generate = {true};

                                    while (generate[0]) {
                                        final String random = getSaltString();

                                        Boolean bisa = true;
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            if (snapshot.child("code").getValue().equals(random)) {
                                                bisa = false;
                                                break;
                                            }
                                        }
                                        if (bisa) {
                                            final Circle circle = new Circle();

                                            ArrayList<String> listId = new ArrayList<String>();
                                            listId.add(createCircleAuth.getCurrentUser().getUid());
                                            circle.setId(listId);
                                            circle.setIdUser(createCircleAuth.getCurrentUser().getUid());
                                            generate[0] = false;
                                            circle.setCode(random);

                                            createCircleDatabase.getReference("Circle").child(name).setValue(circle);

                                            createCircleDatabase.getReference("User").child(createCircleAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.getValue() != null) {
                                                        ArrayList<String> circles = (ArrayList<String>) dataSnapshot.child("circle").getValue();
                                                        if (circles == null)
                                                            circles = new ArrayList<String>();

                                                        circles.add(name);

                                                        createCircleDatabase.getReference("User").child(createCircleAuth.getCurrentUser().getUid()).child("circle").setValue(circles);

                                                        Toast.makeText(CreateCircleActivity.this, "Circle added", Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }


    private void initView() {
        createCircleToolbar = (Toolbar) findViewById(R.id.createCircleToolbar);
        setSupportActionBar(createCircleToolbar);
        getSupportActionBar().setTitle("Create Circle");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        createCircleButton = (Button) findViewById(R.id.createCircleButton);

        createCircleName = (EditText) findViewById(R.id.createCircleName);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        createCircleAuth.addAuthStateListener(createCircleAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (createCircleAuthListener != null) {
            createCircleAuth.removeAuthStateListener(createCircleAuthListener);
        }
    }
}
