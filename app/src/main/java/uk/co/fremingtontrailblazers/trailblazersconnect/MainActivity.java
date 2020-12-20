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
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
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

public class MainActivity extends AppCompatActivity {
    Button mLogoutBtn;
    Button mCreateButton;
    Button mDisplayEventsButton;
    Button mTestButton;
    CalendarView mCalendarView;

    private CollectionReference mColRef = FirebaseFirestore.getInstance().collection("events");     //sets the cloud firestore collection to be the "events" collection
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

        mColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            Calendar calendar = Calendar.getInstance();
            List<EventDay> events = new ArrayList<>();          //Creates a list of EventDay objects
            List<Date> datesList = new ArrayList<>();
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {               //Iterates through each document in cloud firestore
                        //Log.d("document:", document.getId() + " => " + document.get("Groups"));

                        List<Object> groupsList = new ArrayList<>();         //Creates a list object called groupsList

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

        mTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), viewEventActivity.class));
            }
        });

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
                Calendar clickedDayCalendar = eventDay.getCalendar();
                Log.d("Day clicked", String.valueOf(clickedDayCalendar));
                Log.d("eventDateYear", String.valueOf(clickedDayCalendar.get(1)));
                Log.d("eventDateMonth", String.valueOf((clickedDayCalendar.get(2))+1));
                Log.d("eventDateDay", String.valueOf(clickedDayCalendar.get(5)));
                Calendar myCalendar = new GregorianCalendar(clickedDayCalendar.get(1), clickedDayCalendar.get(2), clickedDayCalendar.get(5));    //creates a Date data type and passes it the date, month and year of the event
                Date finalSelectedDate = myCalendar.getTime();
                Log.d("final date", String.valueOf(finalSelectedDate));
                Intent intent = new Intent(getApplicationContext(), viewEventActivity.class);
                intent.putExtra("SELECTED_DATE", finalSelectedDate);
                startActivity(intent);
            }
        });

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