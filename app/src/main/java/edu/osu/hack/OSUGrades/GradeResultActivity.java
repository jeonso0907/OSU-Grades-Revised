package edu.osu.hack.OSUGrades;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
    ListView profList;
    ArrayList<String> list;
    ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_list);
        profList = (ListView) findViewById(R.id.profList);
        final String sessionID = getIntent().getStringExtra("courseName");
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("courses").document(sessionID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Map<String, Object> temp = doc.getData();
                        Log.d("ERROR", "ERORRRRRRRRRRR" + temp.keySet().toString());

                         double averageGPA = (double) temp.get("averageGpa");
                         String GPA = String.format("%.2f", averageGPA);

                         double averageRating = (double) temp.get("rating");
                         String rate = String.format("%.2f", averageRating);


                        list = (ArrayList<String>) temp.get("professors");
                        if (list != null) {
                            Average_GPA = findViewById(R.id.gpa_Average);
                            Average_GPA.setText(GPA);
                            rating = findViewById(R.id.rating);
                            rating.setText(rate);
                            ClassName = findViewById(R.id.className);
                            ClassName.setText(String.valueOf(temp.get("course")));
                            profListSet(list);

                        } else {
                            Average_GPA = findViewById(R.id.gpa_Average);
                            Average_GPA.setText(GPA);
                            rating = findViewById(R.id.rating);
                            rating.setText(rate);

                            ArrayList<String> templist = new ArrayList<>();
                            templist.add("Empty");
                            profListSet(templist);
                        }
                        ClassName = findViewById(R.id.className);
                        ClassName.setText(temp.get("course").toString());
                        add_GPA = findViewById(R.id.add_GPA);
                        add_GPA.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(GradeResultActivity.this, AddInfoActivity.class);
                                intent.putExtra("courseName", sessionID);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                finish();
                                startActivity(intent);
                            }
                        });
                    }
                }
            }
        });
    }
    public void profListSet(ArrayList<String> list) {
        // Set the list view adapter and put the retrieved course data in the list view
        adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                list);
        // Display the course name in the list view
        profList.setAdapter(adapter);
    }
}