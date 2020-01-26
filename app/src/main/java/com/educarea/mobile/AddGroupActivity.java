package com.educarea.mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import com.educarea.mobile.internet.MessageListener;

import transfers.TransferRequestAnswer;
import transfers.Transfers;
import transfers.TransfersFactory;
import transfers.TypeRequestAnswer;

public class AddGroupActivity extends AppCompatActivity implements MessageListener, TypeRequestAnswer {

    private EduApp eduApp;
    private Handler handler;
    private EditText groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        eduApp = (EduApp)getApplicationContext();
        groupName = findViewById(R.id.editTextNewGroupName);
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.obj==null) return;
                String data = (String)msg.obj;
                Transfers in = TransfersFactory.createFromJSON(data);
                if (in != null){
                    if (in instanceof TransferRequestAnswer){
                        if (((TransferRequestAnswer) in).request.equals(GROUP_ADDED)){
                            startActivity(new Intent(AddGroupActivity.this, GroupsListActivity.class));
                        }else eduApp.standartReactionOnAsnwer(data, AddGroupActivity.this);
                    }else eduApp.standartReactionOnAsnwer(data, AddGroupActivity.this);
                }else eduApp.standartReactionOnAsnwer(data, AddGroupActivity.this);
            }
        };
        eduApp.getInetWorker().setMessageListener(this);
    }

    @Override
    public void messageIncome(String message) {
        Message message1 = handler.obtainMessage(0,message);
        message1.sendToTarget();
    }

    public void onClickCreateNewGroup(View view) {
        String name = groupName.getText().toString();
        if (name.equals("")) return;
        TransferRequestAnswer out = new TransferRequestAnswer(CREATE_GROUP,name);
        eduApp.sendTransfers(out);
    }
}
