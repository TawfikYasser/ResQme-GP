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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.resqme.R;
import com.example.resqme.model.SparePart;
import com.example.resqme.model.Winch;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddSpareParts extends AppCompatActivity implements View.OnClickListener{
    Button choosesparepartimage,SbmtBtn;
    TextInputEditText sparepartname,SparePartStatus,Pricesparepart;
    CircleImageView sparepartimage;
    AutoCompleteTextView cartype;
    String  Cartype="";
    Context context;
    DatabaseReference ServicesTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_add_spareparts_data);
        context = this.getApplicationContext();
        ServicesTable = FirebaseDatabase.getInstance().getReference().child("Services");
        initToolbar();
        initViews();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_AddSparePartsData);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("اضافه قطع غيار");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(AddSpareParts.this, R.style.Theme_ResQme);
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
            }
        });
        sparepartimage=findViewById(R.id.chooseimagespareparts) ;
        choosesparepartimage=findViewById(R.id.chooseimagespareparts_button);

        choosesparepartimage.setOnClickListener(this);

        sparepartname=findViewById(R.id.namespareparts);
        SparePartStatus=findViewById(R.id.spare_part_status);
        Pricesparepart=findViewById(R.id.pricespareparts);
        SbmtBtn = findViewById(R.id.addspareparts);
        SbmtBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chooseimagespareparts_button:
                gettingImageFromGallery();
                break;
            case R.id.addspareparts:
                AddSparePart();
                break;

        }
    }

    private void AddSparePart() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String ServiceID = database.getReference("Services").push().getKey();// create new id
        SharedPreferences SP_services = getSharedPreferences("SP_LOCAL_DATA", Context.MODE_PRIVATE);//Pointer on local data
        String sp_userid = SP_services.getString("SP_USERID","SP_DEFAULT");
        SparePart spPart = new SparePart(ServiceID,sparepartname.getText().toString(),"123",Pricesparepart.getText().toString(),SparePartStatus.getText().toString(),"Available",sp_userid,Cartype);
        ServicesTable.child("SparePart").setValue(spPart);//Entering Service in database
        Toast.makeText(this,"تم تسجيل القطعه بنجاح",Toast.LENGTH_LONG).show();
        finish();

//      SparePart(String itemID, String itemName, String itemImage, String itemPrice, String itemNewOrUsed, String itemStatus, String itemServiceProviderId, String itemCarType)
    }

    private void gettingImageFromGallery() {
        ImagePicker.with(this)
                .crop()	 //Crop image(Optional), Check Customization for more option
                .compress(1024)	//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }
}
