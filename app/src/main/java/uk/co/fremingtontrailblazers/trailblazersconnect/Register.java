package uk.co.fremingtontrailblazers.trailblazersconnect;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Register extends AppCompatActivity {
    EditText mFullName, mEmail, mPassword;              //creates variables for all of the required data
    Button mRegisterBtn;
    TextView mLoginHereText;
    FirebaseAuth fAuth;
    private CollectionReference mColRef = FirebaseFirestore.getInstance().collection("users");     //sets the cloud firestore collection to be the "events" collection


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullName = findViewById(R.id.fullName);        //connects the variables to their associated element in the activity_register.xml
        mEmail = findViewById(R.id.TextEmailAddress);
        mPassword = findViewById(R.id.TextPassword);
        mRegisterBtn = findViewById(R.id.registerButton);
        mLoginHereText = findViewById(R.id.loginHereText);

        fAuth = FirebaseAuth.getInstance();     //gets the current FirebaseAuth instance

        if(fAuth.getCurrentUser() != null) {        //checks to see if user is already logged in
            startActivity(new Intent(getApplicationContext(),MainActivity.class));  //if the user is logged in, they are sent to MainActivity
            finish();
        }

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {    //when the user clicks on the Register button
            @Override
            public void onClick(View v) {
                mRegisterBtn.setBackgroundResource(R.drawable.register_button_pressed);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRegisterBtn.setBackgroundResource(R.drawable.register_button);
                    }
                }, 100);
                String email = mEmail.getText().toString().trim();          //gets the email, password and full name from the text input boxes and converts them to strings
                String password = mPassword.getText().toString().trim();
                String fullName = mFullName.getText().toString();

                if(TextUtils.isEmpty(email)){          //if the email field is left empty, it sends an error to the user
                    mEmail.setError("An email address is needed");
                    return;
                }

                if (TextUtils.isEmpty(password)){      //if the password field is left empty, it sends an error to the user
                    mPassword.setError("A password is needed");
                    return;
                }

                if(password.length() < 8){             //this makes sure that the user enters a password that is 8 characters or more
                    mPassword.setError("Your password must be at least 8 characters");
                    return;
                }

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {     //starts the user creation process in FirebaseAuth
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){                       //if the account is successfully created
                            FirebaseUser currentUser = fAuth.getCurrentUser();  //gets the current user
                            currentUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {    //sends a verification email to the user's inputted email address
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Register.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
                            Toast.makeText(Register.this, "Account Created", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), VerifyEmailActivity.class);     //creates a new Intent to start VerifiyEmailActivity
                            intent.putExtra("User Name", fullName); //passes the user's full name and email address to VerifyEmailActivity
                            intent.putExtra("Email", email);
                            startActivity(intent);  //starts VerifyEmailActivity
                            finish();
                        }else {   //if an error occurs, a message is displayed
                            Toast.makeText(Register.this, "An error has occurred, " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mLoginHereText.setOnClickListener(new View.OnClickListener() {  //if the user has already created an account, they should press the "login here" text
            @Override
            public void onClick(View v) {
                mLoginHereText.setTextColor(getResources().getColor(R.color.TbDarkOrange));
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLoginHereText.setTextColor(getResources().getColor(R.color.TrailBlazersOrange));
                    }
                }, 100);
                startActivity(new Intent(getApplicationContext(),Login.class)); //sends the user to the Login activity
                finish();
            }
        });
    }
}