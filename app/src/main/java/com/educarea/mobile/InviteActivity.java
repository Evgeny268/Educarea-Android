package com.educarea.mobile;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.educarea.mobile.adapters.InviteAdapter;

import transfers.MyInvite;
import transfers.MyInvites;
import transfers.TransferRequestAnswer;
import transfers.Transfers;
import transfers.TransfersFactory;
import transfers.TypeRequestAnswer;

public class InviteActivity extends AppInetActivity implements TypeRequestAnswer, InviteAdapter.InviteClickListener {

    private MyInvites myInvites = new MyInvites();
    private RecyclerView recyclerView;
    private InviteAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        recyclerView = findViewById(R.id.recyclerViewInvites);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(false);
        adapter = new InviteAdapter(myInvites.myInvites,InviteActivity.this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        eduApp.sendTransfers(new TransferRequestAnswer(GET_INVITES));
    }

    @Override
    protected void newMessage(String message) {
        Transfers in = TransfersFactory.createFromJSON(message);
        if (in != null){
            if (in instanceof TransferRequestAnswer){
                if (((TransferRequestAnswer) in).request.equals(UPDATE_INFO)){
                    eduApp.sendTransfers(new TransferRequestAnswer(GET_INVITES));
                }else eduApp.standartReactionOnAsnwer(message,InviteActivity.this);
            }else if (in instanceof MyInvites){
                myInvites = (MyInvites) in;
                adapter.setMyInvites(myInvites.myInvites);
                adapter.notifyDataSetChanged();
            }
            else eduApp.standartReactionOnAsnwer(message,InviteActivity.this);
        }else eduApp.standartReactionOnAsnwer(message,InviteActivity.this);
    }

    @Override
    public void onClickAccept(MyInvite myInvite) {
        TransferRequestAnswer out = new TransferRequestAnswer(ACCEPT_INVITE, String.valueOf(myInvite.inviteId));
        eduApp.sendTransfers(out);
    }

    @Override
    public void onClickReject(MyInvite myInvite) {
        TransferRequestAnswer out = new TransferRequestAnswer(REJECT_INVITE, String.valueOf(myInvite.inviteId));
        eduApp.sendTransfers(out);
    }
}
