package edu.osu.hack.OSUGrades;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class AddGPAActivity extends AppCompatActivity {

    TextView ClassName;
    TextView Average_GPA;
    TextView rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_g_p_a);

        final String sessionID = getIntent().getStringExtra("courseName");

        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("courses").document(sessionID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if ( task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if ( doc.exists()) {
                        Map<String, Object> temp = doc.getData();
                        ClassInfo infoTemp;

                        String professorName = ((EditText)findViewById(R.id.newprofessor)).getText().toString();
                        int addGPA = Integer.parseInt(((EditText)findViewById(R.id.newGPA)).getText().toString());
                        double addRate = Double.parseDouble(((EditText)findViewById(R.id.newRate)).getText().toString());

                        String courseID = (String) temp.get("course");
                        double averageGPA = 0;
                        double rate = 0;
                        int reported = 0;

                        if ( temp.containsKey("professorName")) {
                            try {
                                averageGPA = (double) temp.get("averageGPA");
                                rate = (double) temp.get("rating");
                                reported = (int) temp.get("reported");
                            }
                            catch (NullPointerException e) {
                                Log.e("ERROR", "NULLPOINTEXCEPTION");
                            }
                            infoTemp = new ClassInfo(courseID, averageGPA, temp.get("professorName").toString(), rate, reported);
                        }
                        else {
                            try {
                                averageGPA = (double) temp.get("averageGPA");
                                rate = (double) temp.get("rating");
                                reported = (int) temp.get("reported");
                            }
                            catch (NullPointerException e) {
                                Log.e("ERROR", "NULLPOINTEXCEPTION");
                            }
                            infoTemp = new ClassInfo(courseID, averageGPA, rate, reported);
                        }

                        if ( infoTemp.getProfessorName().contains(professorName)) {
                            temp.clear();
                            temp.put("course", infoTemp.getCourseID());
                            if ( infoTemp.getReported() != 0 ) {
                                temp.put("averageGPA", (infoTemp.getGPA() + addGPA) / infoTemp.getReported());
                                temp.put("rating", (infoTemp.getRate() + addRate) / infoTemp.getReported());
                            }
                            else {
                                temp.put("averageGPA", 0);
                                temp.put("rating", 0);
                            }
                            temp.put("reported", infoTemp.getReported() + 1);
                        }
                        else {
                            temp.clear();
                            temp.put("course", infoTemp.getCourseID());
                            if ( infoTemp.getReported() != 0 ) {
                                temp.put("averageGPA", (infoTemp.getGPA() + addGPA) / infoTemp.getReported());
                                temp.put("rating", (infoTemp.getRate() + addRate) / infoTemp.getReported());
                            }
                            else {
                                temp.put("averageGPA", 0);
                                temp.put("rating", 0);
                            }
                            temp.put("reported", infoTemp.getReported() + 1);
                            temp.put("professors", infoTemp.getProfessorName().add(professorName));
                        }

                        docRef.update(temp);


                    }
                }
            }
        });


    }
}