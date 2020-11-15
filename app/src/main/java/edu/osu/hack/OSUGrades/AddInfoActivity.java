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
import com.google.android.gms.tasks.Task;
import com.google.common.reflect.ClassPath;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddInfoActivity  extends AppCompatActivity {
    private final HashMap<String, String> className = new HashMap<String, String>();
    private static final String TAG = "AddInfoActivity";
//    private final String className = getIntent().getStringExtra("className");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);
        className.put("className", "CSE1223");
        String classname = className.get("className");
//        String className = getIntent().getStringExtra("className");
        TextView textView = (TextView)findViewById(R.id.className);
        textView.setText(classname);

        findViewById(R.id.submit).setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String gpaString = ((EditText)findViewById(R.id.gpaGrades)).getText().toString();
            String difString = ((EditText)findViewById(R.id.difCourse)).getText().toString();
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
            if (professor.isEmpty()){
                getData(gpa, difficulty, "");
            } else {
                getData(gpa, difficulty, professor);
            }
        }

        detailStartAcitivty(GradeResultActivity.class);
    }




    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void getData(double gpa, int rate, final String professor) {


        final double currentGpa = gpa;
        final int currentRate = rate;
        DocumentReference dRef = FirebaseFirestore.getInstance().collection("courses").document(className.get("className"));
        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();

                    if (doc.exists()) {
                        Map<String, Object> temp = doc.getData();

                        double averageGpa = (double) temp.get("averageGpa");
                        long averageRating = (long) temp.get("rating");
                        long reported = (long) temp.get("reported");

                        double avg = (averageGpa * reported + currentGpa) / (reported + 1);
                        long rating = (averageRating * reported + currentRate) / (reported + 1);

                        DocumentReference updateRef = FirebaseFirestore.getInstance().collection("courses").document(className.get("className"));
                        updateRef.update("averageGpa", avg);
                        updateRef.update("rating", rating);
                        updateRef.update("reported", reported+1);

                        if (temp.containsKey("professors")) {
                            String[] professors = (String[]) temp.get("professors");
                        } else {
                            if (!professor.isEmpty()) {
                                String[] newList = {professor};
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
        startActivity(intent);
    }

}
