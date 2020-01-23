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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.educarea.mobile.internet.MessageListener;

import transfers.Authentication;
import transfers.Registration;
import transfers.TransferRequestAnswer;
import transfers.Transfers;
import transfers.TransfersFactory;
import transfers.TypeRequestAnswer;

public class InputLogPassActivity extends AppCompatActivity implements TypeRequestAnswer, MessageListener {

    public static final String ENTER_MODE = "ENTER_MODE";
    public static final int MODE_SIGN_UP = 0;
    public static final int MODE_LOG_IN = 1;
    private EduApp eduApp;
    private int currentMode;
    private TextView textViewInputMode;
    private TextView textVersion;
    private EditText editTextLogin;
    private EditText editTextPassword;
    private Button buttonNext;
    private Handler handler;
    private LogRegWorker logRegWorker = null;
    private String login;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_log_pass);
        textVersion = findViewById(R.id.textViewVersion);
        textVersion.setText(getResources().getString(R.string.app_version)+" "+BuildConfig.VERSION_NAME);
        eduApp = (EduApp)getApplicationContext();
        textViewInputMode = findViewById(R.id.textViewInputMode);
        buttonNext = findViewById(R.id.buttonEnterDone);
        editTextLogin = findViewById(R.id.editTextLogin);
        editTextPassword = findViewById(R.id.editTextPassword);
        currentMode = getIntent().getIntExtra(ENTER_MODE,MODE_SIGN_UP);
        if (currentMode == MODE_SIGN_UP){
            textViewInputMode.setText(getString(R.string.sign_up));
        }else {
            textViewInputMode.setText(getString(R.string.log_in));
        }
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (logRegWorker!=null){
                    logRegWorker.cancel(true);
                }
                if (msg!=null){
                    String data = (String) msg.obj;
                    Transfers in = TransfersFactory.createFromJSON(data);
                    if (in != null) {
                        if (in instanceof TransferRequestAnswer) {
                            if (((TransferRequestAnswer) in).request.equals(AUTHENTICATION_DONE)) {
                                eduApp.saveToken(((TransferRequestAnswer) in).extra);
                                startActivity(new Intent(InputLogPassActivity.this, MainActivity.class));
                            } else if (((TransferRequestAnswer) in).request.equals(REGISTRATION_DONE)) {
                                Transfers out = new Authentication(login, password);
                                if (!eduApp.sendTransfers(out)) {
                                    logRegWorker.cancel(true);
                                }
                            } else {
                                eduApp.standartReactionOnAsnwer(data, InputLogPassActivity.this);
                            }
                        } else {
                            eduApp.standartReactionOnAsnwer(data, InputLogPassActivity.this);
                        }
                    } else {
                        eduApp.standartReactionOnAsnwer(data, InputLogPassActivity.this);
                    }
                }
                try {
                    logRegWorker.cancel(true);
                }catch (NullPointerException e){

                }
            }
        };
        eduApp.getInetWorker().setMessageListener(this);
    }




    @Override
    public void messageIncome(String message) {
        Message message1 = handler.obtainMessage(0,message);
        message1.sendToTarget();
    }

    public void onClickNext(View view){
        lockInterface();
        login = editTextLogin.getText().toString();
        password = editTextPassword.getText().toString();
        logRegWorker = new LogRegWorker();
        logRegWorker.execute();
        Transfers out;
        if (currentMode==MODE_SIGN_UP){
            out = new Registration(login,password);
        }else {
            out = new Authentication(login,password);
        }
        if (!eduApp.sendTransfers(out)){
            logRegWorker.cancel(true);
        }
    }

    private void lockInterface(){
        buttonNext.setEnabled(false);
        editTextLogin.setEnabled(false);
        editTextPassword.setEnabled(false);

    }

    private void unlockInterface(){
        buttonNext.setEnabled(true);
        editTextLogin.setEnabled(true);
        editTextPassword.setEnabled(true);
    }

    public static boolean checkLogin(String login){
        if (login.length()==0) return false;
        if (login.length()>30) return false;
        return login.matches("\\w+");
    }

    public static boolean checkPassword(String password){
        if (password.length()==0) return false;
        if (password.length()<6 || password.length()>255){
            return false;
        }else return true;
    }

    private class LogRegWorker extends AsyncTask<Void, Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(3000);
            }catch (InterruptedException e){

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            unlockInterface();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            unlockInterface();
        }
    }
}
