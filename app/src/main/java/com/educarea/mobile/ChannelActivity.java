package com.educarea.mobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.educarea.mobile.adapters.MessageAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

import transfers.ChannelMessage;
import transfers.ChannelMessages;
import transfers.Group;
import transfers.GroupPerson;
import transfers.GroupPersons;
import transfers.TransferRequestAnswer;
import transfers.Transfers;
import transfers.TransfersFactory;
import transfers.TypeRequestAnswer;

import static com.educarea.mobile.EduApp.INTENT_GROUP;
import static com.educarea.mobile.EduApp.INTENT_GROUP_PERSONS;

public class ChannelActivity extends AppInetActivity implements TypeRequestAnswer, MessageAdapter.MessageClickListener {

    private RecyclerView recyclerView;
    private EditText etMessage;
    private ImageButton btnSendText;

    private ArrayList<ChannelMessage> messages;
    private GroupPersons groupPersons;
    private Group group;
    private MessageAdapter adapter;

    private boolean newMessage = true;
    private int insertNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        group = (Group) getIntent().getSerializableExtra(INTENT_GROUP);
        groupPersons = (GroupPersons) getIntent().getSerializableExtra(INTENT_GROUP_PERSONS);
        if (group == null || groupPersons == null) onBackPressed();
        messages = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewChannelMessage);
        btnSendText = findViewById(R.id.btnSendMessage);
        etMessage = findViewById(R.id.editTextMessage);
        GroupPerson me = null;
        for (int i = 0; i < groupPersons.persons.size(); i++) {
            GroupPerson current = groupPersons.persons.get(i);
            if (current.userId==eduApp.getAppData().getUser().iduser){
                me = current;
            }
        }
        if (me.moderator==0 && me.personType==0){
            etMessage.setVisibility(View.GONE);
            btnSendText.setVisibility(View.GONE);
        }
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(false);
        int myId = 0;
        for (int i = 0; i < groupPersons.persons.size(); i++) {
            GroupPerson current = groupPersons.persons.get(i);
            if (current.userId==eduApp.getAppData().getUser().iduser){
                myId = current.groupPersonId;
            }
        }
        adapter = new MessageAdapter(this,myId);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(-1)){
                    if (messages.size()>0){
                        Date lastDate = messages.get(0).date;
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        format.setTimeZone(TimeZone.getTimeZone("UTC"));
                        String datestr = format.format(lastDate);
                        eduApp.sendTransfers(new TransferRequestAnswer(GET_CHANNEL_MESSAGE,String.valueOf(group.groupId),String.valueOf(20),datestr));
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        eduApp.sendTransfers(new TransferRequestAnswer(GET_CHANNEL_MESSAGE,String.valueOf(group.groupId),String.valueOf(20)));
    }

    @Override
    protected void newMessage(String message) {
        Transfers in = TransfersFactory.createFromJSON(message);
        if (in != null){
            if (in instanceof TransferRequestAnswer){
                if (((TransferRequestAnswer) in).request.equals(NEW_CHANNEL_MESSAGE)){
                    eduApp.sendTransfers(new TransferRequestAnswer(GET_CHANNEL_MESSAGE,String.valueOf(group.groupId),String.valueOf(20)));
                }else eduApp.standartReactionOnAsnwer(message, ChannelActivity.this);
            }else if (in instanceof ChannelMessages){
                ChannelMessages income = (ChannelMessages) in;
                addNewChannelMessages(income.channelMessages);
                adapter.setMessagesAndGroupPersons(messages,groupPersons.persons);
                if (newMessage){
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messages.size()-1);
                }else {
                    adapter.notifyItemRangeInserted(0,insertNum);
                }
            }else eduApp.standartReactionOnAsnwer(message, ChannelActivity.this);
        }else eduApp.standartReactionOnAsnwer(message, ChannelActivity.this);
    }

    @Override
    public void onClickMessage(int position, View view) {

    }

    @Override
    public void onLongClickMessage(int position, View view) {
        ChannelMessage message = messages.get(position);
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getString(R.string.copy_to_clip_board), message.text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, getString(R.string.copy_to_clip_board), Toast.LENGTH_SHORT).show();
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(100);
    }

    private void addNewChannelMessages(ArrayList<ChannelMessage> list){
        ChannelMessage channelMessage = null;
        insertNum = 0;
        if (messages.size()>0){
            channelMessage = messages.get(messages.size()-1);
        }
        for (int i = 0; i < list.size(); i++) {
            if (!messages.contains(list.get(i))){
                messages.add(list.get(i));
                insertNum++;
            }
        }
        Collections.sort(messages);
        if (channelMessage!=null){
            ChannelMessage lastMessage = messages.get(messages.size()-1);
            if (channelMessage.compareTo(lastMessage)<0){
                newMessage = true;
            }else {
                newMessage = false;
            }
        }else {
            newMessage = true;
        }
    }

    public void onClickSendChannelMessage(View view) {
        String text = etMessage.getText().toString();
        if (text.equals("")) return;
        etMessage.setText("");
        eduApp.sendTransfers(new TransferRequestAnswer(SEND_CHANNEL_MESSAGE,String.valueOf(group.groupId),text));
    }
}
