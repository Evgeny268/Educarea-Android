package com.educarea.mobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.educarea.mobile.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import transfers.ChannelMessage;
import transfers.GroupPerson;
import transfers.Message;
import transfers.PersonalMessage;
import transfers.StudentsChatMessage;

public class LastMessagesAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_STUDENT = 0;
    private static final int VIEW_TYPE_TEACHER = 1;

    private List<Message> messages;
    private List<GroupPerson> groupPeople;
    private Context context;
    private MessageClickListener listener;
    private int myId;

    public LastMessagesAdapter(Context context, int myId) {
        this.context = context;
        this.myId = myId;
        listener = (MessageClickListener) context;
        messages = new ArrayList<>();
        groupPeople = new ArrayList<>();
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void setGroupPeople(List<GroupPerson> groupPeople) {
        this.groupPeople = groupPeople;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == VIEW_TYPE_STUDENT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.last_message_student,parent,false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.last_message_teacher,parent,false);
        }
        return new LastMessageHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        ((LastMessageHolder) holder).bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        PersonalMessage personalMessage = (PersonalMessage) message;
        int interlocutorId;
        if (personalMessage.personFrom == myId){
            interlocutorId = personalMessage.personTo;
        }else {
            interlocutorId = personalMessage.personFrom;
        }
        for (GroupPerson groupPerson: groupPeople){
            if (groupPerson.groupPersonId == interlocutorId){
                if (groupPerson.personType == 0){
                    return VIEW_TYPE_STUDENT;
                }else {
                    return VIEW_TYPE_TEACHER;
                }
            }
        }
        return VIEW_TYPE_STUDENT;
    }

    public interface MessageClickListener {
        void onClickMessage(int position, View view);
        void onLongClickMessage(int position, View view);
    }

    private class LastMessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private static final int TEXT_LENGTH = 27;
        private TextView personName;
        private TextView messageText;
        private TextView messageDate;
        private MessageClickListener listener;

        public LastMessageHolder(@NonNull View itemView, MessageClickListener listener) {
            super(itemView);
            this.listener = listener;
            personName = itemView.findViewById(R.id.message_person_name);
            messageText = itemView.findViewById(R.id.message_text);
            messageDate = itemView.findViewById(R.id.message_time);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        void bind(Message message){
            int personId = 0;
            String text = message.text;
            if (message instanceof ChannelMessage){
                personId = ((ChannelMessage) message).personFrom;
            }else if (message instanceof StudentsChatMessage){
                personId = ((StudentsChatMessage) message).groupPersonId;
            }else if (message instanceof PersonalMessage){
                if (((PersonalMessage) message).personFrom.equals(myId)){
                    text = context.getString(R.string.you)+": "+text;
                    personId = ((PersonalMessage) message).personTo;
                }else {
                    personId = ((PersonalMessage) message).personFrom;
                }
            }
            GroupPerson groupPerson = new GroupPerson();
            groupPerson.surname = "";
            groupPerson.patronymic = "";
            groupPerson.name = "";
            for (int i = 0; i < groupPeople.size(); i++) {
                if (groupPeople.get(i).groupPersonId == personId){
                    if (groupPeople.get(i).surname == null && groupPeople.get(i).name==null && groupPeople.get(i).patronymic==null){
                        groupPerson.surname = context.getString(R.string.member)+" ID:"+groupPeople.get(i).groupPersonId;
                    }else {
                        if (groupPeople.get(i).surname != null)
                            groupPerson.surname = groupPeople.get(i).surname;
                        if (groupPeople.get(i).name != null)
                            groupPerson.name = groupPeople.get(i).name;
                        if (groupPeople.get(i).patronymic != null)
                            groupPerson.patronymic = groupPeople.get(i).patronymic;
                    }
                }
            }
            String pName = groupPerson.surname+" "+groupPerson.name+" "+groupPerson.patronymic;
            personName.setText(pName);
            if (text.length() > TEXT_LENGTH){
                text = text.substring(0,TEXT_LENGTH)+"...";
            }
            messageText.setText(text);
            messageDate.setText(dateToString(message.date));
        }

        @Override
        public void onClick(View v) {
            listener.onClickMessage(getAdapterPosition(),v);
        }

        @Override
        public boolean onLongClick(View v) {
            listener.onLongClickMessage(getAdapterPosition(),v);
            return true;
        }

        private String dateToString(Date date){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Calendar currentCallendar = Calendar.getInstance();
            currentCallendar.setTime(new Date());
            boolean sameDay = calendar.get(Calendar.DAY_OF_YEAR) == currentCallendar.get(Calendar.DAY_OF_YEAR) &&
                    calendar.get(Calendar.YEAR) == currentCallendar.get(Calendar.YEAR);
            if (sameDay){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                return simpleDateFormat.format(date);
            }else {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                return simpleDateFormat.format(date);
            }
        }
    }

}
