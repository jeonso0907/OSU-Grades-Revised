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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class GradeResultActivity extends AppCompatActivity {

    Button add_GPA;
    Button logout;
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
                        Log.d("ERROR", "ERORRRRRRRRRRR" + temp.keySet().toString());
                        ClassInfo infoTemp = new ClassInfo();

                        double averageGpa = (double) temp.get("averageGpa");
                        long averageRating = (long) temp.get("rating");

                        ArrayList<String> list = (ArrayList<String>) temp.get("professors");

                        if ( !list.isEmpty()) {
                            Average_GPA = findViewById(R.id.gpa_Average);
                            Average_GPA.setText("" + averageGpa);

                            rating = findViewById(R.id.rating);
                            rating.setText("" + averageRating);

                            ClassName = findViewById(R.id.className);
                            ClassName.setText(String.valueOf(infoTemp.getCourseID()));

                            recyclerView = (RecyclerView)findViewById(R.id.professorList);
                            recyclerView.setLayoutManager(linearLayoutManager);

                            mainAdapter = new MainAdapter(list);
                            recyclerView.setAdapter(mainAdapter);

                            // infoTemp = new ClassInfo(courseID, averageGPA * reported, (ArrayList<String>) temp.get("professsors"), rate, reported);
                        }
                        else {
                            Average_GPA = findViewById(R.id.gpa_Average);
                            Average_GPA.setText("" + averageGpa);

                            rating = findViewById(R.id.rating);
                            rating.setText("" + averageRating);

                            recyclerView = findViewById(R.id.professorList);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            ArrayList<String> empty = new ArrayList<>();
                            empty.add("empty");
                            mainAdapter = new MainAdapter(empty);
                            recyclerView.setAdapter(mainAdapter);

                            // infoTemp = new ClassInfo(courseID, averageGPA * reported, rate, reported);
                        }

                        ClassName = findViewById(R.id.className);
                        ClassName.setText(temp.get("course").toString());

                        add_GPA = findViewById(R.id.add_GPA);

                        add_GPA.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(GradeResultActivity.this, AddInfoActivity.class);
                                intent.putExtra("courseName", sessionID);

                                startActivity(intent);
                            }
                        });

                        logout = findViewById(R.id.logout);

                        logout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                switch (v.getId()) {
                                    case R.id.logout:
                                        FirebaseAuth.getInstance().signOut();
                                        logout.setText("LOG IN");
                                        Intent intent = new Intent(GradeResultActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        break;
                                }

                            }
                        });
                    }
                }
            }
        });






    }
}