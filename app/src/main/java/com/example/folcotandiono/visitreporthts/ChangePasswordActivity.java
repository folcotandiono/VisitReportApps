package com.example.folcotandiono.visitreporthts;

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

public class ChangePasswordActivity extends AppCompatActivity {

    private FirebaseAuth changePasswordAuth;
    private FirebaseAuth.AuthStateListener changePasswordAuthListener;
    private FirebaseDatabase changePasswordDatabase;

    private Toolbar changePasswordToolbar;
    private EditText changePasswordOldPassword;
    private EditText changePasswordNewPassword;
    private EditText changePasswordRePassword;
    private Button changePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
        }

        initView();
        initObject();
        initListener();
    }

    private void initListener() {
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (changePasswordNewPassword.getText().toString().isEmpty()) {
                    Toast.makeText(ChangePasswordActivity.this, "New password is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!changePasswordNewPassword.getText().toString().equals(changePasswordRePassword.getText().toString())) {
                    Toast.makeText(ChangePasswordActivity.this, "New password and re password not matched", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (changePasswordNewPassword.getText().toString().length() < 6) {
                    Toast.makeText(ChangePasswordActivity.this, "Password should have 6 minimum characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                changePasswordDatabase.getReference("User").child(changePasswordAuth.getCurrentUser().getUid()).child("password").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue().toString().equals(changePasswordOldPassword.getText().toString())) {
                            changePasswordAuth.getCurrentUser().updatePassword(changePasswordNewPassword.getText().toString());
                            changePasswordDatabase.getReference("User").child(changePasswordAuth.getCurrentUser().getUid()).child("password").setValue(changePasswordNewPassword.getText().toString());
                            Toast.makeText(ChangePasswordActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(ChangePasswordActivity.this, "Old password is wrong", Toast.LENGTH_SHORT).show();
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
        changePasswordAuth = FirebaseAuth.getInstance();

        changePasswordAuthListener = new FirebaseAuth.AuthStateListener() {
            public static final String TAG = "ChangePasswordActivity";

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

        changePasswordDatabase = FirebaseDatabase.getInstance();

        setSupportActionBar(changePasswordToolbar);
        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initView() {
        changePasswordToolbar = (Toolbar) findViewById(R.id.changePasswordToolbar);
        changePasswordOldPassword = (EditText) findViewById(R.id.changePasswordOldPassword);
        changePasswordNewPassword = (EditText) findViewById(R.id.changePasswordNewPassword);
        changePasswordRePassword = (EditText) findViewById(R.id.changePasswordRePassword);
        changePasswordButton = (Button) findViewById(R.id.changePasswordButton);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        changePasswordAuth.addAuthStateListener(changePasswordAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (changePasswordAuthListener != null) {
            changePasswordAuth.removeAuthStateListener(changePasswordAuthListener);
        }
    }
}
