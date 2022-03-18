package com.example.resqme.serviceProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
    CircleImageView cmcimage;
    ProgressDialog progressDialog;
    TextView cmcAddressTV;

    Uri cmcImageURI;
    Context context;
    DatabaseReference ServicesTable;
    AutoCompleteTextView cartype;
    String  Cartype="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cmc);
        context = this.getApplicationContext();
        ServicesTable = FirebaseDatabase.getInstance().getReference().child("CMC");
        initToolbar();
        initViews();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_AddCMC);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("اضافه مركز خدمه");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(AddCmc.this, R.style.Theme_ResQme);
    }

    private void initViews() {
        cartype= findViewById(R.id.itembrand_dropdown_list);
        String[] arrayCarTypes = getResources().getStringArray(R.array.cartypes);
        ArrayAdapter<String> adapterCarTypes = new ArrayAdapter<String>
                (this, R.layout.item_add_car_data_dropdownlist, arrayCarTypes);
        cartype.setThreshold(1);
        cartype.setAdapter(adapterCarTypes);

        cartype.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Object item = parent.getItemAtPosition(position);
                Cartype= item.toString().trim();
                Toast.makeText(AddCmc.this, Cartype, Toast.LENGTH_SHORT).show();
            }
        });

        Addressbtn = findViewById(R.id.choose_address_button_for_cmc);
        Addressbtn.setOnClickListener(this);
        cmcimage = findViewById(R.id.chooseimagecmc) ;
        choosecmcimage = findViewById(R.id.chooseimagecmcbutton);

        choosecmcimage.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);

        cmcAddressTV = findViewById(R.id.tv_cmc_address);

        cmcname = findViewById(R.id.namecmc);
        SbmtBtn = findViewById(R.id.addcmcbtn);
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
            case R.id.addcmcbtn:
                submitCMCData();
        }
    }

    private void submitCMCData() {
        if(!TextUtils.isEmpty(cmcname.getText().toString()) &&!TextUtils.isEmpty(Cartype)
                && !TextUtils.isEmpty(cmcAddressTV.getText())  && cmcImageURI != null ){

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

        StorageReference filepath_CMCImage = FirebaseStorage.getInstance().getReference().child("UserImages").child(cmcImageURI.getLastPathSegment());

        filepath_CMCImage.putFile(cmcImageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filepath_CMCImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String ServiceID = database.getReference("CMC").push().getKey();// create new id
                        SharedPreferences SP_services = getSharedPreferences("SP_LOCAL_DATA", Context.MODE_PRIVATE);//Pointer on local data
                        String sp_userid = SP_services.getString("SP_USERID","SP_DEFAULT");
                        CMC cmc = new CMC(ServiceID,cmcname.getText().toString(),uri.toString(),cmcAddressTV.getText().toString().trim(),Cartype,sp_userid,"Pending","Available");
                        ServicesTable.child(ServiceID).setValue(cmc);//Entering Service in database
                        progressDialog.dismiss();
                        finish();

                    }
                });
            }
        });
//        Toast.makeText(this,"تم تسجيل مركز الخدمه بنجاح",Toast.LENGTH_LONG).show();
//        finish();

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
