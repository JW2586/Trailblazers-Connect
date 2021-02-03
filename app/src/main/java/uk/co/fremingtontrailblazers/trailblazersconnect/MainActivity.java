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
    Button mLogoutBtn;
    Button mCreateButton;
    Button mDisplayEventsButton;
    Button mTestButton;
    CalendarView mCalendarView;
    public boolean canViewEvent;
    FirebaseAuth fAuth;
    FirebaseUser user;
    String userID;
    Task<Void> usertask;
    private final CollectionReference mColRefEvents = FirebaseFirestore.getInstance().collection("events");     //sets the cloud firestore collection to be the "events" collection
    private final CollectionReference mColRefUsers = FirebaseFirestore.getInstance().collection("users");     //sets the cloud firestore collection to be the "events" collection


    private void checkEvents(Date dateFrom) {
        Date dateTo = new Date(dateFrom.getTime() + (1000 * 60 * 60 * 24));
        Log.d("Date from", String.valueOf(dateFrom));
        Log.d("Date to", String.valueOf(dateTo));

        List<String> eventsList = new ArrayList<>();
        mColRefEvents.whereGreaterThanOrEqualTo("Date", dateFrom).whereLessThan("Date", dateTo).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {               //Iterates through each document in cloud firestore
                        Log.d("document:", document.getId() + " => " + document.get("Groups"));
                        eventsList.add(document.getId());
                    }
                } else {
                    Log.d("error", "Error getting documents: ", task.getException());
                }
                Log.d("eventsList", String.valueOf(eventsList));
                Log.d("size", String.valueOf(eventsList.size()));
                if (eventsList.size() == 0) {
                    Log.d("Status: ", "There is not an event on this day");
                    canViewEvent = false;
                } else {
                    Log.d("Status: ", "There is an event on this day");
                    canViewEvent = true;
                    //Toast.makeText(createEventActivity.this, "There is already an event on this day",Toast.LENGTH_SHORT).show();
                }
                Log.d("CANVIEWEVENT", String.valueOf(canViewEvent));
                if(canViewEvent == true) {
                    Intent intent = new Intent(getApplicationContext(), viewEventActivity.class);
                    intent.putExtra("SELECTED_DATE", dateFrom);
                    startActivity(intent);
                    finish();
                }else{
                    Log.d("Status", "event won't be displayed");
                    Toast.makeText(MainActivity.this, "There is not an event on this day",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void displayEvents() {
//========WORKING CLONING CALENDAR FOR MULTIPLE ICONS ON DIFFERENT DAYS===============================================================================================================================================================================================
//        Calendar calendar = Calendar.getInstance();
//        List<EventDay> events = new ArrayList<>();          //Creates a list of EventDay objects
//
//        calendar = (Calendar) calendar.clone();
//        calendar.set(2020, 11, 10);    //Tells the calendar which day to display the dots on, comes from the date extracted from the document
//        events.add(new EventDay(calendar, R.drawable.dot_red));  //Adds the EventDay and the dots to the events list
//        CalendarView calendarView = (CalendarView) findViewById(R.id.eventCalendar); //Tells the library which calendar to add the dots to
//        try {
//            calendarView.setDate(calendar);
//        } catch (OutOfDateRangeException e) {
//            e.printStackTrace();
//        }
//        calendarView.setEvents(events);  //Displays the dots on the calendar
//
//        calendar = (Calendar) calendar.clone();
//        calendar.set(2020, 11, 11);    //Tells the calendar which day to display the dots on, comes from the date extracted from the document
//        events.add(new EventDay(calendar, R.drawable.dot_blue));  //Adds the EventDay and the dots to the events list
//
//        try {
//            calendarView.setDate(calendar);
//        } catch (OutOfDateRangeException e) {
//            e.printStackTrace();
//        }
//        calendarView.setEvents(events);
//===================================================================================================================================================================================================================================================================

//DOCUMENT ITERATION=====================================================================================================================================================================

        mColRefEvents.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            Calendar calendar = Calendar.getInstance();
            List<EventDay> events = new ArrayList<>();          //Creates a list of EventDay objects
            List<Date> datesList = new ArrayList<>();
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {               //Iterates through each document in cloud firestore
                        //Log.d("document:", document.getId() + " => " + document.get("Groups"));

                        List<Object> groupsList;         //Creates a list object called groupsList

                        groupsList = (List<Object>) document.get("Groups");  //Retrieves the groups from the document and adds them to groupsList

                        Date eventDate = document.getDate("Date");     //Retrieves the date from the document and sets it as a Date variable called eventDate
                        datesList.add(eventDate);
                        Log.d("Dates LIST", datesList.toString());
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(eventDate);
                        int month = cal.get(Calendar.MONTH);
                        int year = cal.get(Calendar.YEAR);
                        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
                        Log.d("THE DATE IS:", eventDate.toString());
                        Log.d("THE DAY IS:", String.valueOf(dayOfMonth));
                        Log.d("THE MONTH IS:", String.valueOf(month));

                        Log.d("THE YEAR IS:", String.valueOf(year));
                        LayerDrawable layerDrawable = (LayerDrawable) getResources().getDrawable(R.drawable.multiple_dots); //Loads in the empty dots
                        LayerDrawable dotInstanceDrawable;
                        dotInstanceDrawable = (LayerDrawable) layerDrawable.getConstantState().newDrawable().mutate();
//                        Log.d("groups of:", document.getId() + " => " + groupsList.toString());
//                        Log.d("size:", String.valueOf(groupsList.size()));
                        Log.d("document:", document.getId() + " => " + document.get("Groups"));

                        for (int i = 0; i < groupsList.size(); i++) {        //Iterates through the groupsList for each document
//                            Log.d("group", groupsList.get(i).toString());
                            if (groupsList.get(i).toString().equals("Red")) {           //Checks the colours found in the groupsList
                                Log.d("message", "there is a red in here");
                                Drawable replaceDot = (Drawable) getResources().getDrawable(R.drawable.dot_red);    //If a colour is in the groupsList, it replaces one of the blank dots with that colour
                                dotInstanceDrawable.setDrawableByLayerId(R.id.dot1, replaceDot);
                            }
                            if (groupsList.get(i).toString().equals("Orange")) {
                                Log.d("message", "there is a orange in here");
                                Drawable replaceDot = (Drawable) getResources().getDrawable(R.drawable.dot_orange);    //If a colour is in the groupsList, it replaces one of the blank dots with that colour
                                dotInstanceDrawable.setDrawableByLayerId(R.id.dot2, replaceDot);
                            }
                            if (groupsList.get(i).toString().equals("Yellow")) {
                                Log.d("message", "there is a yellow in here");
                                Drawable replaceDot = (Drawable) getResources().getDrawable(R.drawable.dot_yellow);    //If a colour is in the groupsList, it replaces one of the blank dots with that colour
                                dotInstanceDrawable.setDrawableByLayerId(R.id.dot3, replaceDot);
                            }
                            if (groupsList.get(i).toString().equals("Green")) {
                                Log.d("message", "there is a green in here");
                                Drawable replaceDot = (Drawable) getResources().getDrawable(R.drawable.dot_green);    //If a colour is in the groupsList, it replaces one of the blank dots with that colour
                                dotInstanceDrawable.setDrawableByLayerId(R.id.dot4, replaceDot);
                            }
                            if (groupsList.get(i).toString().equals("Blue")) {
                                Log.d("message", "there is a blue in here");
                                Drawable replaceDot = (Drawable) getResources().getDrawable(R.drawable.dot_blue);    //If a colour is in the groupsList, it replaces one of the blank dots with that colour
                                dotInstanceDrawable.setDrawableByLayerId(R.id.dot5, replaceDot);
                            }
                            if (groupsList.get(i).toString().equals("Indigo")) {
                                Log.d("message", "there is a indigo in here");
                                Drawable replaceDot = (Drawable) getResources().getDrawable(R.drawable.dot_indigo);    //If a colour is in the groupsList, it replaces one of the blank dots with that colour
                                dotInstanceDrawable.setDrawableByLayerId(R.id.dot6, replaceDot);
                            }
                            if (groupsList.get(i).toString().equals("Violet")) {
                                Log.d("message", "there is a violet in here");
                                Drawable replaceDot = (Drawable) getResources().getDrawable(R.drawable.dot_violet);    //If a colour is in the groupsList, it replaces one of the blank dots with that colour
                                dotInstanceDrawable.setDrawableByLayerId(R.id.dot7, replaceDot);
                            }


                        }

                        calendar = (Calendar) calendar.clone();
                        calendar.set(year, month, dayOfMonth);    //Tells the calendar which day to display the dots on, comes from the date extracted from the document
                        events.add(new EventDay(calendar, dotInstanceDrawable));  //Adds the EventDay and the dots to the events list
                        CalendarView calendarView = (CalendarView) findViewById(R.id.eventCalendar); //Tells the library which calendar to add the dots to
                        try {
                            calendarView.setDate(calendar);
                        } catch (OutOfDateRangeException e) {
                            e.printStackTrace();
                        }
                        calendarView.setEvents(events);  //Displays the dots on the calendar
                    }
                } else {
                    Log.d("error", "Error getting documents: ", task.getException());
                }
            }
        });
