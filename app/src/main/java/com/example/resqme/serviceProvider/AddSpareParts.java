package com.example.resqme.serviceProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.resqme.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddSpareParts extends AppCompatActivity implements View.OnClickListener{
    Button choosesparepartimage;
    TextInputEditText sparepartname,sparepartownername,Pricesparepart,sparepartweight;
    CircleImageView sparepartimage;
    AutoCompleteTextView cartype;
    String  Cartype="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.acitivity_add_spareparts_data);
        initViews();
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
        sparepartownername=findViewById(R.id.namesparepartowner);
        Pricesparepart=findViewById(R.id.pricespareparts);
        sparepartweight=findViewById(R.id.itemweight);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chooseimagespareparts_button:
                gettingImageFromGallery();
                break;


        }
    }

    private void gettingImageFromGallery() {
        ImagePicker.with(this)
                .crop()	 //Crop image(Optional), Check Customization for more option
                .compress(1024)	//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }
}
