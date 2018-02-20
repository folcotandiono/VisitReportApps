package com.example.folcotandiono.visitreporthts;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CircleDetailsActivity extends AppCompatActivity {

    private FirebaseAuth circleDetailsAuth;
    private FirebaseAuth.AuthStateListener circleDetailsAuthListener;
    private FirebaseDatabase circleDetailsDatabase;

    private Boolean admin = false;

    private Toolbar circleDetailsToolbar;
    private TextView circleDetailsCircleName;
    private TextView circleDetailsCircleCode;
    private RecyclerView circleDetailsRecyclerView;
    private RecyclerView.Adapter circleDetailsAdapter;
    private RecyclerView.LayoutManager circleDetailsLayoutManager;
    private Button circleDetailsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_details);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            startActivity(new Intent(CircleDetailsActivity.this, LoginActivity.class));
        }

        initView();
        initObject();
        initListener();
    }

    private void initListener() {
        circleDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleDetailsDatabase.getReference("User").child(circleDetailsAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<String> circle = new ArrayList<String>();
                        if (dataSnapshot.child("circle").getValue() != null) {
                            circle = (ArrayList<String>) dataSnapshot.child("circle").getValue();
                            for (int i = 0; i < circle.size(); i++) {
                                if (circle.get(i).equals(circleDetailsCircleName.getText().toString())) {
                                    circle.remove(i);
                                    final ArrayList<String> finalCircle = circle;
                                    circleDetailsDatabase.getReference("Circle").child(getIntent().getStringExtra("circleName")).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            ArrayList<String> id = new ArrayList<String>();
                                            id = (ArrayList<String>) dataSnapshot.child("id").getValue();
                                            for (int i = 0; i < id.size(); i++) {
                                                if (id.get(i).equals(circleDetailsAuth.getCurrentUser().getUid())) {
                                                    id.remove(i);
                                                    circleDetailsDatabase.getReference("User").child(circleDetailsAuth.getCurrentUser().getUid()).child("circle").setValue(finalCircle);
                                                    circleDetailsDatabase.getReference("Circle").child(getIntent().getStringExtra("circleName")).child("id").setValue(id);

                                                    if (!id.isEmpty() && admin) {
                                                        circleDetailsDatabase.getReference("Circle").child(getIntent().getStringExtra("circleName")).child("idUser").setValue(id.get(0));
                                                    }

                                                    onBackPressed();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void initObject() {
        circleDetailsAuth = FirebaseAuth.getInstance();

        circleDetailsAuthListener = new FirebaseAuth.AuthStateListener() {
            public static final String TAG = "CircleDetailsActivity";

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

        circleDetailsDatabase = FirebaseDatabase.getInstance();

        // use a linear layout manager
        circleDetailsLayoutManager = new LinearLayoutManager(this);
        circleDetailsRecyclerView.setLayoutManager(circleDetailsLayoutManager);

        circleDetailsDatabase.getReference("Circle").child(getIntent().getStringExtra("circleName")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String idUser = dataSnapshot.child("idUser").getValue().toString();
                admin = false;
                if (idUser.equals(circleDetailsAuth.getCurrentUser().getUid())) admin = true;
                final Boolean finalAdmin = admin;

                if (dataSnapshot.child("id").getValue() == null) {
                    onBackPressed();
                    return;
                }
                final String[] id = new String[(int) dataSnapshot.child("id").getChildrenCount()];
                final String[] name = new String[(int) dataSnapshot.child("id").getChildrenCount()];
                int cnt = 0;
                for (DataSnapshot snapshot : dataSnapshot.child("id").getChildren()) {
                    id[cnt++] = (String) snapshot.getValue();
                    final int finalCnt = cnt;
                    circleDetailsDatabase.getReference("User").child(id[cnt - 1]).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            name[finalCnt - 1] = dataSnapshot.getValue().toString();
                            circleDetailsAdapter = new CircleDetailsAdapter(id, name, finalAdmin, circleDetailsCircleName.getText().toString(), idUser);
                            circleDetailsRecyclerView.setAdapter(circleDetailsAdapter);
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

        circleDetailsToolbar = (Toolbar) findViewById(R.id.circleDetailsToolbar);
        setSupportActionBar(circleDetailsToolbar);
        getSupportActionBar().setTitle("Circle Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initView() {
        circleDetailsToolbar = (Toolbar) findViewById(R.id.circleDetailsToolbar);

        circleDetailsCircleName = (TextView) findViewById(R.id.circleDetailsCircleName);
        circleDetailsCircleName.setText(getIntent().getStringExtra("circleName"));

        circleDetailsCircleCode = (TextView) findViewById(R.id.circleDetailsCircleCode);
        circleDetailsCircleCode.setText(getIntent().getStringExtra("circleCode"));

        circleDetailsRecyclerView = (RecyclerView) findViewById(R.id.circleDetailsRecyclerView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        circleDetailsRecyclerView.setHasFixedSize(true);

        circleDetailsButton = (Button) findViewById(R.id.circleDetailsButton);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        circleDetailsAuth.addAuthStateListener(circleDetailsAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (circleDetailsAuthListener != null) {
            circleDetailsAuth.removeAuthStateListener(circleDetailsAuthListener);
        }
    }
}
