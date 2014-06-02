package com.rahulserver.twitterbooom.broadcastreceivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.rahulserver.twitterbooom.utils.StaticConstants;

/**
 * Created with IntelliJ IDEA.
 * User: rahulserver
 * Date: 4/20/14
 * Time: 10:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class BootReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if(context.getSharedPreferences("Alarm",context.MODE_PRIVATE).getString("status","").compareTo("y")==0){
        Intent i = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, StaticConstants.RQS_1,
                i, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                StaticConstants.POLLING_INTERVAL_MILLISECONDS, pendingIntent);
        }
    }
}
