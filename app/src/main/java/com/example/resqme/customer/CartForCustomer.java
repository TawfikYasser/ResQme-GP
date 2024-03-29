package com.example.resqme.customer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.resqme.R;
import com.example.resqme.common.DialogMessages;
import com.example.resqme.common.LogData;
import com.example.resqme.model.CMCRequest;
import com.example.resqme.model.SparePartInCart;
import com.example.resqme.model.SparePartsRequest;
import com.example.resqme.model.WinchRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class CartForCustomer extends AppCompatActivity {
    RecyclerView spareCartRV;
    DatabaseReference shoppingDB, sparePartsRequestsDB;
    SpareCartAdapter spareCartAdapter;
    ArrayList<SparePartInCart> sparePartInCarts;
    Context context;
    ProgressDialog progressDialog;
    FloatingActionButton sendSparePartsRequestFromCart;
    LinearLayout noItemsInCart;
    ShimmerFrameLayout shimmerCart;
    Locale locale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_for_customer);
        if(Locale.getDefault().getLanguage().equals("ar")){
            locale = new Locale("en");
            Locale.setDefault(locale);
            Resources resources = CartForCustomer.this.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }else{
            locale = new Locale("en");
        }
        initToolbar();
        forceRTLIfSupported();
        shimmerCart = findViewById(R.id.cart_shimmer);
        shimmerCart.startShimmer();
        noItemsInCart = findViewById(R.id.no_request_layout_cart);
        spareCartRV = findViewById(R.id.spareparts_cart_recycler);
        context = this.getApplicationContext();
        shoppingDB = FirebaseDatabase.getInstance().getReference().child("ShoppingCart");
        sparePartsRequestsDB = FirebaseDatabase.getInstance().getReference().child("SparePartsRequests");
        spareCartRV.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CartForCustomer.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        spareCartRV.setLayoutManager(linearLayoutManager);
        OverScrollDecoratorHelper.setUpOverScroll(spareCartRV, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        sparePartInCarts = new ArrayList<>();
        spareCartAdapter = new SpareCartAdapter(this, sparePartInCarts, shoppingDB);
        spareCartRV.setAdapter(spareCartAdapter);
        SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
        String c_userid = userData.getString("C_USERID", "C_DEFAULT");

        shoppingDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!shimmerCart.isShimmerStarted()){
                    shimmerCart.startShimmer();
                    shimmerCart.setVisibility(View.VISIBLE);
                    spareCartRV.setVisibility(View.GONE);
                }
                sparePartInCarts.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    SparePartInCart sparePartInCart = dataSnapshot.getValue(SparePartInCart.class);
                    if (sparePartInCart.getCustomerID().equals(c_userid)) {
                        sparePartInCarts.add(sparePartInCart);
                        spareCartAdapter.notifyDataSetChanged();
                    }
                }
                shimmerCart.stopShimmer();
                shimmerCart.setVisibility(View.GONE);
                spareCartRV.setVisibility(View.VISIBLE);
                if(sparePartInCarts.size() == 0){
                    noItemsInCart.setVisibility(View.VISIBLE);
                    sendSparePartsRequestFromCart.setEnabled(false);
                }else{
                    noItemsInCart.setVisibility(View.GONE);
                    sendSparePartsRequestFromCart.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        shoppingDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if(!shimmerCart.isShimmerStarted()){
                    shimmerCart.startShimmer();
                    shimmerCart.setVisibility(View.VISIBLE);
                    spareCartRV.setVisibility(View.GONE);
                }
                sparePartInCarts.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    if(!(dataSnapshot.getValue() instanceof String)){
                        SparePartInCart sparePartInCart = dataSnapshot.getValue(SparePartInCart.class);
                        if (sparePartInCart.getCustomerID().equals(c_userid)) {
                            sparePartInCarts.add(sparePartInCart);
                            spareCartAdapter.notifyDataSetChanged();
                        }
                    }
                }
                shimmerCart.stopShimmer();
                shimmerCart.setVisibility(View.GONE);
                spareCartRV.setVisibility(View.VISIBLE);
                if(sparePartInCarts.size() == 0){
                    sparePartInCarts.clear();
                    spareCartAdapter.notifyDataSetChanged();
                }
                if(sparePartInCarts.size() == 0){
                    noItemsInCart.setVisibility(View.VISIBLE);
                }else{
                    noItemsInCart.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendSparePartsRequestFromCart = findViewById(R.id.send_spare_parts_requests_from_cart);

        sendSparePartsRequestFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sparePartInCarts.size() != 0){
                    new AlertDialog.Builder(CartForCustomer.this, R.style.AlertDialogCustom)
                            .setTitle("طلب قطع غيار")
                            .setMessage("هل أنت متأكد من المتابعة؟ سيتم إرسال طلب بقطع الغيار التي اخترتها ويمكنك متابعة الطلبات في صفحة طلبات قطع الغيار.")
                            .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Initiation of Spare Parts Request
                                    LogData.saveLog("APP_CLICK","","","CLICK ON SEND SPARE PARTS REQUESTS BUTTON", "CART");
                                    initiationOfSparePartsRequest();
                                }
                            })
                            .setNegativeButton("لا", null)
                            .show();
                }else{
                    Toast.makeText(context, "لا توجد طلبات لإرسالها.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initiationOfSparePartsRequest() {
        progressDialog = new ProgressDialog(CartForCustomer.this);
        /*
        * The flow of request initiation as follows:
        * 0. Getting current customer location.
        * 1. Getting request data of each spare parts item (from the arraylist).
        * 2. Send each request of each item from the arraylist.
        * 3. Delete each item from the cart.
        * 4. Show Success/Failure message.
        *
        * */
        progressDialog.setMessage("جاري إرسال الطلبات، شكراً لإنتظارك :)...");
        progressDialog.show();
        progressDialog.setCancelable(false);


        FusedLocationProviderClient locationProviderClient = LocationServices.
                getFusedLocationProviderClient(CartForCustomer.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }

        locationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
            @Override
            public boolean isCancellationRequested() {
                return false;
            }

            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }
        }).addOnCompleteListener(location -> {
            if(location.isSuccessful()) {

                for(int i = 0 ; i <sparePartInCarts.size() ; i++){

                    FirebaseDatabase databaseRef = FirebaseDatabase.getInstance();
                    String sparePartsRequestID = databaseRef.getReference("SparePartsRequests").push().getKey();

                    Date currentTime = Calendar.getInstance().getTime();
                    String pattern = "yyyy-MM-dd HH:mm:ss";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                    String date = simpleDateFormat.format(currentTime);
                    String requestTimestamp = date;

                    SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
                    String c_userid = userData.getString("C_USERID", "C_DEFAULT");
                    String c_car_id = userData.getString("C_CARID", "C_DEFAULT");

                    //Send to Firebase

                    SparePartsRequest sparePartsRequest = new SparePartsRequest(

                            sparePartsRequestID, "Pending", c_userid, String.valueOf(location.getResult().getLatitude()),
                            String.valueOf(location.getResult().getLongitude()),
                            requestTimestamp, sparePartInCarts.get(i).getItemPrice(),
                            c_car_id, sparePartInCarts.get(i).getItemID(), sparePartInCarts.get(i).getItemServiceProviderId()

                    );


                    sparePartsRequestsDB.child(sparePartsRequestID).setValue(sparePartsRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                Snackbar.make(CartForCustomer.this.findViewById(android.R.id.content),task.getException().getMessage().toString(),Snackbar.LENGTH_LONG)
                                        .setBackgroundTint(getResources().getColor(R.color.red_color))
                                        .setTextColor(getResources().getColor(R.color.white))
                                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
                for(int j = 0; j < sparePartInCarts.size() ; j++){
                    // Remove the item from the cart.
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                            .child("ShoppingCart").child(sparePartInCarts.get(j).getItemInCartID());
                    reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                Snackbar.make(CartForCustomer.this.findViewById(android.R.id.content),task.getException().getMessage().toString(),Snackbar.LENGTH_LONG)
                                        .setBackgroundTint(getResources().getColor(R.color.red_color))
                                        .setTextColor(getResources().getColor(R.color.white))
                                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
                Snackbar.make(CartForCustomer.this.findViewById(android.R.id.content),"تم إرسال جميع الطلبات",Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getResources().getColor(R.color.blue_back))
                        .setTextColor(getResources().getColor(R.color.white))
                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
                progressDialog.dismiss();
                DialogMessages.showSuccessDialogWinchRequest(CartForCustomer.this);
            }else{
                progressDialog.dismiss();
                Snackbar.make(CartForCustomer.this.findViewById(android.R.id.content),"حدث مشكلة اثناء الحصول على عنوانك الحالي، برجاء المحاولة لاحقاً",Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getResources().getColor(R.color.red_color))
                        .setTextColor(getResources().getColor(R.color.white))
                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE).show();
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