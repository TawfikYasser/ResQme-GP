package com.example.resqme.common;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resqme.R;

import com.example.resqme.customer.CustomerHome;
import com.example.resqme.model.Customer;
import com.example.resqme.model.ServiceProvider;
import com.example.resqme.serviceProvider.ServiceProviderAddService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class Registeration extends AppCompatActivity implements View.OnClickListener{


    // Views Initiation
    CircleImageView iUserImage;
    TextInputEditText etUsername, etEmailAddress, etWhatsApp, etPassword, etAddress;
    Button btnChooseDate, btnCreateAccount, btnChooseImage, btnChooseAddress;
    RadioGroup rgUserType, rgUserGender;
    RadioButton rbtnCustomer, rbtnServiceProvider, rbtnMale, rbtnFemale;
    TextView tvLogin, tvAddress;

    String bod = "";

    Uri mainImageUri = null;

    //Firebase Initiation
    FirebaseAuth registrationFirebase;
    FirebaseUser theUser;
    DatabaseReference databaseTableCustomers, databaseTableSP; // Reference on database
    StorageReference mStorageReference;


    //More views
    ProgressDialog progressDialog;
    InternetConnection ic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);
        initViews();
        firebaseData();
        forceRTLIfSupported();
        ic = new InternetConnection(this);
        MaterialDatePicker.Builder<Long> materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("اختار تاريخ الميلاد");
        final MaterialDatePicker<Long> materialDatePicker = materialDateBuilder.build();
        btnChooseDate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

                    }
                });

        materialDatePicker.addOnPositiveButtonClickListener(
                (MaterialPickerOnPositiveButtonClickListener) selection -> bod = materialDatePicker.getHeaderText());


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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }

    void initViews(){
        iUserImage = findViewById(R.id.choose_image_register);

        btnChooseImage = findViewById(R.id.choose_image_button);
        btnChooseImage.setOnClickListener(this);

        etUsername = findViewById(R.id.username_register);
        etEmailAddress = findViewById(R.id.email_register);
        etWhatsApp = findViewById(R.id.whatsapp_register);
        etPassword = findViewById(R.id.password_register);

        btnChooseDate = findViewById(R.id.pick_date_button);
        btnChooseDate.setOnClickListener(this);
        btnCreateAccount = findViewById(R.id.register_btn);
        btnCreateAccount.setOnClickListener(this);
        btnChooseAddress = findViewById(R.id.choose_address_button);
        btnChooseAddress.setOnClickListener(this);

        rgUserType = findViewById(R.id.radio_register_user_type);
        rgUserGender = findViewById(R.id.radio_register_user_gender);
        rbtnCustomer = findViewById(R.id.radio_customer);
        rbtnServiceProvider = findViewById(R.id.radio_sp);
        rbtnMale = findViewById(R.id.radio_male);
        rbtnFemale = findViewById(R.id.radio_female);
        tvLogin = findViewById(R.id.login_text_from_register);
        tvLogin.setOnClickListener(this);
        tvAddress = findViewById(R.id.choosed_address_text);

        progressDialog = new ProgressDialog(this);
    }

    void firebaseData(){
        registrationFirebase = FirebaseAuth.getInstance();
        databaseTableCustomers = FirebaseDatabase.getInstance().getReference().child("Customer");
        databaseTableSP = FirebaseDatabase.getInstance().getReference().child("ServiceProviders");
        mStorageReference= FirebaseStorage.getInstance().getReference();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_text_from_register:
                Intent toLoginIntent = new Intent(Registeration.this,Login.class);
                startActivity(toLoginIntent);
                finish();
                break;
            case R.id.register_btn:
                if(!ic.checkInternetConnection()){
                    Snackbar.make(findViewById(android.R.id.content),"لا يوجد اتصال بالإنترنت.",Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.red_color))
                            .setTextColor(getResources().getColor(R.color.white))
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                }else{
                    createAccountClick();
                }
                break;
            case R.id.choose_address_button:
                checkLocationPermission();
                break;

        }
    }

    void getAddress() {
        Intent addressMapIntent = new Intent(Registeration.this,AddressMap.class);
        startActivityForResult(addressMapIntent, 11);
    }

    private void checkLocationPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                if(!ic.checkInternetConnection()){
                    Snackbar.make(findViewById(android.R.id.content),"لا يوجد اتصال بالإنترنت.",Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.red_color))
                            .setTextColor(getResources().getColor(R.color.white))
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
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


    void createAccountClick(){
        String username = etUsername.getText().toString();
        String email = etEmailAddress.getText().toString();
        String password = etPassword.getText().toString();
        String whatsApp = etWhatsApp.getText().toString();

        if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)
        && !TextUtils.isEmpty(whatsApp) && !TextUtils.isEmpty(bod) && mainImageUri != null && !TextUtils.isEmpty(tvAddress.getText())){
            // Check if number is not less than 11 digit
            if(whatsApp.length() == 11){
                if(whatsApp.startsWith("012")||whatsApp.startsWith("011")||whatsApp.startsWith("010")|| whatsApp.startsWith("015")){
                    // Convert bod to date
                    SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy");
                    Date date = null;
                    try {
                        date = sdf.parse(bod);
                    }   catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //check if date is not today or in the future
                    if(date.after(new Date())){
                        Snackbar.make(findViewById(android.R.id.content),"لا يمكن أن يكون تاريخ الميلاد في المستقبل",Snackbar.LENGTH_LONG)
                                .setBackgroundTint(getResources().getColor(R.color.red_color))
                                .setTextColor(getResources().getColor(R.color.white))
                                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                    }else{
                        // Validate if the date is today
                        //get current date in the same format of date
                        Date currentDate = new Date();
                        SimpleDateFormat sdf2 = new SimpleDateFormat("d MMMM yyyy");
                        String currentDateString = sdf2.format(currentDate);
                        Date currentDate2 = null;
                        try {
                            currentDate2 = sdf.parse(currentDateString);
                        }   catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if(date.equals(currentDate2)){
                            Snackbar.make(findViewById(android.R.id.content),"لا يمكن أن يكون تاريخ الميلاد هو تاريخ اليوم",Snackbar.LENGTH_LONG)
                                    .setBackgroundTint(getResources().getColor(R.color.red_color))
                                    .setTextColor(getResources().getColor(R.color.white))
                                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                        }else{
                            // based on date and currentDate2, check if age is less than 18
                            if(date.before(currentDate2)){
                                //check if age is less than 18
                                int age = currentDate2.getYear() - date.getYear();
                                if(age < 18){
                                    Snackbar.make(findViewById(android.R.id.content),"لا يمكن أن يكون السن أقل من 18 سنة",Snackbar.LENGTH_LONG)
                                            .setBackgroundTint(getResources().getColor(R.color.red_color))
                                            .setTextColor(getResources().getColor(R.color.white))
                                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                                }else{
                                    //Everything is ok
                                    new AlertDialog.Builder(this, R.style.AlertDialogCustom)
                                    .setTitle("تأكيد إنشاء الحساب")
                                    .setMessage("هل أنت متأكد من البيانات التي تم إدخالها؟ ، من فضلك راجع جميع البيانات...")
                                    .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            progressDialog.setMessage("مرحباً "+username+" يرجى الانتظار قليلاً، جاري إنشاء الحساب... ");
                                            progressDialog.show();
                                            progressDialog.setCancelable(false);
                                            createNewAccount(email.trim(),password.trim(),username.trim());
                                        }
                                    })
                                    .setNegativeButton("لا", null)
                                    .show();
                                }
                            }
                        }

                    }

                }else{
                    Snackbar.make(findViewById(android.R.id.content),"يجب أن يبدء رقم الواتساب بـ 012/011/010/015",Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.red_color))
                            .setTextColor(getResources().getColor(R.color.white))
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                }
            }else{
                Snackbar.make(findViewById(android.R.id.content),"يجب ألا يقل أو يزيد رقم الواتساب عن 11 رقم",Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getResources().getColor(R.color.red_color))
                        .setTextColor(getResources().getColor(R.color.white))
                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
            }
        }else{
            progressDialog.dismiss();
            Snackbar.make(findViewById(android.R.id.content),"يجب إدخال جميع البيانات!",Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getResources().getColor(R.color.red_color))
                    .setTextColor(getResources().getColor(R.color.white))
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
        }
    }


    void createNewAccount(String email, String password, String username){
        registrationFirebase.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = registrationFirebase.getCurrentUser();
                    //after creation we need to update profile with username and img
                    updateUserInfo(username,user);
                }else{
                    progressDialog.dismiss();
                    String errorMsg = task.getException().getMessage();
                    Snackbar.make(findViewById(android.R.id.content),errorMsg,Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.red_color))
                            .setTextColor(getResources().getColor(R.color.white))
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                }
            }
        });
    }


    void updateUserInfo(String username, final FirebaseUser user){


        final String userEmail= user.getEmail();
        final String generatedID = user.getUid();
        final StorageReference filepath = mStorageReference.child("UserImages").child(mainImageUri.getLastPathSegment());
        filepath.putFile(mainImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username).setPhotoUri(uri).build();

                        int selectedType = rgUserType.getCheckedRadioButtonId();
                        rbtnCustomer=(RadioButton)findViewById(selectedType);

                        int selectedGender = rgUserGender.getCheckedRadioButtonId();
                        rbtnMale=(RadioButton)findViewById(selectedGender);

                        String selectedUserType = rbtnCustomer.getText().toString();
                        if(selectedUserType.equals("عميل")){
                            DatabaseReference databaseReference_customer =databaseTableCustomers.child(generatedID);
                            Customer customer = new Customer("0", username, userEmail, etPassword.getText().toString(), uri.toString(), tvAddress.getText().toString().trim(), etWhatsApp.getText().toString(), bod, generatedID, "5", rbtnMale.getText().toString(), selectedUserType);
                            databaseReference_customer.setValue(customer);
                        }else{
                            DatabaseReference databaseReference_sp =databaseTableSP.child(generatedID);
                            ServiceProvider serviceProvider = new ServiceProvider(username, userEmail, etPassword.getText().toString(), uri.toString(), tvAddress.getText().toString().trim(), etWhatsApp.getText().toString(), bod, generatedID, "5", rbtnMale.getText().toString(), selectedUserType, "","","","");
                            databaseReference_sp.setValue(serviceProvider);
                        }

                        user.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    //user info updated
                                    int selectedType = rgUserType.getCheckedRadioButtonId();
                                    rbtnCustomer=(RadioButton)findViewById(selectedType);
                                    if(rbtnCustomer.getText().toString().equals("عميل")){
                                        saveLocalDataCustomer(username, userEmail, etPassword.getText().toString().trim(),
                                                tvAddress.getText().toString().trim(), etWhatsApp.getText().toString().trim(), bod, uri.toString(),
                                                "عميل", rbtnMale.getText().toString(), "0", "5", generatedID);
                                        Intent mainIntent = new Intent(Registeration.this, CustomerHome.class);
                                        startActivity(mainIntent);
                                        finish();
                                    }else{
                                        saveLocalDataSP(username, userEmail, etPassword.getText().toString().trim(),
                                                tvAddress.getText().toString().trim(), etWhatsApp.getText().toString().trim(), bod, uri.toString(),
                                                "مقدم خدمة", rbtnMale.getText().toString(), "5", generatedID);
                                        Intent mainIntent = new Intent(Registeration.this, ServiceProviderAddService.class);
                                        startActivity(mainIntent);
                                        finish();
                                    }
                                    progressDialog.dismiss();
                                }else{
                                    //something wrong
                                    progressDialog.dismiss();
                                    Snackbar.make(findViewById(android.R.id.content),"يوجد خطأ ما، برجاء المحاولة مرة أخرى!",Snackbar.LENGTH_LONG)
                                            .setBackgroundTint(getResources().getColor(R.color.red_color))
                                            .setTextColor(getResources().getColor(R.color.white))
                                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                                }
                            }
                        });
                    }
                });
            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 11){
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("ADDRESS_VALUE");
                tvAddress.setVisibility(View.VISIBLE);
                tvAddress.setText(result);
            }
        }

    }
        void saveLocalDataCustomer(String username, String email, String password, String address, String whatsApp,
                       String DOB, String userImage, String userType, String userGender, String carID, String userRate, String userID){

        SharedPreferences cld = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = cld.edit();
        editor.putString("C_USERNAME", username);
        editor.putString("C_EMAIL", email);
        editor.putString("C_PASSWORD", password);
        editor.putString("C_ADDRESS", address);
        editor.putString("C_WHATSAPP", whatsApp);
        editor.putString("C_DOB", DOB);
        editor.putString("C_USERIMAGE", userImage);
        editor.putString("C_USERTYPE", userType);
        editor.putString("C_USERGENDER", userGender);
        editor.putString("C_CARID", carID);
        editor.putString("C_USERRATE", userRate);
        editor.putString("C_USERID", userID);
        editor.apply();
    }
    void saveLocalDataSP(String username, String email, String password, String address, String whatsApp,
                               String DOB, String userImage, String userType, String userGender, String userRate, String userID){

        SharedPreferences spld = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spld.edit();
        editor.putString("SP_USERNAME", username);
        editor.putString("SP_EMAIL", email);
        editor.putString("SP_PASSWORD", password);
        editor.putString("SP_ADDRESS", address);
        editor.putString("SP_WHATSAPP", whatsApp);
        editor.putString("SP_DOB", DOB);
        editor.putString("SP_USERIMAGE", userImage);
        editor.putString("SP_USERTYPE", userType);
        editor.putString("SP_USERGENDER", userGender);
        editor.putString("SP_USERRATE", userRate);
        editor.putString("SP_USERID", userID);
        editor.putString("SP_ServiceType", "");
        editor.apply();

    }

}