package com.educarea.mobile;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import transfers.GroupPerson;
import transfers.GroupPersonCode;
import transfers.TransferRequestAnswer;
import transfers.Transfers;
import transfers.TransfersFactory;

import static com.educarea.mobile.EduApp.INTENT_GROUP_PERSON;
import static transfers.TypeRequestAnswer.CREATE_PERSON_CODE;
import static transfers.TypeRequestAnswer.DELETE_PERSON_CODE;
import static transfers.TypeRequestAnswer.GET_PERSON_CODE;
import static transfers.TypeRequestAnswer.NO_PERSON_CODE;
import static transfers.TypeRequestAnswer.UNBIND_USER;
import static transfers.TypeRequestAnswer.UPDATE_INFO;
import static transfers.TypeRequestAnswer.USER_ALREADY_BIND;

public class PersonCodeActivity extends AppInetActivity {

    private GroupPerson groupPerson;
    private TextView personCode;
    private Button buttonSetCode;

    private GroupPersonCode groupPersonCode;



    private enum ButtonMode
    {
        CREATE_CODE,
        DELETE_CODE,
        UNBIND_USER;
    }

    private ButtonMode mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_code);
        personCode = findViewById(R.id.textPersonCode);
        buttonSetCode = findViewById(R.id.buttonChangeCode);
        groupPerson = (GroupPerson) getIntent().getSerializableExtra(INTENT_GROUP_PERSON);
        if (groupPerson == null) onBackPressed();

        personCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getString(R.string.copy_to_clip_board), personCode.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(PersonCodeActivity.this, getString(R.string.copy_to_clip_board), Toast.LENGTH_SHORT).show();
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(100);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        eduApp.sendTransfers(new TransferRequestAnswer(GET_PERSON_CODE, String.valueOf(groupPerson.groupPersonId)));
    }

    @Override
    protected void newMessage(String message) {
        Transfers in = TransfersFactory.createFromJSON(message);
        if (in != null){
            if (in instanceof TransferRequestAnswer){
                if (((TransferRequestAnswer) in).request.equals(NO_PERSON_CODE)){
                    mode = ButtonMode.CREATE_CODE;
                }else if (((TransferRequestAnswer) in).request.equals(USER_ALREADY_BIND)){
                    mode = ButtonMode.UNBIND_USER;
                }else if (((TransferRequestAnswer) in).request.equals(UPDATE_INFO)){
                    eduApp.sendTransfers(new TransferRequestAnswer(GET_PERSON_CODE, String.valueOf(groupPerson.groupPersonId)));
                    return;
                }else eduApp.standartReactionOnAsnwer(message,this);
            }else if (in instanceof GroupPersonCode){
                mode = ButtonMode.DELETE_CODE;
                groupPersonCode = (GroupPersonCode) in;
            }else eduApp.standartReactionOnAsnwer(message,this);
        }else eduApp.standartReactionOnAsnwer(message,this);
        updateInterface();
    }

    protected void updateInterface(){
        buttonSetCode.setEnabled(true);
        switch (mode){
            case CREATE_CODE:
                buttonSetCode.setText(getString(R.string.create_code));
                personCode.setText("");
                personCode.setEnabled(false);
                break;
            case DELETE_CODE:
                buttonSetCode.setText(getString(R.string.delete_code));
                personCode.setEnabled(true);
                personCode.setText(groupPersonCode.code);
                break;
            case UNBIND_USER:
                buttonSetCode.setText(getString(R.string.untie_user));
                personCode.setText("");
                personCode.setEnabled(false);
                break;
        }
    }

    public void onClickCodeButton(View view) {
        buttonSetCode.setEnabled(false);
        switch (mode){
            case CREATE_CODE:
                eduApp.sendTransfers(new TransferRequestAnswer(CREATE_PERSON_CODE,String.valueOf(groupPerson.groupPersonId)));
                break;
            case DELETE_CODE:
                eduApp.sendTransfers(new TransferRequestAnswer(DELETE_PERSON_CODE, String.valueOf(groupPerson.groupPersonId)));
                break;
            case UNBIND_USER:
                eduApp.sendTransfers(new TransferRequestAnswer(UNBIND_USER, String.valueOf(groupPerson.groupPersonId)));
                break;
        }
    }
}
