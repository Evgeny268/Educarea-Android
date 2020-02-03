package com.educarea.mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import transfers.GroupPerson;
import transfers.GroupPersonInvite;
import transfers.TransferRequestAnswer;
import transfers.Transfers;
import transfers.TransfersFactory;
import transfers.TypeRequestAnswer;

import static com.educarea.mobile.EduApp.INTENT_GROUP_PERSON;
import static transfers.TypeRequestAnswer.GET_PERSON_INVITES;

public class BindActivity extends AppInetActivity implements TypeRequestAnswer {

    private GroupPerson groupPerson;
    private EditText inviteUserName;
    private Button sendInvite;


    private enum BindButtonMode
    {
        BIND_USER,
        REJECT_INVITE,
        UNBIND_USER;
    }
    private BindButtonMode mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind);
        groupPerson = (GroupPerson) getIntent().getSerializableExtra(INTENT_GROUP_PERSON);
        if (groupPerson == null) onBackPressed();
        inviteUserName = findViewById(R.id.editTextInviteUserName);
        sendInvite = findViewById(R.id.buttonBindUser);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (groupPerson.userId!=0){
            mode = BindButtonMode.UNBIND_USER;
            setInterface();
        }else {
            TransferRequestAnswer out = new TransferRequestAnswer(GET_PERSON_INVITES, String.valueOf(groupPerson.groupPersonId));
            eduApp.sendTransfers(out);
        }
    }

    @Override
    protected void newMessage(String message) {
        Transfers in = TransfersFactory.createFromJSON(message);
        if (in != null){
           if (in instanceof GroupPersonInvite){
               if (((GroupPersonInvite) in).userId==0){
                   mode = BindButtonMode.BIND_USER;
                   setInterface();
               }else {
                   mode = BindButtonMode.REJECT_INVITE;
                   setInterface();
               }
           }else if (in instanceof TransferRequestAnswer){
               if (((TransferRequestAnswer) in).request.equals(UPDATE_INFO)){
                   onBackPressed();
               }else eduApp.standartReactionOnAsnwer(message,BindActivity.this);
           }
           else eduApp.standartReactionOnAsnwer(message,BindActivity.this);
        }else eduApp.standartReactionOnAsnwer(message,BindActivity.this);
    }

    private void setInterface(){
        switch (mode){
            case BIND_USER:
                sendInvite.setText(getString(R.string.bind_user));
                inviteUserName.setVisibility(View.VISIBLE);
                break;
            case UNBIND_USER:
                sendInvite.setText(getString(R.string.untie_user));
                inviteUserName.setVisibility(View.GONE);
                break;
            case REJECT_INVITE:
                sendInvite.setText(getString(R.string.cancel_invitation));
                inviteUserName.setVisibility(View.GONE);
        }
    }

    public void onCliclSendBind(View view) {
        if (mode == BindButtonMode.BIND_USER){
            TransferRequestAnswer out = new TransferRequestAnswer(INVITE_USER_TO_PERSON, String.valueOf(groupPerson.groupPersonId), inviteUserName.getText().toString());
            eduApp.sendTransfers(out);
        }else {
            TransferRequestAnswer out = new TransferRequestAnswer(UNTIE_USER, String.valueOf(groupPerson.groupPersonId));
            eduApp.sendTransfers(out);
        }
    }
}
