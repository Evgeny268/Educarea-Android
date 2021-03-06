package com.educarea.mobile;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.educarea.mobile.internet.MessageListener;

import transfers.Group;
import transfers.GroupPerson;
import transfers.GroupPersons;
import transfers.TransferRequestAnswer;
import transfers.Transfers;
import transfers.TransfersFactory;
import transfers.TypeRequestAnswer;

import static com.educarea.mobile.EduApp.INTENT_GROUP;
import static com.educarea.mobile.EduApp.INTENT_GROUP_PERSONS;

public class GroupMenuActivity extends AppInetActivity implements MessageListener, TypeRequestAnswer {

    private Group group;
    private TextView groupName;
    private Button deleteGroup;
    private Button btnStudentChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_menu);
        groupName = findViewById(R.id.textViewMenuGroupName);
        eduApp = (EduApp)getApplicationContext();
        group = (Group) getIntent().getSerializableExtra(INTENT_GROUP);
        deleteGroup = findViewById(R.id.buttonDeleteGroup);
        btnStudentChat = findViewById(R.id.buttonOpenStudentChat);
        if (group == null) onBackPressed();
        groupName.setText(group.name);
        if (eduApp.moderator){
            if (!eduApp.getInetWorker().isOfflineMode()) {
                deleteGroup.setVisibility(View.VISIBLE);
            }
        }else {
            deleteGroup.setVisibility(View.GONE);
        }
        GroupPersons groupPersons = eduApp.getAppData().getGroupPersons();
        GroupPerson me = null;
        for (int i = 0; i < groupPersons.persons.size(); i++) {
            GroupPerson current = groupPersons.persons.get(i);
            if (current.userId==eduApp.getAppData().getUser().iduser){
                me = current;
            }
        }
        if (me!=null) {
            if (me.personType == 1) {
                btnStudentChat.setEnabled(false);
                btnStudentChat.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!eduApp.getInetWorker().isOfflineMode()) {
            eduApp.sendTransfers(new TransferRequestAnswer(GET_MY_GROUPS));
            eduApp.sendTransfers(new TransferRequestAnswer(GET_GROUP_PERSONS, String.valueOf(group.groupId)));
        }
    }

    @Override
    protected void newMessage(String message) {
        String data = message;
        Transfers in = TransfersFactory.createFromJSON(data);
        if (in!=null){
            if (in instanceof GroupPersons){
                eduApp.getAppData().setGroupPersons((GroupPersons) in, GroupMenuActivity.this);
            }
            else eduApp.standartReactionOnAsnwer(data,GroupMenuActivity.this);
        }else eduApp.standartReactionOnAsnwer(data,GroupMenuActivity.this);
    }


    public void onClickMembers(View view) {
        Intent intent = new Intent(GroupMenuActivity.this, PersonsActivity.class);
        intent.putExtra(INTENT_GROUP,group);
        startActivity(intent);
    }


    public void onClickOpenTimetable(View view) {
        Intent intent = new Intent(GroupMenuActivity.this, CompactTimetableActivity.class);
        intent.putExtra(INTENT_GROUP, group);
        intent.putExtra(INTENT_GROUP_PERSONS,eduApp.getAppData().getGroupPersons(group.groupId));
        startActivity(intent);
    }

    public void onClickDeleteGroup(View view) {
        Intent intent = new Intent(GroupMenuActivity.this, DeleteGroupActivity.class);
        intent.putExtra(INTENT_GROUP, group);
        startActivity(intent);
    }

    public void onClickOpenChannel(View view) {
        Intent intent = new Intent(GroupMenuActivity.this, ChannelActivity.class);
        intent.putExtra(INTENT_GROUP,group);
        intent.putExtra(INTENT_GROUP_PERSONS,eduApp.getAppData().getGroupPersons(group.groupId));
        startActivity(intent);
    }

    public void onClickOpenStudentChat(View view) {
        Intent intent = new Intent(GroupMenuActivity.this, StudentsChatActivity.class);
        intent.putExtra(INTENT_GROUP,group);
        intent.putExtra(INTENT_GROUP_PERSONS,eduApp.getAppData().getGroupPersons(group.groupId));
        startActivity(intent);
    }

    public void onClickOpenEvents(View view) {
        Intent intent = new Intent(GroupMenuActivity.this, EventsCalendarActivity.class);
        intent.putExtra(INTENT_GROUP,group);
        intent.putExtra(INTENT_GROUP_PERSONS,eduApp.getAppData().getGroupPersons(group.groupId));
        startActivity(intent);
    }

    public void onClickOpenPersonalMessages(View view) {
        Intent intent = new Intent(GroupMenuActivity.this, LastMessagesActivity.class);
        intent.putExtra(INTENT_GROUP,group);
        intent.putExtra(INTENT_GROUP_PERSONS,eduApp.getAppData().getGroupPersons(group.groupId));
        startActivity(intent);
    }
}
