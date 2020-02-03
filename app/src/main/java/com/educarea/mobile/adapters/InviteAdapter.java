package com.educarea.mobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.educarea.mobile.R;

import java.util.ArrayList;

import transfers.MyInvite;

public class InviteAdapter extends RecyclerView.Adapter<InviteAdapter.InviteHolder> {

    private ArrayList<MyInvite> myInvites;
    private InviteClickListener listener;

    public InviteAdapter(ArrayList<MyInvite> myInvites, InviteClickListener listener) {
        this.myInvites = myInvites;
        this.listener = listener;
    }

    public void setMyInvites(ArrayList<MyInvite> myInvites) {
        this.myInvites = myInvites;
    }

    @NonNull
    @Override
    public InviteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.invite;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutId,parent,false);
        InviteHolder holder = new InviteHolder(view, listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull InviteHolder holder, int position) {
        holder.bind(myInvites.get(position));
    }

    @Override
    public int getItemCount() {
        return myInvites.size();
    }

    public class InviteHolder extends RecyclerView.ViewHolder{

        private MyInvite myInvite;
        private InviteClickListener listener;
        private TextView groupName;
        private Button accept;
        private Button reject;

        public InviteHolder(@NonNull View itemView, final InviteClickListener listener) {
            super(itemView);
            this.listener = listener;
            groupName = itemView.findViewById(R.id.inviteGroupName);
            accept = itemView.findViewById(R.id.invite_accept);
            reject = itemView.findViewById(R.id.invite_reject);

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyInvite myInvite = myInvites.get(getAdapterPosition());
                    listener.onClickAccept(myInvite);
                }
            });

            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyInvite myInvite = myInvites.get(getAdapterPosition());
                    listener.onClickReject(myInvite);
                }
            });
        }

        void bind(MyInvite myInvite){
            this.myInvite = new MyInvite(myInvite.inviteId, myInvite.groupName, myInvite.groupPerson);
            groupName.setText(myInvite.groupName);
        }
    }


    public interface InviteClickListener{

        void onClickAccept(MyInvite myInvite);

        void onClickReject(MyInvite myInvite);
    }
}
