package com.educarea.mobile.adapters.diffUtils;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import transfers.PersonalMessage;

public class LastMessagesDiffUtilCallback extends DiffUtil.Callback {

    private final List<PersonalMessage> oldList;
    private final List<PersonalMessage> newList;

    public LastMessagesDiffUtilCallback(List<PersonalMessage> oldList, List<PersonalMessage> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        PersonalMessage oldMessage = oldList.get(oldItemPosition);
        PersonalMessage newMessage = newList.get(newItemPosition);
        return oldMessage.equals(newMessage);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        PersonalMessage oldMessage = oldList.get(oldItemPosition);
        PersonalMessage newMessage = newList.get(newItemPosition);
        return oldMessage.equals(newMessage);
    }
}
