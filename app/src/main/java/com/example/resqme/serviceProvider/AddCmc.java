package com.example.resqme.serviceProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.resqme.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddCmc extends AppCompatActivity implements View.OnClickListener{
    Button choosecmcimage;
    TextInputEditText cmcname,cmcownername,cmcbrand,cmclocation,cmctime,cmcdescription;
    CircleImageView cmcimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_cmc);
        initViews();
    }

    private void initViews() {

        cmcimage=findViewById(R.id.chooseimagecmc) ;
        choosecmcimage=findViewById(R.id.chooseimagecmcbutton);

        choosecmcimage.setOnClickListener(this);

        cmcbrand=findViewById(R.id.cmcbrand);
        cmcdescription=findViewById(R.id.cmcdescription);
        cmclocation=findViewById(R.id.cmclocation);
        cmcownername=findViewById(R.id.namecmcowner);
        cmcname=findViewById(R.id.namecmc);
        cmctime=findViewById(R.id.cmctime);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chooseimagecmcbutton:
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
