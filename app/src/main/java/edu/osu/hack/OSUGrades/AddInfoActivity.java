package edu.osu.hack.OSUGrades;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.reflect.ClassPath;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddInfoActivity  extends AppCompatActivity {

    private final HashMap<String, String> className = new HashMap<String, String>();

    private static final String TAG = "AddInfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);
        final String sessionID = getIntent().getStringExtra("courseName");
        className.put("courseName", sessionID);
        String classname = className.get("courseName");

        Log.d(TAG, "classname: " + classname);

        TextView textView = (TextView)findViewById(R.id.className);
        textView.setText(classname);


        findViewById(R.id.increase).setOnClickListener(onButtonListener);
        findViewById(R.id.decrease).setOnClickListener(onButtonListener);
        findViewById(R.id.submit).setOnClickListener(onClickListener);

    }
    View.OnClickListener onButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView textView = (TextView)findViewById(R.id.difCourse);
            int var = Integer.parseInt(textView.getText().toString());
            switch (v.getId()) {
                case R.id.decrease:
                    if (var > 1) {
                        var--;
                        textView.setText(var+"");
                    }
                    break;

                case R.id.increase:
                    if (var < 5) {
                        var++;
                        textView.setText(var+"");
                    }
                    break;


            }
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String gpaString = ((EditText)findViewById(R.id.gpaGrades)).getText().toString();
            String difString = ((TextView)findViewById(R.id.difCourse)).getText().toString();
            String professor = ((EditText)findViewById(R.id.profName)).getText().toString();
            if (gpaString.isEmpty() || difString.isEmpty()) {
                startToast("Gpa and difficulty must be typed");
            } else {
                rateGpa(gpaString, difString, professor);
            }
        }
    };

    private void rateGpa(String gpaString, String difString, String professor) {
        double gpa = 0.0;
        int difficulty = 0;
        try {
            gpa = Double.parseDouble(gpaString);
            difficulty = Integer.parseInt(difString);
        } catch (NumberFormatException e) {
            startToast("Gpa and difficulty must be number.");
        }

        if (Double.compare(gpa, 0.0) > 0 && difficulty > 0) {
            if (Double.compare(gpa, 4.0) > 0 || difficulty > 5) {
                startToast("gpa is less than or equal to 4.0");
            } else {
                if (professor.isEmpty()){
                    getData(gpa, difficulty, "");
//                setData(professor);
                } else {
                    getData(gpa, difficulty, professor);
                }
                detailStartAcitivty(GradeResultActivity.class);
            }

        }


    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void getData(double gpa, int rate, final String professor) {


        final double currentGpa = gpa;
        final int currentRate = rate;

        DocumentReference dRef = FirebaseFirestore.getInstance().collection("courses").document(className.get("courseName"));
        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Map<String, Object> temp = doc.getData();
                        Log.d(TAG, "average Gpa: " + temp.get("averageGpa"));

                        long reported = (long) temp.get("reported");
                        DocumentReference updateRef = FirebaseFirestore.getInstance().collection("courses").document(className.get("courseName"));

                        String GPA = "";
                        String rate = "";

                        if ( temp.get("averageGpa") instanceof Double ) {
                            double averageGPA = (double) temp.get("averageGpa");
                            GPA = "" + averageGPA;
                        } else {
                            long averageGPA = (long) temp.get("averageGpa");
                            GPA = "" + averageGPA;
                        }

                        if ( temp.get("rating") instanceof Double ) {
                            double averageRating = (double) temp.get("rating");
                            rate = "" + averageRating;
                        } else {
                            long averageRating = (long) temp.get("rating");
                            rate = "" + averageRating;
                        }

                        double avg = (Double.parseDouble(GPA) * reported + currentGpa) / (reported + 1);
                        double rating = (Double.parseDouble(rate) * reported + currentRate) / (reported + 1);

                        updateRef.update("averageGpa", avg);
                        updateRef.update("rating", rating);
                        updateRef.update("reported", reported + 1);

                        if (temp.containsKey("professors")) {
                            if (!professor.isEmpty()) {
                                List<String> professors = (ArrayList<String>) temp.get("professors");
                                professors.add(professor);
                                updateRef.update("professors", professors);
                            }
                        } else {
                            if (!professor.isEmpty()) {
                                String[] newList = {professor};
                                temp.put("professors", Arrays.asList(newList));
                                Log.d(TAG, "professors: " + temp.get("professors"));

                                updateRef.set(temp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error writing document", e);
                                            }
                                        });
                            }
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                }
            }
        });

    }

    private void detailStartAcitivty(Class c) {
        Intent intent = new Intent(this, c);
        intent.putExtra("courseName", className.get("courseName"));
        startActivity(intent);
    }


}