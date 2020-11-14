package edu.osu.hack.OSUGrades;

import android.os.Bundle;
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

        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("courses").document("");
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

                        if ( temp.containsKey("professorName")) {
                            infoTemp = new ClassInfo((String) temp.get("course"), (float) temp.get("averageGPA"), (String) temp.get("professorName"), (double) temp.get("rating"), (int) temp.get("reported"));
                        }
                        else {
                            infoTemp = new ClassInfo((String) temp.get("course"), (float) temp.get("averageGPA"), (double) temp.get("rating"), (int) temp.get("reported"));
                        }

                        if ( infoTemp.getProfessorName().contains(professorName)) {
                            temp.clear();
                            temp.put("course", infoTemp.getCourseID());
                            temp.put("averageGPA", (infoTemp.getGPA() + addGPA) / infoTemp.getReported());
                            temp.put("rating", (infoTemp.getRate() + addRate) / infoTemp.getReported());
                            temp.put("reported", infoTemp.getReported() + 1);
                        }
                        else {
                            temp.clear();
                            temp.put("course", infoTemp.getCourseID());
                            temp.put("averageGPA", (infoTemp.getGPA() + addGPA) / infoTemp.getReported());
                            temp.put("rating", (infoTemp.getRate() + addRate) / infoTemp.getReported());
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