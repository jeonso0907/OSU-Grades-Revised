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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
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
                        double averageGpa = (double) temp.get("averageGpa");
                        double averageRating = (double) temp.get("rating");
                        double reported = (double) temp.get("reported");
                        double avg = (averageGpa * reported + currentGpa) / (reported + 1);
                        double rating = (averageRating * reported + currentRate) / (reported + 1);
                        DocumentReference updateRef = FirebaseFirestore.getInstance().collection("courses").document(className.get("courseName"));
                        updateRef.update("averageGpa", avg);
                        updateRef.update("rating", rating);
                        updateRef.update("reported", reported+1);
                        if (temp.containsKey("professors")) {
                            if (!professor.isEmpty()) {
                                List<String> professors = (ArrayList<String>) temp.get("professors");
                                professors.add(professor);
                                updateRef.update("professors", professors);
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
        finish();
    }
}