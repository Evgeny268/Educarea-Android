package com.educarea.mobile;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import transfers.TransferRequestAnswer;
import transfers.Transfers;
import transfers.TransfersFactory;
import transfers.TypeRequestAnswer;

public class AccountActivity extends AppInetActivity implements TypeRequestAnswer {

    private Button logoutButton;
    private Button closeSessionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        logoutButton = findViewById(R.id.buttonLogout);
        closeSessionButton = findViewById(R.id.buttonCloseOSession);
        TextView textLogin = findViewById(R.id.textViewAccountName);
        textLogin.setText(eduApp.getAppData().getUser().login);
    }

    @Override
    protected void newMessage(String message) {
        Transfers in = TransfersFactory.createFromJSON(message);
        if (in != null){
            if (in instanceof TransferRequestAnswer){
                if (((TransferRequestAnswer) in).request.equals(LOGOUT)){
                    eduApp.clearAllUserData(this);
                }else if (((TransferRequestAnswer) in).request.equals(UPDATE_INFO)){
                    unlockInterface();
                    Toast.makeText(this, getString(R.string.done), Toast.LENGTH_SHORT).show();
                }else eduApp.standartReactionOnAsnwer(message, this);
            }else eduApp.standartReactionOnAsnwer(message, this);
        }else eduApp.standartReactionOnAsnwer(message, this);
    }

    private void lockInterface(){
        logoutButton.setEnabled(false);
        closeSessionButton.setEnabled(false);
    }

    private void unlockInterface(){
        logoutButton.setEnabled(true);
        closeSessionButton.setEnabled(true);
    }


    public void onClickLeaveAccount(View view) {
        lockInterface();
        eduApp.sendTransfers(new TransferRequestAnswer(LOGOUT));
        String out = null;
        try {
            out = eduApp.objToJson(new TransferRequestAnswer(LOGOUT));
        }catch (IOException e){
            Log.d("EDUCAREA", "onClickLeaveAccount: json error");
        }
        try {
            eduApp.getInetWorker().send(out);
        }catch (Exception e){
            eduApp.clearAllUserData(this);
        }
    }

    public void onClickCloseOtherSession(View view) {
        lockInterface();
        eduApp.sendTransfers(new TransferRequestAnswer(LOGOUT_OTHER_SESSION));
    }
}
