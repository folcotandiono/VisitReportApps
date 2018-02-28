package com.example.folcotandiono.visitreporthts;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddCustomerActivity extends AppCompatActivity {

    private FirebaseAuth addCustomerAuth;
    private FirebaseAuth.AuthStateListener addCustomerAuthListener;
    private FirebaseDatabase addCustomerDatabase;
    private FirebaseStorage addCustomerStorage;

    private Toolbar addCustomerToolbar;
    private EditText addCustomerPlaceName;
    private TextView addCustomerAddress;
    private Button addCustomerTakePhoto;
    private ImageView addCustomerPhoto;

    private Location lastKnownLocation;
    private String mCurrentPhotoPath;
    private String imageFileName;

    protected ResultReceiver mReceiver;
    private AddressResultReceiver mResultReceiver;

    static final int REQUEST_TAKE_PHOTO = 1;

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Show a toast message if an address was found.
            if (resultCode == 0) {
                String address = resultData.getString("address");
                addCustomerAddress.setText(address);
            }

        }
    }

    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra("receiver", mResultReceiver);
        intent.putExtra("location", lastKnownLocation);
        startService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            startActivity(new Intent(AddCustomerActivity.this, LoginActivity.class));
        }

        initView();
        initObject();
        initListener();
    }

    private void initListener() {
        addCustomerTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acquire a reference to the system Location Manager
                LocationManager locationManager = (LocationManager) AddCustomerActivity.this.getSystemService(Context.LOCATION_SERVICE);
                String locationProvider = LocationManager.NETWORK_PROVIDER;
                // Or use LocationManager.GPS_PROVIDER

                if (ActivityCompat.checkSelfPermission(AddCustomerActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddCustomerActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(AddCustomerActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                }
                if (ActivityCompat.checkSelfPermission(AddCustomerActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddCustomerActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                }
                else {
                    lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Ensure that there's a camera activity to handle the intent
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File

                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(AddCustomerActivity.this,
                                    "com.example.folcotandiono.visitreporthts.android.fileprovider",
                                    photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                        }
                    }
                }
            }
        });
    }

    private void initObject() {
        mResultReceiver = new AddressResultReceiver(new Handler());
        addCustomerAuth = FirebaseAuth.getInstance();

        addCustomerAuthListener = new FirebaseAuth.AuthStateListener() {
            public static final String TAG = "AddCustomerActivity";

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

        addCustomerDatabase = FirebaseDatabase.getInstance();
        addCustomerStorage = FirebaseStorage.getInstance();

        setSupportActionBar(addCustomerToolbar);
        getSupportActionBar().setTitle("Add Customer");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initView() {
        addCustomerToolbar = findViewById(R.id.addCustomerToolbar);
        addCustomerPlaceName = findViewById(R.id.addCustomerPlaceName);
        addCustomerAddress = findViewById(R.id.addCustomerAddress);
        addCustomerTakePhoto = findViewById(R.id.addCustomerTakePhoto);
        addCustomerPhoto = findViewById(R.id.addCustomerPhoto);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_customer, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addCustomerAdd) {
            if (addCustomerPlaceName.getText().toString().isEmpty()) {
                Toast.makeText(this, "Place name is empty", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (addCustomerPhoto.getDrawable() == null) {
                Toast.makeText(this, "Please take location's photo", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (addCustomerAddress.getText().toString().isEmpty()) {
                Toast.makeText(this, "Address is empty", Toast.LENGTH_SHORT).show();
                return false;
            }
            addCustomerDatabase.getReference("Customer").equalTo(addCustomerPlaceName.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null) {

                    }
                    else {
                        Toast.makeText(AddCustomerActivity.this, "Place name already exists", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            // Create a storage reference from our app
            StorageReference storageRef = addCustomerStorage.getReference();

            // Create a reference to "mountains.jpg"
            StorageReference imageRef = storageRef.child(imageFileName + ".jpg");

            // Get the data from an ImageView as bytes
            addCustomerPhoto.setDrawingCacheEnabled(true);
            addCustomerPhoto.buildDrawingCache();
            Bitmap bitmap = addCustomerPhoto.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = imageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(AddCustomerActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Customer customer = new Customer();
                    customer.setAddress(addCustomerAddress.getText().toString());
                    customer.setLat(lastKnownLocation.getLatitude());
                    customer.setLng(lastKnownLocation.getLongitude());
                    customer.setUrl(downloadUrl.toString());

                    addCustomerDatabase.getReference("Customer").child(addCustomerPlaceName.getText().toString()).setValue(customer);

                    Toast.makeText(AddCustomerActivity.this, "Customer added", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            galleryAddPic();
            setPic();
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = 500;
        int targetH = 500;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

//        Toast.makeText(this, String.valueOf(BitmapCompat.getAllocationByteCount(bitmap)), Toast.LENGTH_SHORT).show();

        addCustomerPhoto.setImageBitmap(bitmap);
        startIntentService();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        addCustomerAuth.addAuthStateListener(addCustomerAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (addCustomerAuthListener != null) {
            addCustomerAuth.removeAuthStateListener(addCustomerAuthListener);
        }
    }
}
