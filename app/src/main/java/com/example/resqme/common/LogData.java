package com.example.resqme.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.example.resqme.R;
import com.example.resqme.customer.CustomerHome;
import com.example.resqme.model.LogDataModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LogData {

    // LOGID - TIMESTAMP - USERID - EVENTTYPE - SERVICEID - SERVICENAME - APPCLICKNAME - PAGENAME - DEVICESDK - DEVICENAME - DEVICEMODEL
    public static void saveLog(
            String eventType, String serviceID, String serviceName, String appClickName, String pageName){
        DatabaseReference logTable = FirebaseDatabase.getInstance().getReference().child("LOG");
        // LOG ID
        String logRecordID = logTable.push().getKey();
        // TIMESTAMP
        Date currentTime = Calendar.getInstance().getTime();
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String logTimestamp = simpleDateFormat.format(currentTime);
        // Device Data
        String sdk_version = String.valueOf(Build.VERSION.SDK_INT);
        String device_name = android.os.Build.DEVICE;
        String device_model = android.os.Build.MODEL;
        // User Data
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            // Saving the log
            logTable.child(logRecordID).setValue(new LogDataModel(logRecordID, logTimestamp,
                    userID, eventType, serviceID, serviceName, appClickName, pageName, sdk_version, device_name, device_model));
        }

    }
}