//==========================================================================================================================================================================================================================================
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mLogoutBtn = findViewById(R.id.logoutButton);
        mCreateButton = findViewById(R.id.createButton);
        mCalendarView = findViewById(R.id.eventCalendar);
        mDisplayEventsButton = findViewById(R.id.displayEventsButton);
        mTestButton = findViewById(R.id.testButton);
        mTestButton.setVisibility(View.INVISIBLE);
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        usertask = Objects.requireNonNull(fAuth.getCurrentUser()).reload();
        usertask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                user = fAuth.getCurrentUser();
            }
        });
        if(fAuth.getCurrentUser() == null) {            //checks to see if user is already logged in, if they are, it sends them to the MainActivity
            startActivity(new Intent(getApplicationContext(),Register.class));
            finish();
        }
        userID = fAuth.getCurrentUser().getUid();
        Log.d("CURRENT USER",userID);
        mCreateButton.setVisibility(View.INVISIBLE);
        mColRefUsers.document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("tag", "DocumentSnapshot data: " + document.getData());
                        if (String.valueOf(document.get("Role")).equals("Coach")) {
                            Log.d("tag", "User is a coach");
                            mCreateButton.setVisibility(View.VISIBLE);
                        }else{
                            Log.d("tag", "User is not a coach");
                        }
                    } else {
                        Log.d("tag", "No such document");
                    }
                } else {
                    Log.d("tag", "get failed with ", task.getException());
                }
            }
        });
        displayEvents();

