package com.example.resqme.serviceProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.resqme.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddWinchData  extends AppCompatActivity implements View.OnClickListener{
    Button choosewinchimagebtn,chooseisencedriverwinchbtn,chooselisencewinchbtn;
    TextInputEditText Winchname,Winchownername,Pricewinch,platenumberwinch;
    CircleImageView winchimage,lisencedriverwinch, lisencewinch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_winch_data);
        initViews();
    }

    private void initViews() {
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
       Winchownername=findViewById(R.id.namewinchowner);
       Pricewinch=findViewById(R.id.costwinch);
       platenumberwinch=findViewById(R.id.platenumberwinch);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chooseimagelisencedirverwinch_button:
                gettingImageFromGallery();
                break;
            case R.id.chooseimagelisencewinch_button:
                gettingImageFromGallery();
                break;
            case R.id.chooseimagewinch_button:
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
    }}
