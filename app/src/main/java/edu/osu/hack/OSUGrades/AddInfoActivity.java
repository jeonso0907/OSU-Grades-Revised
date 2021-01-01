package edu.osu.hack.OSUGrades;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class AddInfoActivity  extends AppCompatActivity {

    // Set a map to store a new data
    private final HashMap<String, String> className = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);
        // Set Firebase Auth and get a current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // If the user is null, stop and exit the app
        if (user == null) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        // Get the course name data from the previous grade result activity
        final String sessionID = getIntent().getStringExtra("courseName");
        // Assign the map key with the course name and its data
        className.put("courseName", sessionID);
        // Set class name as a string
        String classname = className.get("courseName");
        // Get the text views to update the data
        TextView textView = (TextView)findViewById(R.id.className);
        // Set the title text view with the course name
        textView.setText(classname);
        findViewById(R.id.increase).setOnClickListener(onButtonListener);
        findViewById(R.id.decrease).setOnClickListener(onButtonListener);
        findViewById(R.id.submit).setOnClickListener(onClickListener);
    }
    // Set on click listener for each button
    View.OnClickListener onButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Set text view to get the difficulty data from the user
            TextView textView = (TextView)findViewById(R.id.difCourse);
            // Convert the string as a integer
            int var = Integer.parseInt(textView.getText().toString());
            // Based on the difficulty, update the UI and represent the user's choice
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

    // Set on click listener for submit button
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Get new data from each edit text typed by the user
            String gpaString = ((EditText)findViewById(R.id.gpaGrades)).getText().toString();
            String difString = ((TextView)findViewById(R.id.difCourse)).getText().toString();
            String professor = ((EditText)findViewById(R.id.profName)).getText().toString();
            // If either GPA or difficulty is empty, toast the error message
            // Else, start rating the new data
            if (gpaString.isEmpty() || difString.isEmpty()) {
                startToast("Gpa and difficulty must be typed");
            } else {
                rateGpa(gpaString, difString, professor);
            }
        }
    };

    // Rate the course based on the user typed input
    private void rateGpa(String gpaString, String difString, String professor) {
        // Initialize the base data to store
        double gpa = 0.0;
        int difficulty = 0;
        // Try rather the data is valid or not
        try {
            gpa = Double.parseDouble(gpaString);
            difficulty = Integer.parseInt(difString);
        } catch (NumberFormatException e) {
            // Toast error message if the data is not valid
            startToast("Gpa and difficulty must be number.");
        }
        // Check rather the numerical data is in the right range
        if (Double.compare(gpa, 0.0) > 0 && difficulty > 0) {
            if (Double.compare(gpa, 4.0) > 0 || difficulty > 5) {
                startToast("gpa is less than or equal to 4.0");
            } else {
                // If the professor is empty, intent without any professor,
                // Else, intent with a professor
                // Calculate the lively update the new data for either case
                if (professor.isEmpty()){
                    getData(gpa, difficulty, "");
                } else {
                    getData(gpa, difficulty, professor);
                }
                detailStartActivity(GradeResultActivity.class);
            }
        }
    }

    // Toast with the given string message
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // Calculate and lively update the Firebase database
    private void getData(double gpa, int rate, final String professor) {
        // Initialize the base data to store
        final double currentGpa = gpa;
        final int currentRate = rate;
        // Set a Firebase live listener to lively update the data and the previous activity's UI
        DocumentReference dRef = FirebaseFirestore.getInstance().collection("courses").document(className.get("courseName"));
        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // If the task is successful, proceed to the next step
                if (task.isSuccessful()) {
                    // Set the document with the task result
                    DocumentSnapshot doc = task.getResult();
                    // If the document exists, store the data
                    if (doc.exists()) {
                        // Set a map to store the data
                        Map<String, Object> temp = doc.getData();
                        // Set each data with the appropriate text
                        double averageGpa = (double) temp.get("averageGpa");
                        double averageRating = (double) temp.get("rating");
                        double reported = (double) temp.get("reported");
                        // Calculate the data based on the current data and the new data
                        double avg = (averageGpa * reported + currentGpa) / (reported + 1);
                        double rating = (averageRating * reported + currentRate) / (reported + 1);
                        // Update the Firebase database
                        DocumentReference updateRef = FirebaseFirestore.getInstance().collection("courses").document(className.get("courseName"));
                        updateRef.update("averageGpa", avg);
                        updateRef.update("rating", rating);
                        updateRef.update("reported", reported+1);
                        if (temp.containsKey("professors")) {
                            // If the professor exists, update professor data
                            if (!professor.isEmpty()) {
                                List<String> professors = (ArrayList<String>) temp.get("professors");
                                professors.add(professor);
                                updateRef.update("professors", professors);
                            }
                        }
                    }
                }
            }
        });
    }
    private void detailStartActivity(Class c) {
        finish();
    }
}