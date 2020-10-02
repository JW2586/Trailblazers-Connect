package uk.co.fremingtontrailblazers.trailblazersconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    Button mLogoutBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLogoutBtn = findViewById(R.id.logoutButton);
        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLogoutBtn.setBackgroundResource(R.drawable.logout_button_pressed);     //changes the look of the logout button when it is pressed

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLogoutBtn.setBackgroundResource(R.drawable.logout_button);
                    }
                }, 100);
                FirebaseAuth.getInstance().signOut();                                   //signs the user out of the app
                startActivity(new Intent(getApplicationContext(), Login.class));        //takes the user back to the login page
            }
        });
    }
}