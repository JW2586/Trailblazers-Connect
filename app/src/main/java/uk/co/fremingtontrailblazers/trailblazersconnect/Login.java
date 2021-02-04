package uk.co.fremingtontrailblazers.trailblazersconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    EditText mEmail, mPassword;         //creates variables for each UI element
    Button mLoginBtn;
    TextView mCreateAccountText;
    TextView mForgotPassword;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.TextEmailAddress);   //assigns the UI elements to each variable
        mPassword = findViewById(R.id.TextPassword);
        fAuth = FirebaseAuth.getInstance();
        mLoginBtn = findViewById(R.id.loginButton);
        mCreateAccountText = findViewById(R.id.createAccountText);
        mForgotPassword = findViewById(R.id.forgotPasswordText);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginBtn.setBackgroundResource(R.drawable.login_button_pressed);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLoginBtn.setBackgroundResource(R.drawable.login_button);
                    }
                }, 100);
                String email = mEmail.getText().toString().trim();          //gets the email and password from the text input boxes and converts them to strings
                String password = mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){                               //if the email field is left empty, it sends an error to the user
                    mEmail.setError("An email address is needed");
                    return;
                }

                if (TextUtils.isEmpty(password)){                           //if the password field is left empty, it sends an error to the user
                    mPassword.setError("A password is needed");
                    return;
                }

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {  //starts the FirebaseAuth sign in process
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));      //sends user to MainActivity if they have successfully signed in
                            finish();
                        }else{
                            Toast.makeText(Login.this, "An error has occurred, " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mCreateAccountText.setOnClickListener(new View.OnClickListener() {      //user presses this if they do not yet have an account
            @Override
            public void onClick(View v) {
                mCreateAccountText.setTextColor(getResources().getColor(R.color.TbDarkOrange));
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCreateAccountText.setTextColor(getResources().getColor(R.color.TrailBlazersOrange));
                    }
                }, 100);
                startActivity(new Intent(getApplicationContext(),Register.class));    //sends the user to Register activity if they have not yet created an account
            }
        });

        mForgotPassword.setOnClickListener(new View.OnClickListener() {     //user presses this if they have forgotten their password
            @Override
            public void onClick(View v) {
                EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());      //creates a dialog box allowing the user to reset their password
                passwordResetDialog.setTitle("Reset Password?");
                passwordResetDialog.setMessage("Enter your Email Address to receive a link to reset your password:");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = resetMail.getText().toString();
                        fAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {    //starts the FirebaseAuth procces to reset the user's password
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Login.this, "A Password reset link has been sent to your email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this, "Error! Password reset link has not been sent: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                passwordResetDialog.create().show();
            }
        });

    }
}