package com.rahulserver.twitterbooom.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

/**
 * Created with IntelliJ IDEA.
 * User: rahulserver
 * Date: 5/10/14
 * Time: 1:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class PushNotification extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final String link = getIntent().getExtras().getString("link");
        final String msg = getIntent().getExtras().getString("msg");
        if (msg == null || msg.length() == 0) {
            finish();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Message From Twitter Seva Team");
        builder.setMessage(msg);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (link != null && link.length() != 0) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(browserIntent);
                }
                finish();
            }
        });
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {

        }
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}