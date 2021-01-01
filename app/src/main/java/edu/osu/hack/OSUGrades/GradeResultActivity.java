package edu.osu.hack.OSUGrades;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    // Set a button to add a new gpa
    Button add_GPA;
    // Set text views to show the result of the course
    TextView ClassName;
    TextView Average_GPA;
    TextView rating;
    // Set a list to show the list of the professors
    ListView profList;
    // Set an array list to store the professor list
    ArrayList<String> list;
    // Set an array adapter to lively update the professor list
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_list);
        // Set a Firebase Auth and get the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // If the user is null, stop and exit the app
        if (user == null) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        // Set the professor list
        profList = (ListView) findViewById(R.id.profList);
        // Get the course name from the previous course list activity
        final String sessionID = getIntent().getStringExtra("courseName");
        // Get the course data based on the course name
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("courses").document(sessionID);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                // If there is an error, print the log error message
                if (error != null) {
                    Log.w("ERROR", "Listen failed", error);
                }
                // If the data value is not null and exists, get the data as strings and set
                // each text view with appropriate data
                if (value != null && value.exists()) {
                    String averageGpa = String.format("%.2f", (double) value.getData().get("averageGpa"));
                    String averageRate = String.format("%.2f", (double) value.getData().get("rating"));
                    Average_GPA = findViewById(R.id.gpa_Average);
                    Average_GPA.setText(averageGpa);
                    rating = findViewById(R.id.rating);
                    rating.setText(averageRate);
                    // Get the professor list data and store in the list
                    list = (ArrayList<String>) value.getData().get("professors");
                    // Set the list in the UI
                    profListSet(list);
                }
                // Print the succeeded user data as a log
                Log.d("Current", "Current Data: " + value.getData());
            }
        });

        // Set live Firebase listener to update the live data from the user
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // If the course data is valid, proceed to the next step
                if (task.isSuccessful()) {
                    // Set the document from the task result
                    DocumentSnapshot doc = task.getResult();
                    // If the document exists, get the lively updated data
                    if (doc.exists()) {
                        // Set a map to store a new data
                        Map<String, Object> temp = doc.getData();
                        // Calculate and update the new lively updated data
                        double averageGPA = (double) temp.get("averageGpa");
                        String GPA = String.format("%.2f", averageGPA);
                        double averageRating = (double) temp.get("rating");
                        String rate = String.format("%.2f", averageRating);
                        // Store a new data
                        list = (ArrayList<String>) temp.get("professors");
                        // If professor data is not typed, update the other data
                        // Else, update the whole data including the professor list
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
                        // Update the text views based on the new data
                        ClassName = findViewById(R.id.className);
                        ClassName.setText(temp.get("course").toString());
                        add_GPA = findViewById(R.id.add_GPA);
                        // Set a click listener for the add GPA button
                        // Intent to the add GPA activity
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
        // Make each professor list intent to the outer 'Rate my professor' page
        profList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String profName = profList.getItemAtPosition(position).toString();
                String url = "https://www.ratemyprofessors.com/search.jsp?query=" + profName;
                // Intent to the outer page
                startUrlActivity(url);
            }
        });
    }

    // If back button pressed, close the current activity and go back to the previous course list
    @Override
    public void onBackPressed() {
        finish();
    }

    // Intent to the rate my professor page
    public void startUrlActivity(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}