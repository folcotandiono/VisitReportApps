package com.example.folcotandiono.visitreporthts;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CircleOptionsActivity extends AppCompatActivity {

    private FirebaseAuth circleOptionsAuth;
    private FirebaseAuth.AuthStateListener circleOptionsAuthListener;
    private FirebaseDatabase circleOptionsDatabase;

    private Toolbar circleOptionsToolbar;
    private RecyclerView circleOptionsRecyclerView;
    private RecyclerView.Adapter circleOptionsAdapter;
    private RecyclerView.LayoutManager circleOptionsLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_options);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            startActivity(new Intent(CircleOptionsActivity.this, LoginActivity.class));
        }

        initView();
        initObject();
    }

    private void initView() {
        circleOptionsRecyclerView = (RecyclerView) findViewById(R.id.circleOptionsRecyclerView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        circleOptionsRecyclerView.setHasFixedSize(true);
    }

    private void initObject() {
        circleOptionsAuth = FirebaseAuth.getInstance();

        circleOptionsAuthListener = new FirebaseAuth.AuthStateListener() {
            public static final String TAG = "CircleOptionsActivity";

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

        circleOptionsDatabase = FirebaseDatabase.getInstance();
        // use a linear layout manager
        circleOptionsLayoutManager = new LinearLayoutManager(this);
        circleOptionsRecyclerView.setLayoutManager(circleOptionsLayoutManager);

        circleOptionsDatabase.getReference("User").child(circleOptionsAuth.getCurrentUser().getUid()).child("circle").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String[] listCircleName = new String[(int) dataSnapshot.getChildrenCount()];
                final String[] listCircleCode = new String[(int) dataSnapshot.getChildrenCount()];
                if (dataSnapshot.getValue() == null) {
                    circleOptionsAdapter = new CircleOptionsAdapter(listCircleName, listCircleCode);
                    circleOptionsRecyclerView.setAdapter(circleOptionsAdapter);
                    return;
                }
                int cnt = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    listCircleName[cnt++] = (String) snapshot.getValue();
                }

                for (int i = 0; i < cnt; i++) {
                    final int finalI = i;
                    circleOptionsDatabase.getReference("Circle").child(listCircleName[i]).child("code").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            listCircleCode[finalI] = "Code : " + (String) dataSnapshot.getValue();

                            // specify an adapter (see also next example)
                            circleOptionsAdapter = new CircleOptionsAdapter(listCircleName, listCircleCode);
                            circleOptionsRecyclerView.setAdapter(circleOptionsAdapter);
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

        circleOptionsToolbar = (Toolbar) findViewById(R.id.circleOptionsToolbar);
        setSupportActionBar(circleOptionsToolbar);
        getSupportActionBar().setTitle("Circle Options");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        circleOptionsAuth.addAuthStateListener(circleOptionsAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (circleOptionsAuthListener != null) {
            circleOptionsAuth.removeAuthStateListener(circleOptionsAuthListener);
        }
    }
}
