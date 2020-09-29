package com.wjh160030.contactsapp;

import android.app.DatePickerDialog;
import androidx.fragment.app.DialogFragment;
import android.os.Bundle;
import android.app.Fragment;
import android.widget.TextView;
import android.widget.DatePicker;
import android.app.Dialog;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
//Written by WIlliam Hood with help from https://www.youtube.com/watch?v=33BFCdL0Di0
//
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //current date as default day
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener)getActivity(), year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        //empty class. Overriden in contact screen
    }
}