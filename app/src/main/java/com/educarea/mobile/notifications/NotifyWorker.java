package com.educarea.mobile.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.educarea.mobile.AppData;
import com.educarea.mobile.DialogActivity;
import com.educarea.mobile.LastMessagesActivity;
import com.educarea.mobile.MainActivity;
import com.educarea.mobile.R;
import com.educarea.mobile.StudentsChatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import transfers.GroupPerson;

public class NotifyWorker implements CloudMessageType{

    public static final String CHANNEL_GROUP_CHANNEL = "CHANNEL_GROUP_CHANNEL";
    public static final String CHANNEL_STUDENTS_CHAT = "CHANNEL_STUDENTS_CHAT";
    public static final String CHANNEL_EVENT = "CHANNEL_EVENT";
    public static final String CHANNEL_PERSONAL_MESSAGE = "CHANNEL_PERSONAL_MESSAGE";
    public static final String CHANNEL_APP_NEWS = "CHANNEL_APP_NEWS";

    private static final int SHORT_MESSAGE_SIZE = 27;

    public static void createAppChannels(Context context){
        createGroupChannel(context);
        createStudentsChatChannel(context);
        createAppNewsChannel(context);
        createEventChannel(context);
        createPersonalMessageChannel(context);
    }

    protected static void createGroupChannel(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = context.getApplicationContext().getString(R.string.group_channel);
            String description = context.getApplicationContext().getString(R.string.group_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_GROUP_CHANNEL, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private static void createStudentsChatChannel(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getApplicationContext().getString(R.string.students_chat);
            String description = context.getApplicationContext().getString(R.string.students_chat_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_STUDENTS_CHAT, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    protected static void createEventChannel(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = context.getApplicationContext().getString(R.string.events);
            String description = context.getApplicationContext().getString(R.string.event_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_EVENT, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    protected static void createAppNewsChannel(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = context.getApplicationContext().getString(R.string.app_news);
            String description = context.getApplicationContext().getString(R.string.app_news_description);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_APP_NEWS, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    protected static void createPersonalMessageChannel(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = context.getApplicationContext().getString(R.string.personal_messages);
            String description = context.getApplicationContext().getString(R.string.personal_message_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_PERSONAL_MESSAGE, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void parseCloudMessage(Map<String,String> data, Context context){
        String type = data.get("type");
        if (type!=null){
            if (type.equals(app_news)){
                notifyAppNews(data, context);
            }else if (type.equals(channel_message)){
                notifyChannelMessage(data, context);
            } else if (type.equals(student_message)){
                notifyStudentsChatMessage(data, context);
            } else if (type.equals(event)){
                notifyEvent(data, context);
            } else if (type.equals(channel_personal_message)){
                notifyPersonalMessage(data, context);
            }
        }
    }

    private static void notifyAppNews(Map<String,String> data, Context context){
        String message = data.get("message");
        if (message==null) return;
        if (message.equals(update_available)){
            message = context.getString(R.string.update_available);
        }
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_APP_NEWS)
                .setSmallIcon(R.drawable.ic_add_black_24dp)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pi)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(2222, builder.build());
    }

    private static void notifyChannelMessage(Map<String,String> data, Context context){
        String message = data.get("message");
        String group = data.get("group");
        String who = data.get("who");
        if (who==null){
            who = "";
        }else {
            who=":"+data.get("who");
        }
        if (message == null || group == null) return;
        Intent i = new Intent(context, MainActivity.class);
        i.putExtra("Dialog",data.get("who"));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_GROUP_CHANNEL)
                .setSmallIcon(R.drawable.ic_add_black_24dp)
                .setContentTitle(context.getString(R.string.group_channel)+who)
                .setContentText(group)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pi)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(2223, builder.build());
    }

    private static void notifyEvent(Map<String,String> data, Context context){
        String text = "";
        String name = data.get("name");
        String sdate = data.get("date");
        Long lDate = null;
        if (name== null || sdate == null){
            return;
        }
        try {
            lDate = Long.parseLong(sdate);
        }catch (Exception e){
            return;
        }
        Date date = new Date(lDate);
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        text = name + ". "+ context.getString(R.string.date_and_time)+": "+format.format(date);
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_EVENT)
                .setSmallIcon(R.drawable.ic_add_black_24dp)
                .setContentTitle(context.getString(R.string.new_event))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pi)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(2225, builder.build());
    }

    private static void notifyStudentsChatMessage(Map<String,String> data, Context context){
        if (StudentsChatActivity.STUDENT_CHAT_OPEN){
            return;
        }
        String message = data.get("message");
        String group = data.get("group");
        String who = data.get("who");
        if (who==null){
            who = "";
        }else {
            who=":"+data.get("who");
        }
        if (message == null || group == null) return;
        Intent i = new Intent(context, MainActivity.class);
        i.putExtra("Dialog",data.get("who"));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_STUDENTS_CHAT)
                .setSmallIcon(R.drawable.ic_add_black_24dp)
                .setContentTitle(context.getString(R.string.students_chat)+who)
                .setContentText(group)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pi)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(2224, builder.build());
    }

    private static void notifyPersonalMessage(Map<String,String> data, Context context){
        try {
            AppData appData = new AppData();
            appData.loadData(context);
            String message = data.get("message");
            String shortMessage = message;
            if (shortMessage.length() > SHORT_MESSAGE_SIZE){
                shortMessage = shortMessage.substring(0,SHORT_MESSAGE_SIZE)+"...";
            }
            int groupId = Integer.parseInt(data.get("group_id"));
            if (LastMessagesActivity.messages_open_group!=null){
                if (LastMessagesActivity.messages_open_group.equals(groupId)){
                    return;
                }
            }
            int groupPersonId = Integer.parseInt(data.get("groupPersonId"));
            if (DialogActivity.personal_message_interlocutorId != null){
                if (DialogActivity.personal_message_interlocutorId == groupPersonId){
                    return;
                }
            }
            GroupPerson person = appData.getGroupPersonById(groupPersonId);
            String personName = getShortName(person, context);
            Intent i = new Intent(context, MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_PERSONAL_MESSAGE)
                    .setSmallIcon(R.drawable.ic_add_black_24dp)
                    .setContentTitle(personName)
                    .setContentText(shortMessage)
                    .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(message))
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(pi)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(2226, builder.build());
        }catch (Exception ignored){}
    }

