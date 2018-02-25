package com.example.folcotandiono.visitreporthts;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private TextView registerEmail;
    private TextView registerPhonenumber;
    private TextView registerName;
    private TextView registerPassword;
    private TextView registerRePassword;
    private Spinner registerRole;
    private Button registerRegister;

    private FirebaseAuth registerAuth;
    private FirebaseAuth.AuthStateListener registerAuthListener;
    private FirebaseDatabase registerDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
        initObject();
        initListener();
    }

    private void initView() {
        registerEmail = (TextView) findViewById(R.id.registerEmail);
        registerPhonenumber = (TextView) findViewById(R.id.registerPhonenumber);
        registerName = (TextView) findViewById(R.id.registerName);
        registerPassword = (TextView) findViewById(R.id.registerPassword);
        registerRePassword = (TextView) findViewById(R.id.registerRePassword);
        registerRole = (Spinner) findViewById(R.id.registerRole);

        // https://developer.android.com/guide/topics/ui/controls/spinner.html
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.role, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        registerRole.setAdapter(adapter);

        registerRegister = (Button) findViewById(R.id.registerRegister);


    }

    private void initObject() {
        registerAuth = FirebaseAuth.getInstance();
        registerAuthListener = new FirebaseAuth.AuthStateListener() {
            public static final String TAG = "RegisterActivity";

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

        registerDatabase = FirebaseDatabase.getInstance();
    }

    private void initListener() {
        registerRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    private void register() {
        final String email = registerEmail.getText().toString();
        final String phonenumber = registerPhonenumber.getText().toString();
        final String name = registerName.getText().toString();
        final String password = registerPassword.getText().toString();
        String rePassword = registerRePassword.getText().toString();
        final String role = registerRole.getSelectedItem().toString();

        if (email.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Email is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(RegisterActivity.this, "Email is not valid", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phonenumber.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Phonenumber is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.PHONE.matcher(phonenumber).matches()) {
            Toast.makeText(RegisterActivity.this, "Phonenumber is not valid", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Name is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(rePassword)) {
            Toast.makeText(RegisterActivity.this, "Password and re-password not matched", Toast.LENGTH_SHORT).show();
            return;
        }

        registerAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    public static final String TAG = "RegisterActivity";

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            FirebaseAuthException e = (FirebaseAuthException )task.getException();
                            Toast.makeText(RegisterActivity.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "Registration complete",
                                    Toast.LENGTH_LONG).show();

                            User user = new User();
                            user.setName(name);
                            user.setEmail(email);
                            user.setPhonenumber(phonenumber);
                            user.setPassword(password);

                            if (role.equals("Sales")) user.setRole(RegisterActivity.this.getResources().getInteger(R.integer.sales));
                            else user.setRole(RegisterActivity.this.getResources().getInteger(R.integer.salesManager));

                            registerDatabase.getReference("User").child(task.getResult().getUser().getUid()).setValue(user);
                            registerDatabase.getReference("User").child(task.getResult().getUser().getUid()).child("checkIn").child("status").setValue(true);
                            registerDatabase.getReference("User").child(task.getResult().getUser().getUid()).child("checkIn").child("circle").setValue("");
                            registerDatabase.getReference("User").child(task.getResult().getUser().getUid()).child("checkIn").child("date").setValue("");

                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        registerAuth.addAuthStateListener(registerAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (registerAuthListener != null) {
            registerAuth.removeAuthStateListener(registerAuthListener);
        }
    }
}
