package com.educarea.mobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.educarea.mobile.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import transfers.DateObject;
import transfers.Event;
import transfers.GroupPerson;

public class EventAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_EVENT = 0;

    private List<DateObject> events;
    private List<GroupPerson> groupPeople;
    private Context context;
    private EventClickListener listener;

    public EventAdapter(Context context) {
        this.context = context;
        listener = (EventClickListener) context;
        events = new ArrayList<>();
        groupPeople = new ArrayList<>();
    }

    public void setEventsAndGroupPersons(List<DateObject> events, List<GroupPerson> groupPeople){
        this.events = events;
        this.groupPeople = groupPeople;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == VIEW_TYPE_EVENT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event, parent, false);
        }
        return new EventHolder(view, listener, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        DateObject dateObject = events.get(position);
        if (dateObject instanceof Event){
            return VIEW_TYPE_EVENT;
        }else {
            return -1;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DateObject dateObject = events.get(position);
        ((EventHolder) holder).bind(dateObject);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    private class EventHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private int viewType;
        private TextView eventName;
        private TextView eventAuthor;
        private TextView eventText;
        private TextView eventDate;
        private TextView eventTime;
        private EventClickListener listener;

        public EventHolder(@NonNull View itemView, EventClickListener listener, int viewType) {
            super(itemView);
            this.listener = listener;
            this.viewType = viewType;
            eventName = itemView.findViewById(R.id.event_name);
            eventAuthor = itemView.findViewById(R.id.event_author);
            eventText = itemView.findViewById(R.id.event_text);
            eventDate = itemView.findViewById(R.id.event_date);
            eventTime = itemView.findViewById(R.id.event_time);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        void bind(DateObject dateObject){
            if (viewType==0){
                Integer personId = null;
                if (dateObject instanceof Event){
                    personId = ((Event) dateObject).groupPersonId;
                }
                if (personId==null){
                    eventAuthor.setVisibility(View.GONE);
                }else {
                    GroupPerson author = null;
                    for (GroupPerson person: groupPeople){
                        if (personId.equals(person.groupPersonId)){
                            author = person;
                        }
                    }
                    if (author==null){
                        eventAuthor.setVisibility(View.GONE);
                    }else {
                        String name = personToStringName(author);
                        eventAuthor.setVisibility(View.VISIBLE);
                        eventAuthor.setText(name);
                    }
                }
                eventDate.setText(dateToStringDate(dateObject.date));
                eventTime.setText(dateToStringTime(dateObject.date));
                if (dateObject instanceof Event){
                    eventName.setText(((Event) dateObject).title);
                    if (((Event) dateObject).text!=null) {
                        eventText.setText(((Event) dateObject).text);
                    }
                }
            }
        }

        @Override
        public void onClick(View v) {
            listener.onClickEvent(getAdapterPosition(), v);
        }

        @Override
        public boolean onLongClick(View v) {
            listener.onLongClickEvent(getAdapterPosition(), v);
            return true;
        }
    }

    public interface EventClickListener {
        void onClickEvent(int position, View view);
        void onLongClickEvent(int position, View view);
    }

    private String personToStringName(GroupPerson person){
        String fullname = "";
        if (person.surname!=null){
            if (!person.surname.equals("")){
                fullname+=person.surname+" ";
            }
        }
        if (person.name!=null){
            if (!person.name.equals("")){
                fullname+=person.name+" ";
            }
        }
        if (person.patronymic!=null){
            if (!person.patronymic.equals("")){
                fullname+=person.patronymic;
            }
        }
        if (fullname.equals("")){
            fullname = context.getString(R.string.member)+" ID:"+person.groupPersonId;
        }
        return fullname;
    }

    private String dateToStringDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String sDay = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        if (sDay.length() == 1) {
            sDay = "0"+sDay;
        }
        String sMonth = String.valueOf(calendar.get(Calendar.MONTH)+1);
        if (sMonth.length() == 1) {
            sMonth = "0"+sMonth;
        }
        String sYear = String.valueOf(calendar.get(Calendar.YEAR));
        return sDay+"."+sMonth+"."+sYear;
    }

    private String dateToStringTime(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String sHour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        if (sHour.length() == 1){
            sHour="0"+sHour;
        }
        String sMinute = String.valueOf(calendar.get(Calendar.MINUTE));
        if (sMinute.length() == 1){
            sMinute = "0"+sMinute;
        }
        return sHour+":"+sMinute;
    }
}
