package uk.co.fremingtontrailblazers.trailblazersconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VerifyEmailActivity extends AppCompatActivity {
    Button mResendButton;   //creates variables for each UI element
    Button mEnterButton;
    FirebaseAuth fAuth;
    FirebaseUser user;
    String userID;
    private CollectionReference mColRef = FirebaseFirestore.getInstance().collection("users");     //sets the cloud firestore collection to be the "users" collection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        mResendButton = findViewById(R.id.resendButton);    //assigning the UI elements to each variable
        mEnterButton = findViewById(R.id.enterButton);

        fAuth = FirebaseAuth.getInstance(); //gets the current FirebaseAuth instance
        userID = fAuth.getCurrentUser().getUid();   //gets the current user and their userID
        user = fAuth.getCurrentUser();
        String fullName = getIntent().getStringExtra("User Name");  //receives the user's full name and email address from the Register activity
        String email = getIntent().getStringExtra("Email");

        mResendButton.setOnClickListener(new View.OnClickListener() {     //if the user has not received a verification email, they click this button
            @Override
            public void onClick(final View v) {
                mResendButton.setBackgroundResource(R.drawable.resend_email_button_pressed);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mResendButton.setBackgroundResource(R.drawable.resend_email_button);
                    }
                }, 100);
                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {   //re-sends the verification email to the user
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(VerifyEmailActivity.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            }
        });

        mEnterButton.setOnClickListener(new View.OnClickListener() {    //user clicks this when they have verified their email address
            @Override
            public void onClick(View v) {
                mEnterButton.setBackgroundResource(R.drawable.enter_app_button_pressed);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mEnterButton.setBackgroundResource(R.drawable.enter_app_button);
                    }
                }, 100);
                user = fAuth.getCurrentUser();  //gets the current user
                user.reload();      //reloads the user
                user = fAuth.getCurrentUser();  //gets the current user again to ensure that it is correct

                //***the program waits for 2 seconds to ensure that the user has been successfully reloaded***
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(user.isEmailVerified()){     //if the user's email address has been verified in Firebase
                            Toast.makeText(VerifyEmailActivity.this, "Email successfully verified", Toast.LENGTH_SHORT).show();
                            userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();   //gets the current user's ID for the title of the created document
                            Map<String, Object> userData = new HashMap<>();     //creates a new HashMap to store the userData for FireStore
                            userData.put("Full Name", fullName);    //adds the user's name and email address to userData
                            userData.put("Email", email);
                            userData.put("Role", "Runner"); //sets the user's role as a "Runner" by default
                            mColRef.document(userID).set(userData).addOnSuccessListener(new OnSuccessListener<Void>() { //adds the userData to a document in FireStore with the title of the user's ID
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            }).addOnFailureListener(new OnFailureListener() {                   //if the data is not successfully added to firestore, an error message is added to the Logcat
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));  //sends the user to MainActivity
                        }else{
                            Toast.makeText(VerifyEmailActivity.this, "Your Email has not been verified", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 2000);
            }
        });
    }
}