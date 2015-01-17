package com.wecredible.typetowake;

import android.content.BroadcastReceiver;

/**
 * Created by Sean on 1/16/15.
 */
public class AlarmManagerHelper extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        setAlarms(context);
    }

    public static void setAlarms(Context context) {

    }

    public static void cancelAlarms(Context context) {

    }

    private static PendingIntent createPendingIntent(Context context, AlarmModel model) {
        return null;
    }
}
