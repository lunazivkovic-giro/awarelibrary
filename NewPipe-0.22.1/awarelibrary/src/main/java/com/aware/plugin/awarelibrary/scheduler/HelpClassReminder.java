package com.aware.plugin.awarelibrary.scheduler;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.aware.ESM;
import com.aware.plugin.awarelibrary.CheckNewAnswers;
import com.aware.plugin.awarelibrary.MyCallbackState;
import com.aware.plugin.awarelibrary.R;
import com.aware.ui.esms.ESMFactory;
import com.aware.ui.esms.ESM_Likert;
import com.aware.utils.Scheduler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HelpClassReminder extends IntentService {

    public HelpClassReminder() {
        super("AuthenticationCheckService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //Once this code runs and its done the service stops so need to worry about long running service
        checkAuthenticationStatus();
    }

    /**Getting user data*/
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

    /**Getting state data*/
    public void readStateData(MyCallbackState myCallback) {

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference attractionsRef = rootRef.collection("state");
        attractionsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Map<String, Object>> attractionsList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Map<String, Object> attraction = document.getData();

                        attractionsList.add(attraction);
                    }
                    myCallback.onCallbackState(attractionsList);
                }
            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ChannelName";
            String description = "Channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("111", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /** Sending notification with data regarding user's personality test results */
    public void personalityTestResults(Context context) {

        readData(new MyCallbackState() {
            @Override
            public void onCallbackState(List<Map<String, Object>> attractionsList) {

                String android_id = Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);

                for (Map<String, Object> item : attractionsList) {
                    if (item.get("deviceId").toString().equals(android_id)) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            CharSequence name = "ChannelName";
                            String description = "Channel description";
                            int importance = NotificationManager.IMPORTANCE_DEFAULT;
                            NotificationChannel channel = new NotificationChannel("111", name, importance);
                            channel.setDescription(description);
                            // Register the channel with the system; you can't change the importance
                            // or other notification behaviors after this
                            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                            notificationManager.createNotificationChannel(channel);
                        }

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "111")
                                .setSmallIcon(R.drawable.ic_baseline_auto_awesome_24)
                                .setContentTitle("Personality test results")
                                .setContentText("Congradulations! Since you watched 10 videos, we're going to revile your personality traits. Agreeableness: " + item.get("agreeableness").toString() +
                                        "Conscientiousness: " + item.get("conscientiousness").toString() +
                                        "Extroversion: " + item.get("extroversion").toString() +
                                        "Neuroticism: " + item.get("neuroticism").toString() +
                                        "Openness: " + item.get("openness").toString())
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);



                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                        // notificationId is a unique int for each notification that you must define
                        Random notification_id = new Random();
                        notificationManager.notify(notification_id.nextInt(100), builder.build());

                    }
                }
            }
        });
    }


    /**Sending notification regarding most common user's activity*/
    public void statistics(Context context) {

        readStateData(new MyCallbackState() {
            @Override
            public void onCallbackState(List<Map<String, Object>> attractionsList) {

                String res = "";
                String android_id = Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                int activity =0;
                int num = 0;

                for (Map<String, Object> item : attractionsList) {
                    if (item.get("deviceId").toString().equals(android_id)) {
                        num++;
                        activity += (Integer.parseInt(item.get("activity").toString()));

                    }

                }

                if(num!=0) {
                    double average = activity / num;

                    if(average ==0){

                        res = "in vehicle";
                    }else if(average == 1){

                        res = "on bicycle";
                    }else if(average == 2){

                        res = "walking";
                    }else if(average == 3){

                        res = "still";
                    }else if(average == 5){

                        res = "still";
                    }else if(average == 7){

                        res = "walking";
                    }else if(average == 8){

                        res = "running";
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence name = "ChannelName";
                    String description = "Channel description";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel("111", name, importance);
                    channel.setDescription(description);
                    // Register the channel with the system; you can't change the importance
                    // or other notification behaviors after this
                    NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "111")
                        .setSmallIcon(R.drawable.ic_baseline_auto_awesome_24)
                        .setContentTitle("Personality test results")
                        .setContentText("Congradulations! Since you watched 20 videos, we're going to revile your most common activity during watching videos:" + res )
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                // notificationId is a unique int for each notification that you must define
                Random notification_id = new Random();
                notificationManager.notify(notification_id.nextInt(100), builder.build());

            }

        });

    }


    /** Sending notification that reminds the user to watch 5 videos a day*/
    private void checkAuthenticationStatus() {
        readData(new MyCallbackState() {
            @Override
            public void onCallbackState(List<Map<String, Object>> attractionsList) {

                String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);

                       for (Map<String, Object> item : attractionsList) {
                            if (item.get("deviceId").toString().equals(android_id) && Integer.parseInt(item.get("numDay").toString()) < 6) {

                                createNotificationChannel();

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "111")
                                        .setSmallIcon(R.drawable.warning)
                                        .setContentTitle("Reminder")
                                        .setContentText("Please watch 5 videos today.")
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);


                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                                // notificationId is a unique int for each notification that you must define
                                Random notification_id = new Random();
                                notificationManager.notify(notification_id.nextInt(100), builder.build());

                            }
                       }
            }
        });
    }
}