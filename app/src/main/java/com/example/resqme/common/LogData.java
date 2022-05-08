package com.example.resqme.common;

import android.content.Context;

import com.example.resqme.model.LogDataModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LogData {

    public static void saveLog(String clickedServiceID, String isService, String eventName, String appClick){
        DatabaseReference logTable = FirebaseDatabase.getInstance().getReference().child("LOG");
        //LOG ID
        String logRecordID = logTable.push().getKey();
        // TIMESTAMP
        Date currentTime = Calendar.getInstance().getTime();
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String logTimestamp = simpleDateFormat.format(currentTime);
        // Saving the log
        logTable.child(logRecordID).setValue(new LogDataModel(logRecordID, logTimestamp,
                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                isService, clickedServiceID, appClick, eventName));
    }
}
