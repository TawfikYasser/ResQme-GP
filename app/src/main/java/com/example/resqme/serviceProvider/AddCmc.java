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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.resqme.R;
import com.example.resqme.common.AddressMap;
import com.example.resqme.common.MyReports;
import com.example.resqme.common.Splash;
import com.example.resqme.model.CMC;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.UserProfileChangeRequest;
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

public class AddCmc extends AppCompatActivity implements View.OnClickListener{
    Button choosecmcimage,SbmtBtn,Addressbtn;
    TextInputEditText cmcname;
    ImageView cmcimage;
    ProgressDialog progressDialog;
    TextView cmcAddressTV;

    Uri cmcImageURI;
    Context context;
    DatabaseReference ServicesTable,ServiceProvidersTable;
    AutoCompleteTextView cartype;
    String  CarMfgCountry = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cmc);
        context = this.getApplicationContext();
        ServicesTable = FirebaseDatabase.getInstance().getReference().child("CMCs");
        ServiceProvidersTable = FirebaseDatabase.getInstance().getReference().child("ServiceProviders");
        initToolbar();
        initViews();
        forceRTLIfSupported();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_AddCMC);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("إضافة بيانات مركز الخدمة");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(AddCmc.this, R.style.Theme_ResQme);
    }

    private void initViews() {
        cartype= findViewById(R.id.cmc_brand_dropdown_list);
        String[] carmfgcountry = getResources().getStringArray(R.array.carmanufacturingcountry);
        ArrayAdapter<String> adapterCarTypes = new ArrayAdapter<String>
                (this, R.layout.item_add_car_data_dropdownlist, carmfgcountry);
        cartype.setThreshold(1);
        cartype.setAdapter(adapterCarTypes);

        cartype.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Object item = parent.getItemAtPosition(position);
                CarMfgCountry= item.toString().trim();
            }
        });

        Addressbtn = findViewById(R.id.choose_address_button_for_cmc);
        Addressbtn.setOnClickListener(this);
        cmcimage = findViewById(R.id.cmc_image_add_cmc_data) ;
        choosecmcimage = findViewById(R.id.chooseimagecmcbutton);

        choosecmcimage.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);

        cmcAddressTV = findViewById(R.id.tv_cmc_address);

        cmcname = findViewById(R.id.cmc_name_et);
        SbmtBtn = findViewById(R.id.submit_cmc_data_btn);
        SbmtBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chooseimagecmcbutton:
                gettingImageFromGallery();
                break;
            case R.id.choose_address_button_for_cmc:
                checkLocationPermission();
                break;
            case R.id.submit_cmc_data_btn:
                submitCMCData();
                break;
        }
    }

    private void submitCMCData() {
        if(!TextUtils.isEmpty(cmcname.getText().toString())
                &&!TextUtils.isEmpty(CarMfgCountry) && !TextUtils.isEmpty(cmcAddressTV.getText())  && cmcImageURI != null ){
            new AlertDialog.Builder(this)
                    .setTitle("تأكيد إدخال البيانات")
                    .setMessage("هل أنت متأكد من البيانات التي تم إدخالها؟ ، من فضلك راجع جميع البيانات...")
                    .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.setMessage("يرجى الإنتظار قليلاً جاري إرسال البيانات!");
                            progressDialog.show();
                            AddCMC();
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
    private void AddCMC() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        StorageReference filepath_CMCImage = FirebaseStorage.getInstance().getReference().child("ServiceImages").child(cmcImageURI.getLastPathSegment());

        filepath_CMCImage.putFile(cmcImageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filepath_CMCImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String ServiceID = database.getReference("CMCs").push().getKey();// create new id
                        SharedPreferences SP_services = getSharedPreferences("SP_LOCAL_DATA", Context.MODE_PRIVATE); //Pointer on local data
                        String sp_userid = SP_services.getString("SP_USERID","SP_DEFAULT");
                        CMC cmc = new CMC(ServiceID, cmcname.getText().toString().trim(), uri.toString(),
                                cmcAddressTV.getText().toString().trim(),
                                CarMfgCountry, sp_userid, "Pending", "Available");
                        ServicesTable.child(ServiceID).setValue(cmc); //Set Service data in database
                        ServiceProvidersTable.child(sp_userid).child("serviceType").setValue("cmc");// Set the value of serviceType attribute in the service provider table
                        ServiceProvidersTable.child(sp_userid).child("cmc").setValue("true");// Set the value of CMC attribute in the service provider table
                        // Related to service provider service type handling.
                        SharedPreferences cld = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = cld.edit();
                        editor.putString("SP_ServiceType", "CMC");
                        editor.putString("SP_CMC", "TRUE");
                        editor.apply();
                        progressDialog.dismiss();
                        Intent i = new Intent(AddCmc.this, ServiceProviderHome.class);
                        startActivity(i);
                        finish();

                    }
                });
            }
        });

    }

    private void gettingImageFromGallery() {
        ImagePicker.with(this)
                .crop()	 //Crop image(Optional), Check Customization for more option
                .compress(1024)	//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start(1);
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            Uri uri = data.getData();
            cmcImageURI = uri;
            cmcimage.setImageURI(uri);
        }
        else{
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("ADDRESS_VALUE");
                cmcAddressTV.setVisibility(View.VISIBLE);
                cmcAddressTV.setText(result);
            }
        }
    }
    void getAddress() {
        Intent addressMapIntent = new Intent(AddCmc.this, AddressMap.class);
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
