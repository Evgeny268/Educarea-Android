package com.educarea.mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.educarea.mobile.internet.MessageListener;

import transfers.Authorization;
import transfers.TransferRequestAnswer;
import transfers.Transfers;
import transfers.TransfersFactory;
import transfers.TypeRequestAnswer;
import transfers.User;

public class MainActivity extends AppCompatActivity implements TypeRequestAnswer, MessageListener {

    private TextView textVersion;
    private Button buttonReconnect;
    private EduApp eduApp;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        eduApp = (EduApp)getApplicationContext();
        textVersion = findViewById(R.id.textViewVersion);
        textVersion.setText(getResources().getString(R.string.app_version)+" "+BuildConfig.VERSION_NAME);
        buttonReconnect = findViewById(R.id.buttonReconnect);
        if (eduApp.getInetWorker()==null) {
            AppStart appStart = new AppStart();
            appStart.execute();
        }else if (!eduApp.getInetWorker().isConnected()){
            eduApp.getInetWorker().setMessageListener(this);
            AppStart appStart = new AppStart();
            appStart.execute();
        }else {
            String auth_token = eduApp.getUser_token();
            eduApp.getInetWorker().setMessageListener(MainActivity.this);
            if (auth_token==null){
                Intent intent = new Intent(MainActivity.this,LogRegActivity.class);
                startActivity(intent);
            }else {
                Authorization out = new Authorization(auth_token);
                eduApp.sendTransfers(out);
            }
        }

        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String data = (String) msg.obj;
                Transfers in = TransfersFactory.createFromJSON(data);
                if (in != null) {
                    if (in instanceof TransferRequestAnswer) {
                        if (((TransferRequestAnswer) in).request.equals(AUTHORIZATION_DONE)) {
                            eduApp.user = new User(Integer.parseInt(((TransferRequestAnswer) in).extra),((TransferRequestAnswer) in).extraArr[0]);
                            startActivity(new Intent(MainActivity.this, GroupsListActivity.class));
                        }else if (((TransferRequestAnswer) in).request.equals(LOGOUT)){
                            eduApp.saveToken("");
                            Intent intent = new Intent(MainActivity.this,LogRegActivity.class);
                            startActivity(intent);
                        }
                        else {
                            eduApp.standartReactionOnAsnwer(data, MainActivity.this);
                        }
                    }else {
                        eduApp.standartReactionOnAsnwer(data,MainActivity.this);
                    }
                }else {
                    eduApp.standartReactionOnAsnwer(data, MainActivity.this);
                }
            }
        };
    }

    @Override
    public void messageIncome(String message) {
        Message message1 = handler.obtainMessage(0,message);
        message1.sendToTarget();
    }

    public void onClickReconnect(View view) {
        buttonReconnect.setEnabled(false);
        buttonReconnect.setVisibility(View.GONE);
        AppStart appStart = new AppStart();
        appStart.execute();
    }

    class AppStart extends AsyncTask<Void,Void,Boolean>{
        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean connect = eduApp.initInternet();
            return connect;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean){
                String auth_token = eduApp.getUser_token();
                eduApp.getInetWorker().setMessageListener(MainActivity.this);
                if (auth_token==null){
                    Intent intent = new Intent(MainActivity.this,LogRegActivity.class);
                    startActivity(intent);
                }else {
                    Authorization out = new Authorization(auth_token);
                    eduApp.sendTransfers(out);
                }
            }else {
                Toast.makeText(MainActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                buttonReconnect.setVisibility(View.VISIBLE);
                buttonReconnect.setEnabled(true);
            }
        }
    }
}
