package com.example.MusicX;
import android.app.Application;

import com.onesignal.OneSignal;

public class PushNotification extends Application {
    public static final String ONESIGNAL_APP_ID="b80c58fe-8a0c-47ef-8a38-192e964824e5";


    public void onCreate(){
        super.onCreate();
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE,OneSignal.LOG_LEVEL.NONE);

        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

    }


}