//        mTestButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), viewEventActivity.class));
//            }
//        });

//        LayerDrawable layerDrawable = (LayerDrawable) getResources().getDrawable(R.drawable.multiple_dots);
//        Drawable replaceGreen = (Drawable) getResources().getDrawable(R.drawable.dot_green);
//        layerDrawable.setDrawableByLayerId(R.id.dot4, replaceGreen);
//        Drawable replaceOrange = (Drawable) getResources().getDrawable(R.drawable.dot_orange);
//        layerDrawable.setDrawableByLayerId(R.id.dot2, replaceOrange);
//        List<EventDay> events = new ArrayList<>();
//        Calendar calendar = Calendar.getInstance();
//        events.add(new EventDay(calendar, R.drawable.multiple_dots));
//        CalendarView calendarView = (CalendarView) findViewById(R.id.eventCalendar);
//        calendarView.setEvents(events);

        mCalendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Log.d("EVENTDAY", String.valueOf(eventDay));
                Calendar clickedDayCalendar = eventDay.getCalendar();
                Log.d("Day clicked", String.valueOf(clickedDayCalendar));
                Log.d("eventDateYear", String.valueOf(clickedDayCalendar.get(1)));
                Log.d("eventDateMonth", String.valueOf((clickedDayCalendar.get(2))+1));
                Log.d("eventDateDay", String.valueOf(clickedDayCalendar.get(5)));
                Calendar myCalendar = new GregorianCalendar(clickedDayCalendar.get(1), clickedDayCalendar.get(2), clickedDayCalendar.get(5));    //creates a Date data type and passes it the date, month and year of the event
                Date finalSelectedDate = myCalendar.getTime();
                Log.d("final date", String.valueOf(finalSelectedDate));
                checkEvents(finalSelectedDate);
            }
        });

        mDisplayEventsButton.setOnClickListener(new View.OnClickListener() {
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
                finish();
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
                finish();
            }
        });
    }
}