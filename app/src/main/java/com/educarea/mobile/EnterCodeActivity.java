package com.educarea.mobile;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import transfers.TransferRequestAnswer;
import transfers.Transfers;
import transfers.TransfersFactory;
import transfers.TypeRequestAnswer;

public class EnterCodeActivity extends AppInetActivity implements TypeRequestAnswer {

    private EditText codeField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_code);
        codeField = findViewById(R.id.editTextCode);
    }

    @Override
    protected void newMessage(String message) {
        Transfers in = TransfersFactory.createFromJSON(message);
        if (in!=null){
            if (in instanceof TransferRequestAnswer){
                if (((TransferRequestAnswer) in).request.equals(INVITE_SUCCESS)){
                    Toast.makeText(this, getString(R.string.done), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }else if (((TransferRequestAnswer) in).request.equals(PERSON_CODE_NOT_FOUND)){
                    Toast.makeText(this, getString(R.string.code_not_exist), Toast.LENGTH_SHORT).show();
                    codeField.setText("");
                }else eduApp.standartReactionOnAsnwer(message, this);
            }else eduApp.standartReactionOnAsnwer(message, this);
        }else eduApp.standartReactionOnAsnwer(message, this);
    }

    public void onClickSendCode(View view) {
        String text = codeField.getText().toString();
        if (text.equals("")) return;
        eduApp.sendTransfers(new TransferRequestAnswer(INVITE_BY_PERSON_CODE, text));
    }
}
