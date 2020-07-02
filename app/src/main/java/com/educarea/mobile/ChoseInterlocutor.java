package com.educarea.mobile;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.educarea.mobile.adapters.GroupPersonAdapter;

import java.util.Objects;

import transfers.Group;
import transfers.GroupPerson;
import transfers.GroupPersons;
import transfers.TransferRequestAnswer;
import transfers.Transfers;
import transfers.TransfersFactory;
import transfers.TypeRequestAnswer;

import static com.educarea.mobile.EduApp.INTENT_GROUP;
import static com.educarea.mobile.EduApp.INTENT_GROUP_PERSON;
import static com.educarea.mobile.EduApp.INTENT_GROUP_PERSONS;

public class ChoseInterlocutor extends AppInetActivity implements TypeRequestAnswer, GroupPersonAdapter.GroupPersonClickListener {

    private Toolbar toolbar;
    private GroupPersons persons = null;
    private RecyclerView recyclerView;
    private GroupPersonAdapter adapter;
    private Group group;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose_interlocutor);
        group = (Group) getIntent().getSerializableExtra(INTENT_GROUP);
        if (group == null) onBackPressed();
        GroupPerson groupPerson = eduApp.getAppData().getUserGroups().getGroupPerson(group);
        toolbar = findViewById(R.id.toolbarChoseInterlocutor);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.chose_interlocutor));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclerView = findViewById(R.id.rvChoseInterlocutor);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new GroupPersonAdapter(this, groupPerson.userId);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        eduApp.sendTransfers(new TransferRequestAnswer(GET_GROUP_PERSONS, String.valueOf(group.groupId)));
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
                }else eduApp.standartReactionOnAsnwer(data, ChoseInterlocutor.this);
            }else eduApp.standartReactionOnAsnwer(data, ChoseInterlocutor.this);
        }else {
            eduApp.standartReactionOnAsnwer(data, ChoseInterlocutor.this);
        }
    }

    @Override
    public void onClickGroupPerson(int position, View view) {
        GroupPerson interlocutor = persons.persons.get(position);
        Intent intent = new Intent(ChoseInterlocutor.this, DialogActivity.class);
        intent.putExtra(INTENT_GROUP,group);
        intent.putExtra(INTENT_GROUP_PERSONS,eduApp.getAppData().getGroupPersons(group.groupId));
        intent.putExtra(INTENT_GROUP_PERSON, interlocutor);
        startActivity(intent);
    }

    @Override
    public void onLongClickGroupPerson(int position, View view) {

    }
}