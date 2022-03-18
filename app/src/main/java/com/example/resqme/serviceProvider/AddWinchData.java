package com.example.resqme.serviceProvider;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.resqme.R;
import com.example.resqme.common.AddressMap;
import com.example.resqme.common.Registeration;
import com.example.resqme.model.Car;
import com.example.resqme.model.Report;
import com.example.resqme.model.Winch;
import com.github.dhaval2404.imagepicker.ImagePicker;
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

import de.hdodenhof.circleimageview.CircleImageView;


public class AddWinchData  extends AppCompatActivity implements View.OnClickListener{
    MaterialButton chooseWinchAddressBtn, chooseDriverLicenceImageBtn, chooseWinchLicenceImageBtn, submitWinchDataBtn;
    TextInputEditText winchNameET, winchCostPerKMET;
    ImageView driverLicenceImage, winchLicenceImage;
    Context context;
    DatabaseReference winchesDB;
    ProgressDialog progressDialog;
    TextView winchAddressTV;
    StorageReference storageWinchImages;

    Uri driverLicenceUri = null;
    Uri winchLicenceUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_winch_data);
        winchesDB = FirebaseDatabase.getInstance().getReference().child("Winches");
        storageWinchImages= FirebaseStorage.getInstance().getReference();
        context = this.getApplicationContext();
        initViews();
        initToolbar();
        forceRTLIfSupported();
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
            case R.id.choose_driver_licence_image_for_winch_btn:
                getDriverLicenceImage();
                break;
            case R.id.choose_winch_licence_image_for_winch_btn:
                getWinchLicenceImage();
                break;
            case R.id.submit_winch_data_btn:
                submitWinchData();
                break;
        }
}

    private void submitWinchData() {
        if(!TextUtils.isEmpty(winchNameET.getText().toString()) && !TextUtils.isEmpty(winchCostPerKMET.getText().toString())
        && !TextUtils.isEmpty(winchAddressTV.getText()) && winchLicenceUri != null && driverLicenceUri != null){

            new AlertDialog.Builder(this)
                    .setTitle("تأكيد إدخال البيانات")
                    .setMessage("هل أنت متأكد من البيانات التي تم إدخالها؟ ، من فضلك راجع جميع البيانات...")
                    .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.setMessage("يرجى الإنتظار قليلاً جاري إرسال البيانات!");
                            progressDialog.show();
                            uploadWinchData();

                        }
                    })
                    .setNegativeButton("لا", null)
                    .show();

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

                                        SharedPreferences serviceProviderLocale = getSharedPreferences("SP_LOCAL_DATA", Context.MODE_PRIVATE);//Pointer on local data
                                        String sp_userid = serviceProviderLocale.getString("SP_USERID","SP_DEFAULT");
                                        Winch winch = new Winch(winchID, winchNameET.getText().toString().trim(),
                                                winchCostPerKMET.getText().toString().trim(), "Pending", "Available", winchAddressTV.getText().toString().trim()
                                        , winchAddressTV.getText().toString().trim(),  uri.toString(), uri2.toString(), sp_userid);

                                        winchesDB.child(winchID).setValue(winch);
                                        progressDialog.dismiss();
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


    void getDriverLicenceImage(){
        ImagePicker.with(this)
                .crop()	 //Crop image(Optional), Check Customization for more option
                .compress(1024)	//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start(1);
    }

    void getWinchLicenceImage(){
        ImagePicker.with(this)
                .crop()	 //Crop image(Optional), Check Customization for more option
                .compress(1024)	//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start(2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            Uri uri = data.getData();
            driverLicenceUri = uri;
            driverLicenceImage.setImageURI(uri);
        }else if(requestCode == 2){
            Uri uri = data.getData();
            winchLicenceUri = uri;
            winchLicenceImage.setImageURI(uri);
        }else if(requestCode == 3){
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
