package uk.co.fremingtontrailblazers.trailblazersconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    Button mLogoutBtn;          //creates variables for each UI element
    Button mCreateButton;
    Button mDisplayEventsButton;
    CalendarView mCalendarView;
    public boolean canViewEvent;
    FirebaseAuth fAuth;
    FirebaseUser user;
    String userID;
    Task<Void> usertask;
    private final CollectionReference mColRefEvents = FirebaseFirestore.getInstance().collection("events");     //sets the cloud firestore collection to be the "events" collection
    private final CollectionReference mColRefUsers = FirebaseFirestore.getInstance().collection("users");     //sets the cloud firestore collection to be the "events" collection

    private void checkEvents(Date dateFrom) {
        Date dateTo = new Date(dateFrom.getTime() + (1000 * 60 * 60 * 24)); //sets the end date to be the selected date + 24 hours

        List<String> eventsList = new ArrayList<>();    //creates a list to store all of the returned events
        mColRefEvents.whereGreaterThanOrEqualTo("Date", dateFrom).whereLessThan("Date", dateTo).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {   //queries FireStore to only return events that are within 24 hours of the selected date(same day)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {               //iterates through each document in cloud firestore
                        eventsList.add(document.getId());       //if there is an event on the selected day, it is added to eventsList
                    }
                } else {
                }
                if (eventsList.size() == 0) {   //if there are no events on this day, then there is no event to view
                    canViewEvent = false;
                } else {        //if there is an event on this day, then the event can be viewed
                    canViewEvent = true;
                }
                if(canViewEvent == true) {  //if there is an event on this day the user is sent to viewEventActivity
                    Intent intent = new Intent(getApplicationContext(), viewEventActivity.class);
                    intent.putExtra("SELECTED_DATE", dateFrom);     //the selected date, dateFrom, is passed into viewEventActivity
                    startActivity(intent);
                    finish();
                }else{  //if there is not an events on the selected day, a Toast message is displayed to the user
                    Toast.makeText(MainActivity.this, "There is not an event on this day",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void displayEvents() {      //this method is used to display the event icons on the calendar
        mColRefEvents.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            Calendar calendar = Calendar.getInstance();
            List<EventDay> events = new ArrayList<>();          //creates a list of EventDay objects
            List<Date> datesList = new ArrayList<>();           //creates a list of dates
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {               //iterates through each document in cloud firestore
                        List<Object> groupsList;         //creates a list object called groupsList to store the groups of an event
                        groupsList = (List<Object>) document.get("Groups");  //retrieves the groups from the document and adds them to groupsList
                        Date eventDate = document.getDate("Date");     //retrieves the date from the document and sets it as a Date variable called eventDate
                        datesList.add(eventDate);             //adds the retrieved date, eventDate, to datesList

                        Calendar cal = Calendar.getInstance();  //creates a new calendar object set to the date of the event
                        cal.setTime(eventDate);
                        int month = cal.get(Calendar.MONTH);
                        int year = cal.get(Calendar.YEAR);
                        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

                        LayerDrawable layerDrawable = (LayerDrawable) getResources().getDrawable(R.drawable.multiple_dots); //loads in the empty dots
                        LayerDrawable dotInstanceDrawable;
                        dotInstanceDrawable = (LayerDrawable) layerDrawable.getConstantState().newDrawable().mutate();  //mutates layerDrawable to create a new instance of it called dotInstanceDrawable

                        for (int i = 0; i < groupsList.size(); i++) {        //iterates through the groupsList for each document
                            if (groupsList.get(i).toString().equals("Red")) {           //checks the colours found in the groupsList
                                Drawable replaceDot = (Drawable) getResources().getDrawable(R.drawable.dot_red);    //if a colour is in the groupsList, it sets the replaceDot drawable to be a dot of that colour
                                dotInstanceDrawable.setDrawableByLayerId(R.id.dot1, replaceDot);        //the specific dot in the template is replaced by that colour
                            }
                            if (groupsList.get(i).toString().equals("Orange")) {
                                Drawable replaceDot = (Drawable) getResources().getDrawable(R.drawable.dot_orange);
                                dotInstanceDrawable.setDrawableByLayerId(R.id.dot2, replaceDot);
                            }
                            if (groupsList.get(i).toString().equals("Yellow")) {
                                Drawable replaceDot = (Drawable) getResources().getDrawable(R.drawable.dot_yellow);
                                dotInstanceDrawable.setDrawableByLayerId(R.id.dot3, replaceDot);
                            }
                            if (groupsList.get(i).toString().equals("Green")) {
                                Drawable replaceDot = (Drawable) getResources().getDrawable(R.drawable.dot_green);
                                dotInstanceDrawable.setDrawableByLayerId(R.id.dot4, replaceDot);
                            }
                            if (groupsList.get(i).toString().equals("Blue")) {
                                Drawable replaceDot = (Drawable) getResources().getDrawable(R.drawable.dot_blue);
                                dotInstanceDrawable.setDrawableByLayerId(R.id.dot5, replaceDot);
                            }
                            if (groupsList.get(i).toString().equals("Indigo")) {
                                Drawable replaceDot = (Drawable) getResources().getDrawable(R.drawable.dot_indigo);
                                dotInstanceDrawable.setDrawableByLayerId(R.id.dot6, replaceDot);
                            }
                            if (groupsList.get(i).toString().equals("Violet")) {
                                Drawable replaceDot = (Drawable) getResources().getDrawable(R.drawable.dot_violet);
                                dotInstanceDrawable.setDrawableByLayerId(R.id.dot7, replaceDot);
                            }
                        }
                        calendar = (Calendar) calendar.clone();
                        calendar.set(year, month, dayOfMonth);    //tells the calendar which day to display the dots on, comes from the date extracted from the document
                        events.add(new EventDay(calendar, dotInstanceDrawable));  //adds the EventDay and the dots to the events list
                        CalendarView calendarView = (CalendarView) findViewById(R.id.eventCalendar); //tells the library which calendar to add the dots to
                        try {
                            calendarView.setDate(calendar);
                        } catch (OutOfDateRangeException e) {
                            e.printStackTrace();
                        }
                        calendarView.setEvents(events);  //adds the event to the calendar and displays the dots
                    }
                } else {
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);    //assigning the UI elements to each variable
        mLogoutBtn = findViewById(R.id.logoutButton);
        mCreateButton = findViewById(R.id.createButton);
        mCalendarView = findViewById(R.id.eventCalendar);
        mDisplayEventsButton = findViewById(R.id.displayEventsButton);

        fAuth = FirebaseAuth.getInstance();  //creates a new FirebaseAuth instance called fAuth
        user = fAuth.getCurrentUser();      //gets the current user
        usertask = Objects.requireNonNull(fAuth.getCurrentUser()).reload();  //
        usertask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                user = fAuth.getCurrentUser();
            }
        });
        if(fAuth.getCurrentUser() == null) {    //if the user is not logged in, they are sent to the Register activity
            startActivity(new Intent(getApplicationContext(),Register.class));
            finish();
        }
        userID = fAuth.getCurrentUser().getUid();   //gets the ID of the current user
        mCreateButton.setVisibility(View.INVISIBLE);
        mColRefUsers.document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {  //queries FireStore to find the document containing that user
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (String.valueOf(document.get("Role")).equals("Coach")) {     //if the user's Role is "Coach" then the create event button is made visible
                            mCreateButton.setVisibility(View.VISIBLE);   //allows the create event button to be pressed
                        }
                    }
                }
            }
        });
        displayEvents();    //each time MainActivity is started, the events must be displayed on the calendar

        mCalendarView.setOnDayClickListener(new OnDayClickListener() {  //when the user clicks on a date on the calendar
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                Calendar myCalendar = new GregorianCalendar(clickedDayCalendar.get(1), clickedDayCalendar.get(2), clickedDayCalendar.get(5));    //creates a calendar date set to the date, month and year of the event that was clicked
                Date finalSelectedDate = myCalendar.getTime();  //creates a Date called finalSelectedDate that stores the date that was clicked
                checkEvents(finalSelectedDate);     //passes the finalSelectedDate into checkEvents to see if there is an event on the selected date
            }
        });

        mDisplayEventsButton.setOnClickListener(new View.OnClickListener() {    //when the user clicks on the refresh button
            @Override
            public void onClick(View v) {
                mDisplayEventsButton.setBackgroundResource(R.drawable.refresh_button_pressed);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDisplayEventsButton.setBackgroundResource(R.drawable.refresh_button);
                    }
                }, 100);
                displayEvents();    //calls displayEvents to update the calendar with all of the current events
            }
        });

        mLogoutBtn.setOnClickListener(new View.OnClickListener() {      //when the user clicks on the logout button
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
                fAuth.signOut();   //signs the user out of FirebaseAuth
                startActivity(new Intent(getApplicationContext(), Login.class));    //sends the user back to the login page, Login
                finish();
            }
        });

        mCreateButton.setOnClickListener(new View.OnClickListener() {   //when the user clicks on the create event button
            @Override
            public void onClick(View v) {
                mCreateButton.setBackgroundResource(R.drawable.create_button_pressed);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCreateButton.setBackgroundResource(R.drawable.create_button);
                    }
                }, 100);
                startActivity(new Intent(getApplicationContext(), createEventActivity.class));  //the user is sent to createEventActivity
                finish();
            }
        });
    }
}