package edu.osu.hack.OSUGrades;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddInfoActivity  extends AppCompatActivity {
    private final HashMap<String, String> className = new HashMap<String, String>();
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
        Double gpa = 0.0;
        int difficulty = 0;
        try {
            gpa = Double.parseDouble(gpaString);
            difficulty = Integer.parseInt(difString);
        } catch (NumberFormatException e) {
            startToast("Gpa and difficulty must be number.");
        }

        if (Double.compare(gpa, 0.0) > 0 && difficulty > 0) {
            if (professor.isEmpty()){
                storeData(gpa, difficulty);
            } else {
                storeData(gpa, difficulty, professor);
            }
        }
    }




    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void storeData(Double gpa, int rate) {

        final Map<String, Object> course = new HashMap<>();
        DocumentReference dRef = FirebaseFirestore.getInstance().collection("courses").document(className.get("className"));
        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                       course.put("averageGpa", doc.getData().get("averageGpa"));
                       course.put("rating", doc.getData().get("rating"));
                    }
                }
            }
        });

        double avg = ((double) course.get("averageGpa") * (double) course.get("reported") + gpa) / ((double) course.get("reported") + 1);
        double rating = ((double) course.get("rating") * (double) course.get("reported") + rate) / ((double) course.get("reported") + 1);

//        course.put("averageGpa", dRef.dat)
    }
    private void storeData(Double gpa, int rate, String professor) {
        final Map<String, Object> course = new HashMap<>();
        DocumentReference dRef = FirebaseFirestore.getInstance().collection("courses").document(className.get("className"));
        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        course.put("averageGpa", doc.getData().get("averageGpa"));
                        course.put("rating", doc.getData().get("rating"));
                        course.put("reported", doc.getData().get("reported"));
                        course.put("professors", doc.getData().get("professors"));
                    }
                }
            }
        });

        double avg = ((double) course.get("averageGpa") * (double) course.get("reported") + gpa) / ((double) course.get("reported") + 1);
        double rating = ((double) course.get("rating") * (double) course.get("reported") + rate) / ((double) course.get("reported") + 1);
        if (course.get("professors") != null) {
            String[] professors = (String[]) course.get("professors");
            ArrayList<String> profList = new ArrayList<>();
            boolean hasItem = true;
            for (int i = 0; i < professors.length; i++) {
                if (!professors[i].equals(professor)) {
                    hasItem = false;
                } else {
                    break;
                }
            }
            if (!hasItem) {
                for (String s : professors) {
                    profList.add(s);
                }
                profList.add(professor);
                course.put("professors", profList.toArray());
            }

        } else {
            String[] professors = {professor};
            course.put("professors", professors);
        }

        dRef.update("averageGpa", avg);
        dRef.update("rating", rating);
        dRef.update("professors", course.get("professors"));


    }
}