    public static void cancelChannelMessage(Context context){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(2223);
    }

    public static void cancelStudentsChatMessage(Context context){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(2224);
    }

    public static void cancelEvent(Context context){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(2225);
    }

    public static void cancelAppNews(Context context){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(2222);
    }

    public static String getShortName(GroupPerson groupPerson, Context context){
        if (groupPerson == null){
            return "";
        }
        String name = "";
        if (groupPerson.surname == null && groupPerson.name == null && groupPerson.patronymic == null){
            name = context.getString(R.string.member)+" ID:"+groupPerson.groupPersonId;
        }else {
            if (groupPerson.surname != null){
                if (!groupPerson.surname.equals("")){
                    name+=groupPerson.surname;
                }
            }
            if (groupPerson.name != null){
                if (!groupPerson.name.equals("")){
                    if (name.length()>0){
                        name+=" "+groupPerson.name.substring(0,1).toUpperCase()+".";
                    }else {
                        name+=groupPerson.name;
                    }
                }
            }
            if (groupPerson.patronymic != null){
                if (!groupPerson.patronymic.equals("")){
                    if (name.length()>0){
                        name+=" "+groupPerson.patronymic.substring(0,1).toUpperCase()+".";
                    }else {
                        name+=groupPerson.patronymic;
                    }
                }
            }
            if (name.equals("")){
                name = context.getString(R.string.member)+" ID:"+groupPerson.groupPersonId;
            }
        }
        return name;
    }
}
