package com.educarea.mobile;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.educarea.mobile.adapters.LastMessagesAdapter;
import com.educarea.mobile.adapters.diffUtils.LastMessagesDiffUtilCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
import static transfers.TypeRequestAnswer.GET_LAST_PERSONAL_MESSAGES;

public class LastMessagesActivity extends AppInetActivity implements TypeRequestAnswer, LastMessagesAdapter.MessageClickListener {

    public static Integer messages_open_group = null;

    private GroupPersons groupPersons;
    private Group group;
    private GroupPerson me;
    private static final int MESSAGE_COUNT = 20;

    private List<PersonalMessage> messages;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LastMessagesAdapter adapter;
    LinearLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_messages);
        groupPersons = (GroupPersons) getIntent().getSerializableExtra(INTENT_GROUP_PERSONS);
        group = (Group) getIntent().getSerializableExtra(INTENT_GROUP);
        if (group == null || groupPersons == null) onBackPressed();
        for (GroupPerson groupPerson: groupPersons.persons){
            if (groupPerson.userId == eduApp.getAppData().getUser().iduser){
                me = groupPerson;
            }
        }
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.messages));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclerView = findViewById(R.id.rvLastMessages);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(false);
        adapter = new LastMessagesAdapter(this, me.groupPersonId);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)){
                    if (messages.size()>0){
                        int lastId = messages.get(messages.size()-1).personalMessageId;
                        eduApp.sendTransfers(new TransferRequestAnswer(GET_LAST_PERSONAL_MESSAGES, String.valueOf(group.groupId),
                                String.valueOf(MESSAGE_COUNT), String.valueOf(lastId)));
                    }
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        messages_open_group = group.groupId;
        messages = new ArrayList<>();
        eduApp.sendTransfers(new TransferRequestAnswer(GET_LAST_PERSONAL_MESSAGES, String.valueOf(group.groupId), String.valueOf(MESSAGE_COUNT)));
    }

    @Override
    protected void onPause() {
        super.onPause();
        messages_open_group = null;
    }

    @Override
    protected void newMessage(String message) {
        Transfers in = TransfersFactory.createFromJSON(message);
        if (in != null){
            if (in instanceof TransferRequestAnswer){
                if (((TransferRequestAnswer) in).request.equals(NEW_PERSONAL_MESSAGE)){
                    eduApp.sendTransfers(new TransferRequestAnswer(GET_LAST_PERSONAL_MESSAGES, String.valueOf(group.groupId), String.valueOf(MESSAGE_COUNT)));
                }else {
                    eduApp.standartReactionOnAsnwer(message, LastMessagesActivity.this);
                }
            }else if (in instanceof PersonalMessageList){
                PersonalMessageList income = (PersonalMessageList) in;
                addNewMessage(income.messages);
            }
        }else eduApp.standartReactionOnAsnwer(message, LastMessagesActivity.this);
    }

    @Override
    public void onClickMessage(int position, View view) {
        PersonalMessage message = messages.get(position);
        int interlocutorId;
        if (message.personFrom.equals(me.groupPersonId)){
            interlocutorId = message.personTo;
        }else {
            interlocutorId = message.personFrom;
        }
        GroupPerson interlocutor = null;
        for (GroupPerson groupPerson: groupPersons.persons){
            if (groupPerson.groupPersonId == interlocutorId){
                interlocutor = groupPerson;
            }
        }
        Intent intent = new Intent(LastMessagesActivity.this, DialogActivity.class);
        intent.putExtra(INTENT_GROUP,group);
        intent.putExtra(INTENT_GROUP_PERSONS,eduApp.getAppData().getGroupPersons(group.groupId));
        intent.putExtra(INTENT_GROUP_PERSON, interlocutor);
        startActivity(intent);
    }

    @Override
    public void onLongClickMessage(int position, View view) {

    }

    public void onCLickNewMessage(View view) {
        Intent intent = new Intent(LastMessagesActivity.this, ChoseInterlocutor.class);
        intent.putExtra(INTENT_GROUP,group);
        intent.putExtra(INTENT_GROUP_PERSONS,eduApp.getAppData().getGroupPersons(group.groupId));
        startActivity(intent);
    }

    private void addNewMessage(List<PersonalMessage> list){
        final List<PersonalMessage> oldMessages = new ArrayList<>(messages);
        if (oldMessages.isEmpty()){
            messages.clear();
            messages.addAll(list);
            Collections.sort(messages);
            Collections.reverse(messages);
            adapter.setGroupPeople(groupPersons.persons);
            List<Message> m = new ArrayList<>();
            m.addAll(messages);
            adapter.setMessages(m);
            adapter.notifyDataSetChanged();
            return;
        }
        boolean messageNew = false;
        for (final PersonalMessage newMessage: list){
            Iterator<PersonalMessage> oldMessage = messages.iterator();
            while (oldMessage.hasNext()){
                PersonalMessage old = oldMessage.next();
                if ((old.personFrom.equals(newMessage.personFrom) && old.personTo.equals(newMessage.personTo)) ||
                        (old.personFrom.equals(newMessage.personTo) && old.personTo.equals(newMessage.personFrom))){
                    oldMessage.remove();
                }
            }
        }
        for (PersonalMessage newMessage: list){
            if (!messages.contains(newMessage)){
                messages.add(newMessage);
            }
        }
        Collections.sort(messages);
        Collections.reverse(messages);
        LastMessagesDiffUtilCallback diffUtilCallback = new LastMessagesDiffUtilCallback(oldMessages, messages);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilCallback, true);
        List<Message> m = new ArrayList<>();
        m.addAll(messages);
        adapter.setGroupPeople(groupPersons.persons);
        adapter.setMessages(m);
        diffResult.dispatchUpdatesTo(adapter);
        if (!messages.isEmpty()){
            if (messages.get(0).personalMessageId > oldMessages.get(0).personalMessageId){
                Toast.makeText(this, "+1 "+getString(R.string.new_message), Toast.LENGTH_SHORT).show();
                manager.scrollToPosition(0);
            }
        }
    }
}