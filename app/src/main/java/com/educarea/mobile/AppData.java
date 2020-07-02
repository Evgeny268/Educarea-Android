package com.educarea.mobile;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import transfers.EventList;
import transfers.GroupPerson;
import transfers.GroupPersons;
import transfers.Timetables;
import transfers.User;
import transfers.UserGroups;

public class AppData implements Serializable {

    private static final String FILESAVE = "fsave"; //path to file save

    private User user = null;
    private UserGroups userGroups = null;
    private GroupPersons groupPersons = null;
    private Timetables timetables = null;
    private EventList eventList = null;

    public AppData() {
        userGroups = new UserGroups();
        groupPersons = new GroupPersons();
        timetables = new Timetables();
        eventList = new EventList();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user, Context context) {
        this.user = user;
        saveData(context);
    }

    public UserGroups getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(UserGroups userGroups, Context context) {
        this.userGroups = userGroups;
        deleteExtraData();
        saveData(context);
    }

    public GroupPersons getGroupPersons() {
        return groupPersons;
    }

    public GroupPersons getGroupPersons(int groupId) {
        GroupPersons outPersons = new GroupPersons();
        for (int i = 0; i < groupPersons.persons.size(); i++) {
            if (groupPersons.persons.get(i).groupId == groupId){
                outPersons.persons.add(groupPersons.persons.get(i));
            }
        }
        return outPersons;
    }

    public GroupPerson getGroupPersonById(int groupPersonId){
        for (GroupPerson groupPerson: groupPersons.persons){
            if (groupPerson.groupPersonId == groupPersonId){
                return groupPerson;
            }
        }
        return null;
    }

    public void setGroupPersons(GroupPersons groupPersons, Context context) {
        LinkedHashSet<Integer> groupsId = new LinkedHashSet<>();
        for (int i = 0; i < groupPersons.persons.size(); i++) {
            groupsId.add(groupPersons.persons.get(i).groupId);
        }
        ArrayList<Integer> groupsIdList = new ArrayList<>(groupsId);
        for (int i = 0; i < groupsIdList.size(); i++) {
            int j = 0;
            while (j<this.groupPersons.persons.size()){
                if (groupsIdList.get(i) == this.groupPersons.persons.get(j).groupId){
                    this.groupPersons.persons.remove(j);
                }else {
                    j++;
                }
            }
        }
        this.groupPersons.persons.addAll(groupPersons.persons);
        saveData(context);
    }

    public Timetables getTimetables() {
        return timetables;
    }

    public Timetables getTimetables(int groupId) {
        Timetables out = new Timetables();
        for (int i = 0; i < timetables.timetables.size(); i++) {
            if (timetables.timetables.get(i).groupId == groupId){
                out.timetables.add(timetables.timetables.get(i));
            }
        }
        return out;
    }

    public void setTimetables(Timetables timetables, Context context) {
        LinkedHashSet<Integer> groupsId = new LinkedHashSet<>();
        for (int i = 0; i < timetables.timetables.size(); i++) {
            groupsId.add(timetables.timetables.get(i).groupId);
        }
        ArrayList<Integer> groupsIdList = new ArrayList<>(groupsId);
        for (int i = 0; i < groupsIdList.size(); i++){
            int j = 0;
            while (j < this.timetables.timetables.size()){
                if (groupsIdList.get(i) == this.timetables.timetables.get(j).groupId){
                    this.timetables.timetables.remove(j);
                }else {
                    j++;
                }
            }
        }
        this.timetables.timetables.addAll(timetables.timetables);
        saveData(context);
    }

    public void saveData(Context context){
        try(FileOutputStream stream = context.openFileOutput(FILESAVE, Context.MODE_PRIVATE);
        ObjectOutputStream os = new ObjectOutputStream(stream)
        ) {
            os.writeObject(this);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void loadData(Context context){
        try(FileInputStream stream = context.openFileInput(FILESAVE);
            ObjectInputStream is = new ObjectInputStream(stream)){
            Object input = is.readObject();
            if (input!=null){
                if (input instanceof AppData){
                    this.user = ((AppData) input).user;
                    this.userGroups = ((AppData) input).userGroups;
                    this.groupPersons = ((AppData) input).groupPersons;
                    this.timetables = ((AppData) input).timetables;
                    this.eventList = ((AppData) input).eventList;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteExtraData(){
        int i = 0;
        while (i < groupPersons.persons.size()) {
            boolean delete = true;
            for (int j = 0; j < userGroups.groups.size(); j++) {
                if (groupPersons.persons.get(i).groupId == userGroups.groups.get(j).groupId){
                    delete = false;
                }
            }
            if (delete){
                groupPersons.persons.remove(i);
            }else {
                i++;
            }
        }

        i = 0;
        while (i < timetables.timetables.size()) {
            boolean delete = true;
            for (int j = 0; j < userGroups.groups.size(); j++) {
                if (timetables.timetables.get(i).groupId == userGroups.groups.get(j).groupId){
                    delete = false;
                }
            }
            if (delete){
                timetables.timetables.remove(i);
            }else {
                i++;
            }
        }
    }
}
