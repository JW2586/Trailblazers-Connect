package uk.co.fremingtontrailblazers.trailblazersconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button mLogoutBtn;
    Button mCreateButton;
    Button mDisplayEventsButton;
    CalendarView mCalendarView;

    private CollectionReference mColRef = FirebaseFirestore.getInstance().collection("events");     //sets the cloud firestore collection to be the "events" collection
    private void displayEvents() {
//        LayerDrawable layerDrawable = (LayerDrawable) getResources().getDrawable(R.drawable.multiple_dots);
//        Drawable replace = (Drawable) getResources().getDrawable(R.drawable.dot_red);
//        layerDrawable.setDrawableByLayerId(R.id.dot1, replace);
//        List<EventDay> events = new ArrayList<>();
//        Calendar calendar = Calendar.getInstance();
//        events.add(new EventDay(calendar, R.drawable.multiple_dots));
//        CalendarView calendarView = (CalendarView) findViewById(R.id.eventCalendar);
//        calendarView.setEvents(events);
        mColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Log.d("document:", document.getId() + " => " + document.get("Groups"));

                        List<Object> groupsList = new ArrayList<>();
                        groupsList = (List<Object>) document.get("Groups");
                        LayerDrawable layerDrawable = (LayerDrawable) getResources().getDrawable(R.drawable.multiple_dots);
                        //layerDrawable.mutate();

//                        Log.d("groups of:", document.getId() + " => " + groupsList.toString());
//                        Log.d("size:", String.valueOf(groupsList.size()));
                        Log.d("document:", document.getId() + " => " + document.get("Groups"));
                        for (int i=0; i<groupsList.size(); i++){
//                            Log.d("group", groupsList.get(i).toString());
                            if (groupsList.get(i).toString().equals("Red")) {
                                Log.d("message", "there is a red in here");
                                Drawable replaceRed = (Drawable) getResources().getDrawable(R.drawable.dot_red);
                                layerDrawable.setDrawableByLayerId(R.id.dot1, replaceRed);
                            }
                            if (groupsList.get(i).toString().equals("Orange")) {
                                Log.d("message", "there is a orange in here");
                            }
                            if (groupsList.get(i).toString().equals("Yellow")) {
                                Log.d("message", "there is a yellow in here");
                            }
                            if (groupsList.get(i).toString().equals("Green")) {
                                Log.d("message", "there is a green in here");
                            }
                            if (groupsList.get(i).toString().equals("Blue")) {
                                Log.d("message", "there is a blue in here");
                            }
                            if (groupsList.get(i).toString().equals("Indigo")) {
                                Log.d("message", "there is a indigo in here");
                            }
                            if (groupsList.get(i).toString().equals("Violet")) {
                                Log.d("message", "there is a violet in here");
                            }


                        }
                        List<EventDay> events = new ArrayList<>();
                        Log.d("Events before", events.toString());
                        Calendar calendar = Calendar.getInstance();
                        events.add(new EventDay(calendar, R.drawable.multiple_dots));
                        Log.d("Events after", events.toString());
                        CalendarView calendarView = (CalendarView) findViewById(R.id.eventCalendar);
                        calendarView.setEvents(events);
                    }
                } else {
                    Log.d("error", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLogoutBtn = findViewById(R.id.logoutButton);
        mCreateButton = findViewById(R.id.createButton);
//        mCalendarView = findViewById(R.id.eventCalendar);
        mDisplayEventsButton = findViewById(R.id.displayEventsButton);

        LayerDrawable layerDrawable = (LayerDrawable) getResources().getDrawable(R.drawable.multiple_dots);
        Drawable replacegreen = (Drawable) getResources().getDrawable(R.drawable.dot_green);
        layerDrawable.setDrawableByLayerId(R.id.dot4, replacegreen);
        Drawable replace = (Drawable) getResources().getDrawable(R.drawable.dot_orange);
        layerDrawable.setDrawableByLayerId(R.id.dot2, replace);
        List<EventDay> events = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        events.add(new EventDay(calendar, R.drawable.multiple_dots));
        CalendarView calendarView = (CalendarView) findViewById(R.id.eventCalendar);
        calendarView.setEvents(events);

        mDisplayEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayEvents();
            }
        });
        //displayEvents();
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