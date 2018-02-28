package com.example.folcotandiono.visitreporthts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class CustomerDetailActivity extends AppCompatActivity {

    private FirebaseAuth customerDetailAuth;
    private FirebaseAuth.AuthStateListener customerDetailAuthListener;
    private FirebaseDatabase customerDetailDatabase;
    private FirebaseStorage customerDetailStorage;

    private Toolbar customerDetailToolbar;
    private EditText customerDetailPlaceName;
    private TextView customerDetailAddress;
    private Button customerDetailTakePhoto;
    private ImageView customerDetailPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);
    }
}
