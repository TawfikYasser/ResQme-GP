package com.example.resqme.serviceProvider;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.resqme.R;
import com.example.resqme.model.CMC;
import com.example.resqme.model.SparePart;
import com.example.resqme.model.Winch;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddSpareParts extends AppCompatActivity implements View.OnClickListener{
    Button choosesparepartimage,SbmtBtn;
    TextInputEditText sparepartname,SparePartStatus,Pricesparepart;
    RadioGroup rgUsedOrNewItem;
    RadioButton rbtnNewItem, rbtnUsedItem;
    ImageView sparepartimage;
    AutoCompleteTextView cartype;
    String Cartype="";
    Context context;
    DatabaseReference ServicesTable,ServiceProvidersTable;
    StorageReference sparePartsImages;
    Uri sparePartsUri = null;
    ProgressDialog progressDialog;


    String fromSTR = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_add_spareparts_data);
        context = this.getApplicationContext();
        ServicesTable = FirebaseDatabase.getInstance().getReference().child("SpareParts");
        ServiceProvidersTable = FirebaseDatabase.getInstance().getReference().child("ServiceProviders");
        sparePartsImages = FirebaseStorage.getInstance().getReference().child("ServiceImages");
        initToolbar();
        initViews();
        forceRTLIfSupported();


        Intent fromWhere = getIntent();
        fromSTR = fromWhere.getStringExtra("FROM");

        if(TextUtils.isEmpty(fromSTR)){
            fromSTR = "";
        }

        ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                sparePartsUri = result;
                sparepartimage.setImageURI(result);
            }
        });
        choosesparepartimage.setOnClickListener(new View.OnClickListener() {
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

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_AddSparePartsData);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("اضافة بيانات قطع الغيار");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(AddSpareParts.this, R.style.Theme_ResQme);
    }

    private void initViews() {
        progressDialog = new ProgressDialog(this);
        cartype= findViewById(R.id.spare_parts_brand_dropdown_list);
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
            }
        });
        rgUsedOrNewItem = findViewById(R.id.radio_spare_part_item_used_or_new);
        rbtnNewItem = findViewById(R.id.radio_new_item);
        rbtnUsedItem = findViewById(R.id.radio_used_item);
        sparepartimage=findViewById(R.id.spare_parts_image_add_spare_parts_data) ;
        choosesparepartimage=findViewById(R.id.chooseimagespareparts_button);
        choosesparepartimage.setOnClickListener(this);
        sparepartname=findViewById(R.id.spare_parts_item_name_et);
        Pricesparepart=findViewById(R.id.pricespareparts);
        SbmtBtn = findViewById(R.id.submit_spare_part_data);
        SbmtBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit_spare_part_data:
                AddSparePart();
                break;
        }
    }

    private void AddSparePart() {



        if(!TextUtils.isEmpty(sparepartname.getText()) && !TextUtils.isEmpty(Pricesparepart.getText().toString())
        && !TextUtils.isEmpty(Cartype) && sparePartsUri != null){
            new AlertDialog.Builder(this)
                    .setTitle("تأكيد إدخال البيانات")
                    .setMessage("هل أنت متأكد من البيانات التي تم إدخالها؟ ، من فضلك راجع جميع البيانات...")
                    .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.setMessage("يرجى الإنتظار قليلاً جاري إرسال البيانات!");
                            progressDialog.show();
                            progressDialog.setCancelable(false);
                            submitSparePartItemData();
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

    private void submitSparePartItemData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        StorageReference filepath_SparePartsImage = FirebaseStorage.getInstance().getReference().child("ServiceImages").child(sparePartsUri.getLastPathSegment());

        filepath_SparePartsImage.putFile(sparePartsUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filepath_SparePartsImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        int selectedType = rgUsedOrNewItem.getCheckedRadioButtonId();
                        rbtnUsedItem=(RadioButton)findViewById(selectedType);

                        String sparePartID = database.getReference("SpareParts").push().getKey();// create new id
                        SharedPreferences SP_services = getSharedPreferences("SP_LOCAL_DATA", Context.MODE_PRIVATE); //Pointer on local data
                        String sp_userid = SP_services.getString("SP_USERID","SP_DEFAULT");
                        SparePart sparePart = new SparePart(sparePartID, sparepartname.getText().toString(), uri.toString(),
                                Pricesparepart.getText().toString(), rbtnUsedItem.getText().toString(), "Pending",
                                sp_userid, Cartype, "Available");
                        ServicesTable.child(sparePartID).setValue(sparePart); //Set Service data in database

                        if(fromSTR.equals("SPHOME")){
                            ServiceProvidersTable.child(sp_userid).child("serviceType").setValue("CMC");// Set the value of serviceType attribute in the service provider table
                        }else {
                            ServiceProvidersTable.child(sp_userid).child("serviceType").setValue("SpareParts");// Set the value of serviceType attribute in the service provider table
                        }
                        ServiceProvidersTable.child(sp_userid).child("isSpareParts").setValue("True");// Set the value of serviceType attribute in the service provider table

                        // Related to service provider service type handling.
                        SharedPreferences cld = getSharedPreferences ("SP_LOCAL_DATA", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = cld.edit();
                        if(fromSTR.equals("SPHOME")){
                            editor.putString("SP_ServiceType", "CMC");
                        }else{
                            editor.putString("SP_ServiceType", "SpareParts");
                        }

                        editor.putString("SP_SPARE_PARTS", "True");
                        editor.apply();

                        progressDialog.dismiss();
                        Intent toAddcmcintent = new Intent(AddSpareParts.this,ServiceProviderHome.class);
                        startActivity(toAddcmcintent);
                        finish();
                    }
                });
            }
        });
    }

}
