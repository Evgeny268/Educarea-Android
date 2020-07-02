package com.educarea.mobile;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.educarea.mobile.adapters.MessageAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import transfers.Group;
import transfers.GroupPerson;
import transfers.GroupPersons;
import transfers.Message;
import transfers.PersonalMessage;
import transfers.PersonalMessageList;
import transfers.TransferRequestAnswer;
import transfers.Transfers;
import transfers.TransfersFactory;
import transfers.TypeRequestAnswer;

import static com.educarea.mobile.EduApp.INTENT_GROUP;
import static com.educarea.mobile.EduApp.INTENT_GROUP_PERSON;
import static com.educarea.mobile.EduApp.INTENT_GROUP_PERSONS;

public class DialogActivity extends AppInetActivity implements TypeRequestAnswer, MessageAdapter.MessageClickListener {

    public static Integer personal_message_interlocutorId = null;

    private GroupPersons groupPersons;
    private GroupPerson interlocutor;
    private GroupPerson me;
    private Group group;
    private static final int MESSAGE_COUNT = 20;
    private int insertNum;
    private boolean newMessage = true;

    private List<PersonalMessage> messages;
    private EditText editText;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        groupPersons = (GroupPersons) getIntent().getSerializableExtra(INTENT_GROUP_PERSONS);
        interlocutor = (GroupPerson) getIntent().getSerializableExtra(INTENT_GROUP_PERSON);
        group = (Group) getIntent().getSerializableExtra(INTENT_GROUP);
        if (group == null || groupPersons == null || interlocutor == null) onBackPressed();
        editText = findViewById(R.id.editTextMessage);
        for (GroupPerson groupPerson: groupPersons.persons){
            if (groupPerson.userId == eduApp.getAppData().getUser().iduser){
                me = groupPerson;
            }
        }
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getShortName(interlocutor));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclerView = findViewById(R.id.rvDialog);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(false);
        adapter = new MessageAdapter(this, me.groupPersonId);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(-1)){
                    if (messages.size()>0){
                        int lastId = messages.get(0).personalMessageId;
                        eduApp.sendTransfers(new TransferRequestAnswer(GET_PERSONAL_MESSAGE,
                                String.valueOf(interlocutor.groupPersonId), String.valueOf(MESSAGE_COUNT), String.valueOf(lastId)));
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        personal_message_interlocutorId = interlocutor.groupPersonId;
        messages = new ArrayList<>();
        eduApp.sendTransfers(new TransferRequestAnswer(GET_PERSONAL_MESSAGE, String.valueOf(interlocutor.groupPersonId), String.valueOf(MESSAGE_COUNT)));
    }

    @Override
    protected void onPause() {
        super.onPause();
        personal_message_interlocutorId = null;
    }

    @Override
    public void onClickMessage(int position, View view) {

    }

    @Override
    public void onLongClickMessage(int position, View view) {
        Message message = messages.get(position);
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getString(R.string.copy_to_clip_board), message.text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, getString(R.string.copy_to_clip_board), Toast.LENGTH_SHORT).show();
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(100);
    }

    @Override
    protected void newMessage(String message) {
        Transfers in = TransfersFactory.createFromJSON(message);
        if (in != null){
            if (in instanceof TransferRequestAnswer){
                if (((TransferRequestAnswer) in).request.equals(NEW_PERSONAL_MESSAGE)){
                    eduApp.sendTransfers(new TransferRequestAnswer(GET_PERSONAL_MESSAGE, String.valueOf(interlocutor.groupPersonId), String.valueOf(MESSAGE_COUNT)));
                }else {
                    eduApp.standartReactionOnAsnwer(message, DialogActivity.this);
                }
            }else if (in instanceof PersonalMessageList){
                PersonalMessageList income = (PersonalMessageList) in;
                addNewMessage(income.messages);
                List<Message> personalMessages = new ArrayList<>();
                personalMessages.addAll(messages);
                adapter.setMessagesAndGroupPersons(personalMessages, groupPersons.persons);
                if (newMessage){
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messages.size()-1);
                }else {
                    adapter.notifyItemRangeInserted(0,insertNum);
                }
            }
        }else eduApp.standartReactionOnAsnwer(message, DialogActivity.this);
    }

    public void onClickSendMessage(View view) {
        String text = editText.getText().toString();
        if (text.equals("")) return;
        editText.setText("");
        PersonalMessage message = new PersonalMessage();
        message.personFrom = me.groupPersonId;
        message.personTo = interlocutor.groupPersonId;
        message.text = text;
        eduApp.sendTransfers(message);
    }

    public String getShortName(GroupPerson groupPerson){
        String name = "";
        if (groupPerson.surname == null && groupPerson.name == null && groupPerson.patronymic == null){
            name = getString(R.string.member)+" ID:"+groupPerson.groupPersonId;
        }else {
            if (groupPerson.surname != null){
                if (!groupPerson.surname.equals("")){
                    name+=groupPerson.surname;
                }
            }
            if (groupPerson.name != null){
                if (!groupPerson.name.equals("")){
                    if (name.length()>0){
                        name+=" "+groupPerson.name.substring(0,1).toUpperCase()+".";
                    }else {
                        name+=groupPerson.name;
                    }
                }
            }
            if (groupPerson.patronymic != null){
                if (!groupPerson.patronymic.equals("")){
                    if (name.length()>0){
                        name+=" "+groupPerson.patronymic.substring(0,1).toUpperCase()+".";
                    }else {
                        name+=groupPerson.patronymic;
                    }
                }
            }
            if (name.equals("")){
                name = getString(R.string.member)+" ID:"+groupPerson.groupPersonId;
            }
        }
        return name;
    }

    private void addNewMessage(List<PersonalMessage> list){
        PersonalMessage personalMessage = null;
        insertNum = 0;
        if (messages.size()>0){
            personalMessage = (PersonalMessage) messages.get(messages.size()-1);
        }
        for (int i = 0; i < list.size(); i++) {
            if (!messages.contains(list.get(i))){
                messages.add(list.get(i));
                insertNum++;
            }
        }
        Collections.sort(messages);
        if (personalMessage!=null){
            PersonalMessage lastMessage = messages.get(messages.size()-1);
            if (personalMessage.compareTo(lastMessage)<0){
                newMessage = true;
            }else {
                newMessage = false;
            }
        }else {
            newMessage = true;
        }
    }
}