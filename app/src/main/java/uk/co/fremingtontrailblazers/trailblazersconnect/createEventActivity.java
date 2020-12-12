package uk.co.fremingtontrailblazers.trailblazersconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.applandeo.materialcalendarview.CalendarView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.Document;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.VISIBLE;

public class createEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String TITLE_KEY = "Title";
    private static final String DETAILS_KEY = "Details";
    private static final String LOCATION_KEY = "Location";
    private static final String DATE_KEY = "Date";
    private static final String GROUPS_KEY = "Groups";
    private int eventDateYear = 0;
    private int eventDateMonth = 0;
    private int eventDateDay = 0;
    private int eventHour = 0;
    private int eventMinute = 0;
    Button mSaveEventButton;                                //creates variables for each UI element
    EditText mEventDetailsView;
    EditText mEventTitleView;
    EditText mEventLocationView;
    CalendarView mDatePicker;
    CheckBox mCheckBoxRed;
    CheckBox mCheckBoxOrange;
    CheckBox mCheckBoxYellow;
    CheckBox mCheckBoxGreen;
    CheckBox mCheckBoxBlue;
    CheckBox mCheckBoxIndigo;
    CheckBox mCheckBoxViolet;
    Button mSelectDateButton;
    Button mSelectTimeButton;
    TextView mDisplayDate;
    TextView mDisplayTime;
    private CollectionReference mColRef = FirebaseFirestore.getInstance().collection("events");     //sets the cloud firestore collection to be the "events" collection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        mCheckBoxRed = findViewById(R.id.checkBoxRed);              //Assigning the UI elements to each variable
        mCheckBoxOrange = findViewById(R.id.checkBoxOrange);
        mCheckBoxYellow = findViewById(R.id.checkBoxYellow);
        mCheckBoxGreen = findViewById(R.id.checkBoxGreen);
        mCheckBoxBlue = findViewById(R.id.checkBoxBlue);
        mCheckBoxIndigo = findViewById(R.id.checkBoxIndigo);
        mCheckBoxViolet = findViewById(R.id.checkBoxViolet);
        mDatePicker = findViewById(R.id.eventCalendar);
        mEventDetailsView = findViewById(R.id.eventText);
        mEventTitleView = findViewById(R.id.eventTitle);
        mEventLocationView = findViewById(R.id.eventLocation);
        mSaveEventButton = findViewById(R.id.saveEventButton);
        mSelectDateButton = findViewById(R.id.selectDateButton);
        mSelectTimeButton = findViewById(R.id.selectTimeButton);
        mDisplayDate = findViewById(R.id.displayDate);
        mDisplayTime = findViewById(R.id.displayTime);
        mSelectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");

            }
        });

        mSelectTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
        mSaveEventButton.setOnClickListener(new View.OnClickListener() {            //When the SaveEventButton is pressed, it plays the animation and calls the createEvent function
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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.eventDateYear = year;
        this.eventDateMonth = month;
        this.eventDateDay = dayOfMonth;
        Calendar datePickerCalendar = Calendar.getInstance();
        datePickerCalendar.set(Calendar.YEAR, year);
        datePickerCalendar.set(Calendar.MONTH, month);
        datePickerCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance().format(datePickerCalendar.getTime());
        String dateToDisplay = "Selected Date: " + currentDateString;
        mDisplayDate.setText(dateToDisplay);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.eventHour = hourOfDay;
        this.eventMinute = minute;
        if (String.valueOf(minute).length() == 1) {
            String strMinute = "0" + minute;
            String timeToDisplay = "Selected Time: " + hourOfDay + ":" + strMinute;
            mDisplayTime.setText(timeToDisplay);
        } else {
            String timeToDisplay = "Selected Time: " + hourOfDay + ":" + minute;
            mDisplayTime.setText(timeToDisplay);
        }
    }

    private void createEvent() {
        List<String> groupsList = new ArrayList<String>();                  //creates new array to store the list of groups for the event
//        Calendar eventDate =  mDatePicker.getFirstSelectedDate();      //gets the data that is selected on the calendar by the user
//        Log.i("eventDate", String.valueOf(eventDate));              //outputs the date, month and year for debugging purposes
//        Log.i("eventDateYear", String.valueOf(eventDate.get(1)));
//        Log.i("eventDateMonth", String.valueOf((eventDate.get(2))+1));
//        Log.i("eventDateYear", String.valueOf(eventDate.get(5)));
//
//        Calendar myCalendar = new GregorianCalendar(eventDate.get(1), eventDate.get(2), eventDate.get(5));    //creates a Date data type and passes it the date, month and year of the event

        Calendar myCalendar = new GregorianCalendar(eventDateYear, eventDateMonth, eventDateDay, eventHour, eventMinute);    //creates a Date data type and passes it the date, month and year of the event

        Date finalEventDate = myCalendar.getTime();
        Log.i("final date", String.valueOf(finalEventDate));


        if(mCheckBoxRed.isChecked()){                   //checks each checkbox to see if it is checked, if it is it adds the colour to groupsList and outputs the colour for debugging purposes
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

        String eventTitle = mEventTitleView.getText().toString().trim();    //gets the text typed in the title box and saves it as a string
        String eventDetails = mEventDetailsView.getText().toString().trim();    //gets the text typed in the details box and saves it as a string
        String eventLocation = mEventLocationView.getText().toString().trim();    //gets the text typed in the location box and saves it as a string
//        Log.i("details", eventDetails);

        Map<String, Object> eventToSave = new HashMap<String, Object>();                    //creates a hashMap that stores the event data before it is saved to cloud firestore
        eventToSave.put(TITLE_KEY, eventTitle);                            //adds all the required data to the hashMap
        eventToSave.put(DETAILS_KEY, eventDetails);
        eventToSave.put(LOCATION_KEY, eventLocation);
        eventToSave.put(DATE_KEY, finalEventDate);
        eventToSave.put(GROUPS_KEY, groupsList);
        mColRef.add(eventToSave).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {      //saves the data to firestore and checks if the request is successful
            @Override
            public void onSuccess(DocumentReference documentReference) {                    //if the data is successfully saved to firestore, it creates a debug message and takes the user back to the MainActivity
                Log.d("success", "Document has been saved!");
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {                   //if the data is not successfully added to firestore, an error message is added to the Logcat
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("ERROR", "Document was not saved!");
            }
        });


    }

}