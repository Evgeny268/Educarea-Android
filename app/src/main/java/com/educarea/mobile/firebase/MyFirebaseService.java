package com.educarea.mobile.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.educarea.mobile.EduApp;
import com.educarea.mobile.notifications.CloudMessageType;
import com.educarea.mobile.notifications.NotifyWorker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MyFirebaseService extends FirebaseMessagingService {
    public MyFirebaseService() {
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        EduApp eduApp = (EduApp)getApplicationContext();
        eduApp.saveCloudToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size()>0){
            NotifyWorker.parseCloudMessage(remoteMessage.getData(), this);
        }
    }

    public static void subscribeToTopics(){
        FirebaseMessaging.getInstance().subscribeToTopic(CloudMessageType.app_news).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d("EDUCAREA", "subscribe to topics");
                }
            }
        });
    }
}
