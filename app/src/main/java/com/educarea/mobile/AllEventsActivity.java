package com.educarea.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.educarea.mobile.adapters.EventAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import transfers.DateObject;
import transfers.Event;
import transfers.EventList;
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

public class AllEventsActivity extends AppInetActivity implements TypeRequestAnswer, EventAdapter.EventClickListener {

    private Group group;
    private GroupPersons groupPersons;
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<DateObject> events;
    private FloatingActionButton buttonAddEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_events);
        group = (Group) getIntent().getSerializableExtra(INTENT_GROUP);
        groupPersons = (GroupPersons) getIntent().getSerializableExtra(INTENT_GROUP_PERSONS);
        if (group==null || groupPersons==null) onBackPressed();
        recyclerView = findViewById(R.id.recyclerViewEvents);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(false);
        adapter = new EventAdapter(this);
        recyclerView.setAdapter(adapter);
        buttonAddEvent = findViewById(R.id.buttonAddEvent);
        if (canAddEvent()){
            buttonAddEvent.show();
        }else {
            buttonAddEvent.hide();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        eduApp.sendTransfers(new TransferRequestAnswer(GET_EVENTS,String.valueOf(group.groupId)));
    }


    @Override
    protected void newMessage(String message) {
        Transfers in = TransfersFactory.createFromJSON(message);
        if (in!=null){
            if (in instanceof EventList){
                events = new ArrayList<>();
                events.addAll(((EventList) in).events);
                Collections.sort(events);
                adapter.setEventsAndGroupPersons(events, groupPersons.persons);
                adapter.notifyDataSetChanged();
            } else if (in instanceof TransferRequestAnswer){
                if (((TransferRequestAnswer) in).request.equals(UPDATE_INFO)){
                    eduApp.sendTransfers(new TransferRequestAnswer(GET_EVENTS,String.valueOf(group.groupId)));
                } else eduApp.standartReactionOnAsnwer(message, AllEventsActivity.this);
            } else eduApp.standartReactionOnAsnwer(message, AllEventsActivity.this);
        } else eduApp.standartReactionOnAsnwer(message, AllEventsActivity.this);
    }

    @Override
    public void onClickEvent(int position, View view) {

    }

    @Override
    public void onLongClickEvent(int position, View view) {
        DateObject data = events.get(position);
        if (data instanceof Event){
            if (canModifyEvent((Event) data)){
                showPopupMenu(view, position);
            }
        }
    }

    public void onClickAddEvent(View view) {
        Intent intent = new Intent(AllEventsActivity.this, AddEventActivity.class);
        intent.putExtra(INTENT_GROUP,group);
        intent.putExtra(INTENT_GROUP_PERSONS,eduApp.getAppData().getGroupPersons(group.groupId));
        startActivity(intent);
    }

    private void showPopupMenu(View v, final int position){
        PopupMenu popupMenu = new PopupMenu(this,v);
        popupMenu.inflate(R.menu.menu_delete);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_delete:
                        DateObject dateObject = events.get(position);
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

    private boolean canAddEvent(){
        GroupPerson groupPerson = eduApp.getAppData().getUserGroups().getGroupPerson(group);
        return groupPerson.moderator == 1 || groupPerson.personType == 1;
    }

    private boolean canModifyEvent(Event event){
        GroupPerson groupPerson = eduApp.getAppData().getUserGroups().getGroupPerson(group);
        if (groupPerson.moderator == 1){
            return true;
        }else {
            if (event.groupPersonId == null){
                return false;
            }else return event.groupPersonId == groupPerson.groupPersonId;
        }
    }
}