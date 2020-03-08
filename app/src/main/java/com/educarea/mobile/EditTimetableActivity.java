package com.educarea.mobile;

import androidx.fragment.app.DialogFragment;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.educarea.mobile.dialogs.TimePickerFragment;

import java.util.ArrayList;

import transfers.Group;
import transfers.GroupPerson;
import transfers.GroupPersons;
import transfers.Timetable;
import transfers.TransferRequestAnswer;
import transfers.Transfers;
import transfers.TransfersFactory;
import transfers.TypeRequestAnswer;

import static com.educarea.mobile.EduApp.INTENT_GROUP;
import static com.educarea.mobile.EduApp.INTENT_GROUP_PERSONS;
import static com.educarea.mobile.EduApp.INTENT_TIMETABLE;

public class EditTimetableActivity extends AppInetActivity implements TypeRequestAnswer, TimePickerDialog.OnTimeSetListener {

    private Group group;
    private GroupPersons groupPersons;
    private ArrayList<GroupPerson> teachers;
    private String time = "00:00";
    private TextView textTime;
    private EditText objectName;
    private EditText cabinet;
    private RadioGroup radioGroup;
    private Timetable timetable = null;
    private int selectedTeacher = -1;
    private int selectedDay = 1;
    private Spinner spinDayWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_timetable);
        spinDayWeek = findViewById(R.id.spinnerDayOfWeek);
        objectName = findViewById(R.id.editTextObjectName);
        cabinet = findViewById(R.id.editTextCabinet);
        textTime = findViewById(R.id.editTextTime);
        radioGroup = findViewById(R.id.RadioGroupWeekParity);
        textTime.setText("00:00");
        group = (Group) getIntent().getSerializableExtra(INTENT_GROUP);
        groupPersons = (GroupPersons) getIntent().getSerializableExtra(INTENT_GROUP_PERSONS);
        if (group==null || groupPersons==null) onBackPressed();
        timetable = (Timetable) getIntent().getSerializableExtra(INTENT_TIMETABLE);
        teachers = getTeachers(groupPersons.persons);
        String []data = personsToStringNames(teachers);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = findViewById(R.id.spinnerTeachers);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTeacher = position-1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedTeacher = -1;
            }
        });
        radioGroup.check(R.id.radioButtonEveryWeek);
        if (timetable!=null){
            objectName.setText(timetable.objectName);
            cabinet.setText(timetable.cabinet);
            textTime.setText(timetable.time);
            time = timetable.time;
            switch (timetable.parityweek){
                case 0:
                    radioGroup.check(R.id.radioButtonEveryWeek);
                    break;
                case 1:
                    radioGroup.check(R.id.radioButtonOddWeek);
                    break;
                case 2:
                    radioGroup.check(R.id.radioButtonEvenWeek);
            }
            spinDayWeek.setSelection(timetable.day-1);
            for (int i = 0; i < teachers.size(); i++) {
                if (timetable.groupPersonId==teachers.get(i).groupPersonId){
                    selectedTeacher = i;
                    spinner.setSelection(selectedTeacher+1);
                }
            }
        }

    }

    @Override
    protected void newMessage(String message) {
        Transfers in = TransfersFactory.createFromJSON(message);
        if (in!=null){
            if (in instanceof TransferRequestAnswer){
                if (((TransferRequestAnswer) in).request.equals(UPDATE_INFO)){
                    onBackPressed();
                }else eduApp.standartReactionOnAsnwer(message,EditTimetableActivity.this);
            }else eduApp.standartReactionOnAsnwer(message,EditTimetableActivity.this);
        }else eduApp.standartReactionOnAsnwer(message,EditTimetableActivity.this);
    }

    private ArrayList<GroupPerson> getTeachers(ArrayList<GroupPerson> people){
        ArrayList<GroupPerson> list = new ArrayList<GroupPerson>();
        for (int i = 0; i < people.size(); i++) {
            if (people.get(i).personType==1){
                list.add(people.get(i));
            }
        }
        return list;
    }

    private String[] personsToStringNames(ArrayList<GroupPerson> persons){
        String []arr = new String[persons.size()+1];
        arr[0] = getString(R.string.without_teacher);
        for (int i = 0; i < persons.size(); i++) {
            GroupPerson teacher = persons.get(i);
            arr[i+1] = teacher.surname+" "+teacher.name+" "+teacher.patronymic;
        }
        return arr;
    }

    public void onClickTimeObject(View view) {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(),getString(R.string.time));
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String hour = String.valueOf(hourOfDay);
        if (hour.length()==1){
            hour="0"+hour;
        }
        String min = String.valueOf(minute);
        if (min.length()==1){
            min="0"+min;
        }
        time=hour+":"+min;
        textTime.setText(time);
    }

    public void onClickSaveTametable(View view) {
        int groupPersonId;
        if (selectedTeacher==-1){
            groupPersonId = 0;
        }else {
            groupPersonId = teachers.get(selectedTeacher).groupPersonId;
        }
        String objName = objectName.getText().toString();
        String cab = cabinet.getText().toString();
        int parityweek;
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        View radioButton = radioGroup.findViewById(radioButtonId);
        parityweek = radioGroup.indexOfChild(radioButton);
        int day = spinDayWeek.getSelectedItemPosition()+1;
        Timetable newTable = new Timetable(group.groupId, objName, parityweek, day);
        newTable.groupPersonId = groupPersonId;
        newTable.cabinet = cab;
        newTable.time = time;
        if (timetable!=null){
            newTable.timetableId = timetable.timetableId;
        }
        eduApp.sendTransfers(newTable);
    }

}
