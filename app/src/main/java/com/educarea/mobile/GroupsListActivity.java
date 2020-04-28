package com.educarea.mobile;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.educarea.mobile.adapters.UserGroupsAdapter;
import com.educarea.mobile.dialogs.UpdateDialogInfo;
import com.educarea.mobile.internet.MessageListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import transfers.Group;
import transfers.GroupPerson;
import transfers.TransferRequestAnswer;
import transfers.Transfers;
import transfers.TransfersFactory;
import transfers.TypeRequestAnswer;
import transfers.UserGroups;

import static com.educarea.mobile.EduApp.APP_LAST_VERSION;
import static com.educarea.mobile.EduApp.APP_PREFERENCES;
import static com.educarea.mobile.EduApp.INTENT_GROUP;

public class GroupsListActivity extends AppInetActivity implements MessageListener, TypeRequestAnswer, UserGroupsAdapter.MyGroupClickListener {

    private RecyclerView recyclerView;
    private UserGroupsAdapter adapter;
    private LinearLayoutManager manager;
    private FloatingActionButton btnAddGroup;
    private Button btnInvites;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_list);
        toolbar = findViewById(R.id.toolbarGroupList);
        setSupportActionBar(toolbar);
        eduApp = (EduApp)getApplicationContext();
        recyclerView = findViewById(R.id.recyclerMyGroups);
        btnAddGroup = findViewById(R.id.floatingButtonAddGroup);
        btnInvites = findViewById(R.id.buttonEnterCode);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(false);
        adapter = new UserGroupsAdapter(GroupsListActivity.this);
        recyclerView.setAdapter(adapter);
        if (eduApp.getInetWorker().isOfflineMode()){
            btnAddGroup.hide();
            btnInvites.setVisibility(View.GONE);
        }
        showUpdateInfoIfNewVersion();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.setUserGroups(eduApp.getAppData().getUserGroups());
        adapter.notifyDataSetChanged();
        if (!eduApp.getInetWorker().isOfflineMode()) {
            eduApp.sendTransfers(new TransferRequestAnswer(GET_MY_GROUPS));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_account_item) {
            //TODO open account activity
        }
        return true;
    }

    @Override
    protected void newMessage(String message) {
        String data = message;
        Transfers in = TransfersFactory.createFromJSON(data);
        if (in!=null){
            if (in instanceof UserGroups){
                eduApp.getAppData().setUserGroups((UserGroups) in,this);
                adapter.setUserGroups(eduApp.getAppData().getUserGroups());
                adapter.notifyDataSetChanged();
                for (int i = 0; i < ((UserGroups) in).groups.size(); i++) {
                    String groupId = String.valueOf(((UserGroups) in).groups.get(i).groupId);
                    eduApp.sendTransfers(new TransferRequestAnswer(GET_GROUP_PERSONS,groupId));
                    eduApp.sendTransfers(new TransferRequestAnswer(GET_TIMETABLE,groupId));
                }
            }else if (in instanceof TransferRequestAnswer){
                if (((TransferRequestAnswer) in).request.equals(UPDATE_INFO)){
                    eduApp.sendTransfers(new TransferRequestAnswer(GET_MY_GROUPS));
                }else {
                    eduApp.standartReactionOnAsnwer(data, GroupsListActivity.this);
                }
            }else eduApp.standartReactionOnAsnwer(data, GroupsListActivity.this);
        }else {
            eduApp.standartReactionOnAsnwer(data, GroupsListActivity.this);
        }
    }


    @Override
    public void onClickMyGroup(int position, View view) {
        Group group = eduApp.getAppData().getUserGroups().getGroup(position);
        GroupPerson groupPerson = eduApp.getAppData().getUserGroups().getGroupPerson(group);
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
                    case R.id.leave_group: {
                        if (eduApp.getInetWorker().isOfflineMode()){
                            Toast.makeText(GroupsListActivity.this, getString(R.string.offline_alert), Toast.LENGTH_SHORT).show();
                        }else {
                            Group group = eduApp.getAppData().getUserGroups().getGroup(position);
                            TransferRequestAnswer out = new TransferRequestAnswer(LEAVE_GROUP, String.valueOf(group.groupId));
                            eduApp.sendTransfers(out);
                        }
                    }
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    public void onClickInvites(View view) {
        startActivity(new Intent(GroupsListActivity.this,EnterCodeActivity.class));
    }

    public void showUpdateInfoIfNewVersion(){
        SharedPreferences mSetting = getApplicationContext().getSharedPreferences(APP_PREFERENCES,MODE_PRIVATE);
        if (mSetting.contains(APP_LAST_VERSION)){
            int lastVer = mSetting.getInt(APP_LAST_VERSION,0);
            if (lastVer < BuildConfig.VERSION_CODE){
                UpdateDialogInfo dialog = new UpdateDialogInfo();
                dialog.show(getSupportFragmentManager(),"DIALOG_UPDATE_INFO");
            }
        }else {
            UpdateDialogInfo dialog = new UpdateDialogInfo();
            dialog.show(getSupportFragmentManager(),"DIALOG_UPDATE_INFO");
        }
        SharedPreferences.Editor editor = mSetting.edit();
        editor.putInt(APP_LAST_VERSION, BuildConfig.VERSION_CODE);
        editor.apply();
    }
}
