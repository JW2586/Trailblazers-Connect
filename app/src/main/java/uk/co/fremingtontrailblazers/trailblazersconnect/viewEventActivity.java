package uk.co.fremingtontrailblazers.trailblazersconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class viewEventActivity extends AppCompatActivity {
    Button mNextTestButton;
    TextView mEventTitle;
    TextView mEventDate;
    private int selectedEventDay = 0;
    private int selectedEventMonth = 0;
    private int selectedEventYear = 0;
    private long receivedDate = 0;
    private String eventTitle = "";
    private CollectionReference mColRef = FirebaseFirestore.getInstance().collection("events");     //sets the cloud firestore collection to be the "events" collection
    public String getEvents(String eventTitle) {
        Date dateFrom = new Date();
        dateFrom.setTime(receivedDate);
        long longTo = receivedDate + 86400000;
        Date dateTo = new Date();
        dateTo.setTime(longTo);
        Log.d("Date from", String.valueOf(dateFrom));
        Log.d("Date to", String.valueOf(dateTo));
        Calendar calendar = Calendar.getInstance();
        mColRef.whereGreaterThanOrEqualTo("Date", dateFrom).whereLessThan("Date", dateTo).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {               //Iterates through each document in cloud firestore
                        Log.d("document:", document.getId() + " => " + document.get("Groups"));
                        Log.d("title", String.valueOf(document.get("Title")));
                        eventTitle = document.getString("Title");
//                        List<Object> groupsList = new ArrayList<>();         //Creates a list object called groupsList
//                        groupsList = (List<Object>) document.get("Groups");  //Retrieves the groups from the document and adds them to groupsList
//
//                        Date eventDate = document.getDate("Date");     //Retrieves the date from the document and sets it as a Date variable called eventDate
//                        Calendar cal = Calendar.getInstance();
//                        cal.setTime(eventDate);
//                        int month = cal.get(Calendar.MONTH);
//                        int year = cal.get(Calendar.YEAR);
//                        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
//                        int hour = cal.get(Calendar.HOUR_OF_DAY);
//                        int minute = cal.get(Calendar.MINUTE);
//                        Log.d("THE DATE IS:", eventDate.toString());
//                        Log.d("THE DAY IS:", String.valueOf(dayOfMonth));
//                        Log.d("THE MONTH IS:", String.valueOf(month));
//                        Log.d("THE YEAR IS:", String.valueOf(year));
//                        Log.d("THE HOUR IS", String.valueOf(hour));
//                        Log.d("THE MINUTE IS", String.valueOf(minute));

//                        mEventDate.setText(String.valueOf(eventDate));
//                        Log.d("document:", document.getId() + " => " + document.get("Groups"));
                    }
                }
            }
        });
        return eventTitle;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        this.receivedDate = getIntent().getLongExtra("SELECTED_DATE", -1);
        Date finalSelectedDate = new Date();
        finalSelectedDate.setTime(receivedDate);
        Log.d("finalSelectedDate", String.valueOf(finalSelectedDate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(finalSelectedDate);
        this.selectedEventMonth = cal.get(Calendar.MONTH);
        this.selectedEventYear = cal.get(Calendar.YEAR);
        this.selectedEventDay = cal.get(Calendar.DAY_OF_MONTH);
        mNextTestButton = findViewById(R.id.nextTestButton);
        mEventTitle = findViewById(R.id.displayEventTitle);
        mEventDate = findViewById(R.id.displayEventDate);
        mNextTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEvents(eventTitle);
                String title = "Hill Training";
//                Log.d("title", String.valueOf(newEventTitle));
                mEventTitle.setText(eventTitle);
                mEventDate.setText(String.valueOf(finalSelectedDate));

            }
        });
    }
}