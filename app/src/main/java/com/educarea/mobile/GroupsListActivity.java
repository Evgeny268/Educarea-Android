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

import com.educarea.mobile.adapters.UserGroupsAdapter;
import com.educarea.mobile.internet.MessageListener;

import transfers.Group;
import transfers.GroupPerson;
import transfers.TransferRequestAnswer;
import transfers.Transfers;
import transfers.TransfersFactory;
import transfers.TypeRequestAnswer;
import transfers.UserGroups;

import static com.educarea.mobile.EduApp.INTENT_GROUP;

public class GroupsListActivity extends AppCompatActivity implements MessageListener, TypeRequestAnswer, UserGroupsAdapter.MyGroupClickListener {

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
        eduApp.groupPersons = null;
        recyclerView = findViewById(R.id.recyclerMyGroups);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(false);
        adapter = new UserGroupsAdapter(GroupsListActivity.this,userGroups);
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
                    }else if (in instanceof TransferRequestAnswer){
                        if (((TransferRequestAnswer) in).request.equals(UPDATE_INFO)){
                            eduApp.sendTransfers(new TransferRequestAnswer(GET_MY_GROUPS));
                        }else {
                            eduApp.standartReactionOnAsnwer(data, GroupsListActivity.this);
                        }
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
    public void onClickMyGroup(int position, View view) {
        Group group= userGroups.getGroup(position);
        GroupPerson groupPerson = userGroups.getGroupPerson(group);
        if (groupPerson.moderator==1){
            eduApp.moderator = true;
        }else {
            eduApp.moderator = false;
        }
        Intent intent = new Intent(GroupsListActivity.this, GroupMenuActivity.class);
        intent.putExtra(INTENT_GROUP,group);
        startActivity(intent);
    }

    @Override
    public void onLongClickMyGroup(int position, View view) {
        showPopupMenu(view,position);
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

    private void showPopupMenu(View v, final int position){
        PopupMenu popupMenu = new PopupMenu(this,v);
        popupMenu.inflate(R.menu.my_group_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.leave_group:
                        Group group = userGroups.getGroup(position);
                        TransferRequestAnswer out = new TransferRequestAnswer(LEAVE_GROUP,String.valueOf(group.groupId));
                        eduApp.sendTransfers(out);
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    public void onClickInvites(View view) {
        startActivity(new Intent(GroupsListActivity.this,InviteActivity.class));
    }
}
