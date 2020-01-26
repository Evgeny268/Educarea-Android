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

import transfers.TransferRequestAnswer;
import transfers.Transfers;
import transfers.TransfersFactory;
import transfers.TypeRequestAnswer;
import transfers.UserGroups;

public class GroupsListActivity extends AppCompatActivity implements MessageListener, TypeRequestAnswer {

    private EduApp eduApp;
    private RecyclerView recyclerView;
    private UserGroupsAdapter adapter;
    private LinearLayoutManager manager;
    private UserGroups userGroups;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_list);
        userGroups = new UserGroups();
        eduApp = (EduApp)getApplicationContext();
        recyclerView = findViewById(R.id.recyclerMyGroups);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(false);
        adapter = new UserGroupsAdapter(this,userGroups);
        recyclerView.setAdapter(adapter);
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
                    if (in instanceof UserGroups){
                        userGroups.clear();
                        userGroups.add((UserGroups)in);
                        adapter.notifyDataSetChanged();
                    }
                }else {
                    eduApp.standartReactionOnAsnwer(data, GroupsListActivity.this);
                }
            }
        };
        eduApp.getInetWorker().setMessageListener(this);
        eduApp.sendTransfers(new TransferRequestAnswer(GET_MY_GROUPS));
    }

    @Override
    public void messageIncome(String message) {
        Message message1 = handler.obtainMessage(0,message);
        message1.sendToTarget();
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    public void onClickAddGroup(View view) {
        startActivity(new Intent(GroupsListActivity.this, AddGroupActivity.class));
    }
}
