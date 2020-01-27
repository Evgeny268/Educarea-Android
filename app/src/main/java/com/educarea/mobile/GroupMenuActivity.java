package com.educarea.mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.TextView;

import com.educarea.mobile.internet.MessageListener;

import transfers.Group;
import transfers.TransferRequestAnswer;
import transfers.TypeRequestAnswer;

import static com.educarea.mobile.EduApp.INTENT_GROUP;

public class GroupMenuActivity extends AppCompatActivity implements MessageListener, TypeRequestAnswer {

    private EduApp eduApp;
    private Handler handler;
    private Group group;
    private TextView groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_menu);
        groupName = findViewById(R.id.textViewMenuGroupName);
        eduApp = (EduApp)getApplicationContext();
        group = (Group) getIntent().getSerializableExtra(INTENT_GROUP);
        if (group == null) onBackPressed();
        groupName.setText(group.name);
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String data = (String)msg.obj;
                eduApp.standartReactionOnAsnwer(data,GroupMenuActivity.this);
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
}
