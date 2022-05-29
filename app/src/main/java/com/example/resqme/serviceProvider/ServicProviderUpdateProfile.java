package com.example.resqme.serviceProvider;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.resqme.R;
import com.example.resqme.common.AddressMap;
import com.example.resqme.common.InternetConnection;
import com.example.resqme.customer.CustomerUpdateProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class ServicProviderUpdateProfile extends AppCompatActivity implements View.OnClickListener{
    CircleImageView iUserImage;
    TextInputEditText etUsername, etWhatsApp, etPassword;
    Button btnUpdateAccount, btnChooseImage, btnChooseAddress;
    TextView tvAddress;
    Uri mainImageUri = null;
    ProgressDialog progressDialog;
    DatabaseReference databaseServiceProvider;
    SharedPreferences sp_data;
    FirebaseUser user;
    InternetConnection ic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servic_provider_update_profile);
        initViews();
        initToolbar();
        forceRTLIfSupported();
        user = FirebaseAuth.getInstance().getCurrentUser();
        sp_data = getSharedPreferences("SP_LOCAL_DATA", MODE_PRIVATE);
        etUsername.setText(sp_data.getString("SP_USERNAME","SP_DEFAULT"));
        etPassword.setText(sp_data.getString("SP_PASSWORD","SP_DEFAULT"));
        etWhatsApp.setText(sp_data.getString("SP_WHATSAPP","SP_DEFAULT"));
        Glide.with(this).load(sp_data.getString("SP_USERIMAGE","SP_DEFAULT")).into(iUserImage);
        ic = new InternetConnection(this);

        ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                mainImageUri = result;
                iUserImage.setImageURI(result);
            }
        });
        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch("image/*");
            }
        });
    }
    void initViews(){
        iUserImage = findViewById(R.id.sp_update_profile_image);
        btnChooseImage = findViewById(R.id.choose_image_button_update_sp_profile);
        btnChooseImage.setOnClickListener(this);
        etUsername = findViewById(R.id.sp_username_register);
        etWhatsApp = findViewById(R.id.sp_whatsapp_register);
        etPassword = findViewById(R.id.sp_password_register);
        btnUpdateAccount = findViewById(R.id.sp_profile_update_btn);
        btnUpdateAccount.setOnClickListener(this);
        btnChooseAddress = findViewById(R.id.choose_address_button_update_profile_sp);
        btnChooseAddress.setOnClickListener(this);
        tvAddress = findViewById(R.id.update_sp_profile_choosed_address_text);
        progressDialog = new ProgressDialog(this);
    }
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sp_profile_update_btn:
                if(!ic.checkInternetConnection()){
                    Toast.makeText(this, "لا يوجد إتصال بالإنترنت.", Toast.LENGTH_LONG).show();
                }else{
                    updateProfileClick();
                }
                break;
            case R.id.choose_address_button_update_profile_sp:
                checkLocationPermission();
                break;
        }
    }
    void updateProfileClick(){
        databaseServiceProvider = FirebaseDatabase.getInstance().getReference("ServiceProviders");
        new AlertDialog.Builder(this, R.style.AlertDialogCustom)
                .setTitle("تأكيد تغيير البيانات")
                .setMessage("هل أنت متأكد من البيانات التي تم إدخالها؟ ، من فضلك راجع جميع البيانات...")
                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.setMessage(" يرجى الانتظار قليلاً، جاري تغيير البيانات... ");
                        progressDialog.show();

                        // Check if username changed, change it in firebase and local data
                        if(!TextUtils.isEmpty(etUsername.getText())){
                            if(!etUsername.getText().equals(sp_data.getString("SP_USERNAME","SP_DEFAULT"))){
                                // Change username in firebase for the current user, in realtime db, in local data
                                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(etUsername.getText().toString().trim()).build();
                                user.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            databaseServiceProvider.child(user.getUid()).child("username").setValue(etUsername.getText().toString());
                                            SharedPreferences cld = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = cld.edit();
                                            editor.putString("SP_USERNAME", etUsername.getText().toString());
                                            editor.apply();
                                        }
                                    }
                                });
                            }
                        }

                        //Check if password changed, change it in firebase and local data
                        if(!TextUtils.isEmpty(etPassword.getText())){
                            if(!etPassword.getText().equals(sp_data.getString("SP_PASSWORD","SP_DEFAULT"))){
                                user.updatePassword(etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            databaseServiceProvider.child(user.getUid()).child("password").setValue(etPassword.getText().toString());
                                            SharedPreferences cld = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = cld.edit();
                                            editor.putString("SP_PASSWORD", etPassword.getText().toString());
                                            editor.apply();
                                        }
                                    }
                                });


                            }
                        }

                        //Check if whatsApp changed, change it in firebase, local data
                        if(!TextUtils.isEmpty(etWhatsApp.getText())){
                            if(!etWhatsApp.getText().equals(sp_data.getString("SP_WHATSAPP","SP_DEFAULT"))){
                                databaseServiceProvider.child(user.getUid()).child("whatsApp").setValue(etWhatsApp.getText().toString());
                                SharedPreferences cld = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = cld.edit();
                                editor.putString("SP_WHATSAPP", etWhatsApp.getText().toString());
                                editor.apply();
                            }
                        }


                        //Check if address changed, change it in firebase, local data
                        if(!TextUtils.isEmpty(tvAddress.getText())){
                            databaseServiceProvider.child(user.getUid()).child("address").setValue(tvAddress.getText().toString());
                            SharedPreferences cld = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = cld.edit();
                            editor.putString("SP_ADDRESS", tvAddress.getText().toString());
                            editor.apply();
                        }


                        //Check if image changed, upload it to storage, change it in firebase realtime db, change in local data
                        if(mainImageUri != null){
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                            final StorageReference image = storageReference.child("UserImages").child(mainImageUri.getLastPathSegment());
                            // Upload the image to firebase storage
                            image.putFile(mainImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> taskImage) {
                                            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                                    .setPhotoUri(taskImage.getResult()).build();
                                            user.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        databaseServiceProvider.child(user.getUid()).child("image").setValue(taskImage.getResult().toString());
                                                        SharedPreferences cld = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = cld.edit();
                                                        editor.putString("SP_USERIMAGE", taskImage.getResult().toString());
                                                        editor.apply();
                                                        progressDialog.dismiss();
                                                        finish();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                        if(mainImageUri == null){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    finish();
                                }
                            }, 1000);

                        }
                    }
                })
                .setNegativeButton("لا", null)
                .show();

    }
    void getAddress() {
        Intent addressMapIntent = new Intent(ServicProviderUpdateProfile.this, AddressMap.class);
        startActivityForResult(addressMapIntent, 11);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 11) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("ADDRESS_VALUE");
                tvAddress.setVisibility(View.VISIBLE);
                tvAddress.setText(result);
            }
        }
    }
    private void checkLocationPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                if(!ic.checkInternetConnection()){
                    Toast.makeText(ServicProviderUpdateProfile.this, "لا يوجد إتصال بالإنترنت.", Toast.LENGTH_LONG).show();
                }else{
                    getAddress();
                }
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
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_sp_profile_update);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("تحديث بيانات الصفحة الشخصية");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(ServicProviderUpdateProfile.this, R.style.Theme_ResQme);
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}