package com.aware.plugin.awarelibrary;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aware.plugin.awarelibrary.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Class for collecting data about personality test */
public class PersonalityTest extends AppCompatActivity {

    private int answer1 = 0;
    private int answer2 = 0;
    private int answer3 = 0;
    private int answer4 = 0;
    private int answer5 = 0;
    private int answer6 = 0;
    private int answer7 = 0;
    private int answer8 = 0;
    private int answer9 = 0;
    private int answer10 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.personality_test);
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        int id = view.getId();
        if (id == R.id.radioButton1) {
            if (checked) {
                answer1 = 1;
                return;
            }

        } else if (id == R.id.radioButton12) {
            if (checked) {
                answer1 = 2;
            }
        } else if (id == R.id.radioButton13) {
            if (checked) {
                answer1 = 3;
            }
        } else if (id == R.id.radioButton14) {
            if (checked) {
                answer1 = 4;
            }
        } else if (id == R.id.radioButton15) {
            if (checked) {
                answer1 = 5;
            }
        }
    }


    public void onRadioButtonClicked2(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        int id = view.getId();
        if (id == R.id.radioButton2) {
            if (checked) {
                answer2 = 1;
                return;
            }

        } else if (id == R.id.radioButton22) {
            if (checked) {
                answer2 = 2;
            }
        } else if (id == R.id.radioButton23) {
            if (checked) {
                answer2 = 3;
            }
        } else if (id == R.id.radioButton24) {
            if (checked) {
                answer2 = 4;
            }
        } else if (id == R.id.radioButton25) {
            if (checked) {
                answer2 = 5;
            }
        }
    }


    public void onRadioButtonClicked3(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        int id = view.getId();
        if (id == R.id.radioButton3) {
            if (checked) {
                answer3 = 1;
                return;
            }

        } else if (id == R.id.radioButton32) {
            if (checked) {
                answer3 = 2;
            }
        } else if (id == R.id.radioButton33) {
            if (checked) {
                answer3 = 3;
            }
        } else if (id == R.id.radioButton34) {
            if (checked) {
                answer3 = 4;
            }
        } else if (id == R.id.radioButton35) {
            if (checked) {
                answer3 = 5;
            }
        }
    }

    public void onRadioButtonClicked4(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        int id = view.getId();
        if (id == R.id.radioButton4) {
            if (checked) {
                answer4 = 1;
                return;
            }

        } else if (id == R.id.radioButton42) {
            if (checked) {
                answer4 = 2;
            }
        } else if (id == R.id.radioButton43) {
            if (checked) {
                answer4 = 3;
            }
        } else if (id == R.id.radioButton44) {
            if (checked) {
                answer4 = 4;
            }
        } else if (id == R.id.radioButton45) {
            if (checked) {
                answer4 = 5;
            }
        }
    }

    public void onRadioButtonClicked5(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        int id = view.getId();
        if (id == R.id.radioButton5) {
            if (checked) {
                answer5 = 1;
                return;
            }

        } else if (id == R.id.radioButton52) {
            if (checked) {
                answer5 = 2;
            }
        } else if (id == R.id.radioButton53) {
            if (checked) {
                answer5 = 3;
            }
        } else if (id == R.id.radioButton54) {
            if (checked) {
                answer5 = 4;
            }
        } else if (id == R.id.radioButton55) {
            if (checked) {
                answer5 = 5;
            }
        }
    }

    public void onRadioButtonClicked6(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        int id = view.getId();
        if (id == R.id.radioButton6) {
            if (checked) {
                answer6 = 1;
                return;
            }

        } else if (id == R.id.radioButton62) {
            if (checked) {
                answer6 = 2;
            }
        } else if (id == R.id.radioButton63) {
            if (checked) {
                answer6 = 3;
            }
        } else if (id == R.id.radioButton64) {
            if (checked) {
                answer6 = 4;
            }
        } else if (id == R.id.radioButton65) {
            if (checked) {
                answer6 = 5;
            }
        }
    }

    public void onRadioButtonClicked7(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        int id = view.getId();
        if (id == R.id.radioButton7) {
            if (checked) {
                answer7 = 1;
                return;
            }

        } else if (id == R.id.radioButton72) {
            if (checked) {
                answer7 = 2;
            }
        } else if (id == R.id.radioButton73) {
            if (checked) {
                answer7 = 3;
            }
        } else if (id == R.id.radioButton74) {
            if (checked) {
                answer7 = 4;
            }
        } else if (id == R.id.radioButton75) {
            if (checked) {
                answer7 = 5;
            }
        }
    }

    public void onRadioButtonClicked8(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        int id = view.getId();
        if (id == R.id.radioButton8) {
            if (checked) {
                answer8 = 1;
                return;
            }

        } else if (id == R.id.radioButton82) {
            if (checked) {
                answer8 = 2;
            }
        } else if (id == R.id.radioButton83) {
            if (checked) {
                answer8 = 3;
            }
        } else if (id == R.id.radioButton84) {
            if (checked) {
                answer8 = 4;
            }
        } else if (id == R.id.radioButton85) {
            if (checked) {
                answer8 = 5;
            }
        }
    }

    public void onRadioButtonClicked9(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        int id = view.getId();
        if (id == R.id.radioButton9) {
            if (checked) {
                answer9 = 1;
                return;
            }

        } else if (id == R.id.radioButton92) {
            if (checked) {
                answer9 = 2;
            }
        } else if (id == R.id.radioButton93) {
            if (checked) {
                answer9 = 3;
            }
        } else if (id == R.id.radioButton94) {
            if (checked) {
                answer9 = 4;
            }
        } else if (id == R.id.radioButton95) {
            if (checked) {
                answer9 = 5;
            }
        }
    }

    public void onRadioButtonClicked10(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        int id = view.getId();
        if (id == R.id.radioButton10) {
            if (checked) {
                answer10 = 1;
                return;
            }

        } else if (id == R.id.radioButton102) {
            if (checked) {
                answer10 = 2;
            }
        } else if (id == R.id.radioButton103) {
            if (checked) {
                answer10 = 3;
            }
        } else if (id == R.id.radioButton104) {
            if (checked) {
                answer10 = 4;
            }
        } else if (id == R.id.radioButton105) {
            if (checked) {
                answer10 = 5;
            }
        }
    }

    public void submitFunc(View view) {
        if (answer1 != 0 && answer2 != 0 && answer3 != 0 && answer4 != 0 && answer5 != 0 && answer6 != 0 && answer7 != 0 && answer8 != 0 && answer9 != 0 && answer10 != 0) {

            int extroversion = 6 - answer1 + answer6;
            int agreeableness = 6 - answer7 + answer2;
            int conscientiousness = 6 - answer3 + answer8;
            int neuroticism = 6 - answer4 + answer9;
            int openness = 6 - answer5 + answer10;

            SuggestActivity activity = new SuggestActivity();
            SuggestWorkActivity activityWork = new SuggestWorkActivity();
            String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
           User user = new User(activity.ID, activity.ADDRESS,activityWork.ADDRESS, extroversion, agreeableness, conscientiousness, neuroticism, openness, android_id, 0,0);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("user").document(android_id)
                    .set(user)
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

            PersonalityTest.this.finish();
        }
    }

}
