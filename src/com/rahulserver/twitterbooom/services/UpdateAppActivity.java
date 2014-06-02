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
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateAppActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    protected void onStart(){
        super.onStart();
        final String link=getIntent().getExtras().get("link").toString();
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Update App!");
        builder.setMessage("A new version of app is available! Kindly update!!");
        builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(browserIntent);
                finish();
            }
        });
        builder.setNegativeButton("Later",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {

        }
        AlertDialog dialog=builder.create();
        dialog.setCancelable(false);
        dialog.show();

    }
}