package com.example.folcotandiono.visitreporthts;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private TextView loginEmailPhonenumber;
    private TextView loginPassword;
    private Spinner loginRole;
    private Button loginLogin;
    private TextView loginRegister;

    private DatabaseHelper databaseHelper;

    public Sales sales;
    public SalesManager salesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initListener();
        initObject();
    }

    private void initView() {
        loginEmailPhonenumber = (TextView) findViewById(R.id.loginEmailPhonenumber);
        loginPassword = (TextView) findViewById(R.id.loginPassword);
        loginRole = (Spinner) findViewById(R.id.loginRole);

        // https://developer.android.com/guide/topics/ui/controls/spinner.html
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.role, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        loginRole.setAdapter(adapter);

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
        databaseHelper = new DatabaseHelper(LoginActivity.this);
    }

    private void login() {
        String emailPhonenumber = loginEmailPhonenumber.getText().toString();
        String password = loginPassword.getText().toString();
        String role = loginRole.getSelectedItem().toString();

        sales = new Sales();
        salesManager = new SalesManager();

        if(emailPhonenumber.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Email/phonenumber is empty", Toast.LENGTH_SHORT).show();
            return ;
        }
        if (password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
            return ;
        }

        if (Patterns.EMAIL_ADDRESS.matcher(emailPhonenumber).matches()) {
            if (role.equals("Sales")) sales = databaseHelper.getSales(emailPhonenumber, password, true);
            else salesManager = databaseHelper.getSalesManager(emailPhonenumber, password, true);
        }
        else if (Patterns.PHONE.matcher(emailPhonenumber).matches()) {
            if (role.equals("Sales")) sales = databaseHelper.getSales(emailPhonenumber, password, false);
            else salesManager = databaseHelper.getSalesManager(emailPhonenumber, password, false);
        }
        else {
            Toast.makeText(LoginActivity.this, "Email/phonenumber is not valid", Toast.LENGTH_SHORT).show();
            return;
        }

        if (role.equals("Sales")) {
            if (sales.getEmail() != null && !sales.getEmail().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Signing in", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(LoginActivity.this, "Email or password is wrong", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            if (salesManager.getEmail() != null && !salesManager.getEmail().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Signing in", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(LoginActivity.this, "Email or password is wrong", Toast.LENGTH_SHORT).show();
            }
        }

        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);

    }

}
