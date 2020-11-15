package edu.osu.hack.OSUGrades;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CourseListActivity extends AppCompatActivity {

    FirebaseFirestore db;
    ArrayList<String> courseArray = new ArrayList<>();
    ArrayAdapter adapter;
    EditText et_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        db = FirebaseFirestore.getInstance();

        et_search = findViewById(R.id.courseListEditText);

        // Get the course data from the fire store
        db.collection("courses")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                courseArray.add(i, nameGetter(document.getData().toString()));
                                i++;
                            }
                        } else {
                            startToast();
                        }
                    }
                });

        // Set the list view adapter and put the retrieved course data in the list view
        adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                courseArray);

        // Set the course+ list view with its id
        final ListView courseList = (ListView) findViewById(R.id.courseList);

        // Display the course name in the list view
        courseList.setAdapter(adapter);

        // Lively change the result of the list view based on the edit text result
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Make the list clickable
        courseList.setClickable(true);

        // Get the name of the course based on the user's choice
        courseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String courseName = (courseList.getItemAtPosition(position)).toString();
                myStartActivity(courseName);
            }
        });
    }

    private String nameGetter(String document) {

        // Split path into segments
        String[] segments = document.split("=");
        // Grab the last segment
        String courseName = segments[segments.length - 1];
        courseName = courseName.substring(0, courseName.length() - 1);

        return courseName;
    }

    private void startToast() {
        Toast.makeText(this, "Failed to get the course data", Toast.LENGTH_SHORT).show();
    }

    private void myStartActivity(String courseName) {

        Intent intent = new Intent(this, GradeResultActivity.class);
        intent.putExtra("courseName", courseName);
        startActivity(intent);

    }

}