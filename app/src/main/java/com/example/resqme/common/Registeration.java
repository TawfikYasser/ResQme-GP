package com.example.resqme.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resqme.R;

import com.example.resqme.customer.CustomerHome;
import com.example.resqme.model.Customer;
import com.example.resqme.model.ServiceProvider;
import com.example.resqme.serviceProvider.ServiceProviderHome;
import com.github.dhaval2404.imagepicker.ImagePicker;
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


public class Registeration extends AppCompatActivity implements View.OnClickListener{


    // Views Initiation
    ImageView iUserImage;
    TextInputEditText etUsername, etEmailAddress, etWhatsApp, etPassword, etAddress;
    Button btnChooseDate, btnCreateAccount, btnChooseImage;
    RadioGroup rgUserType, rgUserGender;
    RadioButton rbtnCustomer, rbtnServiceProvider, rbtnMale, rbtnFemale;
    TextView tvLogin;

    String bod = "";
    Uri mainImageUri = null;
    //Firebase Initiation
    FirebaseAuth registrationFirebase;
    FirebaseUser theUser;
    DatabaseReference databaseTableCustomers, databaseTableSP;
    StorageReference mStorageReference;


    //More views
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);
        initViews();
        firebaseData();


        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("اختار تاريخ الميلاد");
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
        btnChooseDate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
                    }
                });

        materialDatePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        bod = materialDatePicker.getHeaderText();
                    }
                });


    }

    void initViews(){
        iUserImage = findViewById(R.id.choose_image_register);

        btnChooseImage = findViewById(R.id.choose_image_button);
        btnChooseImage.setOnClickListener(this);

        etUsername = findViewById(R.id.username_register);
        etEmailAddress = findViewById(R.id.email_register);
        etWhatsApp = findViewById(R.id.whatsapp_register);
        etPassword = findViewById(R.id.password_register);
        etAddress = findViewById(R.id.address_register);

        btnChooseDate = findViewById(R.id.pick_date_button);
        btnChooseDate.setOnClickListener(this);
        btnCreateAccount = findViewById(R.id.register_btn);
        btnCreateAccount.setOnClickListener(this);


        rgUserType = findViewById(R.id.radio_register_user_type);
        rgUserGender = findViewById(R.id.radio_register_user_gender);
        rbtnCustomer = findViewById(R.id.radio_customer);
        rbtnServiceProvider = findViewById(R.id.radio_sp);
        rbtnMale = findViewById(R.id.radio_male);
        rbtnFemale = findViewById(R.id.radio_female);
        tvLogin = findViewById(R.id.login_text_from_register);
        tvLogin.setOnClickListener(this);

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
            case R.id.choose_image_button:
                gettingImageFromGallery();
                break;
            case R.id.login_text_from_register:
                Intent toLoginIntent = new Intent(Registeration.this,Login.class);
                startActivity(toLoginIntent);
                finish();
                break;
            case R.id.register_btn:
                String username = etUsername.getText().toString();
                String email = etEmailAddress.getText().toString();
                String password = etPassword.getText().toString();
                if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) ){
                    progressDialog.setMessage("مرحباً "+username+" يرجى الانتظار قليلاً، جاري إنشاء الحساب... ");
                    progressDialog.show();
                    createNewAccount(email,password,username);
                }else{
                    progressDialog.dismiss();
                    Snackbar.make(findViewById(android.R.id.content),"يجب إدخال جميع البيانات!",Snackbar.LENGTH_LONG)
                            .setBackgroundTint(getResources().getColor(R.color.red_color))
                            .setTextColor(getResources().getColor(R.color.white))
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                }
                break;
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
                    Toast.makeText(Registeration.this, "خطأ: "+errorMsg, Toast.LENGTH_SHORT).show();
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
                        UserProfileChangeRequest userProfileChangeRequest =new UserProfileChangeRequest.Builder()
                                .setDisplayName(username).setPhotoUri(uri).build();

                        int selectedType = rgUserType.getCheckedRadioButtonId();
                        rbtnCustomer=(RadioButton)findViewById(selectedType);

                        int selectedGender = rgUserGender.getCheckedRadioButtonId();
                        rbtnMale=(RadioButton)findViewById(selectedGender);

                        String selectedUserType = rbtnCustomer.getText().toString();
                        if(selectedUserType.equals("عميل")){
                            DatabaseReference databaseReference_customer =databaseTableCustomers.child(generatedID);

                            Customer customer = new Customer(0, username, userEmail, etPassword.getText().toString(), uri.toString(), "DEFAULT", etWhatsApp.getText().toString(), bod, generatedID, 5, rbtnMale.getText().toString(), selectedUserType);

                            databaseReference_customer.setValue(customer);

                        }else{
                            DatabaseReference databaseReference_sp =databaseTableSP.child(generatedID);

                            ServiceProvider serviceProvider = new ServiceProvider(username, userEmail, etPassword.getText().toString(), uri.toString(), "DEFAULT", etWhatsApp.getText().toString(), bod, generatedID, 5, rbtnMale.getText().toString(), selectedUserType);

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
                                        Intent mainIntent = new Intent(Registeration.this, CustomerHome.class);
                                        startActivity(mainIntent);
                                        finish();
                                    }else{
                                        Intent mainIntent = new Intent(Registeration.this, ServiceProviderHome.class);
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

    void gettingImageFromGallery(){
        ImagePicker.with(this)
                .crop()	 //Crop image(Optional), Check Customization for more option
                .compress(1024)	//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        mainImageUri = uri;
        iUserImage.setImageURI(uri);
    }


}