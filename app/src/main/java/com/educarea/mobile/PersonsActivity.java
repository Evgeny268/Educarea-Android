package com.educarea.mobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.educarea.mobile.adapters.GroupPersonAdapter;
import com.educarea.mobile.internet.MessageListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import transfers.Group;
import transfers.GroupPerson;
import transfers.GroupPersons;
import transfers.TransferRequestAnswer;
import transfers.Transfers;
import transfers.TransfersFactory;
import transfers.TypeRequestAnswer;

import static com.educarea.mobile.EduApp.INTENT_GROUP;
import static com.educarea.mobile.EduApp.INTENT_GROUP_PERSON;

public class PersonsActivity extends AppInetActivity implements MessageListener, TypeRequestAnswer, GroupPersonAdapter.GroupPersonClickListener {

    private Group group;
    private GroupPersons persons = null;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private GroupPersonAdapter adapter;
    private FloatingActionButton addPersonBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persons);
        persons = new GroupPersons();
        eduApp = (EduApp)getApplicationContext();
        group = (Group) getIntent().getSerializableExtra(INTENT_GROUP);
        if (group == null) onBackPressed();
        addPersonBtn = findViewById(R.id.btnAddPerson);
        recyclerView = findViewById(R.id.recyclerPerson);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(false);
        adapter = new GroupPersonAdapter(PersonsActivity.this, eduApp.getAppData().getUser().iduser);
        recyclerView.setAdapter(adapter);
        if (eduApp.moderator){
            if (!eduApp.getInetWorker().isOfflineMode()) {
                addPersonBtn.show();
            }
        }else {
            addPersonBtn.hide();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        persons = eduApp.getAppData().getGroupPersons(group.groupId);
        adapter.setGroupPersons(persons);
        adapter.notifyDataSetChanged();
        if (!eduApp.getInetWorker().isOfflineMode()) {
            eduApp.sendTransfers(new TransferRequestAnswer(GET_GROUP_PERSONS, String.valueOf(group.groupId)));
        }
    }

    @Override
    protected void newMessage(String message) {
        String data = message;
        Transfers in = TransfersFactory.createFromJSON(data);
        if (in!=null){
            if (in instanceof GroupPersons){
                persons = (GroupPersons) in;
                eduApp.getAppData().setGroupPersons(persons,this);
                adapter.setGroupPersons(persons);
                adapter.notifyDataSetChanged();
            }else if (in instanceof TransferRequestAnswer){
                if (((TransferRequestAnswer) in).request.equals(UPDATE_INFO)){
                    eduApp.sendTransfers(new TransferRequestAnswer(GET_GROUP_PERSONS, String.valueOf(group.groupId)));
                }else eduApp.standartReactionOnAsnwer(data, PersonsActivity.this);
            }else eduApp.standartReactionOnAsnwer(data, PersonsActivity.this);
        }else {
            eduApp.standartReactionOnAsnwer(data, PersonsActivity.this);
        }
    }


    public void onClickAddPerson(View view) {
        Intent intent = new Intent(PersonsActivity.this, AddPersonActivity.class);
        intent.putExtra(INTENT_GROUP,group);
        startActivity(intent);
    }

    @Override
    public void onClickGroupPerson(int position, View view) {
        Intent intent = new Intent(PersonsActivity.this, PersonActivity.class);
        intent.putExtra(INTENT_GROUP, group);
        GroupPerson person = persons.persons.get(position);
        intent.putExtra(INTENT_GROUP_PERSON,person);
        startActivity(intent);

    }

    @Override
    public void onLongClickGroupPerson(int position, View view) {
        if (eduApp.moderator) {
            showPopupMenu(view, position);
        }
    }


    private void showPopupMenu(View v, final int position){
        PopupMenu popupMenu = new PopupMenu(this,v);
        popupMenu.inflate(R.menu.menu_delete);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_delete: {
                        if (!eduApp.getInetWorker().isOfflineMode()) {
                            GroupPerson groupPerson = persons.persons.get(position);
                            TransferRequestAnswer out = new TransferRequestAnswer(DELETE_PERSON, String.valueOf(groupPerson.groupPersonId));
                            eduApp.sendTransfers(out);
                        }else {
                            Toast.makeText(PersonsActivity.this, getString(R.string.offline_alert), Toast.LENGTH_SHORT).show();
                        }
                    }
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }
}
