package com.example.resqme.serviceProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.resqme.R;
import com.example.resqme.model.Report;
import com.example.resqme.model.Winch;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;


public class AddWinchData  extends AppCompatActivity implements View.OnClickListener{
    Button choosewinchimagebtn,chooseisencedriverwinchbtn,chooselisencewinchbtn,Sbmt,Addressbtn;
    TextInputEditText Winchname,platenumberwinch;
    CircleImageView winchimage,lisencedriverwinch, lisencewinch;
    Context context;
    DatabaseReference ServicesTable;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_winch_data);
        ServicesTable = FirebaseDatabase.getInstance().getReference().child("Services");
        context = this.getApplicationContext();
        initToolbar();
        initViews();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_AddWinch);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("اضاقه بيانات الونش");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(AddWinchData.this, R.style.Theme_ResQme);
    }

    private void initViews() {
       Addressbtn = findViewById(R.id.choose_address_button_for_winch);
       Addressbtn.setOnClickListener(this);
       winchimage=findViewById(R.id.chooseimagewinch) ;
       lisencewinch=findViewById(R.id.chooseimagelisencewinch);
       lisencedriverwinch=findViewById(R.id.chooseimagelisencedirverwinch);
       chooselisencewinchbtn=findViewById(R.id.chooseimagelisencewinch_button);
       chooselisencewinchbtn.setOnClickListener(this);
       choosewinchimagebtn=findViewById(R.id.chooseimagewinch_button);
       choosewinchimagebtn.setOnClickListener(this);
       chooseisencedriverwinchbtn=findViewById(R.id.chooseimagelisencedirverwinch_button);
       chooseisencedriverwinchbtn.setOnClickListener(this);
       Winchname=findViewById(R.id.namewinch);
       platenumberwinch=findViewById(R.id.platenumberwinch);
       Sbmt = findViewById(R.id.addwinch);
       Sbmt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chooseimagelisencedirverwinch_button:
            case R.id.chooseimagelisencewinch_button:
            case R.id.chooseimagewinch_button:
                gettingImageFromGallery();
                break;
            case R.id.choose_address_button_for_winch:
//                Add function here ##
                break;
            case R.id.addwinch:
                AddWinchService();
        }
}

    private void AddWinchService() {
//    create Services Table
//    Add Winch Data to service Table

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String ServiceID = database.getReference("Services").push().getKey();// create new id
        SharedPreferences SP_services = getSharedPreferences("SP_LOCAL_DATA", Context.MODE_PRIVATE);//Pointer on local data
        String sp_userid = SP_services.getString("SP_USERID","SP_DEFAULT");
        Winch winch = new Winch( "1","0", "3", "Available","x",ServiceID,  platenumberwinch.getText().toString(),  "Available",sp_userid);
        ServicesTable.child("Winch").setValue(winch);//Entering Service in database
        Toast.makeText(this,"تم تسجيل الونش بنجاح",Toast.LENGTH_LONG).show();
        finish();





    }


    private void gettingImageFromGallery() {
        ImagePicker.with(this)
                .crop()	 //Crop image(Optional), Check Customization for more option
                .compress(1024)	//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }}
