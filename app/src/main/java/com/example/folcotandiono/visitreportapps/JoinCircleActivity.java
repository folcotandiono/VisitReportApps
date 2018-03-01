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

public class JoinCircleActivity extends AppCompatActivity {

    private Toolbar joinCircleToolbar;
    private EditText joinCircleCode;
    private Button joinCircleButton;

    private FirebaseAuth joinCircleAuth;
    private FirebaseAuth.AuthStateListener joinCircleAuthListener;
    private FirebaseDatabase joinCircleDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_circle);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            startActivity(new Intent(JoinCircleActivity.this, LoginActivity.class));
        }

        initView();
        initObject();
        initListener();
    }

    private void initListener() {
        joinCircleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String code = joinCircleCode.getText().toString();
                if (code.isEmpty()) {
                    Toast.makeText(JoinCircleActivity.this, "Circle code is empty", Toast.LENGTH_SHORT).show();
                } else {
                    joinCircleDatabase.getReference("Circle").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Boolean adaCircle = false;
                            final String[] circleName = new String[1];

                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                if (dataSnapshot1.child("code").getValue().equals(code)) { // cek apakah terdapat circle dengan code ini
                                    adaCircle = true;
                                    circleName[0] = dataSnapshot1.getKey();
                                    ArrayList<String> circleUser = new ArrayList<String>();
                                    if (dataSnapshot.child(circleName[0]).child("id").getValue() != null) circleUser = (ArrayList<String>) dataSnapshot.child(circleName[0]).child("id").getValue();

                                    final ArrayList<String> finalCircleUser = circleUser;
                                    joinCircleDatabase.getReference("User").child(joinCircleAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Boolean ada = false; // cek apakah user telah bergabung ke cycle ini
                                            for (DataSnapshot snapshot : dataSnapshot.child("circle").getChildren()) {
                                                if (snapshot.getValue().equals(circleName[0])) {
                                                    ada = true;
                                                    break;
                                                }
                                            }
                                            if (!ada) {
                                                // dapatkan list user dari circle dan list circle dari user untuk ditambah nilainya
                                                final ArrayList<String>[] userCircle = new ArrayList[]{new ArrayList<String>()};

                                                if (dataSnapshot.child("circle").getValue() != null) {
                                                    userCircle[0] = (ArrayList<String>) dataSnapshot.child("circle").getValue();
                                                }
                                                userCircle[0].add(circleName[0]);
                                                finalCircleUser.add(joinCircleAuth.getCurrentUser().getUid());

                                                joinCircleDatabase.getReference("User").child(joinCircleAuth.getCurrentUser().getUid()).child("circle").setValue(userCircle[0]);
                                                joinCircleDatabase.getReference("Circle").child(circleName[0]).child("id").setValue(finalCircleUser);

                                                if (finalCircleUser.size() == 1) {
                                                    joinCircleDatabase.getReference("Circle").child(circleName[0]).child("idUser").setValue(joinCircleAuth.getCurrentUser().getUid());
                                                }

                                                Toast.makeText(JoinCircleActivity.this, "Circle is joined", Toast.LENGTH_SHORT).show();


                                            } else {
                                                Toast.makeText(JoinCircleActivity.this, "Circle already joined", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    break;
                                }
                            }
                            if (adaCircle == false) {
                                Toast.makeText(JoinCircleActivity.this, "No circle with code provided", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    private void initObject() {
        joinCircleAuth = FirebaseAuth.getInstance();
        joinCircleAuthListener = new FirebaseAuth.AuthStateListener() {
            public static final String TAG = "JoinCircleActivity";

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
        joinCircleDatabase = FirebaseDatabase.getInstance();
    }

    private void initView() {
        joinCircleToolbar = (Toolbar) findViewById(R.id.joinCircleToolbar);
        setSupportActionBar(joinCircleToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Join Circle");

        joinCircleCode = (EditText) findViewById(R.id.joinCircleCode);

        joinCircleButton = (Button) findViewById(R.id.joinCircleButton);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        joinCircleAuth.addAuthStateListener(joinCircleAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (joinCircleAuthListener != null) {
            joinCircleAuth.removeAuthStateListener(joinCircleAuthListener);
        }
    }
}
