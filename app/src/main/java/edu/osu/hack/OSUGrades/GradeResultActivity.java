package edu.osu.hack.OSUGrades;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        profList = (ListView) findViewById(R.id.profList);
        final String sessionID = getIntent().getStringExtra("courseName");
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("courses").document(sessionID);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("ERROR", "Listen failed", error);
                }
                if (value != null && value.exists()) {
                    String averageGpa = String.format("%.2f", (double) value.getData().get("averageGpa"));
                    String averageRate = String.format("%.2f", (double) value.getData().get("rating"));
                    Average_GPA = findViewById(R.id.gpa_Average);
                    Average_GPA.setText(averageGpa);
                    rating = findViewById(R.id.rating);
                    rating.setText(averageRate);
                    list = (ArrayList<String>) value.getData().get("professors");
                    profListSet(list);
                    Log.d("Current", "Current Data: " + value.getData());
                } else {
                    Log.d("Current", "Current Data: " + value.getData());
                }
            }
        });
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
        // Make the list clickable
        profList.setClickable(true);
        profList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String profName = profList.getItemAtPosition(position).toString();
                String url = "https://www.ratemyprofessors.com/search.jsp?query=" + profName;
                startUrlActivity(url);
            }
        });
    }
    @Override
    public void onBackPressed() {
        finish();
    }
    public void startUrlActivity(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}