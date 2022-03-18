package com.example.resqme.serviceProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.resqme.R;
import com.example.resqme.common.MyReports;
import com.example.resqme.model.CMC;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddCmc extends AppCompatActivity implements View.OnClickListener{
    Button choosecmcimage,SbmtBtn,Addressbtn;
    TextInputEditText cmcname;
    CircleImageView cmcimage;
    Context context;
    DatabaseReference ServicesTable;
    AutoCompleteTextView cartype;
    String  Cartype="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cmc);
        context = this.getApplicationContext();
        ServicesTable = FirebaseDatabase.getInstance().getReference().child("Services");
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


        cmcname = findViewById(R.id.namecmc);
        SbmtBtn = findViewById(R.id.addcmc);
        SbmtBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chooseimagecmcbutton:
                gettingImageFromGallery();
                break;
            case R.id.choose_address_button_for_cmc:
//                Add function here ##
                break;
            case R.id.addcmc:
                AddCMC();
        }
    }

    private void AddCMC() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String ServiceID = database.getReference("Services").push().getKey();// create new id
        SharedPreferences SP_services = getSharedPreferences("SP_LOCAL_DATA", Context.MODE_PRIVATE);//Pointer on local data
        String sp_userid = SP_services.getString("SP_USERID","SP_DEFAULT");
        CMC cmc = new CMC(ServiceID,cmcname.getText().toString(),"123","ddsfs",Cartype,sp_userid,"Available");
        ServicesTable.child("CMC").setValue(cmc);//Entering Service in database
        Toast.makeText(this,"تم تسجيل مركز الخدمه بنجاح",Toast.LENGTH_LONG).show();
        finish();
    }

    private void gettingImageFromGallery() {
        ImagePicker.with(this)
                .crop()	 //Crop image(Optional), Check Customization for more option
                .compress(1024)	//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }
}
