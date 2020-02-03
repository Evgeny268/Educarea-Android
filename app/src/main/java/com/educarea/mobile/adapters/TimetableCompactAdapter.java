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

import transfers.GroupPerson;
import transfers.Timetable;

public class TimetableCompactAdapter extends RecyclerView.Adapter<TimetableCompactAdapter.TimetableCompactHolder>{

    private ArrayList<Timetable> timetables;
    private ArrayList<GroupPerson> groupPeople;
    private Context context;
    TimetableCompactClickListener listener;

    public TimetableCompactAdapter(Context context) {
        this.context = context;
        listener = (TimetableCompactClickListener) context;
        timetables = new ArrayList<>();
        groupPeople = new ArrayList<>();
    }

    public void setTimetablesAndPersons(ArrayList<Timetable> timetables, ArrayList<GroupPerson> groupPeople){
        this.timetables = timetables;
        this.groupPeople = groupPeople;
    }

    @NonNull
    @Override
    public TimetableCompactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.timetable_compact;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutId,parent,false);
        TimetableCompactHolder holder = new TimetableCompactHolder(view,listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TimetableCompactHolder holder, int position) {
        holder.bind(timetables.get(position));
    }

    @Override
    public int getItemCount() {
        return timetables.size();
    }

    public class TimetableCompactHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private Timetable timetable;
        private TimetableCompactClickListener listener;
        private TextView objectName;
        private TextView personName;
        private TextView cabinet;
        private TextView time;

        public TimetableCompactHolder(@NonNull View itemView, TimetableCompactClickListener listener) {
            super(itemView);
            this.listener = listener;
            objectName = itemView.findViewById(R.id.tc_object_name);
            personName = itemView.findViewById(R.id.tc_person_name);
            cabinet = itemView.findViewById(R.id.tc_cabinet);
            time = itemView.findViewById(R.id.tc_time);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClickTimetableCompact(getAdapterPosition(),v);
        }

        @Override
        public boolean onLongClick(View v) {
            listener.onLongClickTimetableCompact(getAdapterPosition(),v);
            return true;
        }

        void bind(Timetable timetable){
            this.timetable = timetable;
            if (timetable.groupPersonId!=0) {
                GroupPerson groupPerson = new GroupPerson();
                groupPerson.surname = context.getString(R.string.member)+" ID:"+timetable.groupPersonId;
                for (int i = 0; i < groupPeople.size(); i++) {
                    if (timetable.groupPersonId == groupPeople.get(i).groupPersonId) {
                        groupPerson = groupPeople.get(i);
                    }
                }
                personName.setText(groupPerson.surname+" "+groupPerson.name+" "+groupPerson.patronymic);
            }
            objectName.setText(timetable.objectName);
            if (timetable.cabinet!=null){
                cabinet.setText(timetable.cabinet);
            }
            if (timetable.time!=null){
                time.setText(timetable.time);
            }
        }
    }

    public interface TimetableCompactClickListener{

        void onClickTimetableCompact(int position, View view);

        void onLongClickTimetableCompact(int position, View view);

    }
}
