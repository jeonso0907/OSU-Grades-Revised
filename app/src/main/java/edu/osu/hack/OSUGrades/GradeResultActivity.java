package edu.osu.hack.OSUGrades;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class GradeResultActivity extends AppCompatActivity {

    Button add_GPA;
    TextView ClassName;
    TextView Average_GPA;
    TextView rating;

    private MainAdapter mainAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_grade_list);

        final String sessionID = getIntent().getStringExtra("courseName");

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("courses").document(sessionID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if ( task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if ( doc.exists() ) {
                        Map<String, Object> temp = doc.getData();
                        ClassInfo infoTemp;

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

                        Average_GPA = findViewById(R.id.gpa_Average);
                        if ( infoTemp.getReported() != 0) {
                            Average_GPA.setText(String.valueOf(infoTemp.getGPA()/infoTemp.getReported()));
                        }
                        else {
                            Average_GPA.setText(String.valueOf("0"));
                        }

                        rating = findViewById(R.id.rating);
                        if ( infoTemp.getReported() != 0) {
                            rating.setText(String.valueOf(infoTemp.getRate()/infoTemp.getReported()));
                        }
                        else {
                            rating.setText(String.valueOf("0"));
                        }



                        ClassName = findViewById(R.id.className);
                        ClassName.setText(String.valueOf(infoTemp.getCourseID()));

                        if ( infoTemp.getProfessorName() != null) {

                            final ArrayList<String> professorList = infoTemp.getProfessorName();
                            recyclerView = (RecyclerView)findViewById(R.id.professorList);
                            recyclerView.setLayoutManager(linearLayoutManager);

                            mainAdapter = new MainAdapter(professorList);
                            recyclerView.setAdapter(mainAdapter);
                        }
                    }
                }
            }
        });

        /*add_GPA = findViewById(R.id.add_GPA);

        add_GPA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GradeResultActivity.this, AddGPAActivity.class);
                intent.putExtra("courseID", sessionID);
                startActivity(intent);
            }
        });*/





    }
}