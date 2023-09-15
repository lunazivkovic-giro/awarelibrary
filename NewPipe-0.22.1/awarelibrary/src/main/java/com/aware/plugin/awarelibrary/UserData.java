package com.aware.plugin.awarelibrary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.aware.plugin.awarelibrary.models.State;
import com.aware.plugin.awarelibrary.scheduler.Alarm;
import com.aware.plugin.awarelibrary.scheduler.AlarmReminder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserData extends Activity {

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        calculate();
    }

    public void readData(MyCallbackState myCallback) {

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference attractionsRef = rootRef.collection("user");
        attractionsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Map<String, Object>> attractionsList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> attraction = document.getData();
                        attractionsList.add(attraction);
                    }
                    myCallback.onCallbackState(attractionsList);
                }
            }
        });
    }

    Alarm alarm = new Alarm();
    AlarmReminder alarmReminder = new AlarmReminder();


    /** Setting schedulers, checking if the app is fun the first time*/
    public void calculate() {

        alarm.setAlarm(getApplicationContext());
        alarmReminder.setAlarm(getApplicationContext());
        readData(new MyCallbackState() {
            @Override
            public void onCallbackState(List<Map<String, Object>> attractionsList) {
                String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);

                Boolean flag = false;
                for (Map<String, Object> item : attractionsList) {

                    if(item.get("deviceId").toString().equals(android_id)){
                        flag = true;
                        finish();
                    }
                }

                if (flag == false) {
                    startActivity(new Intent(UserData.this, SuggestActivity.class));
                    finish();

                }
            }
        });
    }





    }
