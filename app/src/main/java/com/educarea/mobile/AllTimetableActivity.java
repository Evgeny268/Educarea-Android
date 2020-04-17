package com.educarea.mobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.educarea.mobile.adapters.TimetableExpandedAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import transfers.Group;
import transfers.GroupPerson;
import transfers.GroupPersons;
import transfers.Timetable;
import transfers.Timetables;
import transfers.TransferRequestAnswer;
import transfers.Transfers;
import transfers.TransfersFactory;
import transfers.TypeRequestAnswer;

import static com.educarea.mobile.EduApp.INTENT_GROUP;
import static com.educarea.mobile.EduApp.INTENT_GROUP_PERSONS;
import static com.educarea.mobile.EduApp.INTENT_TIMETABLE;

public class AllTimetableActivity extends AppInetActivity implements TypeRequestAnswer, TimetableExpandedAdapter.TimetableExpandClickListener {

    private Group group;
    private GroupPersons groupPersons;
    private Timetables timetables = null;

    private FloatingActionButton btnAddTimetable;
    private RecyclerView recyclerView;
    private TimetableExpandedAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_timetable);
        btnAddTimetable = findViewById(R.id.btnAddTimetable);
        group = (Group) getIntent().getSerializableExtra(INTENT_GROUP);
        groupPersons = (GroupPersons) getIntent().getSerializableExtra(INTENT_GROUP_PERSONS);
        if (group == null || groupPersons == null) onBackPressed();
        recyclerView = findViewById(R.id.recyclerViewAllTimetable);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(false);
        adapter = new TimetableExpandedAdapter(this);
        recyclerView.setAdapter(adapter);
        if (cadAddTimetable()){
            btnAddTimetable.show();
        }else {
            btnAddTimetable.hide();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        eduApp.sendTransfers(new TransferRequestAnswer(GET_TIMETABLE,String.valueOf(group.groupId)));
    }

    @Override
    protected void newMessage(String message) {
        Transfers in = TransfersFactory.createFromJSON(message);
        if (in != null){
            if (in instanceof Timetables){
                timetables = (Timetables) in;
                adapter.setTimetablesAndPersons(timetables.timetables,groupPersons.persons);
                adapter.notifyDataSetChanged();
            }else if (in instanceof TransferRequestAnswer){
                if (((TransferRequestAnswer) in).request.equals(UPDATE_INFO)){
                    eduApp.sendTransfers(new TransferRequestAnswer(GET_TIMETABLE,String.valueOf(group.groupId)));
                }else eduApp.standartReactionOnAsnwer(message,this);
            }
            else eduApp.standartReactionOnAsnwer(message,this);
        }else eduApp.standartReactionOnAsnwer(message,this);
    }

    public void onClickAddTimetable(View view) {
        Intent intent = new Intent(AllTimetableActivity.this, EditTimetableActivity.class);
        intent.putExtra(INTENT_GROUP, group);
        intent.putExtra(INTENT_GROUP_PERSONS,groupPersons);
        startActivity(intent);
    }

    @Override
    public void onClickTimetableExpand(int position, View view) {
        if (canModifyTimetable(timetables.timetables.get(position))) {
            Timetable timetable = timetables.timetables.get(position);
            Intent intent = new Intent(AllTimetableActivity.this, EditTimetableActivity.class);
            intent.putExtra(INTENT_GROUP, group);
            intent.putExtra(INTENT_GROUP_PERSONS, groupPersons);
            intent.putExtra(INTENT_TIMETABLE, timetable);
            startActivity(intent);
        }else {
            Toast.makeText(this, getString(R.string.can_not_edit_object), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLongClickTimetableExpand(int position, View view) {
        if (canModifyTimetable(timetables.timetables.get(position))) {
            showPopupMenu(view, position);
        }else {
            Toast.makeText(this, getString(R.string.can_not_edit_object), Toast.LENGTH_SHORT).show();
        }
    }

    private void showPopupMenu(View v, final int position){
        PopupMenu popupMenu = new PopupMenu(this,v);
        popupMenu.inflate(R.menu.menu_delete);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_delete:
                        Timetable timetable = timetables.timetables.get(position);
                        TransferRequestAnswer out = new TransferRequestAnswer(DELETE_TIMETABLE,String.valueOf(timetable.timetableId));
                        eduApp.sendTransfers(out);
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private boolean canModifyTimetable(Timetable timetable){
        GroupPerson groupPerson = eduApp.getAppData().getUserGroups().getGroupPerson(group);
        return timetable.groupPersonId == groupPerson.groupPersonId || groupPerson.moderator == 1;
    }

    private boolean cadAddTimetable(){
        GroupPerson groupPerson = eduApp.getAppData().getUserGroups().getGroupPerson(group);
        return groupPerson.moderator == 1 || groupPerson.personType == 1;
    }

}
