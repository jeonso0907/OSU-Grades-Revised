package edu.osu.hack.OSUGrades;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    // Set Firebase Auth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Set buttons with click listeners
        findViewById(R.id.loginBtn).setOnClickListener(onClickListener);
        findViewById(R.id.signupBtn).setOnClickListener(onClickListener);
    }

    // When back button pressed, eliminate the current login activity and exit the app
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        finish();
    }

    // Set on lick listener for each button
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.loginBtn:
                    logIn();
                    break;
                case R.id.signupBtn:
                    signUp();
                    break;
            }
        }
    };

    // Intent to the sign up activity
    private void signUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }


    private void logIn() {
        // Automatically add the OSU email address at the end of the typed name.# and get it as a string
        String email = ((EditText)findViewById(R.id.textEmailAddress)).getText().toString();
        email += "@osu.edu";
        // Get the password as a string
        String password = ((EditText)findViewById(R.id.textPassword)).getText().toString();
        // If either email or password is empty, toast the error message
        // Else, check the email and password with the Firebase Auth
        if (email.isEmpty() || password.isEmpty()) {
            startToast("email or password is not correct");
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // If the user data is valid, check rather the email is verified or not
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        // If the user email is verified, toast the login success message and start
                        // the course list activity, else, toast the message to verify the email
                        if (user.isEmailVerified()) {
                            startToast("Login Success");
                            listStartActivity(CourseListActivity.class);
                        } else {
                            startToast("Please verify your email before log in");
                        }
                    } else {
                        // If the user data is not valid, toast the error message
                        if (task.getException() != null) {
                            startToast("Incorrect Password or incorrect ID");
                        }
                    }
                }
            });
        }
    }

    // Toast the given string
    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // Intent to the course list activity
    private void listStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}