package com.educarea.mobile;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;

import transfers.Group;
import transfers.TransferRequestAnswer;
import transfers.Transfers;
import transfers.TransfersFactory;

import static com.educarea.mobile.EduApp.INTENT_GROUP;
import static transfers.TypeRequestAnswer.DELETE_GROUP;

public class DeleteGroupActivity extends AppInetActivity {

    private String deleteCode;
    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_group);
        TextView textViewCode = findViewById(R.id.textViewDeleteCode);
        group = (Group) getIntent().getSerializableExtra(INTENT_GROUP);
        deleteCode = generateCode();
        textViewCode.setText(deleteCode);
    }

    @Override
    protected void newMessage(String message) {
        Transfers in = TransfersFactory.createFromJSON(message);
        if (in instanceof TransferRequestAnswer) {
            if (((TransferRequestAnswer) in).request.equals(DELETE_GROUP)) {
                startActivity(new Intent(DeleteGroupActivity.this, GroupsListActivity.class));
                finish();
            }else eduApp.standartReactionOnAsnwer(message, this);
        }else eduApp.standartReactionOnAsnwer(message, this);
    }

    private String generateCode(){
        SecureRandom random = new SecureRandom();
        final String alphabet = "0123456789";
        char []simbol = alphabet.toCharArray();
        String key = "";
        for (int i = 0; i < 4; i++) {
            int current = random.nextInt(alphabet.length());
            key += simbol[current];
        }
        return key;
    }

    public void onClickDeleteGroup(View view) {
        EditText editText = findViewById(R.id.editTextDeleteCode);
        String enterCode = editText.getText().toString();
        if (enterCode.equals(deleteCode)){
            eduApp.sendTransfers(new TransferRequestAnswer(DELETE_GROUP,String.valueOf(group.groupId)));
        }else {
            Toast.makeText(this, getString(R.string.wrong_code), Toast.LENGTH_SHORT).show();
        }
    }
}
