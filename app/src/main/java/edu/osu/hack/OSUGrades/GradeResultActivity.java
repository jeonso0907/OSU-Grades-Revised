package edu.osu.hack.OSUGrades;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("courses").document("");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if ( task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if ( doc.exists()) {
                        Map<String, Object> temp = doc.getData();
                        ClassInfo infoTemp;

                        if ( temp.containsKey("professorName")) {
                            infoTemp = new ClassInfo((String) temp.get("course"), (float) temp.get("averageGPA"), (String) temp.get("professorName"), (double) temp.get("rating"), (int) temp.get("reported"));
                        }
                        else {
                            infoTemp = new ClassInfo((String) temp.get("course"), (float) temp.get("averageGPA"), (double) temp.get("rating"), (int) temp.get("reported"));
                        }

                        Average_GPA = findViewById(R.id.gpa_Average);
                        Average_GPA.setText(String.valueOf(infoTemp.getAverage()));

                        rating = findViewById(R.id.rating);
                        rating.setText(String.valueOf(infoTemp.getAverageRate()));

                        ClassName = findViewById(R.id.className);
                        ClassName.setText(String.valueOf(infoTemp.getCourseID()));

                        final ArrayList<String> professorList = infoTemp.getProfessorName();

                        recyclerView = (RecyclerView)findViewById(R.id.professorList);
                        recyclerView.setLayoutManager(linearLayoutManager);

                        mainAdapter = new MainAdapter(professorList);

                        recyclerView.setAdapter(mainAdapter);

                    }
                }
            }
        });

        add_GPA = findViewById(R.id.add_GPA);

        add_GPA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GradeResultActivity.this, AddGPAActivity.class);
                startActivity(intent);
            }
        });





    }
}