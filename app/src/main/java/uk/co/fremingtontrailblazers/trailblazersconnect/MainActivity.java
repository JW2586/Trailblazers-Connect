package uk.co.fremingtontrailblazers.trailblazersconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.applandeo.materialcalendarview.CalendarView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    Button mLogoutBtn;
    Button mCreateButton;
    CalendarView mCalendarView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLogoutBtn = findViewById(R.id.logoutButton);
        mCreateButton = findViewById(R.id.createButton);
        mCalendarView = findViewById(R.id.eventCalendar);

        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLogoutBtn.setBackgroundResource(R.drawable.logout_button_pressed);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLogoutBtn.setBackgroundResource(R.drawable.logout_button);
                    }
                }, 100);
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCreateButton.setBackgroundResource(R.drawable.create_button_pressed);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCreateButton.setBackgroundResource(R.drawable.create_button);
                        Log.i("tag", "create button pressed");
                    }
                }, 100);
                startActivity(new Intent(getApplicationContext(), createEventActivity.class));
            }
        });
    }
}