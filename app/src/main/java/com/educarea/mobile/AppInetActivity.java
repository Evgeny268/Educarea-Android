package com.educarea.mobile;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.educarea.mobile.internet.MessageListener;

public abstract class AppInetActivity extends AppCompatActivity implements MessageListener {

    protected EduApp eduApp;
    protected Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eduApp = (EduApp)getApplicationContext();
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String message = (String) msg.obj;
                newMessage(message);
            }
        };
        eduApp.getInetWorker().setMessageListener(AppInetActivity.this);
    }

    @Override
    public void messageIncome(String message) {
        Message message1 = handler.obtainMessage(0,message);
        message1.sendToTarget();
    }

    protected abstract void newMessage(String message);
}
