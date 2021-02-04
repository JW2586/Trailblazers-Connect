package uk.co.fremingtontrailblazers.trailblazersconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.applandeo.materialcalendarview.EventDay;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class viewEventActivity extends AppCompatActivity {
    Button mNextTestButton;     //creates variables for each UI element
    Button mBackButton;
    TextView mEventTitle;
    TextView mEventDate;
    TextView mEventTime;
    TextView mEventLocation;
    TextView mEventDetails;
    TextView mEventGroups;
    TextView mDisplayRed;
    TextView mDisplayOrange;
    TextView mDisplayYellow;
    TextView mDisplayGreen;
    TextView mDisplayBlue;
    TextView mDisplayIndigo;
    TextView mDisplayViolet;

    private CollectionReference mColRef;

    public viewEventActivity() {
        super();
        mColRef = FirebaseFirestore.getInstance().collection("events"); //sets the cloud firestore collection to be the "events" collection
    }

    public viewEventActivity(int contentLayoutId) {
        super(contentLayoutId);
        mColRef = FirebaseFirestore.getInstance().collection("events"); //sets the cloud firestore collection to be the "events" collection
    }

    public void getEvents(Date dateFrom) {
        mEventTitle = findViewById(R.id.displayEventTitle);   //assigns the UI elements to each variable
        mEventDate = findViewById(R.id.displayEventDate);
        mEventTime = findViewById(R.id.displayEventTime);
        mEventLocation = findViewById(R.id.displayEventLocation);
        mEventDetails = findViewById(R.id.displayEventDetails);
        mEventGroups = findViewById(R.id.displayEventGroups);
        mDisplayRed = findViewById(R.id.displayRed);
        mDisplayOrange = findViewById(R.id.displayOrange);
        mDisplayYellow = findViewById(R.id.displayYellow);
        mDisplayGreen = findViewById(R.id.displayGreen);
        mDisplayBlue = findViewById(R.id.displayBlue);
        mDisplayIndigo = findViewById(R.id.displayIndigo);
        mDisplayViolet = findViewById(R.id.displayViolet);
        Date dateTo = new Date(dateFrom.getTime() + (1000 * 60 * 60 * 24));  //sets the end date to be the selected date + 24 hours
        mColRef.whereGreaterThanOrEqualTo("Date", dateFrom).whereLessThan("Date", dateTo).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {   //queries FireStore to only return events that are within 24 hours of the selected date (same day)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {               //iterates through each document in cloud firestore
                        List<Object> groupsList;         //creates a list of objects called groupsList
                        groupsList = (List<Object>) document.get("Groups");  //retrieves the groups from the document and adds them to groupsList
                        Log.d("document:", document.getId() + " => " + document.get("Groups"));
                        Log.d("title", String.valueOf(document.get("Title")));

                        mEventGroups.setText(String.valueOf(document.get("Groups")));   //retrieves the event's details, title, location and groups and displays them in the text boxes
                        mEventDetails.setText(document.getString("Details"));
                        mEventLocation.setText(document.getString("Location"));
                        mEventTitle.setText(document.getString("Title"));
                        LocalDate localDate = (document.getDate("Date")).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();  //creates a Date object from the retrieved event's date
                        LocalTime time = LocalDateTime.ofInstant((document.getDate("Date")).toInstant(), ZoneId.systemDefault()).toLocalTime(); //creates a Time object from the retrieved event's time
                        Log.d("localDate", String.valueOf(localDate));
                        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");  //creates a date format in the format "date number", "month name", "year" e.g. 01 December 2020
                        mEventTime.setText(String.valueOf(time));   //sets the time text box as the retrieved event's time
                        mEventDate.setText(localDate.format(dateFormatter));   //formats the date using the created format and displays is in the text box

                        for (int i = 0; i < groupsList.size(); i++) {        //iterates through the groupsList for each document
//                            Log.d("group", groupsList.get(i).toString());
                            if (groupsList.get(i).toString().equals("Red")) {           //checks the colours found in the groupsList
                                Log.d("message", "there is a red in here");
                                mDisplayRed.setVisibility(View.VISIBLE);                //displays the corresponding coloured box if the colour is in the retrieved event's groups
                            }
                            if (groupsList.get(i).toString().equals("Orange")) {
                                Log.d("message", "there is a orange in here");
                                mDisplayOrange.setVisibility(View.VISIBLE);
                            }
                            if (groupsList.get(i).toString().equals("Yellow")) {
                                Log.d("message", "there is a yellow in here");
                                mDisplayYellow.setVisibility(View.VISIBLE);
                            }
                            if (groupsList.get(i).toString().equals("Green")) {
                                Log.d("message", "there is a green in here");
                                mDisplayGreen.setVisibility(View.VISIBLE);
                            }
                            if (groupsList.get(i).toString().equals("Blue")) {
                                Log.d("message", "there is a blue in here");
                                mDisplayBlue.setVisibility(View.VISIBLE);
                            }
                            if (groupsList.get(i).toString().equals("Indigo")) {
                                Log.d("message", "there is a indigo in here");
                                mDisplayIndigo.setVisibility(View.VISIBLE);
                            }
                            if (groupsList.get(i).toString().equals("Violet")) {
                                Log.d("message", "there is a violet in here");
                                mDisplayViolet.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        Date receivedDate = (Date)getIntent().getSerializableExtra("SELECTED_DATE");    //receives the selected date from MainActivity
        getEvents(receivedDate);    //displays all of the information for the event on the selected date
        mNextTestButton = findViewById(R.id.nextTestButton);
        mNextTestButton.setVisibility(View.INVISIBLE);
        mBackButton = findViewById(R.id.backButton);
        mBackButton.setOnClickListener(new View.OnClickListener() {     //when the user wants to go back to MainActivity
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
                startActivity(new Intent(getApplicationContext(),MainActivity.class));  //sends the user back to MainActivity
                finish();
            }
        });
//        mNextTestButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getEvents(receivedDate);
//
////                mEventTime.setText(localDate.format(timeFormatter));
////                Calendar cal = Calendar.getInstance();
////                cal.setTime(receivedDate);
////                int eventMonth = cal.get(Calendar.MONTH);
////                int eventYear = cal.get(Calendar.YEAR);
////                int eventDay = cal.get(Calendar.DAY_OF_MONTH);
////                String eventDate = eventDay + String.valueOf(eventMonth) + eventYear;
////                mEventDate.setText(eventDate);
//            }
//        });
    }
}