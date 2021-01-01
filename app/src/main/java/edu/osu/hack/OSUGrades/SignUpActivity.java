package edu.osu.hack.OSUGrades;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    // Set Firebase Auth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Set button with click listener
        findViewById(R.id.createBtn).setOnClickListener(onClickListener);

    }

    // When back button pressed, eliminate the current login activity and exit the app
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        finish();
    }

    // Set on lick listener for a button
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.createBtn:
                    signUp();
                    break;
            }
        }
    };

    // Start the sign up activity
    private void signUp() {
        // Automatically add the OSU email address at the end of the typed name.# and get it as a string
        String email = ((EditText) findViewById(R.id.textEmailAddress2)).getText().toString();
        email += "@osu.edu";
        // Get the password as a string
        String password = ((EditText) findViewById(R.id.textPassword2)).getText().toString();
        // Get the confirm password as a string
        String confirmPw = ((EditText) findViewById(R.id.textConfirmPassword)).getText().toString();
        // If either email, password, or confirm password is empty, toast the error message
        if (email.isEmpty() || password.isEmpty() || confirmPw.isEmpty()) {
            startToast("Email or Password is empty.");
        } else {
            // Check the email is a OSU domain email address
            if (email.endsWith("osu.edu")) {
                // Check that the password is more than or equal to 8
                if (password.length() < 8) {
                    startToast("Password must be longer than 7");
                } else {
                    if (password.equals(confirmPw)) {
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information



                                            // E-mail Confirmation
                                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        afterSignUpAlertMsg();
                                                    } else {
                                                        startToast("Registration failed");
                                                    }
                                                }
                                            });

                                            // UI
                                        } else {
                                            // If sign in fails, display a message to the user.

                                            // UI
                                        }
                                    }
                                });
                    } else {
                        startToast("Password does not match");
                    }
                }
            } else {
                // Toast the error message for not using a OSU domain address
                // Make sure only OSU students are logged in
                startToast("E-mail domain must be osu.edu");
            }
        }

    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void afterSignUpAlertMsg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please check your OSU email")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        startLoginActivity();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}