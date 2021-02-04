package uk.co.fremingtontrailblazers.trailblazersconnect;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {
    Calendar datePickerCalendar = Calendar.getInstance();   //creates a new datePicker and gets the selected date
    int year = datePickerCalendar.get(Calendar.YEAR);
    int month = datePickerCalendar.get(Calendar.MONTH);
    int day = datePickerCalendar.get(Calendar.DAY_OF_MONTH);

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);   //returns the selected date to createEventActivity
    }
}
