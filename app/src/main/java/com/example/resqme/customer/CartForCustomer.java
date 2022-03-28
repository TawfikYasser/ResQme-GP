package com.example.resqme.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.resqme.R;
import com.example.resqme.model.SparePartInCart;
import com.example.resqme.model.WinchRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CartForCustomer extends AppCompatActivity {
    RecyclerView spareCartRV;
    DatabaseReference shoppingDB;
    SpareCartAdapter spareCartAdapter;
    ArrayList<SparePartInCart> sparePartInCarts;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_for_customer);
        initToolbar();
        forceRTLIfSupported();

        spareCartRV = findViewById(R.id.spareparts_cart_recycler);
        context = this.getApplicationContext();
        shoppingDB = FirebaseDatabase.getInstance().getReference().child("ShoppingCart");
        spareCartRV.setHasFixedSize(true);
        spareCartRV.setLayoutManager(new LinearLayoutManager(this));
        sparePartInCarts = new ArrayList<>();
        spareCartAdapter = new SpareCartAdapter(this, sparePartInCarts);
        spareCartRV.setAdapter(spareCartAdapter);
        SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
        String c_userid = userData.getString("C_USERID", "C_DEFAULT");

        shoppingDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sparePartInCarts.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    SparePartInCart sparePartInCart = dataSnapshot.getValue(SparePartInCart.class);
                    if (sparePartInCart.getCustomerID().equals(c_userid)) {
                        sparePartInCarts.add(sparePartInCart);
                        spareCartAdapter = new SpareCartAdapter(context, sparePartInCarts);
                        spareCartRV.setAdapter(spareCartAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_sparecart);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("عربة تسوق قطع الغيار");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(CartForCustomer.this, R.style.Theme_ResQme);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}