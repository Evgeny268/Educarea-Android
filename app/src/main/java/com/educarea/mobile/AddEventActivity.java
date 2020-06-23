package com.educarea.mobile;

import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.educarea.mobile.dialogs.DatePickerFragment;
import com.educarea.mobile.dialogs.TimePickerFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import transfers.Event;
import transfers.Group;
import transfers.GroupPersons;
import transfers.TransferRequestAnswer;
import transfers.Transfers;
import transfers.TransfersFactory;
import transfers.TypeRequestAnswer;

import static com.educarea.mobile.EduApp.INTENT_GROUP;
import static com.educarea.mobile.EduApp.INTENT_GROUP_PERSONS;

public class AddEventActivity extends AppInetActivity implements TypeRequestAnswer, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private Group group;
    private GroupPersons groupPersons;

    private TextView textViewTime;
    private TextView textViewDate;
    private EditText editTextEventName;
    private EditText editTextEventText;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        group = (Group) getIntent().getSerializableExtra(INTENT_GROUP);
        groupPersons = (GroupPersons) getIntent().getSerializableExtra(INTENT_GROUP_PERSONS);
        if (group==null || groupPersons==null) onBackPressed();
        textViewTime = findViewById(R.id.editTextTime);
        textViewDate = findViewById(R.id.editTextDate);
        editTextEventName = findViewById(R.id.editTextEventName);
        editTextEventText = findViewById(R.id.editTextEventText);
        calendar = Calendar.getInstance();
        String time = formatTime(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE));
        String date = formatDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        textViewTime.setText(time);
        textViewDate.setText(date);
    }

    @Override
    protected void newMessage(String message) {
        Transfers in = TransfersFactory.createFromJSON(message);
        if (in!=null){
            if (in instanceof TransferRequestAnswer){
                if (((TransferRequestAnswer) in).request.equals(UPDATE_INFO)){
                    onBackPressed();
                }else eduApp.standartReactionOnAsnwer(message,AddEventActivity.this);
            }else eduApp.standartReactionOnAsnwer(message,AddEventActivity.this);
        }else eduApp.standartReactionOnAsnwer(message,AddEventActivity.this);
    }

    public void onClickTimeObject(View view) {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(),getString(R.string.time));
    }

    public void onClickDateObject(View view) {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), getString(R.string.date));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String date = formatDate(year, month, dayOfMonth);
        textViewDate.setText(date);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        String time = formatTime(hourOfDay, minute);
        textViewTime.setText(time);
    }

    public void onClickSaveEvent(View view) {
        String title = editTextEventName.getText().toString();
        String text = editTextEventText.getText().toString();
        if (title.equals("")){
            return;
        }
        Event event = new Event();
        event.groupId = group.groupId;
        event.date = calendar.getTime();
        event.title = title;
        event.text = text;
        eduApp.sendTransfers(event);

    }

    private String formatTime(int hour, int minute){
        String sHour = String.valueOf(hour);
        if (sHour.length()==1){
            sHour="0"+sHour;
        }
        String sMinute = String.valueOf(minute);
        if (sMinute.length()==1){
            sMinute="0"+sMinute;
        }
        return sHour+":"+sMinute;
    }

    private String formatDate(int year, int month, int dayOfMonth){
        String sDay = String.valueOf(dayOfMonth);
        if (sDay.length()==1){
            sDay="0"+sDay;
        }
        String sMonth = String.valueOf(month+1);
        if (sMonth.length()==1){
            sMonth = "0"+sMonth;
        }
        return sDay+"."+sMonth+"."+year;
    }

}