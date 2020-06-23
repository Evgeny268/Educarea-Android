package com.educarea.mobile;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.educarea.mobile.adapters.EventAdapter;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import transfers.DateObject;
import transfers.Event;
import transfers.EventList;
import transfers.Group;
import transfers.GroupPerson;
import transfers.GroupPersons;
import transfers.TransferRequestAnswer;
import transfers.Transfers;
import transfers.TransfersFactory;
import transfers.TypeRequestAnswer;

import static com.educarea.mobile.EduApp.INTENT_GROUP;
import static com.educarea.mobile.EduApp.INTENT_GROUP_PERSONS;

public class EventsCalendarActivity extends AppInetActivity implements TypeRequestAnswer, EventAdapter.EventClickListener, CompactCalendarView.CompactCalendarViewListener {

    private CompactCalendarView calendarView;
    private Toolbar toolbar;
    private Group group;
    private GroupPersons groupPersons;
    private FloatingActionButton buttonAddEvent;
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<DateObject> events;
    private List<DateObject> currentDateEvents;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_calendar);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.events_calendar));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        calendarView = findViewById(R.id.eventsCalendar);
        calendarView.setListener(this);
        group = (Group) getIntent().getSerializableExtra(INTENT_GROUP);
        groupPersons = (GroupPersons) getIntent().getSerializableExtra(INTENT_GROUP_PERSONS);
        if (group == null || groupPersons == null) onBackPressed();
        calendar = Calendar.getInstance();
        //Event test = new Event(ContextCompat.getColor(this,R.color.extraOne), date.getTime(), "Тестовое событие");
        //calendarView.addEvent(test);
        recyclerView = findViewById(R.id.recyclerViewEvents);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(false);
        adapter = new EventAdapter(this);
        recyclerView.setAdapter(adapter);
        buttonAddEvent = findViewById(R.id.buttonAddEvent);
        if (canAddEvent()) {
            buttonAddEvent.show();
        } else {
            buttonAddEvent.hide();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        eduApp.sendTransfers(new TransferRequestAnswer(GET_EVENTS, String.valueOf(group.groupId)));
    }

    @Override
    protected void newMessage(String message) {
        Transfers in = TransfersFactory.createFromJSON(message);
        if (in != null) {
            if (in instanceof EventList) {
                events = new ArrayList<>();
                events.addAll(((EventList) in).events);
                Collections.sort(events);
                showCurrentDay();
                drawEventsOnCalendar();
            } else if (in instanceof TransferRequestAnswer) {
                if (((TransferRequestAnswer) in).request.equals(UPDATE_INFO)) {
                    eduApp.sendTransfers(new TransferRequestAnswer(GET_EVENTS, String.valueOf(group.groupId)));
                } else eduApp.standartReactionOnAsnwer(message, EventsCalendarActivity.this);
            } else eduApp.standartReactionOnAsnwer(message, EventsCalendarActivity.this);
        } else eduApp.standartReactionOnAsnwer(message, EventsCalendarActivity.this);
    }

    @Override
    public void onDayClick(Date dateClicked) {
        calendar.setTime(dateClicked);
        showCurrentDay();
    }

    @Override
    public void onMonthScroll(Date firstDayOfNewMonth) {
        calendar.setTime(firstDayOfNewMonth);
        showCurrentDay();
    }

    public void drawEventsOnCalendar(){
        calendarView.removeAllEvents();
        for (DateObject ev: events){
            if (ev instanceof Event){
                com.github.sundeepk.compactcalendarview.domain.Event calendarEvent =
                        new com.github.sundeepk.compactcalendarview.domain.Event(
                                ContextCompat.getColor(this,R.color.colorPrimaryDark), ev.date.getTime(), ((Event) ev).title
                        );
                calendarView.addEvent(calendarEvent);
            }
        }
    }

    public void showCurrentDay(){
        currentDateEvents = getCurrentDayEvents(events, calendar);
        adapter.setEventsAndGroupPersons(currentDateEvents, groupPersons.persons);
        adapter.notifyDataSetChanged();
    }

    public List<DateObject> getCurrentDayEvents(List<DateObject> events, Calendar day) {
        List<DateObject> currentDateEvents = new ArrayList<>();
        for (DateObject curEv : events) {
            Calendar eventDay = Calendar.getInstance();
            eventDay.setTime(curEv.date);
            if (eventDay.get(Calendar.YEAR) == day.get(Calendar.YEAR) &&
            eventDay.get(Calendar.MONTH) == day.get(Calendar.MONTH) &&
            eventDay.get(Calendar.DAY_OF_MONTH) == day.get(Calendar.DAY_OF_MONTH)){
                currentDateEvents.add(curEv);
            }
        }
        return currentDateEvents;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_all_events, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_open_all_events) {
            Intent intent = new Intent(EventsCalendarActivity.this, AllEventsActivity.class);
            intent.putExtra(INTENT_GROUP, group);
            intent.putExtra(INTENT_GROUP_PERSONS, eduApp.getAppData().getGroupPersons(group.groupId));
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onClickAddEvent(View view) {
        Intent intent = new Intent(EventsCalendarActivity.this, AddEventActivity.class);
        intent.putExtra(INTENT_GROUP, group);
        intent.putExtra(INTENT_GROUP_PERSONS, eduApp.getAppData().getGroupPersons(group.groupId));
        startActivity(intent);
    }

    @Override
    public void onClickEvent(int position, View view) {

    }

    @Override
    public void onLongClickEvent(int position, View view) {
        DateObject data = currentDateEvents.get(position);
        if (data instanceof Event) {
            if (canModifyEvent((Event) data)) {
                showPopupMenu(view, position);
            }
        }
    }

    private void showPopupMenu(View v, final int position) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.menu_delete);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        DateObject dateObject = currentDateEvents.get(position);
                        if (dateObject instanceof Event) {
                            TransferRequestAnswer out = new TransferRequestAnswer(DELETE_EVENT, String.valueOf(((Event) dateObject).eventId));
                            eduApp.sendTransfers(out);
                        }
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private boolean canAddEvent() {
        GroupPerson groupPerson = eduApp.getAppData().getUserGroups().getGroupPerson(group);
        return groupPerson.moderator == 1 || groupPerson.personType == 1;
    }

    private boolean canModifyEvent(Event event) {
        GroupPerson groupPerson = eduApp.getAppData().getUserGroups().getGroupPerson(group);
        if (groupPerson.moderator == 1) {
            return true;
        } else {
            if (event.groupPersonId == null) {
                return false;
            } else return event.groupPersonId == groupPerson.groupPersonId;
        }
    }
}