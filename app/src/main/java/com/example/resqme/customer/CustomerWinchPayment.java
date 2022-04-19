package com.example.resqme.customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resqme.R;
import com.example.resqme.BuildConfig;
import com.google.firebase.database.annotations.NotNull;
import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.approve.Approval;
import com.paypal.checkout.approve.OnApprove;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.config.SettingsConfig;
import com.paypal.checkout.createorder.CreateOrder;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.ProcessingInstruction;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.error.ErrorInfo;
import com.paypal.checkout.error.OnError;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.CaptureOrderResult;
import com.paypal.checkout.order.OnCaptureComplete;
import com.paypal.checkout.order.Order;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PayLaterButton;
import com.paypal.checkout.paymentbutton.PaymentButton;

import java.util.ArrayList;

public class CustomerWinchPayment extends AppCompatActivity {
    private static final String YOUR_CLIENT_ID = "AaBQqC8ngkHQ5ypmI7URbzeMiRIN3SGQRV_5_e8jjTknA7Iy7FST-iE0BGKfJGJ09adbMhwRyQfCDKMi";
    PaymentButton payPalButton;
    String serviceCost = "";
    TextView tvEGP, tvUSD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_winch_payment);
        initToolbar();
        forceRTLIfSupported();
        Intent intent = getIntent();
        serviceCost = intent.getStringExtra("SERVICE_COST");
        tvEGP = findViewById(R.id.txt_payment_EGP);
        String egCost = String.valueOf((int)Math.round(Integer.parseInt(serviceCost)) / 18);
        tvEGP.setText("تكلفة الخدمة  "+serviceCost+" جنيه، أي ما يعادل "+ egCost+ " دولار.");
        payPalButton = findViewById(R.id.payPayWinchPayment);

        CheckoutConfig config = new CheckoutConfig(
                getApplication(),
                YOUR_CLIENT_ID,
                Environment.SANDBOX,
                "com.example.resqme://paypalpay",
                CurrencyCode.valueOf("USD"),
                UserAction.PAY_NOW
        );
        PayPalCheckout.setConfig(config);


        // Payment Process

        payPalButton.setup(
                new CreateOrder() {
                    @Override
                    public void create(@NotNull CreateOrderActions createOrderActions) {
                        ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
                        purchaseUnits.add(
                                new PurchaseUnit.Builder()
                                        .amount(
                                                new Amount.Builder()
                                                        .currencyCode(CurrencyCode.USD)
                                                        .value(egCost)
                                                        .build()
                                        )
                                        .build()
                        );
                        Order order = new Order(OrderIntent.CAPTURE, new AppContext.Builder().userAction(UserAction.PAY_NOW).build(), purchaseUnits, ProcessingInstruction.ORDER_COMPLETE_ON_PAYMENT_APPROVAL);
                        createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                    }
                },
                new OnApprove() {
                    @Override
                    public void onApprove(@NotNull Approval approval) {
                        approval.getOrderActions().capture(new OnCaptureComplete() {
                            @Override
                            public void onCaptureComplete(@NotNull CaptureOrderResult result) {
                                Toast.makeText(CustomerWinchPayment.this, "تمت العملية بنجاح وتم إرسال الطلب يمكنك متابعته الآن في صفحة الطلبات.", Toast.LENGTH_SHORT).show();
                                Intent goBackAfterPayment = new Intent();
                                goBackAfterPayment.putExtra("PAYMENT_STATUS", "SUCCESS_P_RESQME");
                                setResult(30, goBackAfterPayment);
                                finish();
                            }
                        });
                    }
                }
        );
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_winch_payment);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("صفحة الدفع");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(CustomerWinchPayment.this, R.style.Theme_ResQme);
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