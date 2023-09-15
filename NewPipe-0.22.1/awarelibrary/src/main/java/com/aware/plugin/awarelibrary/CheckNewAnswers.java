package com.aware.plugin.awarelibrary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aware.plugin.awarelibrary.models.State;
import com.aware.providers.ESM_Provider;
import com.aware.utils.Aware_Plugin;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.TimerTask;

public class CheckNewAnswers extends Activity {

    public static String ANSWER;
    public static String CURRENT_APP;
    public static String ANSWERED;
    public static String LIGHTANSWER;
    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /** Getting the survey answer */
        Cursor esmActivity = MainAware.CONTEXT.getContentResolver().query(ESM_Provider.ESM_Data.CONTENT_URI, null, null, null, ESM_Provider.ESM_Data.TIMESTAMP + " DESC LIMIT 1");

        if (esmActivity != null && esmActivity.moveToFirst()) {

            ANSWER = esmActivity.getString(esmActivity.getColumnIndex(ESM_Provider.ESM_Data.ANSWER));
            ANSWERED = esmActivity.getString(esmActivity.getColumnIndex(ESM_Provider.ESM_Data.STATUS));

        }

        /** Saving new answer */
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
            State state1 = new State(MainAware.ACTIVITY_SEND, MainAware.LONGITUDE_SEND, MainAware.LATITUDE_SEND, MainAware.LIGHT_SEND, MainAware.BATTERY_SEND, 2, MainAware.LOCATION_SEND, ANSWERED, android_id);

            db.collection("state")
                    .add(state1)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            System.out.println("Successss");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Error adding document", e);
                        }
                    });


            RandomForestCalculate randomForestCalculate = new RandomForestCalculate();
            randomForestCalculate.calculateAgain(randomForestCalculate.winnerGlobal, randomForestCalculate.winnerScoreGlobal, randomForestCalculate.algorithmGlobal, randomForestCalculate.rewardGlobal, getApplicationContext(), ANSWERED);
            finish();
    }
}
