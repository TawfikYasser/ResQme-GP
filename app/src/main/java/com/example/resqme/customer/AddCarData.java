package com.example.resqme.customer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.resqme.R;
import com.example.resqme.model.Car;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddCarData extends AppCompatActivity implements View.OnClickListener{

    AutoCompleteTextView aCarType, aCarModelYear, aCarMaintenance;
    RadioGroup rgTransmissionType;
    RadioButton rbtnAuto, rbtnManual;
    Button btnChooseDriverLicence, btnChooseCarLicence, btnSaveSend;
    ImageView ivDriverLicence, ivCarLicence;
    Uri driverLicence, carLicence;
    String carType = "", carModel = "", carMaintenance = "";

    DatabaseReference carTable;
    StorageReference carImages;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car_data);
        // Initialization of cars table
        carTable = FirebaseDatabase.getInstance().getReference().child("Cars");
        // Initialization of cars images
        carImages= FirebaseStorage.getInstance().getReference().child("CarImages");

        forceRTLIfSupported();
        initToolbar();
        initViews();

    }

    private void initViews() {



        aCarType = findViewById(R.id.car_types_dropdown_list);
        String[] arrayCarTypes = getResources().getStringArray(R.array.cartypes);
        ArrayAdapter<String> adapterCarTypes = new ArrayAdapter<String>
                (this, R.layout.item_add_car_data_dropdownlist, arrayCarTypes);
        aCarType.setThreshold(1);
        aCarType.setAdapter(adapterCarTypes);

        aCarType.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Object item = parent.getItemAtPosition(position);
                carType = item.toString().trim();
            }
        });

        aCarModelYear = findViewById(R.id.car_model_years_dropdown_list);
        String[] arrayCarModels = getResources().getStringArray(R.array.carmodelyears);
        ArrayAdapter<String> adapterCarModels = new ArrayAdapter<String>
                (this, R.layout.item_add_car_data_dropdownlist, arrayCarModels);
        aCarModelYear.setThreshold(1);
        aCarModelYear.setAdapter(adapterCarModels);

        aCarModelYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Object item = parent.getItemAtPosition(position);
                carModel = item.toString().trim();
            }
        });

        aCarMaintenance = findViewById(R.id.maintenance_period_dropdown_list);
        String[] arrayCarMaintenance = getResources().getStringArray(R.array.maintenanceperoid);
        ArrayAdapter<String> adapterCarMaintenance = new ArrayAdapter<String>
                (this, R.layout.item_add_car_data_dropdownlist, arrayCarMaintenance);
        aCarMaintenance.setThreshold(1);
        aCarMaintenance.setAdapter(adapterCarMaintenance);

        aCarMaintenance.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Object item = parent.getItemAtPosition(position);
                carMaintenance = item.toString().trim();
            }
        });

        rgTransmissionType = findViewById(R.id.radio_transmission);
        rbtnAuto = findViewById(R.id.radio_automatic);
        rbtnManual = findViewById(R.id.radio_manual);

        btnChooseCarLicence = findViewById(R.id.choose_car_licence_image_button);
        btnChooseCarLicence.setOnClickListener(this);
        btnChooseDriverLicence = findViewById(R.id.choose_driver_licence_image_button);
        btnChooseDriverLicence.setOnClickListener(this);
        btnSaveSend = findViewById(R.id.save_car_data_btn);
        btnSaveSend.setOnClickListener(this);

        ivDriverLicence = findViewById(R.id.driver_licence_image);
        ivCarLicence = findViewById(R.id.car_licence_image);
        progressDialog = new ProgressDialog(this);

    }

    private void initToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_addcardata);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("إضافة بيانات العربية");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(AddCarData.this, R.style.Theme_ResQme);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.choose_driver_licence_image_button:
                getDriverLicenceImage();
                break;
            case R.id.choose_car_licence_image_button:
                getCarLicenceImage();
                break;
            case R.id.save_car_data_btn:
                saveSendData();
                break;
        }
    }

    private void saveSendData() {
        int selectedType = rgTransmissionType.getCheckedRadioButtonId();
        rbtnAuto=(RadioButton)findViewById(selectedType);
        //rbtnAuto.getText().toString()

        if(!TextUtils.isEmpty(carType) && !TextUtils.isEmpty(carModel) && !TextUtils.isEmpty(carMaintenance)
        && driverLicence != null && carLicence != null
        && !TextUtils.isEmpty(rbtnAuto.getText().toString())){

            new AlertDialog.Builder(this)
                    .setTitle("تأكيد إدخال البيانات")
                    .setMessage("هل أنت متأكد من البيانات التي تم إدخالها؟ ، من فضلك راجع جميع البيانات...")
                    .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.setMessage("يرجى الإنتظار قليلاً جاري إرسال البيانات!");
                            progressDialog.show();

                            uploadData();

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

    private void uploadData() {
        final Uri[] driverLicence_URI = new Uri[1];
        final Uri[] carLicence_URI = new Uri[1];
        final StorageReference filepath_driverlicence = carImages.child(driverLicence.getLastPathSegment());
        filepath_driverlicence.putFile(driverLicence).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               filepath_driverlicence.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                   @Override
                   public void onSuccess(Uri uri) {
                       driverLicence_URI[0] = uri;
                       final StorageReference filepath_carlicence = carImages.child(carLicence.getLastPathSegment());
                       filepath_carlicence.putFile(carLicence).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                           @Override
                           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                filepath_carlicence.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        carLicence_URI[0] = uri;


                                        //Saving data to firebase realtime database
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        String carID = database.getReference("Cars").push().getKey();

                                        SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
                                        String c_userid = userData.getString("C_USERID","C_DEFAULT");
                                        int selectedType = rgTransmissionType.getCheckedRadioButtonId();
                                        rbtnAuto=(RadioButton)findViewById(selectedType);
                                        Car car = new Car(carID, c_userid, carType, carModel, carMaintenance,
                                                rbtnAuto.getText().toString(), driverLicence.toString().trim(),
                                                carLicence.toString().trim(), "Pending");


                                        // Changing car id in customer local file
                                        SharedPreferences cld = getSharedPreferences ("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = cld.edit();
                                        editor.putString("C_CARID", carID);
                                        editor.apply();

                                        // Saving car data in the local file
                                        SharedPreferences cardLocalData = getSharedPreferences ("CAR_LOCAL_DATA", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor_car = cardLocalData.edit();
                                        editor_car.putString("CAR_ID", carID);
                                        editor_car.putString("CAR_USER_ID", c_userid);
                                        editor_car.putString("CAR_TYPE", carType);
                                        editor_car.putString("CAR_MODEL", carModel);
                                        editor_car.putString("CAR_MAINTENANCE", carMaintenance);
                                        editor_car.putString("CAR_TRANSMISSION", rbtnAuto.getText().toString());
                                        editor_car.putString("CAR_DRIVER_LICENCE", driverLicence.toString().trim());
                                        editor_car.putString("CAR_LICENCE", carLicence.toString().trim());
                                        editor_car.putString("CAR_STATUS", "Pending");
                                        editor_car.apply();

                                        carTable.child(carID).setValue(car);
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
                .start(12);
    }
    void getCarLicenceImage(){
        ImagePicker.with(this)
                .crop()	 //Crop image(Optional), Check Customization for more option
                .compress(1024)	//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start(13);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 12){
            Uri uri = data.getData();
            driverLicence = uri;
            ivDriverLicence.setImageURI(uri);
        }else if(requestCode == 13){
            Uri uri = data.getData();
            carLicence = uri;
            ivCarLicence.setImageURI(uri);
        }

    }

}