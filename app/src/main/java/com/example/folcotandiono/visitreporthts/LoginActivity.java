package com.example.folcotandiono.visitreporthts;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private TextView loginEmail;
    private TextView loginPassword;
    private Button loginLogin;
    private TextView loginRegister;

    private FirebaseAuth loginAuth;
    private AuthStateListener loginAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initListener();
        initObject();
    }

    private void initView() {
        loginEmail = (TextView) findViewById(R.id.loginEmail);
        loginPassword = (TextView) findViewById(R.id.loginPassword);
        loginLogin = (Button) findViewById(R.id.loginLogin);
        loginRegister = (TextView) findViewById(R.id.loginRegister);
    }

    private void initListener() {
        loginLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                login();
            }
        });

        loginRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initObject() {
        loginAuth = FirebaseAuth.getInstance();

        loginAuthListener = new AuthStateListener() {
            public static final String TAG = "LoginActivity";

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
    }

    private void login() {
        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();

        if(email.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Email/phonenumber is empty", Toast.LENGTH_SHORT).show();
            return ;
        }
        if (password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
            return ;
        }

        loginAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    public static final String TAG = "LoginActivity";

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            FirebaseAuthException e = (FirebaseAuthException )task.getException();
                            Toast.makeText(LoginActivity.this, "Failed Login: "+e.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Signing in",
                                    Toast.LENGTH_SHORT).show();

                            finish();

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }

                        // ...
                    }
                });

    }

    @Override
    public void onStart() {
        super.onStart();
        loginAuth.addAuthStateListener(loginAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (loginAuthListener != null) {
            loginAuth.removeAuthStateListener(loginAuthListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginEmail.setText("");
        loginPassword.setText("");
    }
}
