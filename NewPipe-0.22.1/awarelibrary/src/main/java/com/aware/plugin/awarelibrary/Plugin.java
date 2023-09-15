package com.aware.plugin.awarelibrary;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

import com.aware.Applications;
import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.ESM;
import com.aware.Proximity;
import com.aware.plugin.google.activity_recognition.Google_AR_Provider;
import com.aware.plugin.google.activity_recognition.Settings;
import com.aware.providers.Applications_Provider;
import com.aware.providers.Battery_Provider;
import com.aware.providers.Locations_Provider;
import com.aware.ui.esms.ESMFactory;
import com.aware.ui.esms.ESM_Checkbox;
import com.aware.ui.esms.ESM_QuickAnswer;
import com.aware.ui.esms.ESM_Radio;
import com.aware.utils.Aware_Plugin;
import com.aware.utils.Scheduler;

import org.json.JSONException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Plugin extends Aware_Plugin {
    public static double CURRENT_LATITUDE, CURRENT_LONGITUDE;
    public static int CURRENT_ACTIVITY, CURRENT_PERCENTAGE;
    public static String ANSWER, CURRENT_APP;

    public ContextReceiver dataReceiver = new ContextReceiver();

    private static Intent aware;
    private static ContextProducer sContext;
    private static ContextProducer pluginContext;
    private static String answer = "";
    private static String question = "";

    @SuppressLint("Range")
    @Override
    public void onCreate() {
        super.onCreate();

        aware = new Intent(this, Aware.class);
        startService(aware);

        TAG = "AWARE::" + getResources().getString(R.string.app_name);
        DEBUG = Aware.getSetting(this, Aware_Preferences.DEBUG_FLAG).equals("true");
        if (DEBUG) Log.d(TAG, "Aware-library-plugin running");

        pluginContext = new ContextProducer() {
            @Override
            public void onContext() {
                if(question!=null && answer!=null && !(question.equals("") && answer.equals(""))) {

                    // Insert values into database
                    ContentValues rowData = new ContentValues();
                    rowData.put(Provider.Example_Data.TIMESTAMP, System.currentTimeMillis());
                    rowData.put(Provider.Example_Data.DEVICE_ID, Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));
                    rowData.put(Provider.Example_Data.ANSWER, answer);


                    Log.d(TAG, "Sending data " + rowData.toString());
                    getContentResolver().insert(Provider.Example_Data.CONTENT_URI, rowData);
                    //broadcast?
                }
            }
        };
        CONTEXT_PRODUCER = pluginContext;


        /** Initializing sensor data collection */
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_ESM, false);
           Aware.setSetting(getApplicationContext(), Aware_Preferences.FREQUENCY_LOCATION_GPS, 3600);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.FREQUENCY_LOCATION_NETWORK, 3600);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_LOCATION_NETWORK, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_LOCATION_GPS, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_ESM, true);
            Aware.setSetting(getApplicationContext(), Settings.STATUS_PLUGIN_GOOGLE_ACTIVITY_RECOGNITION, true);
            Aware.setSetting(getApplicationContext(), Settings.FREQUENCY_PLUGIN_GOOGLE_ACTIVITY_RECOGNITION, 60);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_BATTERY, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_APPLICATIONS, true);
            Aware.startPlugin(getApplicationContext(), "com.aware.plugin.google.activity_recognition");

            Cursor valuesLocation = getContentResolver().query(Locations_Provider.Locations_Data.CONTENT_URI, null, null, null, Locations_Provider.Locations_Data.TIMESTAMP + " DESC LIMIT 1");
            Cursor valuesActivity = getContentResolver().query(Google_AR_Provider.Google_Activity_Recognition_Data.CONTENT_URI, null, null, null, Google_AR_Provider.Google_Activity_Recognition_Data.TIMESTAMP + " DESC LIMIT 1");
            Cursor valuesBattery = getContentResolver().query(Battery_Provider.Battery_Data.CONTENT_URI, null, null, null, Battery_Provider.Battery_Data.TIMESTAMP + " DESC LIMIT 1");
            Cursor valuesApp = getContentResolver().query(Applications_Provider.Applications_Foreground.CONTENT_URI, null, null, null, Applications_Provider.Applications_Foreground.TIMESTAMP + " DESC LIMIT 1");

            if(valuesLocation != null && valuesLocation.moveToFirst() ) {
                CURRENT_LATITUDE = valuesLocation.getDouble(valuesLocation.getColumnIndex(Locations_Provider.Locations_Data.LATITUDE));
                CURRENT_LONGITUDE = valuesLocation.getDouble(valuesLocation.getColumnIndex(Locations_Provider.Locations_Data.LONGITUDE));
                CURRENT_ACTIVITY = valuesActivity.getColumnIndex(Google_AR_Provider.Google_Activity_Recognition_Data.ACTIVITY_TYPE);
                 }

            if(valuesBattery != null && valuesBattery.moveToFirst() ) {
              CURRENT_PERCENTAGE =  valuesBattery.getInt(valuesBattery.getColumnIndex(Battery_Provider.Battery_Data.LEVEL));
            }

            if(valuesLocation != null && ! valuesLocation.isClosed()) valuesLocation.close();

            ScheduledExecutorService scheduler =
                    Executors.newSingleThreadScheduledExecutor();

            scheduler.scheduleAtFixedRate
                    (new Runnable() {
                        public void run() {
                            if(CURRENT_PERCENTAGE == 100) {
                                if(valuesApp != null && valuesApp.moveToFirst() ) {

                                    CURRENT_APP =  valuesApp.getString(valuesApp.getColumnIndex(Applications_Provider.Applications_Foreground.APPLICATION_NAME));
                                }
                            }
                        }
                    }, 0, 10, TimeUnit.SECONDS);

        Intent refresh = new Intent(Aware.ACTION_AWARE_SYNC_DATA);
        sendBroadcast(refresh);

        IntentFilter filter = new IntentFilter();

        filter.addAction(Proximity.ACTION_AWARE_PROXIMITY);

        registerReceiver(dataReceiver, filter);


                sContext = new ContextProducer() {

            @Override
            public void onContext() {

                ContentValues context_data = new ContentValues();
                context_data.put(Provider.Example_Data.TIMESTAMP, System.currentTimeMillis());
                context_data.put(Provider.Example_Data.DEVICE_ID, Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));

                if( DEBUG ) Log.d(TAG, context_data.toString());

                getContentResolver().insert(Provider.Example_Data.CONTENT_URI, context_data);

            }
        };
        CONTEXT_PRODUCER = sContext;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //Set Sensors Off

        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_LOCATION_NETWORK, false);

        Intent refresh = new Intent(Aware.ACTION_AWARE_CURRENT_CONTEXT);
        sendBroadcast(refresh);

        unregisterReceiver(dataReceiver);

        stopService(aware);

    }

    public class ContextReceiver extends BroadcastReceiver {

        @SuppressLint("Range")
        @Override
        public void onReceive(Context context, Intent intent) {

            Cursor valuesLocation = getContentResolver().query(Locations_Provider.Locations_Data.CONTENT_URI, null, null, null, Locations_Provider.Locations_Data.TIMESTAMP + " DESC LIMIT 1");
            if(valuesLocation != null && valuesLocation.moveToFirst() ) {
                CURRENT_LATITUDE = valuesLocation.getDouble(valuesLocation.getColumnIndex(Locations_Provider.Locations_Data.LATITUDE));
                CURRENT_LONGITUDE = valuesLocation.getDouble(valuesLocation.getColumnIndex(Locations_Provider.Locations_Data.LONGITUDE));
            }
            if(valuesLocation != null && ! valuesLocation.isClosed()) valuesLocation.close();

            sContext.onContext();
        }

    }

}
