package com.educarea.mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.educarea.mobile.internet.MessageListener;

public class GroupsListActivity extends AppCompatActivity implements MessageListener {

    private EduApp eduApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_list);
        eduApp = (EduApp)getApplicationContext();

        eduApp.getInetWorker().setMessageListener(this);
    }

    @Override
    public void messageIncome(String message) {

    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
