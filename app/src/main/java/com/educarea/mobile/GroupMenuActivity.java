package com.educarea.mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.educarea.mobile.internet.MessageListener;

import transfers.Group;
import transfers.GroupPersons;
import transfers.TransferRequestAnswer;
import transfers.Transfers;
import transfers.TransfersFactory;
import transfers.TypeRequestAnswer;

import static com.educarea.mobile.EduApp.INTENT_GROUP;
import static com.educarea.mobile.EduApp.INTENT_GROUP_PERSONS;

public class GroupMenuActivity extends AppCompatActivity implements MessageListener, TypeRequestAnswer {

    private EduApp eduApp;
    private Handler handler;
    private Group group;
    private TextView groupName;
    private Button deleteGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_menu);
        groupName = findViewById(R.id.textViewMenuGroupName);
        eduApp = (EduApp)getApplicationContext();
        group = (Group) getIntent().getSerializableExtra(INTENT_GROUP);
        deleteGroup = findViewById(R.id.buttonDeleteGroup);
        if (group == null) onBackPressed();
        groupName.setText(group.name);
        if (eduApp.moderator){
            deleteGroup.setVisibility(View.VISIBLE);
        }else {
            deleteGroup.setVisibility(View.GONE);
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
                        eduApp.groupPersons = (GroupPersons) in;
                    }else if (in instanceof TransferRequestAnswer){
                        if (((TransferRequestAnswer) in).request.equals(DELETE_GROUP)){
                            startActivity(new Intent(GroupMenuActivity.this,GroupsListActivity.class));
                            finish();
                        }
                    }
                    else eduApp.standartReactionOnAsnwer(data,GroupMenuActivity.this);
                }else eduApp.standartReactionOnAsnwer(data,GroupMenuActivity.this);

            }
        };
        eduApp.getInetWorker().setMessageListener(this);
        eduApp.sendTransfers(new TransferRequestAnswer(GET_MY_GROUPS));
        eduApp.sendTransfers(new TransferRequestAnswer(GET_GROUP_PERSONS,String.valueOf(group.groupId)));
    }

    @Override
    public void messageIncome(String message) {
        Message message1 = handler.obtainMessage(0,message);
        message1.sendToTarget();
    }

    public void onClickMembers(View view) {
        Intent intent = new Intent(GroupMenuActivity.this, PersonsActivity.class);
        intent.putExtra(INTENT_GROUP,group);
        startActivity(intent);
    }


    public void onClickOpenTimetable(View view) {
        Intent intent = new Intent(GroupMenuActivity.this, CompactTimetableActivity.class);
        intent.putExtra(INTENT_GROUP, group);
        intent.putExtra(INTENT_GROUP_PERSONS,eduApp.groupPersons);
        startActivity(intent);
    }

    public void onClickDeleteGroup(View view) {
        if (eduApp.moderator){
            eduApp.sendTransfers(new TransferRequestAnswer(DELETE_GROUP,String.valueOf(group.groupId)));
        }
    }

    public void onClickOpenChannel(View view) {
        Intent intent = new Intent(GroupMenuActivity.this, ChannelActivity.class);
        intent.putExtra(INTENT_GROUP,group);
        intent.putExtra(INTENT_GROUP_PERSONS,eduApp.groupPersons);
        startActivity(intent);
    }
}
