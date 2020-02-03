package com.educarea.mobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.educarea.mobile.R;

import transfers.GroupPerson;
import transfers.GroupPersons;

public class GroupPersonAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_STUDENT = 0;
    private static final int VIEW_TYPE_TEACHER = 1;
    private static final int VIEW_TYPE_ME = 2;

    private Context mContext;
    private GroupPersons groupPersons;
    private int myId;
    private GroupPersonClickListener listener;

    public GroupPersonAdapter(Context mContext, GroupPersons groupPersons, int myId) {
        this.mContext = mContext;
        this.groupPersons = groupPersons;
        this.myId = myId;
        this.listener = (GroupPersonClickListener) mContext;
    }

    public void setGroupPersons(GroupPersons groupPersons) {
        this.groupPersons = groupPersons;
    }

    @Override
    public int getItemCount() {
        return groupPersons.persons.size();
    }

    @Override
    public int getItemViewType(int position) {
        GroupPerson groupPerson = groupPersons.persons.get(position);
        if (groupPerson.userId==myId){
            return VIEW_TYPE_ME;
        }else if (groupPerson.personType==0){
            return VIEW_TYPE_STUDENT;
        }else {
            return VIEW_TYPE_TEACHER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_STUDENT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_student,parent,false);
        }else if (viewType == VIEW_TYPE_TEACHER){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_teacher,parent,false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_me,parent,false);
        }
        return new GroupPersonHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GroupPerson groupPerson = groupPersons.persons.get(position);
        ((GroupPersonHolder) holder).bind(groupPerson);
    }

    private class GroupPersonHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        TextView personName;
        TextView moderatorBottom;
        GroupPersonClickListener listener;

        public GroupPersonHolder(@NonNull View itemView, GroupPersonClickListener listener) {
            super(itemView);
            this.listener = listener;
            personName = itemView.findViewById(R.id.person_name);
            moderatorBottom = itemView.findViewById(R.id.person_mod_bottom);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        void bind(GroupPerson groupPerson){
            if (groupPerson.surname == null && groupPerson.name == null && groupPerson.patronymic == null){
                personName.setText(mContext.getString(R.string.member)+" ID:"+groupPerson.groupPersonId);
            }else {
                String allName = "";
                if (groupPerson.surname!=null){
                    allName+=groupPerson.surname+" ";
                }
                if (groupPerson.name!=null){
                    allName+=groupPerson.name+" ";
                }
                if (groupPerson.patronymic!=null){
                    allName+=groupPerson.patronymic;
                }
                personName.setText(allName);
            }
            if (groupPerson.moderator==0){
                moderatorBottom.setVisibility(View.GONE);
            }else {
                moderatorBottom.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            listener.onClickGroupPerson(getAdapterPosition(), v);
        }

        @Override
        public boolean onLongClick(View v) {
            listener.onLongClickGroupPerson(getAdapterPosition(), v);
            return true;
        }
    }

    public interface GroupPersonClickListener{
        void onClickGroupPerson(int position, View view);
        void onLongClickGroupPerson(int position, View view);
    }
}
