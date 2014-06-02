package com.rahulserver.twitterbooom.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created with IntelliJ IDEA.
 * User: rahulserver
 * Date: 5/21/14
 * Time: 5:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServiceStartReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Intent i=new Intent(context,MailRetreiverGmailService.class);
        i.putExtra("tweet",intent.getStringExtra("tweet"));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startService(i);
    }
}
