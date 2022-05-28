package com.example.resqme.serviceProvider;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.resqme.R;
import com.example.resqme.common.AddressMap;
import com.example.resqme.common.Registeration;
import com.example.resqme.common.Splash;
import com.example.resqme.model.Car;
import com.example.resqme.model.Report;
import com.example.resqme.model.Winch;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class AddWinchData  extends AppCompatActivity implements View.OnClickListener{
    MaterialButton chooseWinchAddressBtn, chooseDriverLicenceImageBtn, chooseWinchLicenceImageBtn, submitWinchDataBtn;
    TextInputEditText winchNameET, winchCostPerKMET;
    ImageView driverLicenceImage, winchLicenceImage;
    Context context;
    DatabaseReference winchesDB,ServiceProvidersTable;
    ProgressDialog progressDialog;
    TextView winchAddressTV;
    StorageReference storageWinchImages;
    Uri driverLicenceUri = null;
    Uri winchLicenceUri = null;

    String fromSTR = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_winch_data);
        winchesDB = FirebaseDatabase.getInstance().getReference().child("Winches");
        ServiceProvidersTable =  FirebaseDatabase.getInstance().getReference().child("ServiceProviders");
        storageWinchImages= FirebaseStorage.getInstance().getReference().child("ServiceImages");
        context = this.getApplicationContext();
        initViews();
        initToolbar();
        forceRTLIfSupported();

        Intent fromWhere = getIntent();
        fromSTR = fromWhere.getStringExtra("FROM");

        if(TextUtils.isEmpty(fromSTR)){
            fromSTR = "";
        }

        ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                driverLicenceUri = result;
                driverLicenceImage.setImageURI(result);
            }
        });
        chooseDriverLicenceImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch("image/*");
            }
        });

        ActivityResultLauncher<String> launcher2 = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                winchLicenceUri = result;
                winchLicenceImage.setImageURI(result);
            }
        });
        chooseWinchLicenceImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher2.launch("image/*");
            }
        });

    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_AddWinch);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("اضاقه بيانات الونش");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(AddWinchData.this, R.style.Theme_ResQme);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }

    private void initViews() {
        chooseWinchAddressBtn = findViewById(R.id.choose_winch_address_btn);
        chooseWinchAddressBtn.setOnClickListener(this);
        chooseDriverLicenceImageBtn = findViewById(R.id.choose_driver_licence_image_for_winch_btn);
        chooseDriverLicenceImageBtn.setOnClickListener(this);
        chooseWinchLicenceImageBtn = findViewById(R.id.choose_winch_licence_image_for_winch_btn);
        chooseWinchLicenceImageBtn.setOnClickListener(this);
        submitWinchDataBtn = findViewById(R.id.submit_winch_data_btn);
        submitWinchDataBtn.setOnClickListener(this);
        winchNameET = findViewById(R.id.winchname_et);
        winchCostPerKMET = findViewById(R.id.costperkm_et);
        winchAddressTV = findViewById(R.id.tv_winch_address);
        driverLicenceImage = findViewById(R.id.driver_licence_image_add_winch_data);
        winchLicenceImage = findViewById(R.id.winch_licence_image_add_winch_data);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.choose_winch_address_btn:
                checkLocationPermission();
                break;
            case R.id.submit_winch_data_btn:
                submitWinchData();
                break;
        }
}

    private void submitWinchData() {
        if(!TextUtils.isEmpty(winchNameET.getText().toString()) && !TextUtils.isEmpty(winchCostPerKMET.getText().toString())
        && !TextUtils.isEmpty(winchAddressTV.getText()) && winchLicenceUri != null && driverLicenceUri != null){
            if(Integer.parseInt( winchCostPerKMET.getText().toString() ) >= 5 && Integer.parseInt( winchCostPerKMET.getText().toString() ) <= 30  ){
                new AlertDialog.Builder(this)
                        .setTitle("تأكيد إدخال البيانات")
                        .setMessage("هل أنت متأكد من البيانات التي تم إدخالها؟ ، من فضلك راجع جميع البيانات...")
                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.setMessage("يرجى الإنتظار قليلاً جاري إرسال البيانات!");
                                progressDialog.show();
                                progressDialog.setCancelable(false);
                                uploadWinchData();
                            }
                        })
                        .setNegativeButton("لا", null)
                        .show();
            }else{
                Toast.makeText(context, "سعر الكيلو لازم يكون بين 5 جنيه أو 30 جنيه.", Toast.LENGTH_SHORT).show();
            }

        }else{
            Snackbar.make(findViewById(android.R.id.content),"يجب إدخال جميع البيانات!", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getResources().getColor(R.color.red_color))
                    .setTextColor(getResources().getColor(R.color.white))
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
        }
    }

    private void uploadWinchData() {
        //Upload the images, save the data to firebase db.
        final StorageReference filepath_driverlicence = storageWinchImages.child(driverLicenceUri.getLastPathSegment());
        filepath_driverlicence.putFile(driverLicenceUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filepath_driverlicence.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final StorageReference filepath_carlicence = storageWinchImages.child(winchLicenceUri.getLastPathSegment());
                        filepath_carlicence.putFile(winchLicenceUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                filepath_carlicence.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri2) {

                                        //Saving data to firebase realtime database
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        String winchID = database.getReference("Winches").push().getKey();

                                        SharedPreferences serviceProviderLocale = getSharedPreferences("SP_LOCAL_DATA", Context.MODE_PRIVATE); //Pointer on local data
                                        String sp_userid = serviceProviderLocale.getString("SP_USERID","SP_DEFAULT");
                                        String sp_rate = serviceProviderLocale.getString("SP_USERRATE","SP_DEFAULT");

                                        //Converting winch current location to lat and long

                                        Geocoder coder = new Geocoder(getApplicationContext());
                                        List<Address> address;
                                        LatLng p1 = null;
                                        try {
                                            address = coder.getFromLocationName(winchAddressTV.getText().toString().trim(), 5);
                                            Address location = address.get(0);
                                            p1 = new LatLng(location.getLatitude(), location.getLongitude());
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }

                                        Winch winch = new Winch(winchID, winchNameET.getText().toString().trim(),
                                                winchCostPerKMET.getText().toString().trim(), "Pending", "Available", winchAddressTV.getText().toString().trim()
                                        , String.valueOf(p1.latitude), String.valueOf(p1.longitude),  uri.toString(), uri2.toString(), sp_userid, sp_rate);
                                        winchesDB.child(winchID).setValue(winch);
                                        if(fromSTR.equals("SPHOME")){
                                            ServiceProvidersTable.child(sp_userid).child("serviceType").setValue("CMC");// Set the value of serviceType attribute in the service provider table
                                        }else{
                                            ServiceProvidersTable.child(sp_userid).child("serviceType").setValue("Winch");// Set the value of serviceType attribute in the service provider table
                                        }
                                        ServiceProvidersTable.child(sp_userid).child("isWinch").setValue("True");// Set the value of serviceType attribute in the service provider table

                                        // Related to service provider service type handling.
                                        SharedPreferences cld = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = cld.edit();
                                        if(fromSTR.equals("SPHOME")){
                                            editor.putString("SP_ServiceType", "CMC");
                                        }else{
                                            editor.putString("SP_ServiceType", "Winch");
                                        }
                                        editor.putString("SP_WINCH", "True");
                                        editor.apply();

                                        progressDialog.dismiss();
                                        Intent i = new Intent(AddWinchData.this, ServiceProviderHome.class);
                                        startActivity(i);
                                        finish();
                                    }
                                });
                            }
                        });
                    }
                }) ;
            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 3){
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("ADDRESS_VALUE");
                winchAddressTV.setVisibility(View.VISIBLE);
                winchAddressTV.setText(result);
            }
        }
    }

    void getAddress() {
        Intent addressMapIntent = new Intent(AddWinchData.this, AddressMap.class);
        startActivityForResult(addressMapIntent, 3);
    }

    private void checkLocationPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                getAddress();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(),"");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

}
