package com.example.resqme.common;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.resqme.R;
import com.example.resqme.customer.CMCRequests;
import com.example.resqme.customer.CustomerProfile;
import com.example.resqme.customer.SparePartsRequests;
import com.example.resqme.customer.WinchRequests;
import com.example.resqme.model.CMCRequest;
import com.example.resqme.model.Car;
import com.example.resqme.model.NotificationHistory;
import com.example.resqme.model.Report;
import com.example.resqme.model.RequestDetailsModel;
import com.example.resqme.model.SparePartsRequest;
import com.example.resqme.model.WinchRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class OnAppStartLogic extends android.app.Application{
    @Override
    public void onCreate() {
        super.onCreate();
        /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // Notification Work
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Needed for notification history

            DatabaseReference nhref = FirebaseDatabase.getInstance().getReference().child("NotificationHistory");
            nhref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                    } else {
                        NotificationHistory notificationHistory = new NotificationHistory("c-00000");
                        nhref.child("c-00000").setValue(notificationHistory);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            /* Notification Work */

            // Reports
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Reports");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Report report = dataSnapshot.getValue(Report.class);
                        if (report.getUserID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                && report.getReportStatus().equals("Approved")) {

                            // Checking if the notification already sent
                            // if yes, don't send, else, send it

                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("NotificationHistory");
                            rootRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.hasChild(report.getReportID())) {
                                        //Mark the notification as sent
                                        NotificationHistory notificationHistory = new NotificationHistory(report.getReportID());
                                        DatabaseReference nh = FirebaseDatabase.getInstance().getReference().child("NotificationHistory");
                                        nh.child(report.getReportID()).setValue(notificationHistory);

                                        //Send it
                                        int num = (int) System.currentTimeMillis();
                                        Intent intent = new Intent(OnAppStartLogic.this, MyReports.class);
                                        @SuppressLint("WrongConstant") PendingIntent pendingIntent = PendingIntent.getActivity(OnAppStartLogic.this, num, intent, Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                                        String CHANNEL_ID = "reports_channel" + report.getReportID();
                                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(OnAppStartLogic.this, CHANNEL_ID)
                                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                                .setContentTitle("إشعار بخصوص احد التقارير التي ارسلتها")
                                                .setContentText("لقد تم الرد على التقرير الخاص بك، برجاء مراجعة البريد الإلكتروني...")
                                                .setAutoCancel(true)
                                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                                .setContentIntent(pendingIntent);

                                        NotificationManager notificationManager = (NotificationManager) OnAppStartLogic.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            CharSequence name = "ResQme Notification";// The user-visible name of the channel.
                                            int importance = NotificationManager.IMPORTANCE_HIGH;
                                            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                                            notificationManager.createNotificationChannel(mChannel);
                                        }

                                        notificationManager.notify(num, notificationBuilder.build()); // 0 is the request code, it should be unique i
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            // Winch requests
            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("WinchRequests");
            reference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        WinchRequest winchRequest = dataSnapshot.getValue(WinchRequest.class);
                        if (winchRequest.getCustomerID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                && winchRequest.getWinchRequestStatus().equals("Approved")) {


                            // Checking if the notification already sent
                            // if yes, don't send, else, send it

                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("NotificationHistory");
                            rootRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.hasChild(winchRequest.getWinchRequestID())) {
                                        //Mark the notification as sent
                                        NotificationHistory notificationHistory = new NotificationHistory(winchRequest.getWinchRequestID());
                                        DatabaseReference nh = FirebaseDatabase.getInstance().getReference().child("NotificationHistory");
                                        nh.child(winchRequest.getWinchRequestID()).setValue(notificationHistory);

                                        //Send it

                                        int num = (int) System.currentTimeMillis();
                                        Intent intent = new Intent(OnAppStartLogic.this, WinchRequests.class);
                                        @SuppressLint("WrongConstant") PendingIntent pendingIntent = PendingIntent.getActivity(OnAppStartLogic.this, num, intent, Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                                        String CHANNEL_ID = "winchrequests_channel" + winchRequest.getWinchRequestID();
                                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(OnAppStartLogic.this, CHANNEL_ID)
                                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                                .setContentTitle("إشعار بخصوص طلبك لونش")
                                                .setContentText("لقد قام صاحب الونش بقبول طلبك، يمكنك تتبع حركة الونش بالذهاب الى صفحة طلبات الونش او بالضغط على هذا الاشعار.")
                                                .setAutoCancel(true)
                                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                                .setContentIntent(pendingIntent);

                                        NotificationManager notificationManager = (NotificationManager) OnAppStartLogic.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            CharSequence name = "ResQme Notification";// The user-visible name of the channel.
                                            int importance = NotificationManager.IMPORTANCE_HIGH;
                                            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                                            notificationManager.createNotificationChannel(mChannel);
                                        }

                                        notificationManager.notify(num, notificationBuilder.build()); // 0 is the request code, it should be unique id
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            // CMC Requests
            DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("CMCRequests");
            reference2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        CMCRequest cmcRequest = dataSnapshot.getValue(CMCRequest.class);
                        if (cmcRequest.getCustomerID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                && cmcRequest.getCmcRequestStatus().equals("Approved")) {


                            // Checking if the notification already sent
                            // if yes, don't send, else, send it

                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("NotificationHistory");
                            rootRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.hasChild(cmcRequest.getCmcRequestID())) {
                                        //Mark the notification as sent
                                        NotificationHistory notificationHistory = new NotificationHistory(cmcRequest.getCmcRequestID());
                                        DatabaseReference nh = FirebaseDatabase.getInstance().getReference().child("NotificationHistory");
                                        nh.child(cmcRequest.getCmcRequestID()).setValue(notificationHistory);

                                        //Send it

                                        int num = (int) System.currentTimeMillis();
                                        Intent intent = new Intent(OnAppStartLogic.this, CMCRequests.class);
                                        @SuppressLint("WrongConstant") PendingIntent pendingIntent = PendingIntent.getActivity(OnAppStartLogic.this, num, intent, Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                                        String CHANNEL_ID = "cmcrequests_channel" + cmcRequest.getCmcRequestID();
                                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(OnAppStartLogic.this, CHANNEL_ID)
                                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                                .setContentTitle("إشعار بخصوص طلبك لمركز الصيانة")
                                                .setContentText("لقد قام صاحب مركز الصيانة بقبول طلبك، يمكنك الآن التواصل معه.")
                                                .setAutoCancel(true)
                                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                                .setContentIntent(pendingIntent);

                                        NotificationManager notificationManager = (NotificationManager) OnAppStartLogic.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            CharSequence name = "ResQme Notification";// The user-visible name of the channel.
                                            int importance = NotificationManager.IMPORTANCE_HIGH;
                                            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                                            notificationManager.createNotificationChannel(mChannel);
                                        }

                                        notificationManager.notify(num, notificationBuilder.build()); // 0 is the request code, it should be unique id
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            //Spare Parts Requests
            DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference().child("SparePartsRequests");
            reference3.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        SparePartsRequest sparePartsRequest = dataSnapshot.getValue(SparePartsRequest.class);
                        if (sparePartsRequest.getCustomerID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                && sparePartsRequest.getSparePartsRequestStatus().equals("Approved")) {

                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("NotificationHistory");
                            rootRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.hasChild(sparePartsRequest.getSparePartsRequestID())) {
                                        //Mark the notification as sent
                                        NotificationHistory notificationHistory = new NotificationHistory(sparePartsRequest.getSparePartsRequestID());
                                        DatabaseReference nh = FirebaseDatabase.getInstance().getReference().child("NotificationHistory");
                                        nh.child(sparePartsRequest.getSparePartsRequestID()).setValue(notificationHistory);

                                        //Send it

                                        int num = (int) System.currentTimeMillis();
                                        Intent intent = new Intent(OnAppStartLogic.this, SparePartsRequests.class);
                                        @SuppressLint("WrongConstant") PendingIntent pendingIntent = PendingIntent.getActivity(OnAppStartLogic.this, num, intent, Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                                        String CHANNEL_ID = "sparepartsrequests_channel" + sparePartsRequest.getSparePartsRequestID();
                                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(OnAppStartLogic.this, CHANNEL_ID)
                                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                                .setContentTitle("إشعار بخصوص طلبك لقطع غيار")
                                                .setContentText("أحد طلباتك تم قبولها، برجاء التوجه الى صفحة طلبات قطع الغيار أو اضغط على الاشعار.")
                                                .setAutoCancel(true)
                                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                                .setContentIntent(pendingIntent);

                                        NotificationManager notificationManager = (NotificationManager) OnAppStartLogic.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            CharSequence name = "ResQme Notification";// The user-visible name of the channel.
                                            int importance = NotificationManager.IMPORTANCE_HIGH;
                                            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                                            notificationManager.createNotificationChannel(mChannel);
                                        }

                                        notificationManager.notify(num, notificationBuilder.build()); // 0 is the request code, it should be unique id
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            // CAR Status notification

            DatabaseReference carsDB = FirebaseDatabase.getInstance().getReference().child("Cars");
            carsDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Car car = dataSnapshot.getValue(Car.class);
                        if (car.getUserID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            if (car.getCarStatus().equals("Approved")) {


                                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("NotificationHistory");
                                rootRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.hasChild(car.getCarID() + "STATUS:Approved")) {
                                            //Mark the notification as sent
                                            NotificationHistory notificationHistory = new NotificationHistory(car.getCarID() + "STATUS:Approved");
                                            DatabaseReference nh = FirebaseDatabase.getInstance().getReference().child("NotificationHistory");
                                            nh.child(car.getCarID() + "STATUS:Approved").setValue(notificationHistory);

                                            //Send it

                                            int num = (int) System.currentTimeMillis();
                                            Intent intent = new Intent(OnAppStartLogic.this, CustomerProfile.class);
                                            @SuppressLint("WrongConstant") PendingIntent pendingIntent = PendingIntent.getActivity(OnAppStartLogic.this, num, intent, Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                                            String CHANNEL_ID = "car_channel" + car.getCarID();
                                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(OnAppStartLogic.this, CHANNEL_ID)
                                                    .setSmallIcon(R.mipmap.ic_launcher_round)
                                                    .setContentTitle("إشعار بخصوص عربيتك")
                                                    .setContentText("مبروك! تم قبول العربية يمكنك الآن استخدام خدمات التطبيق.")
                                                    .setAutoCancel(true)
                                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                                    .setContentIntent(pendingIntent);

                                            NotificationManager notificationManager = (NotificationManager) OnAppStartLogic.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                CharSequence name = "ResQme Notification";// The user-visible name of the channel.
                                                int importance = NotificationManager.IMPORTANCE_HIGH;
                                                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                                                notificationManager.createNotificationChannel(mChannel);
                                            }

                                            notificationManager.notify(num, notificationBuilder.build()); // 0 is the request code, it should be unique id
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            }
                            if (car.getCarStatus().equals("Refused")) {


                                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("NotificationHistory");
                                rootRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.hasChild(car.getCarID() + "STATUS:Refused")) {
                                            //Mark the notification as sent
                                            NotificationHistory notificationHistory = new NotificationHistory(car.getCarID() + "STATUS:Refused");
                                            DatabaseReference nh = FirebaseDatabase.getInstance().getReference().child("NotificationHistory");
                                            nh.child(car.getCarID() + "STATUS:Refused").setValue(notificationHistory);

                                            //Send it

                                            int num = (int) System.currentTimeMillis();
                                            Intent intent = new Intent(OnAppStartLogic.this, CustomerProfile.class);
                                            @SuppressLint("WrongConstant") PendingIntent pendingIntent = PendingIntent.getActivity(OnAppStartLogic.this, num, intent, Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                                            String CHANNEL_ID = "car_channel" + car.getCarID();
                                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(OnAppStartLogic.this, CHANNEL_ID)
                                                    .setSmallIcon(R.mipmap.ic_launcher_round)
                                                    .setContentTitle("إشعار بخصوص عربيتك")
                                                    .setContentText("للأسف تم رفض العربية، تواصل معنا للمزيد.")
                                                    .setAutoCancel(true)
                                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                                    .setContentIntent(pendingIntent);

                                            NotificationManager notificationManager = (NotificationManager) OnAppStartLogic.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                CharSequence name = "ResQme Notification";// The user-visible name of the channel.
                                                int importance = NotificationManager.IMPORTANCE_HIGH;
                                                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                                                notificationManager.createNotificationChannel(mChannel);
                                            }

                                            notificationManager.notify(num, notificationBuilder.build()); // 0 is the request code, it should be unique id
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            // Customer Profile History
            // For each record in request details db, if the customer id is the same as current user
            // for each type [battery, engine, filter, lights, oil, or tier] check if the timestamp from
            // the related winch request is reached based on the type, send notification

            DatabaseReference requestDetailsRef = FirebaseDatabase.getInstance().getReference("RequestDetails");
            requestDetailsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds : snapshot.getChildren()){
                        RequestDetailsModel requestDetails = ds.getValue(RequestDetailsModel.class);
                        if(requestDetails.getCustomerId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            //For each type, check if the related winch request timestamp overpassed certain period based on the type
                            if(requestDetails.getBattery().equals("1")){

                                DatabaseReference winchRequestDB = FirebaseDatabase.getInstance().getReference().child("WinchRequests");
                                winchRequestDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            for (DataSnapshot issue : snapshot.getChildren()) {
                                                WinchRequest winchRequest = issue.getValue(WinchRequest.class);
                                                if(winchRequest.getWinchRequestID().equals(requestDetails.getWinchRequestId())){

                                                    // After 2 year we have to send a notification

                                                    // Get the difference between the current time and the time of the request in months
                                                    // convert the date from string to date
                                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                    Date date = null;
                                                    try {
                                                        date = sdf.parse(winchRequest.getWinchRequestInitiationDate());

                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                    long diffInMillies = Math.abs(System.currentTimeMillis() - date.getTime());
                                                    double diffInYears = TimeUnit.MILLISECONDS.toDays(diffInMillies) * 0.002738;

                                                    if(diffInYears > 2){


                                                        // Send the notification


                                                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("NotificationHistory");
                                                        rootRef.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (!snapshot.hasChild(requestDetails.getRequestDetailsId())) {
                                                                    //Mark the notification as sent
                                                                    NotificationHistory notificationHistory = new NotificationHistory(requestDetails.getRequestDetailsId());
                                                                    DatabaseReference nh = FirebaseDatabase.getInstance().getReference().child("NotificationHistory");
                                                                    nh.child(requestDetails.getRequestDetailsId()).setValue(notificationHistory);

                                                                    //Send it

                                                                    int num = (int) System.currentTimeMillis();
                                                                    Intent intent = new Intent(OnAppStartLogic.this, CustomerProfile.class); // Send to customer history page
                                                                    @SuppressLint("WrongConstant") PendingIntent pendingIntent = PendingIntent.getActivity(OnAppStartLogic.this, num, intent, Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                                                                    String CHANNEL_ID = "request_details_channel" + requestDetails.getRequestDetailsId();
                                                                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(OnAppStartLogic.this, CHANNEL_ID)
                                                                            .setSmallIcon(R.mipmap.ic_launcher_round)
                                                                            .setContentTitle("مرحباً يا صديقي العميل")
                                                                            .setContentText("بنفكرك إن عدى حوالى سنتين من آخر مرة عملت Check على البطارية. تقدر تتابع طلباتك من صفحة الطلبات السابقة في الصفحة الشخصية، أو دوس على الاشعار ده.")
                                                                            .setAutoCancel(true)
                                                                            .setStyle(new NotificationCompat.BigTextStyle()
                                                                                    .bigText("بنفكرك إن عدى حوالى سنتين من آخر مرة عملت Check على البطارية. تقدر تتابع طلباتك من صفحة الطلبات السابقة في الصفحة الشخصية، أو دوس على الاشعار ده."))
                                                                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                                                            .setContentIntent(pendingIntent);

                                                                    NotificationManager notificationManager = (NotificationManager) OnAppStartLogic.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                        CharSequence name = "ResQme Notification";// The user-visible name of the channel.
                                                                        int importance = NotificationManager.IMPORTANCE_HIGH;
                                                                        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                                                                        notificationManager.createNotificationChannel(mChannel);
                                                                    }

                                                                    notificationManager.notify(num, notificationBuilder.build()); // 0 is the request code, it should be unique id
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });

                                                    }


                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                            if(requestDetails.getFilter().equals("1")){

                                DatabaseReference winchRequestDB = FirebaseDatabase.getInstance().getReference().child("WinchRequests");
                                winchRequestDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            for (DataSnapshot issue : snapshot.getChildren()) {
                                                WinchRequest winchRequest = issue.getValue(WinchRequest.class);
                                                if(winchRequest.getWinchRequestID().equals(requestDetails.getWinchRequestId())){


                                                    // Filter after 8 months

                                                    // Get the difference between the current time and the time of the request in months
                                                    // convert the date from string to date
                                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                    Date date = null;
                                                    try {
                                                        date = sdf.parse(winchRequest.getWinchRequestInitiationDate());

                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                    long diffInMillies = Math.abs(System.currentTimeMillis() - date.getTime());
                                                    Double diffInMonths = TimeUnit.MILLISECONDS.toDays(diffInMillies) * 0.0328767;

                                                    if(diffInMonths > 8){


                                                        // Send the notification


                                                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("NotificationHistory");
                                                        rootRef.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (!snapshot.hasChild(requestDetails.getRequestDetailsId())) {
                                                                    //Mark the notification as sent
                                                                    NotificationHistory notificationHistory = new NotificationHistory(requestDetails.getRequestDetailsId());
                                                                    DatabaseReference nh = FirebaseDatabase.getInstance().getReference().child("NotificationHistory");
                                                                    nh.child(requestDetails.getRequestDetailsId()).setValue(notificationHistory);

                                                                    //Send it

                                                                    int num = (int) System.currentTimeMillis();
                                                                    Intent intent = new Intent(OnAppStartLogic.this, CustomerProfile.class); // Send to customer history page
                                                                    @SuppressLint("WrongConstant") PendingIntent pendingIntent = PendingIntent.getActivity(OnAppStartLogic.this, num, intent, Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                                                                    String CHANNEL_ID = "request_details_channel" + requestDetails.getRequestDetailsId();
                                                                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(OnAppStartLogic.this, CHANNEL_ID)
                                                                            .setSmallIcon(R.mipmap.ic_launcher_round)
                                                                            .setContentTitle("مرحباً يا صديقي العميل")
                                                                            .setContentText("بنفكرك إن عدى حوالى 8 شهور من آخر غيرت فيها زيت العربية. تقدر تتابع طلباتك من صفحة الطلبات السابقة في الصفحة الشخصية، أو دوس على الاشعار ده.")
                                                                            .setAutoCancel(true)
                                                                            .setStyle(new NotificationCompat.BigTextStyle()
                                                                                    .bigText("بنفكرك إن عدى حوالى 8 شهور من آخر غيرت فيها زيت العربية. تقدر تتابع طلباتك من صفحة الطلبات السابقة في الصفحة الشخصية، أو دوس على الاشعار ده."))
                                                                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                                                            .setContentIntent(pendingIntent);

                                                                    NotificationManager notificationManager = (NotificationManager) OnAppStartLogic.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                        CharSequence name = "ResQme Notification";// The user-visible name of the channel.
                                                                        int importance = NotificationManager.IMPORTANCE_HIGH;
                                                                        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                                                                        notificationManager.createNotificationChannel(mChannel);
                                                                    }

                                                                    notificationManager.notify(num, notificationBuilder.build()); // 0 is the request code, it should be unique id
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });

                                                    }


                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }

                            if(requestDetails.getOil().equals("1")){
                                DatabaseReference winchRequestDB = FirebaseDatabase.getInstance().getReference().child("WinchRequests");
                                winchRequestDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            for (DataSnapshot issue : snapshot.getChildren()) {
                                                WinchRequest winchRequest = issue.getValue(WinchRequest.class);
                                                if(winchRequest.getWinchRequestID().equals(requestDetails.getWinchRequestId())){
                                                    // Get the difference between the current time and the time of the request in months
                                                    // convert the date from string to date
                                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                    Date date = null;
                                                    try {
                                                        date = sdf.parse(winchRequest.getWinchRequestInitiationDate());

                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                    long diffInMillies = Math.abs(System.currentTimeMillis() - date.getTime());
                                                    Double diffInMonths = TimeUnit.MILLISECONDS.toDays(diffInMillies) * 0.0328767;

                                                    if(diffInMonths > 8){


                                                        // Send the notification


                                                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("NotificationHistory");
                                                        rootRef.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (!snapshot.hasChild(requestDetails.getRequestDetailsId())) {
                                                                    //Mark the notification as sent
                                                                    NotificationHistory notificationHistory = new NotificationHistory(requestDetails.getRequestDetailsId());
                                                                    DatabaseReference nh = FirebaseDatabase.getInstance().getReference().child("NotificationHistory");
                                                                    nh.child(requestDetails.getRequestDetailsId()).setValue(notificationHistory);

                                                                    //Send it

                                                                    int num = (int) System.currentTimeMillis();
                                                                    Intent intent = new Intent(OnAppStartLogic.this, CustomerProfile.class); // Send to customer history page
                                                                    @SuppressLint("WrongConstant") PendingIntent pendingIntent = PendingIntent.getActivity(OnAppStartLogic.this, num, intent, Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                                                                    String CHANNEL_ID = "request_details_channel" + requestDetails.getRequestDetailsId();
                                                                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(OnAppStartLogic.this, CHANNEL_ID)
                                                                            .setSmallIcon(R.mipmap.ic_launcher_round)
                                                                            .setContentTitle("مرحباً يا صديقي العميل")
                                                                            .setContentText("بنفكرك إن عدى حوالى 8 شهور من آخر غيرت فيها زيت العربية. تقدر تتابع طلباتك من صفحة الطلبات السابقة في الصفحة الشخصية، أو دوس على الاشعار ده.")
                                                                            .setAutoCancel(true)
                                                                            .setStyle(new NotificationCompat.BigTextStyle()
                                                                                    .bigText("بنفكرك إن عدى حوالى 8 شهور من آخر غيرت فيها زيت العربية. تقدر تتابع طلباتك من صفحة الطلبات السابقة في الصفحة الشخصية، أو دوس على الاشعار ده."))
                                                                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                                                            .setContentIntent(pendingIntent);

                                                                    NotificationManager notificationManager = (NotificationManager) OnAppStartLogic.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                        CharSequence name = "ResQme Notification";// The user-visible name of the channel.
                                                                        int importance = NotificationManager.IMPORTANCE_HIGH;
                                                                        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                                                                        notificationManager.createNotificationChannel(mChannel);
                                                                    }

                                                                    notificationManager.notify(num, notificationBuilder.build()); // 0 is the request code, it should be unique id
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });

                                                    }

                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                            if(requestDetails.getTier().equals("1")){

                                DatabaseReference winchRequestDB = FirebaseDatabase.getInstance().getReference().child("WinchRequests");
                                winchRequestDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            for (DataSnapshot issue : snapshot.getChildren()) {
                                                WinchRequest winchRequest = issue.getValue(WinchRequest.class);
                                                if(winchRequest.getWinchRequestID().equals(requestDetails.getWinchRequestId())){

                                                    // After 4 year

                                                    // Get the difference between the current time and the time of the request in months
                                                    // convert the date from string to date
                                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                    Date date = null;
                                                    try {
                                                        date = sdf.parse(winchRequest.getWinchRequestInitiationDate());

                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                    long diffInMillies = Math.abs(System.currentTimeMillis() - date.getTime());
                                                    Double diffInYears = TimeUnit.MILLISECONDS.toDays(diffInMillies) * 0.002738;

                                                    if(diffInYears > 4){


                                                        // Send the notification


                                                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("NotificationHistory");
                                                        rootRef.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (!snapshot.hasChild(requestDetails.getRequestDetailsId())) {
                                                                    //Mark the notification as sent
                                                                    NotificationHistory notificationHistory = new NotificationHistory(requestDetails.getRequestDetailsId());
                                                                    DatabaseReference nh = FirebaseDatabase.getInstance().getReference().child("NotificationHistory");
                                                                    nh.child(requestDetails.getRequestDetailsId()).setValue(notificationHistory);

                                                                    //Send it

                                                                    int num = (int) System.currentTimeMillis();
                                                                    Intent intent = new Intent(OnAppStartLogic.this, CustomerProfile.class); // Send to customer history page
                                                                    @SuppressLint("WrongConstant") PendingIntent pendingIntent = PendingIntent.getActivity(OnAppStartLogic.this, num, intent, Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                                                                    String CHANNEL_ID = "request_details_channel" + requestDetails.getRequestDetailsId();
                                                                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(OnAppStartLogic.this, CHANNEL_ID)
                                                                            .setSmallIcon(R.mipmap.ic_launcher_round)
                                                                            .setContentTitle("مرحباً يا صديقي العميل")
                                                                            .setContentText("بنفكرك إن عدى حوالى 4 سنين من آخر مرة عملت فيها Check على العجلات. تقدر تتابع طلباتك من صفحة الطلبات السابقة في الصفحة الشخصية، أو دوس على الاشعار ده.")
                                                                            .setAutoCancel(true)
                                                                            .setStyle(new NotificationCompat.BigTextStyle()
                                                                                    .bigText("بنفكرك إن عدى حوالى 4 سنين من آخر مرة عملت فيها Check على العجلات. تقدر تتابع طلباتك من صفحة الطلبات السابقة في الصفحة الشخصية، أو دوس على الاشعار ده."))
                                                                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                                                            .setContentIntent(pendingIntent);

                                                                    NotificationManager notificationManager = (NotificationManager) OnAppStartLogic.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                        CharSequence name = "ResQme Notification";// The user-visible name of the channel.
                                                                        int importance = NotificationManager.IMPORTANCE_HIGH;
                                                                        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                                                                        notificationManager.createNotificationChannel(mChannel);
                                                                    }

                                                                    notificationManager.notify(num, notificationBuilder.build()); // 0 is the request code, it should be unique id
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });

                                                    }



                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }
}
