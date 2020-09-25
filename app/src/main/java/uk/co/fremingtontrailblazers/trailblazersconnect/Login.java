package uk.co.fremingtontrailblazers.trailblazersconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    EditText mEmail, mPassword;
    Button mLoginBtn;
    TextView mCreateAccountText;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.TextEmailAddress);
        mPassword = findViewById(R.id.TextPassword);
        fAuth = FirebaseAuth.getInstance();
        mLoginBtn = findViewById(R.id.loginButton);
        mCreateAccountText = findViewById(R.id.createAccountText);

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

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else{
                            Toast.makeText(Login.this, "An error has occurred, " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mCreateAccountText.setOnClickListener(new View.OnClickListener() {
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
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });

    }
}