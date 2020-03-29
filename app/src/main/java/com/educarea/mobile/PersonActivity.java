package com.educarea.mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import transfers.Group;
import transfers.GroupPerson;

import static com.educarea.mobile.EduApp.INTENT_BACK;
import static com.educarea.mobile.EduApp.INTENT_GROUP;
import static com.educarea.mobile.EduApp.INTENT_GROUP_PERSON;

public class PersonActivity extends AppInetActivity {

    private Group group = null;
    private GroupPerson groupPerson = null;

    private TextView surname;
    private TextView name;
    private TextView patronymic;
    private FloatingActionButton buttonEdit;
    private Button btnBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        btnBind = findViewById(R.id.buttonAccountUser);
        surname = findViewById(R.id.viewTextSurname);
        name = findViewById(R.id.viewTextName);
        patronymic = findViewById(R.id.viewTextPatronymic);
        buttonEdit = findViewById(R.id.btnEditPerson);
        group = (Group) getIntent().getSerializableExtra(INTENT_GROUP);
        groupPerson = (GroupPerson) getIntent().getSerializableExtra(INTENT_GROUP_PERSON);
        if (group == null || groupPerson == null) onBackPressed();
        if (groupPerson.surname==null && groupPerson.name==null && groupPerson.patronymic==null){
            surname.setText(getString(R.string.member)+" ID:"+groupPerson.groupPersonId);
        }else {
            surname.setText(groupPerson.surname);
            name.setText(groupPerson.name);
            patronymic.setText(groupPerson.patronymic);
        }
        if (eduApp.moderator){
            buttonEdit.show();
            btnBind.setVisibility(View.VISIBLE);
            btnBind.setEnabled(true);
        }else {
            buttonEdit.hide();
            btnBind.setVisibility(View.GONE);
            btnBind.setEnabled(false);
        }
    }

    @Override
    protected void newMessage(String message) {
        eduApp.standartReactionOnAsnwer(message, PersonActivity.this);
    }

    public void onClickPersonEdit(View view) {
        Intent intent = new Intent(PersonActivity.this, AddPersonActivity.class);
        intent.putExtra(INTENT_GROUP,group);
        intent.putExtra(INTENT_GROUP_PERSON, groupPerson);
        startActivity(intent);
        finish();
    }


    public void onClickBindUser(View view) {
        Intent intent = new Intent(PersonActivity.this, PersonCodeActivity.class);
        intent.putExtra(INTENT_GROUP_PERSON,groupPerson);
        startActivity(intent);
        finish();
    }
}
