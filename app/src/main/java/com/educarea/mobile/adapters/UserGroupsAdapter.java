package com.educarea.mobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.educarea.mobile.R;

import transfers.Group;
import transfers.GroupPerson;
import transfers.UserGroups;

public class UserGroupsAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_USER = 0;
    private static final int VIEW_TYPE_MODERATOR = 1;

    private Context mContext;
    private UserGroups userGroups;
    private MyGroupClickListener myGroupClickListener;

    public UserGroupsAdapter(Context mContext, UserGroups userGroups) {
        this.mContext = mContext;
        this.userGroups = userGroups;
        this.myGroupClickListener = (MyGroupClickListener) mContext;
    }

    public void setUserGroups(UserGroups userGroups) {
        this.userGroups = userGroups;
    }

    @Override
    public int getItemCount() {
        return userGroups.groups.size();
    }

    @Override
    public int getItemViewType(int position) {
        Group group = userGroups.getGroup(position);
        GroupPerson person = userGroups.getGroupPerson(group);
        if (person.moderator==0){
            return VIEW_TYPE_USER;
        }else {
            return VIEW_TYPE_MODERATOR;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_USER){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_groups,parent,false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_groups_moderator,parent,false);
        }
        return new MyGroupsHolder(view, myGroupClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Group group = userGroups.getGroup(position);
        GroupPerson groupPerson = userGroups.getGroupPerson(group);
        ((MyGroupsHolder) holder).bind(group, groupPerson);
    }

    private class MyGroupsHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        TextView viewGroupName;
        TextView viewRole;
        MyGroupClickListener myGroupClickListener;

        public MyGroupsHolder(@NonNull View itemView, MyGroupClickListener myGroupClickListener) {
            super(itemView);
            viewGroupName = itemView.findViewById(R.id.my_group_name);
            viewRole = itemView.findViewById(R.id.my_group_role);
            this.myGroupClickListener = myGroupClickListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        void bind(Group group, GroupPerson groupPerson){
            viewGroupName.setText(group.name);
            if (groupPerson.personType==0){
                viewRole.setText(mContext.getString(R.string.role_student));
            }else {
                viewRole.setText(mContext.getString(R.string.role_teacher));
            }
        }

        @Override
        public void onClick(View v) {
            myGroupClickListener.onClickMyGroup(getAdapterPosition(),v);
        }

        @Override
        public boolean onLongClick(View v) {
            myGroupClickListener.onLongClickMyGroup(getAdapterPosition(),v);
            return true;
        }
    }

    public interface MyGroupClickListener{
        void onClickMyGroup(int position, View view);
        void onLongClickMyGroup(int position, View view);
    }
}
