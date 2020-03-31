package com.educarea.mobile.firebase;

import androidx.annotation.NonNull;

import com.educarea.mobile.EduApp;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseService extends FirebaseMessagingService {
    public MyFirebaseService() {
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        EduApp eduApp = (EduApp)getApplicationContext();
        eduApp.saveCloudToken(s);
    }
}
