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
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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
    private static final String TITLE_KEY = "Title";        //sets up constants for use in FireStore
    private static final String DETAILS_KEY = "Details";
    private static final String LOCATION_KEY = "Location";
    private static final String DATE_KEY = "Date";
    private static final String GROUPS_KEY = "Groups";
    private int eventDateYear = 0;
    private int eventDateMonth = 0;
    private int eventDateDay = 0;
    private int eventHour = 0;
    private int eventMinute = 0;
    public boolean eventCanBeMade = false;                  //controls whether or not an event can be created
    Button mSaveEventButton;                                //creates variables for each UI element
    EditText mEventDetailsView;
    EditText mEventTitleView;
    EditText mEventLocationView;
    CheckBox mCheckBoxRed;
    CheckBox mCheckBoxOrange;
    CheckBox mCheckBoxYellow;
    CheckBox mCheckBoxGreen;
    CheckBox mCheckBoxBlue;
    CheckBox mCheckBoxIndigo;
    CheckBox mCheckBoxViolet;
    Button mSelectDateButton;
    Button mSelectTimeButton;
    Button mBackButton;
    TextView mDisplayDate;
    TextView mDisplayTime;
    private CollectionReference mColRef = FirebaseFirestore.getInstance().collection("events");     //sets the cloud firestore collection to be the "events" collection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        mCheckBoxRed = findViewById(R.id.checkBoxRed);              //assigning the UI elements to each variable
        mCheckBoxOrange = findViewById(R.id.checkBoxOrange);
        mCheckBoxYellow = findViewById(R.id.checkBoxYellow);
        mCheckBoxGreen = findViewById(R.id.checkBoxGreen);
        mCheckBoxBlue = findViewById(R.id.checkBoxBlue);
        mCheckBoxIndigo = findViewById(R.id.checkBoxIndigo);
        mCheckBoxViolet = findViewById(R.id.checkBoxViolet);
        mEventDetailsView = findViewById(R.id.eventText);
        mEventTitleView = findViewById(R.id.eventTitle);
        mEventLocationView = findViewById(R.id.eventLocation);
        mSaveEventButton = findViewById(R.id.saveEventButton);
        mSelectDateButton = findViewById(R.id.selectDateButton);
        mSelectTimeButton = findViewById(R.id.selectTimeButton);
        mDisplayDate = findViewById(R.id.displayDate);
        mDisplayTime = findViewById(R.id.displayTime);
        mBackButton = findViewById(R.id.saveBackButton);

        mBackButton.setOnClickListener(new View.OnClickListener() {         //back button to return to MainActivity
            @Override
            public void onClick(View v) {
                mBackButton.setBackgroundResource(R.drawable.back_button_pressed);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBackButton.setBackgroundResource(R.drawable.back_button);
                    }
                }, 100);
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });
        mSelectDateButton.setOnClickListener(new View.OnClickListener() {       //button to select the date for the event being created
            @Override
            public void onClick(View v) {
                mSelectDateButton.setBackgroundResource(R.drawable.select_date_button_pressed);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSelectDateButton.setBackgroundResource(R.drawable.select_date_button);
                    }
                }, 100);
                DialogFragment datePicker = new DatePickerFragment();       //creates and displays a new DatePickerFragment to select the date
                datePicker.show(getSupportFragmentManager(), "date picker");

            }
        });

        mSelectTimeButton.setOnClickListener(new View.OnClickListener() {      //button to select the time for the event being created
            @Override
            public void onClick(View v) {
                mSelectTimeButton.setBackgroundResource(R.drawable.select_time_button_pressed);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSelectTimeButton.setBackgroundResource(R.drawable.select_time_button);
                    }
                }, 100);
                DialogFragment timePicker = new TimePickerFragment();       //creates and displays a TimePickerFragment to select the time
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
        mSaveEventButton.setOnClickListener(new View.OnClickListener() {            //when the SaveEventButton is pressed, it calls the createEvent function
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
                createEvent();      //createEvent is called to add the event to the database
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {                    //gets the date from the DatePicker
        this.eventDateYear = year;
        this.eventDateMonth = month;
        this.eventDateDay = dayOfMonth;
        Calendar datePickerCalendar = Calendar.getInstance();
        datePickerCalendar.set(Calendar.YEAR, year);
        datePickerCalendar.set(Calendar.MONTH, month);
        datePickerCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance().format(datePickerCalendar.getTime());
        String dateToDisplay = "Selected Date: " + currentDateString;
        mDisplayDate.setText(dateToDisplay);    //displays the selected date underneath the button
        Calendar myCalendar = new GregorianCalendar(eventDateYear, eventDateMonth, eventDateDay, eventHour, eventMinute);    //creates a Date data type and passes it the date, month and year of the event
        Date testEventDate = myCalendar.getTime();
        checkEvents(testEventDate);       //calls checkEvents to check if there is already an event on that date
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {                              //gets the time from the TimePicker
        this.eventHour = hourOfDay;
        this.eventMinute = minute;
        if (String.valueOf(minute).length() == 1) {             //to ensure that 2 digits are displayed for the minute
            String strMinute = "0" + minute;                    //if the time selected has the minute 0 it ensures that 00 is displayed instead
            String timeToDisplay = "Selected Time: " + hourOfDay + ":" + strMinute;
            mDisplayTime.setText(timeToDisplay);    //displays the selected time underneath the button
        } else {
            String timeToDisplay = "Selected Time: " + hourOfDay + ":" + minute;
            mDisplayTime.setText(timeToDisplay);    //displays the selected time underneath the button
        }
    }

    private void checkEvents(Date testEventDate) {
        Date dateFrom = testEventDate;      //sets the starting date to be the selected date
        long longFrom = testEventDate.getTime();
        long longTo = longFrom + 86400000;  //sets the end date to be the selected date + 24 hours
        Date dateTo = new Date();
        dateTo.setTime(longTo);
        Log.d("Date from", String.valueOf(dateFrom));
        Log.d("Date to", String.valueOf(dateTo));

        List<String> eventsList = new ArrayList<>(); //creates a list to store all of the returned events
        mColRef.whereGreaterThanOrEqualTo("Date", dateFrom).whereLessThan("Date", dateTo).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {     //queries FireStore to only return events that are within 24 hours of the selected date (same day)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {               //iterates through each document in cloud firestore
                        Log.d("document:", document.getId() + " => " + document.get("Groups"));
                        eventsList.add(document.getId());   //if there is an event on the selected day, it is added to eventsList
                    }
                }else {
                    Log.d("error", "Error getting documents: ", task.getException());
            }
            Log.d("eventsList", String.valueOf(eventsList));
            Log.d("size", String.valueOf(eventsList.size()));

            if(eventsList.size() == 0){     //if there are no events on this day, then the event can be made
                Log.d("Status: ", "This event can be made");
                eventCanBeMade = true;
                //Toast.makeText(createEventActivity.this, "This event can be made",Toast.LENGTH_SHORT).show();

            }else{                //if there is already an event on this day, then the event cannot be made
                Log.d("Status: ", "There is already an event on this day");
                String warningString = "There is already an event on this day, choose a different date";
                mDisplayDate.setText(warningString);    //displays a message underneath the button to choose a different date
                eventCanBeMade = false;
                //Toast.makeText(createEventActivity.this, "There is already an event on this day",Toast.LENGTH_SHORT).show();
            }
            }
        });
    }
    private void createEvent() {
        List<String> groupsList = new ArrayList<String>();                  //creates new array to store the list of groups for the event

        Calendar myCalendar = new GregorianCalendar(eventDateYear, eventDateMonth, eventDateDay, eventHour, eventMinute);    //creates a Date data type and passes it the date, month and year of the event
        Date finalEventDate = myCalendar.getTime();
        Log.i("final date", String.valueOf(finalEventDate));

        Log.d("Can event be made? ", String.valueOf(eventCanBeMade));
        if(eventCanBeMade == true){     //only creates the event if the event is allowed to be created
            if(mCheckBoxRed.isChecked()){                   //checks each checkbox to see if it is checked, if it is it adds the colour to groupsList
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
            if(groupsList.isEmpty()){
                Toast.makeText(createEventActivity.this, "You must select a Group", Toast.LENGTH_SHORT).show();
            }else{
                Map<String, Object> eventToSave = new HashMap<String, Object>();                    //creates a hashMap that stores the event data before it is saved to cloud firestore
                eventToSave.put(TITLE_KEY, eventTitle);                            //adds all the required data to the hashMap
                eventToSave.put(DETAILS_KEY, eventDetails);
                eventToSave.put(LOCATION_KEY, eventLocation);
                eventToSave.put(DATE_KEY, finalEventDate);
                eventToSave.put(GROUPS_KEY, groupsList);
                mColRef.add(eventToSave).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {      //saves the data to firestore and checks if the request is successful
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("success", "Document has been saved!");
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));      //if the data is successfully saved to firestore, the user is taken back to the MainActivity
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {                   //if the data is not successfully added to firestore, an error message is added to the Logcat
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("ERROR", "Document was not saved!");
                    }
                });
            }

        }else{
            Toast.makeText(createEventActivity.this, "You must select a Date", Toast.LENGTH_SHORT).show();  //if the event cannot be made, the user must select a different date
            Log.d("Status", "Event is not going to be made");
        }
    }

}