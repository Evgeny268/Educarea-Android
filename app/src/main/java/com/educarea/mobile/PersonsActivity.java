package com.educarea.mobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

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

public class PersonsActivity extends AppCompatActivity implements MessageListener, TypeRequestAnswer, GroupPersonAdapter.GroupPersonClickListener {

    private EduApp eduApp;
    private Handler handler;
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
        adapter = new GroupPersonAdapter(PersonsActivity.this, persons,eduApp.user.iduser);
        recyclerView.setAdapter(adapter);
        if (eduApp.moderator){
            addPersonBtn.show();
        }else {
            addPersonBtn.hide();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String data = (String)msg.obj;
                Transfers in = TransfersFactory.createFromJSON(data);
                if (in!=null){
                    if (in instanceof GroupPersons){
                        persons = (GroupPersons) in;
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
        };
        eduApp.getInetWorker().setMessageListener(this);
        eduApp.sendTransfers(new TransferRequestAnswer(GET_GROUP_PERSONS, String.valueOf(group.groupId)));
    }

    @Override
    public void messageIncome(String message) {
        Message message1 = handler.obtainMessage(0,message);
        message1.sendToTarget();
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

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PersonsActivity.this, GroupMenuActivity.class);
        intent.putExtra(INTENT_GROUP,group);
        startActivity(intent);
    }
}
