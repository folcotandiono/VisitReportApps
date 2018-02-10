package com.example.folcotandiono.visitreporthts;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

    private DatabaseHelper databaseHelper;

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
        databaseHelper = new DatabaseHelper(RegisterActivity.this);
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
        String email = registerEmail.getText().toString();
        String phonenumber = registerPhonenumber.getText().toString();
        String name = registerName.getText().toString();
        String password = registerPassword.getText().toString();
        String rePassword = registerRePassword.getText().toString();

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

        if (registerRole.getSelectedItem().toString().equals("Sales")) {
            Sales temp = databaseHelper.getSalesByEmail(email);

            if (temp.getEmail() != null && !temp.getEmail().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Email already taken", Toast.LENGTH_SHORT).show();
                return;
            }

            temp = databaseHelper.getSalesByPhonenumber(phonenumber);

            if (temp.getEmail() != null && !temp.getEmail().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Phonenumber already taken", Toast.LENGTH_SHORT).show();
                return;
            }

            Sales sales = new Sales();
            sales.setName(name);
            sales.setEmail(email);
            sales.setPhonenumber(phonenumber);
            sales.setPassword(password);
            databaseHelper.insertSales(sales);
        }
        else {
            SalesManager temp = databaseHelper.getSalesManagerByEmail(email);

            if (temp.getEmail() != null && !temp.getEmail().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Email already taken", Toast.LENGTH_SHORT).show();
                return;
            }

            temp = databaseHelper.getSalesManagerByPhonenumber(phonenumber);

            if (temp.getEmail() != null && !temp.getEmail().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Phonenumber already taken", Toast.LENGTH_SHORT).show();
                return;
            }
            SalesManager salesManager = new SalesManager();
            salesManager.setName(name);
            salesManager.setEmail(email);
            salesManager.setPhonenumber(phonenumber);
            salesManager.setPassword(password);
            databaseHelper.insertSalesManager(salesManager);
        }
        Toast.makeText(RegisterActivity.this, "Account created", Toast.LENGTH_SHORT).show();
    }
}
