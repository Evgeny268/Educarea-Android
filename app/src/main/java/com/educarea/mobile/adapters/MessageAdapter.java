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
import transfers.StudentsChatMessage;

public class MessageAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_ME = 0;
    private static final int VIEW_TYPE_STRANGER = 1;
    private static final int VIEW_TYPE_SYSTEM = 2;

    private List<Message> messages;
    private List<GroupPerson> groupPeople;
    private Context context;
    private MessageClickListener listener;
    private int myId;

    public MessageAdapter(Context context, int myId) {
        this.context = context;
        this.myId = myId;
        listener = (MessageClickListener) context;
        messages = new ArrayList<>();
        groupPeople = new ArrayList<>();
    }

    public void setMessagesAndGroupPersons(List<Message> messages, List<GroupPerson> groupPeople){
        this.messages = messages;
        this.groupPeople = groupPeople;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == VIEW_TYPE_ME){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_message,parent,false);
        }else if (viewType == VIEW_TYPE_STRANGER){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stranger_message,parent,false);
        }else {
            //TODO как только появятся системные сообщения, то дописать данную часть
        }
        return new MessageHolder(view, listener, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message instanceof ChannelMessage){
            if (((ChannelMessage) message).personFrom==myId){
                return VIEW_TYPE_ME;
            }else {
                return VIEW_TYPE_STRANGER;
            }
        }else if (message instanceof StudentsChatMessage){
            if (((StudentsChatMessage) message).groupPersonId==myId){
                return VIEW_TYPE_ME;
            }else {
                return VIEW_TYPE_STRANGER;
            }
        }else {
            return VIEW_TYPE_SYSTEM;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        ((MessageHolder) holder).bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private class MessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private int viewType;
        private TextView personName;
        private TextView messageText;
        private TextView messageDate;
        private MessageClickListener listener;

        public MessageHolder(@NonNull View itemView, MessageClickListener listener, int viewType) {
            super(itemView);
            this.listener = listener;
            this.viewType = viewType;
            personName = itemView.findViewById(R.id.message_person_name);
            messageText = itemView.findViewById(R.id.message_text);
            messageDate = itemView.findViewById(R.id.message_time);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        void bind(Message message){
            if (viewType==0 || viewType==1){
                int personId = 0;
                if (message instanceof ChannelMessage){
                    personId = ((ChannelMessage) message).personFrom;
                }else if (message instanceof StudentsChatMessage){
                    personId = ((StudentsChatMessage) message).groupPersonId;
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
                messageText.setText(message.text);
                messageDate.setText(dateToString(message.date));
            }else {
                //TODO как только появятся системные сообщения, то дописать данную часть
            }
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
    }

    public interface MessageClickListener {
        void onClickMessage(int position, View view);
        void onLongClickMessage(int position, View view);
    }

    private static String dateToString(Date date){
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
