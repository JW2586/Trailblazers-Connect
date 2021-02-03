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

    Button mResendButton;
    Button mEnterButton;
    FirebaseAuth fAuth;
    FirebaseUser user;
    String userID;
    private CollectionReference mColRef = FirebaseFirestore.getInstance().collection("users");     //sets the cloud firestore collection to be the "events" collection


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        mResendButton = findViewById(R.id.resendButton);
        mEnterButton = findViewById(R.id.enterButton);
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();
        String fullName = getIntent().getStringExtra("User Name");
        String email = getIntent().getStringExtra("Email");
        Log.d("CURRENT USER", String.valueOf(userID));

        mResendButton.setOnClickListener(new View.OnClickListener() {
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
                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(VerifyEmailActivity.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("tag", "onFailure: Email not sent " + e.getMessage());
                    }
                });
            }
        });

        mEnterButton.setOnClickListener(new View.OnClickListener() {
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
                user = fAuth.getCurrentUser();
                user.reload();
                user = fAuth.getCurrentUser();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TAG", String.valueOf(user.isEmailVerified()));
                        if(user.isEmailVerified()){
                            Toast.makeText(VerifyEmailActivity.this, "Email successfully verified", Toast.LENGTH_SHORT).show();
                            userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("Full Name", fullName);
                            userData.put("Email", email);
                            userData.put("Role", "Runner");
                            mColRef.document(userID).set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("success", "Document has been saved!");
                                }
                            }).addOnFailureListener(new OnFailureListener() {                   //if the data is not successfully added to firestore, an error message is added to the Logcat
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("ERROR", "Document was not saved!");
                                }
                            });
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else{
                            Toast.makeText(VerifyEmailActivity.this, "Your Email has not been verified", Toast.LENGTH_SHORT).show();
                            Log.d("CURRENT USER", String.valueOf(userID));
                        }
                    }
                }, 2000);

            }
        });
    }
}