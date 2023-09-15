package com.aware.plugin.awarelibrary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aware.plugin.awarelibrary.models.Algorithm;
import com.aware.plugin.awarelibrary.models.State;
import com.example.quickvideoplayer.Activity_Player;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.Randomize;

/** Predicting satisfactory parameters after 25th usage */
public class PredictAfter25th extends AppCompatActivity {

    public int t;
    public double n1;
    public double n2;
    public double n3;
    public double n4;
    public int num1;
    public int num2;
    public int num3;
    public int num4;
    public int unensweredNum1;
    public int unensweredNum2;
    public int unensweredNum3;
    public int unensweredNum4;

    public static int winnerGlobal;
    public static double winnerScoreGlobal;
    public static int algorithmGlobal;
    public static double rewardGlobal;
    public static boolean asked = true;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /** Predicting the satisfactory parameters and applying them*/
    public void calculate(Context context, int activityNew, int locationNew){
        try {

            readData(new MyCallbackState() {
                @Override
                public void onCallbackState( List<Map<String, Object>> attractionsList) {
                    String android_id = Settings.Secure.getString(context.getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    List<State> listState = new ArrayList<>();

                        for (Map<String, Object> item : attractionsList) {
                            if ((item.get("deviceId").toString()).equals(android_id)) {

                                State state = new State();

                                state.setValue(Integer.parseInt(item.get("value").toString()));
                                state.setDeviceId((item.get("deviceId").toString()));
                                state.setActivity(Integer.parseInt(item.get("activity").toString()));
                                state.setHomeOrWork((item.get("homeOrWork").toString()));
                                state.setBattery(Integer.parseInt(item.get("battery").toString()));
                                state.setStars(item.get("stars").toString());
                                listState.add(state);
                            }
                        }

                            List<String> attZone = new ArrayList<>();
                            Attribute value = new Attribute("value");
                            Attribute activity = new Attribute("activity");
                            Attribute homeOrWork = new Attribute("homeOrWork");
                            Attribute battery = new Attribute("battery");

                            ArrayList<Attribute> atts = new ArrayList<>();
                            atts.add(value);
                            atts.add(activity);
                            atts.add(homeOrWork);
                            atts.add(battery);

                            attZone.add("1");
                            attZone.add("2");
                            attZone.add("3");
                            attZone.add("4");
                            attZone.add("5");
                            atts.add(new Attribute("stars", attZone));

                            Instances dataUnlabeled = new Instances("TestInstances", atts, 0);
                            for (State state : listState) {

                                Instance iExample = new DenseInstance(5);
                                iExample.setValue(atts.get(0), state.getValue());
                                iExample.setValue(atts.get(1), state.getActivity());
                                if (state.getHomeOrWork().equals("else")) {

                                    iExample.setValue(atts.get(2), 0);
                                } else if (state.getHomeOrWork().equals("home")) {

                                    iExample.setValue(atts.get(2), 1);
                                } else {

                                    iExample.setValue(atts.get(2), 2);
                                }
                                iExample.setValue(atts.get(3), state.getBattery());
                                iExample.setValue(atts.get(4), state.getStars());
                                dataUnlabeled.add(iExample);

                            }

                            try {
                                Instances m_Training = new Instances(dataUnlabeled);
                                m_Training.setClassIndex(m_Training.numAttributes() - 1);

                                Randomize m_Filter = new Randomize();
                                m_Filter.setInputFormat(m_Training);

                                Instances localInstances = Filter.useFilter(m_Training, m_Filter);
                                RandomForest m_Classifier = new RandomForest();

                                m_Classifier.buildClassifier(localInstances);

                                Evaluation m_Evaluation = new Evaluation(localInstances);
                                m_Evaluation.crossValidateModel(m_Classifier, localInstances, 2,
                                        m_Training.getRandomNumberGenerator(1));


                                Instances dataUnlabeledPredict1 = new Instances("Predict", atts, 0);
                                Instance iExample = new DenseInstance(3);
                                iExample.setValue(atts.get(0), 1);
                                iExample.setValue(atts.get(1), activityNew);
                                iExample.setValue(atts.get(2), locationNew);
                                dataUnlabeledPredict1.add(iExample);

                                dataUnlabeledPredict1.setClassIndex(dataUnlabeledPredict1.numAttributes() - 1);

                                double[] predict = m_Classifier.distributionForInstance(dataUnlabeledPredict1.firstInstance());

                                List<Double> list = new ArrayList<>();
                                list.add(predict[0]);
                                list.add(predict[1]);
                                list.add(predict[2]);
                                list.add(predict[3]);
                                list.add(predict[4]);


                                Instances dataUnlabeledPredict2 = new Instances("Predict", atts, 0);
                                Instance iExample2 = new DenseInstance(3);
                                iExample2.setValue(atts.get(0), 2);
                                iExample2.setValue(atts.get(1), activityNew);
                                iExample2.setValue(atts.get(2), locationNew);
                                dataUnlabeledPredict2.add(iExample2);

                                dataUnlabeledPredict2.setClassIndex(dataUnlabeledPredict2.numAttributes() - 1);

                                double[] predict2 = m_Classifier.distributionForInstance(dataUnlabeledPredict2.firstInstance());

                                List<Double> list2 = new ArrayList<>();
                                list2.add(predict2[0]);
                                list2.add(predict2[1]);
                                list2.add(predict2[2]);
                                list2.add(predict2[3]);
                                list2.add(predict2[4]);

                                Instances dataUnlabeledPredict3 = new Instances("Predict", atts, 0);
                                Instance iExample3 = new DenseInstance(3);
                                iExample3.setValue(atts.get(0), 3);
                                iExample3.setValue(atts.get(1), activityNew);
                                iExample3.setValue(atts.get(2), locationNew);
                                dataUnlabeledPredict3.add(iExample3);

                                dataUnlabeledPredict3.setClassIndex(dataUnlabeledPredict3.numAttributes() - 1);

                                double[] predict3 = m_Classifier.distributionForInstance(dataUnlabeledPredict3.firstInstance());

                                List<Double> list3 = new ArrayList<>();
                                list3.add(predict3[0]);
                                list3.add(predict3[1]);
                                list3.add(predict3[2]);
                                list3.add(predict3[3]);
                                list3.add(predict3[4]);

                                Instances dataUnlabeledPredict4 = new Instances("Predict", atts, 0);
                                Instance iExample4 = new DenseInstance(3);
                                iExample4.setValue(atts.get(0), 4);
                                iExample4.setValue(atts.get(1), activityNew);
                                iExample4.setValue(atts.get(2), locationNew);
                                dataUnlabeledPredict4.add(iExample4);

                                dataUnlabeledPredict4.setClassIndex(dataUnlabeledPredict4.numAttributes() - 1);

                                double[] predict4 = m_Classifier.distributionForInstance(dataUnlabeledPredict4.firstInstance());

                                List<Double> list4 = new ArrayList<>();
                                list4.add(predict4[0]);
                                list4.add(predict4[1]);
                                list4.add(predict4[2]);
                                list4.add(predict4[3]);
                                list4.add(predict4[4]);

                                Instances dataUnlabeledPredict5 = new Instances("Predict", atts, 0);
                                Instance iExample5 = new DenseInstance(3);
                                iExample5.setValue(atts.get(0), 5);
                                iExample5.setValue(atts.get(1), activityNew);
                                iExample5.setValue(atts.get(2), locationNew);
                                dataUnlabeledPredict5.add(iExample5);

                                dataUnlabeledPredict5.setClassIndex(dataUnlabeledPredict5.numAttributes() - 1);

                                double[] predict5 = m_Classifier.distributionForInstance(dataUnlabeledPredict5.firstInstance());

                                List<Double> list5 = new ArrayList<>();
                                list5.add(predict5[0]);
                                list5.add(predict5[1]);
                                list5.add(predict5[2]);
                                list5.add(predict5[3]);
                                list5.add(predict5[4]);

                                Instances dataUnlabeledPredict6 = new Instances("Predict", atts, 0);
                                Instance iExample6 = new DenseInstance(3);
                                iExample6.setValue(atts.get(0), 6);
                                iExample6.setValue(atts.get(1), activityNew);
                                iExample6.setValue(atts.get(2), locationNew);
                                dataUnlabeledPredict6.add(iExample6);

                                dataUnlabeledPredict6.setClassIndex(dataUnlabeledPredict6.numAttributes() - 1);

                                double[] predict6 = m_Classifier.distributionForInstance(dataUnlabeledPredict6.firstInstance());

                                List<Double> list6 = new ArrayList<>();
                                list6.add(predict6[0]);
                                list6.add(predict6[1]);
                                list6.add(predict6[2]);
                                list6.add(predict6[3]);
                                list6.add(predict6[4]);

                                Arrays.sort(predict);
                                Arrays.sort(predict2);
                                Arrays.sort(predict3);
                                Arrays.sort(predict4);
                                Arrays.sort(predict5);
                                Arrays.sort(predict6);

                                double pred1 = predict[4];
                                double pred2 = predict2[4];
                                double pred3 = predict3[4];
                                double pred4 = predict4[4];
                                double pred5 = predict5[4];
                                double pred6 = predict6[4];

                                double lista[] = new double[0];
                                lista[0] = pred1;
                                lista[1] = pred2;
                                lista[2] = pred3;
                                lista[3] = pred4;
                                lista[4] = pred5;
                                lista[5] = pred6;


                                Arrays.sort(lista);
                                int val = 0;
                                if(lista[5]== pred1){
                                    val = 1;
                                }else if(lista[5]== pred2){
                                    val = 2;
                                }else if(lista[5]== pred3){
                                    val = 3;
                                }else if(lista[5]== pred4){
                                    val = 4;
                                }else if(lista[5]== pred5){
                                    val = 5;
                                }else if(lista[5]== pred6){
                                    val = 6;
                                }


                                String video = "3"+val;
                                long longVideo = Long.parseLong(video);

                                final Intent intent = new Intent(getApplicationContext(), Activity_Player.class);
                                intent.putExtra("videoId", longVideo);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getApplicationContext().startActivity(intent);

                                MainAware.surveyAware(MainAware.CONTEXT, MainAware.CONTEXT.getPackageName());
                            } catch (Exception e) {

                                System.out.println(e);
                            }
                        }
            });

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    public void readData(MyCallbackState myCallback) {

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference attractionsRef = rootRef.collection("state");
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
}
