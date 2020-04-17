package com.educarea.mobile;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import transfers.Group;
import transfers.GroupPerson;
import transfers.TransferRequestAnswer;
import transfers.Transfers;
import transfers.TransfersFactory;
import transfers.TypeRequestAnswer;

import static com.educarea.mobile.EduApp.INTENT_GROUP;
import static com.educarea.mobile.EduApp.INTENT_GROUP_PERSON;

public class AddPersonActivity extends AppInetActivity implements TypeRequestAnswer {

    private EditText surname;
    private EditText name;
    private EditText patronymic;
    private RadioGroup radioGroup;
    private CheckBox checkBox;
    private Group group;
    private GroupPerson groupPerson;
    private boolean edit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);
        radioGroup = findViewById(R.id.radioGroupRole);
        radioGroup.check(R.id.radioButtonStudent);
        checkBox = findViewById(R.id.checkBoxModerator);
        surname = findViewById(R.id.viewTextSurname);
        name = findViewById(R.id.viewTextName);
        patronymic = findViewById(R.id.viewTextPatronymic);
        group = (Group) getIntent().getSerializableExtra(INTENT_GROUP);
        if (group==null){
            onBackPressed();
        }
        groupPerson = (GroupPerson) getIntent().getSerializableExtra(INTENT_GROUP_PERSON);
        if (groupPerson!=null){
            edit = true;
            surname.setText(groupPerson.surname);
            name.setText(groupPerson.name);
            patronymic.setText(groupPerson.patronymic);
            if (groupPerson.personType==0){
                radioGroup.check(R.id.radioButtonStudent);
            }else {
                radioGroup.check(R.id.radioButtonTeacher);
            }
            if (groupPerson.moderator==1){
                checkBox.setChecked(true);
            }else {
                if (groupPerson.userId == eduApp.getAppData().getUser().iduser){
                    radioGroup.setEnabled(false);
                    checkBox.setEnabled(false);
                    checkBox.setVisibility(View.GONE);
                    RadioButton student = findViewById(R.id.radioButtonStudent);
                    RadioButton teacher = findViewById(R.id.radioButtonTeacher);
                    student.setVisibility(View.GONE);
                    teacher.setVisibility(View.GONE);
                }
            }
        }else edit = false;
    }

    @Override
    protected void newMessage(String message) {
        Transfers in = TransfersFactory.createFromJSON(message);
        if (in!=null){
            if (in instanceof TransferRequestAnswer){
                if (((TransferRequestAnswer) in).request.equals(UPDATE_INFO)){
                    Toast.makeText(this, getString(R.string.done), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }else eduApp.standartReactionOnAsnwer(message,AddPersonActivity.this);
            }else eduApp.standartReactionOnAsnwer(message,AddPersonActivity.this);
        }else {
            eduApp.standartReactionOnAsnwer(message,AddPersonActivity.this);
        }
    }

    public void onClickSavePerson(View view) {
        int personType;
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        View radioButton = radioGroup.findViewById(radioButtonId);
        personType = radioGroup.indexOfChild(radioButton);
        int moderator;
        if (checkBox.isChecked()){
            moderator = 1;
        } else  moderator = 0;
        GroupPerson groupPerson = new GroupPerson();
        groupPerson.groupId = group.groupId;
        groupPerson.personType = personType;
        groupPerson.moderator = moderator;
        groupPerson.surname = surname.getText().toString();
        groupPerson.name = name.getText().toString();
        groupPerson.patronymic = patronymic.getText().toString();
        if (edit){
            groupPerson.groupPersonId = this.groupPerson.groupPersonId;
            String jsonGroupPerson = null;
            try {
                jsonGroupPerson = eduApp.objToJson(groupPerson);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                return;
            }
            TransferRequestAnswer out = new TransferRequestAnswer(UPDATE_PERSON,jsonGroupPerson);
            eduApp.sendTransfers(out);
        }else {
            String jsonGroupPerson = null;
            try {
                jsonGroupPerson = eduApp.objToJson(groupPerson);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                return;
            }
            TransferRequestAnswer out = new TransferRequestAnswer(CREATE_PERSON,jsonGroupPerson);
            eduApp.sendTransfers(out);
        }
    }
}
