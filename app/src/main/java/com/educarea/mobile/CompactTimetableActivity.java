package com.educarea.mobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.educarea.mobile.adapters.TimetableCompactAdapter;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

import transfers.Group;
import transfers.GroupPersons;
import transfers.Timetable;
import transfers.Timetables;
import transfers.TransferRequestAnswer;
import transfers.Transfers;
import transfers.TransfersFactory;
import transfers.TypeRequestAnswer;

import static com.educarea.mobile.EduApp.INTENT_GROUP;
import static com.educarea.mobile.EduApp.INTENT_GROUP_PERSONS;

public class CompactTimetableActivity extends AppInetActivity implements TypeRequestAnswer, TimetableCompactAdapter.TimetableCompactClickListener {

    private Group group;
    private GroupPersons groupPersons;
    private Timetables timetables = null;
    private Calendar calendar;

    private RecyclerView recyclerView;
    private TimetableCompactAdapter adapter;
    private TextView textDate;
    private TextView textDayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compact_timetable);
        textDate = findViewById(R.id.textViewTimeDate);
        textDayName = findViewById(R.id.textViewDayName);
        group = (Group) getIntent().getSerializableExtra(INTENT_GROUP);
        groupPersons = (GroupPersons) getIntent().getSerializableExtra(INTENT_GROUP_PERSONS);
        if (group == null || groupPersons == null) onBackPressed();
        recyclerView = findViewById(R.id.recyclerViewCompactTimetable);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(false);
        adapter = new TimetableCompactAdapter(this);
        recyclerView.setAdapter(adapter);
        calendar = Calendar.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        timetables = eduApp.getAppData().getTimetables(group.groupId);
        showCurrentDay();
        if (!eduApp.getInetWorker().isOfflineMode()) {
            eduApp.sendTransfers(new TransferRequestAnswer(GET_TIMETABLE, String.valueOf(group.groupId)));
        }
    }

    @Override
    protected void newMessage(String message) {
        Transfers in = TransfersFactory.createFromJSON(message);
        if (in != null){
            if (in instanceof Timetables){
                timetables = (Timetables) in;
                showCurrentDay();
            }else if (in instanceof TransferRequestAnswer){
                if (((TransferRequestAnswer) in).request.equals(UPDATE_INFO)){
                    eduApp.sendTransfers(new TransferRequestAnswer(GET_TIMETABLE,String.valueOf(group.groupId)));
                }else eduApp.standartReactionOnAsnwer(message,this);
            }
            else eduApp.standartReactionOnAsnwer(message,this);
        }else eduApp.standartReactionOnAsnwer(message,this);
    }

    @Override
    public void onClickTimetableCompact(int position, View view) {

    }

    @Override
    public void onLongClickTimetableCompact(int position, View view) {

    }

    private void showCurrentDay(){
        ArrayList<Timetable> dayTimetable = TimetableUtils.getTimetablesForDay(timetables.timetables,calendar);
        adapter.setTimetablesAndPersons(dayTimetable,groupPersons.persons);
        adapter.notifyDataSetChanged();
        SimpleDateFormat parser = new SimpleDateFormat("dd.MM.yyyy");
        textDate.setText(parser.format(calendar.getTime()));
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day){
            case 1:
                textDayName.setText(getString(R.string.sunday));
                break;
            case 2:
                textDayName.setText(getString(R.string.monday));
                break;
            case 3:
                textDayName.setText(getString(R.string.tuesday));
                break;
            case 4:
                textDayName.setText(getString(R.string.wednesday));
                break;
            case 5:
                textDayName.setText(getString(R.string.thursday));
                break;
            case 6:
                textDayName.setText(getString(R.string.friday));
                break;
            case 7:
                textDayName.setText(getString(R.string.saturday));
                break;
        }
    }

    public void onClickNextDay(View view) {
        calendar.add(Calendar.DAY_OF_MONTH,1);
        showCurrentDay();
    }

    public void onClickPreviousDay(View view) {
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        showCurrentDay();
    }

    public void onClickOpenAllTimeTable(View view) {
        Intent intent = new Intent(CompactTimetableActivity.this, AllTimetableActivity.class);
        intent.putExtra(INTENT_GROUP, group);
        intent.putExtra(INTENT_GROUP_PERSONS,eduApp.getAppData().getGroupPersons(group.groupId));
        startActivity(intent);
    }

}
