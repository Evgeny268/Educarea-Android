package com.educarea.mobile.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.educarea.mobile.MainActivity;
import com.educarea.mobile.R;

import java.util.Map;

public class NotifyWorker implements CloudMessageType{

    public static final String CHANNEL_GROUP_CHANNEL = "CHANNEL_GROUP_CHANNEL";
    public static final String CHANNEL_APP_NEWS = "CHANNEL_APP_NEWS";

    public static void createAppChannels(Context context){
        createGroupChannel(context);
        createAppNewsChannel(context);
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

    public static void parseCloudMessage(Map<String,String> data, Context context){
        String type = data.get("type");
        if (type!=null){
            if (type.equals(app_news)){
                notifyAppNews(data, context);
            }else if (type.equals(channel_message)){
                notifyChannelMessage(data, context);
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
}
