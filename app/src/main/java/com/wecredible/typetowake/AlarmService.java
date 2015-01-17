package com.wecredible.typetowake;

import android.app.Service;

/**
 * Created by Sean on 1/16/15.
 */
public class AlarmService extends Service {
    public static String TAG = AlarmService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AlarmManagerHelper.setAlarms(this);
        return super.onStartCommand(intent, flags, startId);
    }
}
