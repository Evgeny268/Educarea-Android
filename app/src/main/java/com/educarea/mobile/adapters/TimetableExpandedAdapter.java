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

public class TimetableExpandedAdapter extends RecyclerView.Adapter<TimetableExpandedAdapter.TimetableExpandHolder>{

    private ArrayList<Timetable> timetables;
    private ArrayList<GroupPerson> groupPeople;
    private Context context;
    private TimetableExpandClickListener listener;

    public TimetableExpandedAdapter(Context context) {
        this.context = context;
        listener = (TimetableExpandClickListener) context;
        timetables = new ArrayList<>();
        groupPeople = new ArrayList<>();
    }

    public void setTimetablesAndPersons(ArrayList<Timetable> timetables, ArrayList<GroupPerson> groupPeople){
        this.timetables = timetables;
        this.groupPeople = groupPeople;
    }

    @NonNull
    @Override
    public TimetableExpandHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.timetable_expanded;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutId,parent,false);
        TimetableExpandHolder holder = new TimetableExpandHolder(view,listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TimetableExpandHolder holder, int position) {
        holder.bind(timetables.get(position));
    }

    @Override
    public int getItemCount() {
        return timetables.size();
    }

    public class TimetableExpandHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private Timetable timetable;
        private TimetableExpandClickListener listener;
        private TextView objectName;
        private TextView personName;
        private TextView cabinet;
        private TextView parityweek;
        private TextView day;
        private TextView time;

        public TimetableExpandHolder(@NonNull View itemView, final TimetableExpandClickListener listener) {
            super(itemView);
            this.listener = listener;
            objectName = itemView.findViewById(R.id.te_object_name);
            personName = itemView.findViewById(R.id.te_person_name);
            cabinet = itemView.findViewById(R.id.te_cabinet);
            parityweek = itemView.findViewById(R.id.te_parityweek);
            day = itemView.findViewById(R.id.te_day);
            time = itemView.findViewById(R.id.te_time);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClickTimetableExpand(getAdapterPosition(),v);
        }

        @Override
        public boolean onLongClick(View v) {
            listener.onLongClickTimetableExpand(getAdapterPosition(),v);
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
            }else {
                personName.setText("");
            }
            objectName.setText(timetable.objectName);
            if (timetable.cabinet!=null){
                cabinet.setText(timetable.cabinet);
            }
            switch (timetable.parityweek){
                case 0:
                    parityweek.setText(context.getString(R.string.every_week));
                    break;
                case 1:
                    parityweek.setText(context.getString(R.string.odd_week));
                    break;
                case 2:
                    parityweek.setText(context.getString(R.string.even_week));
                    break;
            }

            switch (timetable.day){
                case 1:
                    day.setText(context.getString(R.string.sunday));
                    break;
                case 2:
                    day.setText(context.getString(R.string.monday));
                    break;
                case 3:
                    day.setText(context.getString(R.string.tuesday));
                    break;
                case 4:
                    day.setText(context.getString(R.string.wednesday));
                    break;
                case 5:
                    day.setText(context.getString(R.string.thursday));
                    break;
                case 6:
                    day.setText(context.getString(R.string.friday));
                    break;
                case 7:
                    day.setText(context.getString(R.string.saturday));
                    break;
            }
            if (timetable.time!=null){
                time.setText(timetable.time);
            }
        }
    }

    public interface TimetableExpandClickListener{

        void onClickTimetableExpand(int position, View view);

        void onLongClickTimetableExpand(int position, View view);
    }
}
