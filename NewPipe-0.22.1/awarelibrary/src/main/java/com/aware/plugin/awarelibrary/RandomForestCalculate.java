package com.aware.plugin.awarelibrary;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aware.plugin.awarelibrary.models.Algorithm;
import com.aware.plugin.awarelibrary.models.State;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStream;
import java.io.InputStreamReader;
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

public class RandomForestCalculate extends AppCompatActivity {

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

    public void calculate(Context context,  int activityNew, int locationNew, int batteryNew, int qualityNew){
        try {

            readData(new MyCallbackState() {
                @Override
                public void onCallbackState( List<Map<String, Object>> attractionsList) {
                    String android_id = Settings.Secure.getString(context.getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    List<State> listState = new ArrayList<>();
                    if(attractionsList.size()!=0) {

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

                        if (listState.size() >= 2) {
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

                                weka.filters.unsupervised.instance.Randomize m_Filter = new Randomize();
                                m_Filter.setInputFormat(m_Training);

                                Instances localInstances = Filter.useFilter(m_Training, m_Filter);
                                RandomForest m_Classifier = new weka.classifiers.trees.RandomForest();

                                m_Classifier.buildClassifier(localInstances);

                                Evaluation m_Evaluation = new Evaluation(localInstances);
                                m_Evaluation.crossValidateModel(m_Classifier, localInstances, 2,
                                        m_Training.getRandomNumberGenerator(1));
                                String result = "Correct:"
                                        + m_Evaluation.correct()
                                        + "/Wrong:"
                                        + m_Evaluation.incorrect()
                                        + "/Correct(%):"
                                        + m_Evaluation.pctCorrect();
                                Log.e("Result", result);
                                Log.e("Result", "================================================");


                                Instances dataUnlabeledPredict = new Instances("Predict", atts, 0);
                                Instance iExample = new DenseInstance(3);
                                iExample.setValue(atts.get(0), qualityNew);
                                iExample.setValue(atts.get(1), activityNew);
                                iExample.setValue(atts.get(2), locationNew);
                                dataUnlabeledPredict.add(iExample);

                                dataUnlabeledPredict.setClassIndex(dataUnlabeledPredict.numAttributes() - 1);

                                double[] predict = m_Classifier.distributionForInstance(dataUnlabeledPredict.firstInstance());
                                MainAware.PREDICTION = predict;

                                List<Double> list = new ArrayList<>();
                                list.add(predict[0]);
                                list.add(predict[1]);
                                list.add(predict[2]);
                                list.add(predict[3]);
                                list.add(predict[4]);

                                randomSampling(list, predict, context);
                            } catch (Exception e) {

                                System.out.println(e);
                            }

                        } else {
                            List<Double> list = new ArrayList<>();
                            list.add(0.2);
                            list.add(0.2);
                            list.add(0.2);
                            list.add(0.2);
                            list.add(0.2);

                            double[] predict = {0.2, 0.2, 0.2, 0.2, 0.2};
                            randomSampling(list, predict,context);

                        }
                    }else{
                        List<Double> list = new ArrayList<>();
                        list.add(0.0);
                        list.add(0.0);
                        list.add(0.0);
                        list.add(0.0);
                        list.add(0.0);

                        double[] predict = {0.0, 0.0, 0.0, 0.0, 0.0};
                        randomSampling(list, predict,context);
                    }
                }
            });

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    public int choose() {
        return (int) ((Math.random() * (2 - 1)) + 1);
    }


    public void randomSampling(List<Double> predictions, double[] pred, Context context) {

        double h1 = pred[0];
        double h2 = pred[1];
        double h3 = pred[2];
        double h4 = pred[3];
        double h5 = pred[4];
        int winner = 0;
        double winnerScore = 0.0;
        Arrays.sort(pred);

        if(pred[4]==h1){
            winner = 1;
            winnerScore = h1;
        }else if(pred[4]==h2){

            winner = 2;
            winnerScore = h2;
        }else if(pred[4]==h3){

            winner = 3;
            winnerScore = h3;
        }else if(pred[4]==h4){

            winner = 4;
            winnerScore = h4;
        }else{

            winner = 5;
            winnerScore = h5;
        }


        double finalWinnerScore = winnerScore;
        int finalWinner = winner;

        readDataAlgorithm(new MyCallbackState() {
            @Override
            public void onCallbackState(List<Map<String, Object>> attractionsList) {
                String android_id = Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);


                List<Algorithm> listAlgorithm = new ArrayList<>();
                for (Map<String, Object> item : attractionsList) {
                    if ((item.get("deviceId").toString()).equals(android_id)) {
                        Algorithm state = new Algorithm();
                        t++;

                        if (item.get("reward1") == null) {
                            state.setReward1(0.0);
                        } else {
                            state.setReward1(Double.parseDouble(item.get("reward1").toString()));
                            num1++;
                        }

                        if (item.get("reward2") == null) {
                            state.setReward2(0.0);
                        } else {
                            state.setReward2(Double.parseDouble(item.get("reward2").toString()));
                            num2++;
                        }

                        if (item.get("reward3") == null) {
                            state.setReward3(0.0);
                        } else {
                            state.setReward3(Double.parseDouble(item.get("reward3").toString()));
                            num3++;
                        }

                        if (item.get("reward4") == null) {
                            state.setReward4(0.0);
                        } else {
                            state.setReward4(Double.parseDouble(item.get("reward4").toString()));
                            num4++;
                        }

                        if (item.get("unanswered1") == null) {
                            state.setUnanswered1(false);
                        } else {
                            state.setUnanswered1(true);
                            unensweredNum1++;
                        }

                        if (item.get("unanswered2") == null) {
                            state.setUnanswered2(false);
                        } else {
                            state.setUnanswered2(true);
                            unensweredNum2++;
                        }

                        if (item.get("unanswered3") == null) {
                            state.setUnanswered3(false);
                        } else {
                            state.setUnanswered3(true);
                            unensweredNum3++;
                        }

                        if (item.get("unanswered4") == null) {
                            state.setUnanswered4(false);
                        } else {
                            state.setUnanswered4(true);

                            unensweredNum4++;
                        }


                        listAlgorithm.add(state);
                    }
                }
                    t++;

                    int num = 0;
                    //racunam ukupan score svih algoritama do sad
                    for (Algorithm algorithm : listAlgorithm) {
                        n1 += algorithm.getReward1();
                        n2 += algorithm.getReward2();
                        n3 += algorithm.getReward3();
                        n4 += algorithm.getReward4();
                        num++;
                    }


                    double averageReward1 = 0.0;
                    double averageReward2 = 0.0;
                    double averageReward3 = 0.0;
                    double averageReward4 = 0.0;

                    double A1 = 0.0;
                    double A2 = 0.0;
                    double A3 = 0.0;
                    double A4 = 0.0;

                    double c = 1.0;

                    if (num1 == 0) {
                        averageReward1 = 0.0;
                        A1 = averageReward1;
                    } else {
                        averageReward1 = n1 / num1;
                        A1 = averageReward1 + c * (Math.sqrt(Math.log(t) / num1));
                    }
                    if (num2 == 0) {
                        averageReward2 = 0.0;
                        A2 = averageReward2;
                    } else {
                        averageReward2 = n2 / num2;
                        A2 = averageReward2 + c * (Math.sqrt(Math.log(t) / num2));
                    }
                    if (num3 == 0) {
                        averageReward3 = 0.0;
                        A3 = averageReward3;
                    } else {
                        averageReward3 = n3 / num3;
                        A3 = averageReward3 + c * (Math.sqrt(Math.log(t) / num3));
                    }
                    if (num4 == 0) {
                        averageReward4 = 0.0;
                        A4 = averageReward4;
                    } else {
                        averageReward4 = n4 / num4;
                        A4 = averageReward4 + c * (Math.sqrt(Math.log(t) / num4));
                    }


                    double[] array = new double[4];
                    array[0] = A1;
                    array[1] = A2;
                    array[2] = A3;
                    array[3] = A4;
                    Arrays.sort(array);
                    winnerGlobal = finalWinner;
                    winnerScoreGlobal = finalWinnerScore;


                    Algorithm algorithm = new Algorithm();
                    algorithm.setDeviceId(android_id);
                    if (array[0] == A1) {

                        if (marginOfConfidence(predictions)) {

                            algorithmGlobal = 1;
                            rewardGlobal = n1;
                            MainAware.surveyAware(MainAware.CONTEXT, MainAware.CONTEXT.getPackageName());
                        } else {

                            if (unensweredNum1 <= 3) {

                                algorithm.setUnanswered1(true);
                                insertAlgorithm(algorithm);
                                asked = false;
                            } else {

                                algorithmGlobal = 1;
                                rewardGlobal = n1;
                                MainAware.surveyAware(MainAware.CONTEXT, MainAware.CONTEXT.getPackageName());
                            }
                        }
                    } else if (array[0] == A2) {

                        if (randomBasedOnActivity()) {

                            algorithmGlobal = 2;
                            rewardGlobal = n2;
                            MainAware.surveyAware(MainAware.CONTEXT, MainAware.CONTEXT.getPackageName());
                        } else {
                            if (unensweredNum2 <= 3) {
                                algorithm.setUnanswered2(true);
                                insertAlgorithm(algorithm);
                                asked = false;
                            } else {

                                algorithmGlobal = 2;
                                rewardGlobal = n2;
                                MainAware.surveyAware(MainAware.CONTEXT, MainAware.CONTEXT.getPackageName());
                            }
                        }
                    } else if (array[1] == A3) {

                        if (leastConfidenceSampling(pred)) {

                            algorithmGlobal = 3;
                            rewardGlobal = n3;
                            MainAware.surveyAware(MainAware.CONTEXT, MainAware.CONTEXT.getPackageName());
                        } else {

                            if (unensweredNum3 <= 3) {
                                algorithm.setUnanswered3(true);
                                insertAlgorithm(algorithm);
                                asked = false;
                            } else {
                                algorithmGlobal = 3;
                                rewardGlobal = n3;
                                MainAware.surveyAware(MainAware.CONTEXT, MainAware.CONTEXT.getPackageName());
                            }
                        }
                    } else {

                        int tf = choose();
                        if (tf == 1) {
                            algorithmGlobal = 4;
                            rewardGlobal = n4;
                            MainAware.surveyAware(MainAware.CONTEXT, MainAware.CONTEXT.getPackageName());
                        } else {

                            if (unensweredNum4 <= 3) {
                                algorithm.setUnanswered4(true);
                                insertAlgorithm(algorithm);
                                asked = false;
                            } else {
                                algorithmGlobal = 4;
                                rewardGlobal = n4;
                                MainAware.surveyAware(MainAware.CONTEXT, MainAware.CONTEXT.getPackageName());
                            }
                        }
                    }
                }



        });
    }


    private void insertAlgorithm(Algorithm algorithm ){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("algorithm")
                .add(algorithm)
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



    }
    public void readDataAlgorithm(MyCallbackState myCallback) {

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference attractionsRef = rootRef.collection("algorithm");
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

    public Boolean marginOfConfidence(List<Double> prediction) {
        Boolean send = false;

        Collections.sort(prediction, Collections.reverseOrder());
        Double highest = prediction.get(0) * 100;
        Double nextHighest = prediction.get(1) * 100;

        if(highest - nextHighest < 20){
            send = true;
        }


        return send;
    }

    public Boolean randomBasedOnActivity() {
        Boolean send = false;
        if(MainAware.ACTIVITY_SEND == 0 || MainAware.ACTIVITY_SEND == 8){
            send = true;
        }
        return send;
    }

    public Boolean leastConfidenceSampling(double[] predict) {
        Boolean send = false;
        if (predict[0] >= 0.50) {
        } else if (predict[1] >= 0.50) {
        } else if (predict[2] >= 0.50) {
        } else if (predict[3] >= 0.50) {
        } else if (predict[4] >= 0.50) {
        } else {
            send = true;
        }
        return send;
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



    public void calculateAgain(int winner, double winnerScore, int algorithm, double reward, Context context, String answered){
        try {
            String android_id = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            final double[] finalWinnerScorenew = {0.0};
            final int[] finalnewWinner = {0};

            readData(new MyCallbackState() {
                @Override
                public void onCallbackState( List<Map<String, Object>> attractionsList) {

                    List<State> listState = new ArrayList<>();

                        for (Map<String, Object> item : attractionsList) {
                            if (item.get("deviceId").toString().equals(android_id)) {
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
                        if (listState.size() >= 2) {
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

                                weka.filters.unsupervised.instance.Randomize m_Filter = new Randomize();
                                m_Filter.setInputFormat(m_Training);

                                Instances localInstances = Filter.useFilter(m_Training, m_Filter);
                                RandomForest m_Classifier = new weka.classifiers.trees.RandomForest();

                                m_Classifier.buildClassifier(localInstances);

                                Evaluation m_Evaluation = new Evaluation(localInstances);
                                m_Evaluation.crossValidateModel(m_Classifier, localInstances, 2,
                                        m_Training.getRandomNumberGenerator(1));
                                String result = "Correct:"
                                        + m_Evaluation.correct()
                                        + "/Wrong:"
                                        + m_Evaluation.incorrect()
                                        + "/Correct(%):"
                                        + m_Evaluation.pctCorrect();
                                Log.e("Result", result);
                                Log.e("Result", "================================================");


                                Instances dataUnlabeledPredict = new Instances("Predict", atts, 0);
                                Instance iExample = new DenseInstance(3);
                                iExample.setValue(atts.get(0), 2);
                                iExample.setValue(atts.get(1), 3);
                                iExample.setValue(atts.get(2), 0);
                                dataUnlabeledPredict.add(iExample);

                                dataUnlabeledPredict.setClassIndex(dataUnlabeledPredict.numAttributes() - 1);
                                double[] predict = m_Classifier.distributionForInstance(dataUnlabeledPredict.firstInstance());
                                MainAware.PREDICTION = predict;

                                double h1 = predict[0];
                                double h2 = predict[1];
                                double h3 = predict[2];
                                double h4 = predict[3];
                                double h5 = predict[4];
                                int winnerNew = 0;
                                double winnerScoreNew = 0.0;
                                Arrays.sort(predict);

                                if(predict[4]==h1){
                                    winnerNew = 1;
                                    winnerScoreNew = h1;
                                }else if(predict[4]==h2){

                                    winnerNew = 2;
                                    winnerScoreNew = h2;
                                }else if(predict[4]==h3){

                                    winnerNew = 3;
                                    winnerScoreNew = h3;
                                }else if(predict[4]==h4){

                                    winnerNew = 4;
                                    winnerScoreNew = h4;
                                }else{

                                    winnerNew = 5;
                                    winnerScoreNew = h5;
                                }


                                finalWinnerScorenew[0] = winnerScoreNew;
                                finalnewWinner[0] = winnerNew;

                                String android_id = Settings.Secure.getString(context.getContentResolver(),
                                        Settings.Secure.ANDROID_ID);
                                Algorithm algorithm1 = new Algorithm();

                                algorithm1.setDeviceId(android_id);
                                if(asked) {

                                    if (winner == Integer.parseInt(answered)) {
                                        //nagradi algoritam 1+
                                        if (algorithm == 1) {
                                            algorithm1.setReward1(reward + 1);
                                        } else if (algorithm == 2) {

                                            algorithm1.setReward2(reward + 1);
                                        } else if (algorithm == 3) {

                                            algorithm1.setReward3(reward + 1);
                                        } else {

                                            algorithm1.setReward4(reward + 1);
                                        }
                                    } else {

                                        //ovde gledamo difference u groundtruth
                                        double difference = finalWinnerScorenew[0] - winnerScore;
                                        //nagrada ++
                                        if (algorithm == 1) {
                                            algorithm1.setReward1(reward + 1 + difference);
                                        } else if (algorithm == 2) {

                                            algorithm1.setReward2(reward + 1 + difference);
                                        } else if (algorithm == 3) {

                                            algorithm1.setReward3(reward + 1 + difference);
                                        } else {

                                            algorithm1.setReward4(reward + 1 + difference);
                                        }
                                    }
                                }else{
                                    if (winner == Integer.parseInt(answered))
                                        {
                                        //nagradi algoritam 1+
                                        if (algorithm == 1) {
                                            algorithm1.setReward1(reward + 1);
                                        } else if (algorithm == 2) {

                                            algorithm1.setReward2(reward + 1);
                                        } else if (algorithm == 3) {

                                            algorithm1.setReward3(reward + 1);
                                        } else {

                                            algorithm1.setReward4(reward + 1);
                                        }
                                    } else {

                                        //nagrada ++
                                        if (algorithm == 1) {
                                            algorithm1.setReward1(reward -1);
                                        } else if (algorithm == 2) {

                                            algorithm1.setReward2(reward -1);
                                        } else if (algorithm == 3) {

                                            algorithm1.setReward3(reward -1);
                                        } else {

                                            algorithm1.setReward4(reward-1);
                                        }
                                    }


                                }

     FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("algorithm")
                        .add(algorithm1)
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


                            } catch (Exception e) {

                                System.out.println(e);
                            }

                        } else {
                        //ako ima samo jedan
                            String android_id = Settings.Secure.getString(context.getContentResolver(),
                                    Settings.Secure.ANDROID_ID);
                            Algorithm algorithm1 = new Algorithm();

                            algorithm1.setDeviceId(android_id);
                            if(asked) {

                                if (winner == Integer.parseInt(answered)) {
                                    //nagradi algoritam 1+
                                    if (algorithm == 1) {
                                        algorithm1.setReward1(reward + 1);
                                    } else if (algorithm == 2) {

                                        algorithm1.setReward2(reward + 1);
                                    } else if (algorithm == 3) {

                                        algorithm1.setReward3(reward + 1);
                                    } else {

                                        algorithm1.setReward4(reward + 1);
                                    }
                                } else {

                                    double difference = finalWinnerScorenew[0] - winnerScore;
                                    //nagrada ++
                                    if (algorithm == 1) {
                                        algorithm1.setReward1(reward + 1 + difference);
                                    } else if (algorithm == 2) {

                                        algorithm1.setReward2(reward + 1 + difference);
                                    } else if (algorithm == 3) {

                                        algorithm1.setReward3(reward + 1 + difference);
                                    } else {

                                        algorithm1.setReward4(reward + 1 + difference);
                                    }
                                }
                            }else {

                                if (winner == Integer.parseInt(answered)) {
                                    //nagradi algoritam 1+
                                    if (algorithm == 1) {
                                        algorithm1.setReward1(reward + 1);
                                    } else if (algorithm == 2) {

                                        algorithm1.setReward2(reward + 1);
                                    } else if (algorithm == 3) {

                                        algorithm1.setReward3(reward + 1);
                                    } else {

                                        algorithm1.setReward4(reward + 1);
                                    }
                                } else {

                                    //nagrada ++
                                    if (algorithm == 1) {
                                        algorithm1.setReward1(reward - 1);
                                    } else if (algorithm == 2) {

                                        algorithm1.setReward2(reward - 1);
                                    } else if (algorithm == 3) {

                                        algorithm1.setReward3(reward - 1);
                                    } else {

                                        algorithm1.setReward4(reward - 1);
                                    }
                                }
                            }
                            //ne nagradjujem kada je isti rezultat
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("algorithm")
                                    .add(algorithm1)
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


                        }

                }
            });



        } catch (Exception e) {

            e.printStackTrace();
        }

    }


}
