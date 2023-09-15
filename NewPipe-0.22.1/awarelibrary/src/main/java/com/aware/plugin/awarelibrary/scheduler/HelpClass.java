package com.aware.plugin.awarelibrary.scheduler;

import android.app.IntentService;
import android.content.Intent;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.aware.plugin.awarelibrary.MyCallbackState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HelpClass extends IntentService {

    public HelpClass() {
        super("AuthenticationCheckService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //Once this code runs and its done the service stops so need to worry about long running service
        checkAuthenticationStatus();
    }

    /** Getting user data*/
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

    /** Updating number of daily watched videos to 0*/
    private void checkAuthenticationStatus() {
        readData(new MyCallbackState() {
            @Override
            public void onCallbackState(List<Map<String, Object>> attractionsList) {

                String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference washingtonRef = db.collection("user").document(android_id);

                washingtonRef
                        .update("numDay", 0)
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
            }
        });
    }
}