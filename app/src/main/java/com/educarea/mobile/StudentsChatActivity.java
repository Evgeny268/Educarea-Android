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
import android.widget.ImageButton;
import android.widget.Toast;

import com.educarea.mobile.adapters.MessageAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import transfers.Group;
import transfers.GroupPerson;
import transfers.GroupPersons;
import transfers.Message;
import transfers.StudentsChatMessage;
import transfers.StudentsChatMessages;
import transfers.TransferRequestAnswer;
import transfers.Transfers;
import transfers.TransfersFactory;

import static com.educarea.mobile.EduApp.INTENT_GROUP;
import static com.educarea.mobile.EduApp.INTENT_GROUP_PERSONS;
import static transfers.TypeRequestAnswer.GET_STUDENT_MESSAGE;
import static transfers.TypeRequestAnswer.NEW_STUDENT_MESSAGE;

public class StudentsChatActivity extends AppInetActivity implements MessageAdapter.MessageClickListener {

    public static boolean STUDENT_CHAT_OPEN = false;
    public static final int MESSAGE_GET_COUNT = 20;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private EditText etMessage;
    private ImageButton btnSendText;
    private ArrayList<StudentsChatMessage> chatMessages;
    private GroupPersons groupPersons;
    private Group group;
    private MessageAdapter adapter;
    private boolean newMessage = true;
    private int insertNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_chat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.students_chat));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        group = (Group) getIntent().getSerializableExtra(INTENT_GROUP);
        groupPersons = (GroupPersons) getIntent().getSerializableExtra(INTENT_GROUP_PERSONS);
        if (group == null || groupPersons == null) onBackPressed();
        chatMessages = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewMessage);
        btnSendText = findViewById(R.id.btnSendMessage);
        etMessage = findViewById(R.id.editTextMessage);
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
                    if (chatMessages.size()>0){
                        int lastMessageId = chatMessages.get(0).studentsChatId;
                        eduApp.sendTransfers(new TransferRequestAnswer(GET_STUDENT_MESSAGE,
                                String.valueOf(group.groupId),String.valueOf(MESSAGE_GET_COUNT),String.valueOf(lastMessageId)));
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        STUDENT_CHAT_OPEN = true;
        eduApp.sendTransfers(new TransferRequestAnswer(GET_STUDENT_MESSAGE,
                String.valueOf(group.groupId),String.valueOf(MESSAGE_GET_COUNT)));
    }

    @Override
    protected void onPause() {
        super.onPause();
        STUDENT_CHAT_OPEN = false;
    }

    @Override
    protected void newMessage(String message) {
        Transfers in = TransfersFactory.createFromJSON(message);
        if (in!=null){
            if (in instanceof TransferRequestAnswer){
                if (((TransferRequestAnswer) in).request.equals(NEW_STUDENT_MESSAGE)){
                    eduApp.sendTransfers(new TransferRequestAnswer(GET_STUDENT_MESSAGE,
                            String.valueOf(group.groupId),String.valueOf(MESSAGE_GET_COUNT)));
                }else eduApp.standartReactionOnAsnwer(message, this);
            }else if (in instanceof StudentsChatMessages){
                StudentsChatMessages income = (StudentsChatMessages) in;
                addNewStudentMessages(income.studentsChatMessages);
                ArrayList<Message> messages = new ArrayList<>();
                messages.addAll(chatMessages);
                adapter.setMessagesAndGroupPersons(messages, groupPersons.persons);
                if (newMessage){
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messages.size()-1);
                }else {
                    adapter.notifyItemRangeInserted(0,insertNum);
                }
            }else eduApp.standartReactionOnAsnwer(message, this);
        }else eduApp.standartReactionOnAsnwer(message, this);
    }

    private void addNewStudentMessages(ArrayList<StudentsChatMessage> list){
        StudentsChatMessage studentsChatMessage = null;
        insertNum = 0;
        if (chatMessages.size()>0){
            studentsChatMessage = chatMessages.get(chatMessages.size()-1);
        }
        for (int i = 0; i < list.size(); i++) {
            if (!chatMessages.contains(list.get(i))){
                chatMessages.add(list.get(i));
                insertNum++;
            }
        }
        Collections.sort(chatMessages);
        if (studentsChatMessage!=null){
            StudentsChatMessage lastMessage = chatMessages.get(chatMessages.size()-1);
            if (studentsChatMessage.compareTo(lastMessage)<0){
                newMessage = true;
            }else {
                newMessage = false;
            }
        }else {
            newMessage = true;
        }
    }

    public void onClickSendMessage(View view) {
        String text = etMessage.getText().toString();
        if (text.equals("")) return;
        etMessage.setText("");
        eduApp.sendTransfers(new StudentsChatMessage(group.groupId, text));
    }

    @Override
    public void onClickMessage(int position, View view) {

    }

    @Override
    public void onLongClickMessage(int position, View view) {
        Message message = chatMessages.get(position);
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getString(R.string.copy_to_clip_board), message.text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, getString(R.string.copy_to_clip_board), Toast.LENGTH_SHORT).show();
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(100);
    }
}