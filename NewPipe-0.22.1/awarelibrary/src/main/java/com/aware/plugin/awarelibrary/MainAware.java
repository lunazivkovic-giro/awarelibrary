package com.aware.plugin.awarelibrary;

import static com.google.android.gms.tasks.Tasks.await;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.ESM;
import com.aware.Screen;
import com.aware.plugin.awarelibrary.models.State;
import com.aware.plugin.awarelibrary.models.User;
import com.aware.plugin.awarelibrary.scheduler.HelpClassReminder;
import com.aware.plugin.google.activity_recognition.Google_AR_Provider;
import com.aware.providers.Battery_Provider;
import com.aware.providers.ESM_Provider;
import com.aware.providers.Light_Provider;
import com.aware.providers.Locations_Provider;
import com.aware.ui.esms.ESMFactory;
import com.aware.ui.esms.ESM_Likert;
import com.aware.utils.Aware_Plugin;
import com.aware.utils.Aware_TTS;
import com.aware.utils.Scheduler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainAware {

    public static double CURRENT_LATITUDE, CURRENT_LONGITUDE;
    public static int CURRENT_ACTIVITY, CURRENT_PERCENTAGE;
    public static String ANSWER, CURRENT_APP, ANSWERED, LIGHTANSWER, STARS;
    public static Long TIMESTAMP;
    public static Context CONTEXT;
    public static Integer QUALITY;
    public static String BATTERY = "battery";
    public static String LOCATION = "location";
    public static String ACTIVITY = "activity";
    public static String LIGHT = "light";
    public static List<String> SENSORS = new ArrayList<>();
    public static Boolean CONDUCTED;
    public static double[] PREDICTION;
    public static int ACTIVITY_SEND;
    public static Double LONGITUDE_SEND;
    public static Double LATITUDE_SEND;
    public static Integer LIGHT_SEND;
    public static Integer BATTERY_SEND;
    public static Integer VALUE_SEND;
    public static String LOCATION_SEND;
    public static String STARS_SEND;
    public static Timer timer = new Timer();

    /** Turning on sensors */
    @SuppressLint("Range")
    public void startAware(Context context, String packageName, int time, List<String> sensors) {

        SENSORS = sensors;
        Aware.startAWARE(context);
        Aware.startPlugin(context, packageName);
        CONTEXT = context;
        Aware.setSetting(context, Aware_Preferences.STATUS_ESM, false);

        for(String sensor : sensors){
            if(sensor.equals(BATTERY)){

                Aware.setSetting(context, Aware_Preferences.STATUS_BATTERY, true);
                Aware.startBattery(context);
                Cursor valuesBattery = context.getContentResolver().query(Battery_Provider.Battery_Data.CONTENT_URI, null, null, null, Battery_Provider.Battery_Data.TIMESTAMP + " DESC LIMIT 1");
                if(valuesBattery != null && valuesBattery.moveToFirst() ) {
                    CURRENT_PERCENTAGE =  valuesBattery.getInt(valuesBattery.getColumnIndex(Battery_Provider.Battery_Data.LEVEL));
                }
            }

            if(sensor.equals(ACTIVITY)){
                Aware.setSetting(context, com.aware.plugin.google.activity_recognition.Settings.STATUS_PLUGIN_GOOGLE_ACTIVITY_RECOGNITION, true);
                Aware.setSetting(context, com.aware.plugin.google.activity_recognition.Settings.FREQUENCY_PLUGIN_GOOGLE_ACTIVITY_RECOGNITION, time);
                Aware.startPlugin(context, "com.aware.plugin.google.activity_recognition");
                Cursor valuesActivity = context.getContentResolver().query(Google_AR_Provider.Google_Activity_Recognition_Data.CONTENT_URI, null, null, null, Google_AR_Provider.Google_Activity_Recognition_Data.TIMESTAMP + " DESC LIMIT 1");
                if(valuesActivity != null && valuesActivity.moveToFirst() ) {
                      CURRENT_ACTIVITY = valuesActivity.getColumnIndex(Google_AR_Provider.Google_Activity_Recognition_Data.ACTIVITY_TYPE);
                }
            }


            if(sensor.equals(LIGHT)){
                Aware.setSetting(context, Aware_Preferences.STATUS_LIGHT, true);Aware.setSetting(context, com.aware.plugin.google.activity_recognition.Settings.FREQUENCY_PLUGIN_GOOGLE_ACTIVITY_RECOGNITION, time);
                Aware.startLight(context);
                Cursor valuesLight = context.getContentResolver().query(Light_Provider.Light_Data.CONTENT_URI, null, null, null, Light_Provider.Light_Data.TIMESTAMP + " DESC LIMIT 1");
                if(valuesLight != null && valuesLight.moveToFirst() ) {
                    LIGHTANSWER =  valuesLight.getString(valuesLight.getColumnIndex(Light_Provider.Light_Data.LIGHT_LUX));
                }
            }
        }
        Aware.setSetting(context, Aware_Preferences.STATUS_ESM, true);
        Aware.startESM(context);

        /** Checking if the current watching is less than 25th */
        readData(new MyCallbackState() {
            @Override
            public void onCallbackState(List<Map<String, Object>> attractionsList) {
                String android_id = Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                for (Map<String, Object> item : attractionsList) {

                    if (item.get("deviceId").toString().equals(android_id)) {
                        if(Integer.parseInt(item.get("numTotal").toString()) + 1 >= 25){

                            PredictAfter25th predictAfter25th = new PredictAfter25th();
                            predictAfter25th.calculate(context, CURRENT_ACTIVITY, 1);
                            return;
                        }
                    }
                }
            }
        });
    }

    /** Getting data collected through sensors in current session*/
    @SuppressLint("Range")
    public void getData(Context context, Double timeStart) throws InterruptedException {

        ArrayList<Double> listLatitude = new ArrayList<>();
        ArrayList<Double> listLongitude = new ArrayList<>();
        ArrayList<Integer> listActivity = new ArrayList<>();
        ArrayList<Integer> listLight = new ArrayList<>();
        ArrayList<Integer> listBattery = new ArrayList<>();

        for(String sensor : SENSORS){
            if(sensor.equals(LOCATION)){
                Cursor locationData = context.getContentResolver().query(Locations_Provider.Locations_Data.CONTENT_URI, null, "timestamp > " + timeStart, null, "timestamp ASC");

                if(locationData!=null) {
                    locationData.moveToFirst();
                    do {
                        listLatitude.add(locationData.getDouble(locationData.getColumnIndex(Locations_Provider.Locations_Data.LATITUDE)));
                        listLongitude.add(locationData.getDouble(locationData.getColumnIndex(Locations_Provider.Locations_Data.LONGITUDE)));

                    } while (locationData.moveToNext());
                }
            }
            if(sensor.equals(ACTIVITY)){
                Cursor activityData = context.getContentResolver().query(Google_AR_Provider.Google_Activity_Recognition_Data.CONTENT_URI, null, "timestamp > " + timeStart, null, "timestamp ASC");

                    activityData.moveToFirst();

                      do {
                          listActivity.add(activityData.getInt(activityData.getColumnIndex(Google_AR_Provider.Google_Activity_Recognition_Data.ACTIVITY_TYPE)));

                      } while (activityData.moveToNext());

            }
            if(sensor.equals(LIGHT)){

                Cursor valuesLight = context.getContentResolver().query(Light_Provider.Light_Data.CONTENT_URI, null,  "timestamp > " + timeStart, null, "timestamp ASC");

                valuesLight.moveToFirst();
                do {
                    listLight.add(Integer.parseInt(valuesLight.getString(valuesLight.getColumnIndex(Light_Provider.Light_Data.LIGHT_LUX))));

                } while (valuesLight.moveToNext());

            }
            if(sensor.equals(BATTERY)){

                Cursor valuesBattery = context.getContentResolver().query(Battery_Provider.Battery_Data.CONTENT_URI, null, "timestamp > " + timeStart, null, "timestamp ASC");

                if(valuesBattery != null && valuesBattery.moveToFirst() ) {
                    do {

                        listBattery.add(valuesBattery.getInt(valuesBattery.getColumnIndex(Battery_Provider.Battery_Data.LEVEL)));

                    } while (valuesBattery.moveToNext());
                }

            }
        }

        if(listLongitude.size()==0){

        }else {
            int activity = mostCommon(listActivity);
            Double longitude = mostCommon(listLongitude);
            Double latitude = mostCommon(listLatitude);
            Integer light = mostCommon(listLight);
            Integer battery = mostCommon(listBattery);

            /** Updating total and daily number of watching */
            readData(new MyCallbackState() {
                @Override
                public void onCallbackState(List<Map<String, Object>> attractionsList) {
                    String android_id = Settings.Secure.getString(context.getContentResolver(),
                            Settings.Secure.ANDROID_ID);

                    User user = new User();

                    for (Map<String, Object> item : attractionsList) {

                        if(item.get("deviceId").toString().equals(android_id)){
                            user.setAddress(item.get("address").toString());
                            user.setAgreeableness(Integer.parseInt(item.get("agreeableness").toString()));
                            user.setConscientiousness(Integer.parseInt(item.get("conscientiousness").toString()));
                            user.setExtroversion(Integer.parseInt(item.get("extroversion").toString()));
                            user.setNeuroticism(Integer.parseInt(item.get("neuroticism").toString()));
                            user.setNumDay(Integer.parseInt(item.get("numDay").toString()) +1);
                            user.setNumTotal(Integer.parseInt(item.get("numTotal").toString()) +1 );
                            user.setDeviceId((item.get("deviceId").toString()));
                            user.setOpenness(Integer.parseInt(item.get("openness").toString()));
                            user.setWorkAddress((item.get("workAddress").toString()));
                            user.setId((item.get("id").toString()));

                        }


                    }

                    if(user.getNumTotal() == 10){

                        HelpClassReminder helpClassReminder = new HelpClassReminder();
                        helpClassReminder.personalityTestResults(context);

                    }

                    if(user.getNumTotal() == 20){

                        HelpClassReminder helpClassReminder = new HelpClassReminder();
                        helpClassReminder.statistics(context);

                    }

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference washingtonRef = db.collection("user").document(android_id);
                    washingtonRef
                            .update("numDay", user.getNumDay(), "numTotal", user.getNumTotal())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    System.out.println("success");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    System.out.println("fail");
                                }
                            });


                    String addressHome = user.getAddress();
                    String addressWork = user.getWorkAddress();
                    AddressAware addressAware1 = getLocationFromAddress(context, addressHome);
                    AddressAware addressAwareWork = getLocationFromAddress(context, addressWork);
                    int location;
                    String loc;
                    if (addressAware1.getLatitude() - 10.0 < latitude && latitude < addressAware1.getLatitude() + 10.0 && addressAware1.getLongitude() - 10 < longitude && longitude < addressAware1.getLongitude() + 10) {
                        location = 1;
                        loc = "home";

                    } else if (addressAwareWork.getLatitude() - 10.0 < latitude && latitude < addressAwareWork.getLatitude() + 10.0 && addressAwareWork.getLongitude() - 10 < longitude && longitude < addressAwareWork.getLongitude() + 10) {
                        location = 2;
                        loc = "work";
                    } else {
                        location = 0;
                        loc = "else";

                    }
                    ACTIVITY_SEND = activity;
                    LOCATION_SEND = loc;
                    BATTERY_SEND = battery;
                    VALUE_SEND = QUALITY;
                    LONGITUDE_SEND = longitude;
                    LATITUDE_SEND = latitude;
                    LIGHT_SEND = light;

                    if(QUALITY==null){
                        QUALITY = 3;
                    }

                    RandomForestCalculate randomForestCalculate = new RandomForestCalculate();
                    randomForestCalculate.calculate(context, activity, location, battery, QUALITY);

                }
            });
        }

    }

    /** Getting user data */
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

    /** Turning address as string into longitude and latitude */
    public AddressAware getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
            AddressAware addressAware = new AddressAware();
            addressAware.setLatitude(location.getLatitude());
            addressAware.setLongitude(location.getLongitude());

            return addressAware;

        } catch (IOException e) {

            e.printStackTrace();
        }

        return null;
    }


    /** Finfing most common element in array */
    public static <T> T mostCommon(List<T> list) {

        Integer a = -1;

        if(list.size() == 0){
            return (T) a;
        }
        Map<T, Integer> map = new HashMap<>();

        for (T t : list) {
            Integer val = map.get(t);
            map.put(t, val == null ? 1 : val + 1);
        }

        Map.Entry<T, Integer> max = null;

        for (Map.Entry<T, Integer> e : map.entrySet()) {
            if (max == null || e.getValue() > max.getValue())
                max = e;
        }

        return max.getKey();
    }

    /** Sending survey with stars as rating option*/
    @SuppressLint("Range")
    public static void surveyAware(Context context, String packageName) {

        CONDUCTED = false;

        Cursor esmActivity = context.getContentResolver().query(ESM_Provider.ESM_Data.CONTENT_URI, null, null, null, ESM_Provider.ESM_Data.TIMESTAMP + " DESC LIMIT 1");

        if (esmActivity != null && esmActivity.moveToFirst()) {

            ANSWER = esmActivity.getString(esmActivity.getColumnIndex(ESM_Provider.ESM_Data.ANSWER));
            TIMESTAMP = esmActivity.getLong(esmActivity.getColumnIndex(ESM_Provider.ESM_Data.ANSWER_TIMESTAMP));
            ANSWERED = esmActivity.getString(esmActivity.getColumnIndex(ESM_Provider.ESM_Data.STATUS));

        }

        ESMFactory factory = new ESMFactory();
        if (esmActivity != null && esmActivity.moveToFirst()) {
            if ((System.currentTimeMillis() - 60000) > TIMESTAMP) {


                try {

                    ESM_Likert esmLikert = new ESM_Likert();
                    esmLikert.setLikertMax(5)
                            .setLikertMaxLabel("Great")
                            .setLikertMinLabel("Poor")
                            .setLikertStep(1)
                            .setTitle("Likert")
                            .setInstructions("Likert ESM")
                            .setSubmitButton("OK")
                            .setNotificationTimeout(30);

                    //add them to the factory
                    factory.addESM(esmLikert);
                    ESM.queueESM(context, factory.build());

                    Scheduler.Schedule contextual = new Scheduler.Schedule("test_contextual");
                    contextual.addContext(ESM.ACTION_AWARE_ESM_ANSWERED)
                            .setActionType(Scheduler.ACTION_TYPE_ACTIVITY)
                            .setActionClass(context.getPackageName() + "/" + CheckNewAnswers.class.getName()  );
                    Scheduler.saveSchedule(context, contextual);
                    //Queue them


                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }

        }
    else{
            try {

                ESM_Likert esmLikert = new ESM_Likert();
                esmLikert.setLikertMax(5)
                        .setLikertMaxLabel("Great")
                        .setLikertMinLabel("Poor")
                        .setLikertStep(1)
                        .setTitle("Likert")
                        .setInstructions("Likert ESM")
                        .setSubmitButton("OK")
                        .setNotificationTimeout(30);

                //add them to the factory
                factory.addESM(esmLikert);

                //Queue them
                ESM.queueESM(context, factory.build());
                Scheduler.Schedule contextual = new Scheduler.Schedule("test_contextual");
                contextual.addContext(ESM.ACTION_AWARE_ESM_ANSWERED)
                        .setActionType(Scheduler.ACTION_TYPE_SERVICE)
                        .setActionClass(context.getPackageName() + "/" + CheckNewAnswers.class.getName() );

                Scheduler.saveSchedule(context, contextual);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

}


