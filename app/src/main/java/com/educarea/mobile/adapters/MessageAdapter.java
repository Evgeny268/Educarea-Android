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

import transfers.ChannelMessage;
import transfers.GroupPerson;

public class MessageAdapter extends RecyclerView.Adapter{

    private static final int VIEW_TYPE_ME = 0;
    private static final int VIEW_TYPE_STRANGER = 1;

    private ArrayList<ChannelMessage> messages;
    private ArrayList<GroupPerson> groupPeople;
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

    public void setMessagesAndGroupPersons(ArrayList<ChannelMessage> messages, ArrayList<GroupPerson> groupPeople){
        this.messages = messages;
        this.groupPeople = groupPeople;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_ME){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_message,parent,false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stranger_message,parent,false);
        }
        return new MessageHolder(view, listener);
    }

    @Override
    public int getItemViewType(int position) {
        ChannelMessage channelMessage = messages.get(position);
        if (channelMessage.personFrom==myId){
            return VIEW_TYPE_ME;
        }else {
            return VIEW_TYPE_STRANGER;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChannelMessage channelMessage = messages.get(position);
        ((MessageHolder) holder).bind(channelMessage);
    }

    private class MessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        TextView personName;
        TextView message;
        TextView date;
        MessageClickListener listener;

        public MessageHolder(@NonNull View itemView, MessageClickListener listener) {
            super(itemView);
            this.listener = listener;
            personName = itemView.findViewById(R.id.message_person_name);
            message = itemView.findViewById(R.id.message_text);
            date = itemView.findViewById(R.id.message_time);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        void bind(ChannelMessage channelMessage){
            GroupPerson groupPerson = new GroupPerson();
            groupPerson.surname = "";
            groupPerson.patronymic = "";
            groupPerson.name = "";
            for (int i = 0; i < groupPeople.size(); i++) {
                if (groupPeople.get(i).groupPersonId == channelMessage.personFrom){
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
            personName.setText(groupPerson.surname+" "+groupPerson.name+" "+groupPerson.patronymic);
            message.setText(channelMessage.text);
            date.setText(dateToString(channelMessage.date));
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

    public interface MessageClickListener{
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
