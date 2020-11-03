package uk.co.fremingtontrailblazers.trailblazersconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.applandeo.materialcalendarview.CalendarView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.Document;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class createEventActivity extends AppCompatActivity {
    private static final String DETAILS_KEY = "Details";
    private static final String DATE_KEY = "Date";
    private static final String GROUPS_KEY = "Groups";
    Button mSaveEventButton;
    EditText mEventDetailsView;
    CalendarView mDatePicker;
    CheckBox mCheckBoxRed;
    CheckBox mCheckBoxOrange;
    CheckBox mCheckBoxYellow;
    CheckBox mCheckBoxGreen;
    CheckBox mCheckBoxBlue;
    CheckBox mCheckBoxIndigo;
    CheckBox mCheckBoxViolet;

    private CollectionReference mColRef = FirebaseFirestore.getInstance().collection("events");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        mCheckBoxRed = findViewById(R.id.checkBoxRed);
        mCheckBoxOrange = findViewById(R.id.checkBoxOrange);
        mCheckBoxYellow = findViewById(R.id.checkBoxYellow);
        mCheckBoxGreen = findViewById(R.id.checkBoxGreen);
        mCheckBoxBlue = findViewById(R.id.checkBoxBlue);
        mCheckBoxIndigo = findViewById(R.id.checkBoxIndigo);
        mCheckBoxViolet = findViewById(R.id.checkBoxViolet);

        mSaveEventButton = findViewById(R.id.saveEventButton);

        mSaveEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSaveEventButton.setBackgroundResource(R.drawable.save_event_button_pressed);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSaveEventButton.setBackgroundResource(R.drawable.save_event_button);
                    }
                }, 100);

                createEvent();


            }
        });
    }

    private void createEvent() {
        List<String> groupsList = new ArrayList<String>();

        mDatePicker = findViewById(R.id.eventCalendar);
        mEventDetailsView = findViewById(R.id.eventText);

        Calendar eventDate =  mDatePicker.getFirstSelectedDate();
        Log.i("eventDate", String.valueOf(eventDate));
        Log.i("eventDateYear", String.valueOf(eventDate.get(1)));
        Log.i("eventDateMonth", String.valueOf((eventDate.get(2))+1));
        Log.i("eventDateYear", String.valueOf(eventDate.get(5)));

        Calendar myCalendar = new GregorianCalendar(eventDate.get(1), eventDate.get(2), eventDate.get(5));
        Date finalEventDate = myCalendar.getTime();
        Log.i("final date", String.valueOf(finalEventDate));


        if(mCheckBoxRed.isChecked()){
            groupsList.add("Red");
            Log.i("tag", "Red is added");
            Log.i("list", groupsList.toString());
        }
        if(mCheckBoxOrange.isChecked()){
            groupsList.add("Orange");
            Log.i("tag", "Orange is added");
            Log.i("list", groupsList.toString());
        }
        if(mCheckBoxYellow.isChecked()){
            groupsList.add("Yellow");
            Log.i("tag", "Yellow is added");
            Log.i("list", groupsList.toString());
        }
        if(mCheckBoxGreen.isChecked()){
            groupsList.add("Green");
            Log.i("tag", "Green is added");
            Log.i("list", groupsList.toString());
        }
        if(mCheckBoxBlue.isChecked()){
            groupsList.add("Blue");
            Log.i("tag", "Blue is added");
            Log.i("list", groupsList.toString());
        }
        if(mCheckBoxIndigo.isChecked()){
            groupsList.add("Indigo");
            Log.i("tag", "Indigo is added");
            Log.i("list", groupsList.toString());
        }
        if(mCheckBoxViolet.isChecked()){
            groupsList.add("Violet");
            Log.i("tag", "Violet is added");
            Log.i("list", groupsList.toString());
        }

        String eventDetails = mEventDetailsView.getText().toString().trim();
        Log.i("details", eventDetails);

        Map<String, Object> eventToSave = new HashMap<String, Object>();
        eventToSave.put(DETAILS_KEY, eventDetails);
        eventToSave.put(DATE_KEY, finalEventDate);
        eventToSave.put(GROUPS_KEY, groupsList);
        mColRef.add(eventToSave).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("success", "Document has been saved!");
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("ERROR", "Document was not saved!");
            }
        });


    }
}