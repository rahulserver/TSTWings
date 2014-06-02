package com.rahulserver.twitterbooom.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.rahulserver.twitterbooom.R;
import com.rahulserver.twitterbooom.utils.StaticConstants;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: rahulserver
 * Date: 5/3/14
 * Time: 8:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class DialogActivity extends Activity {
    public static boolean alertShowing = false;
    final Context ctx = this;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);

    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            final String mailTwitterJSON = getIntent().getExtras().getString("mailTwitterJSON");
            final JSONObject jsonObject = new JSONObject(mailTwitterJSON);
            int version = jsonObject.getInt("version");
            int subversion = jsonObject.getInt("subversion");
            if ((version * 10 + subversion) > (StaticConstants.version * 10 + StaticConstants.subversion)) {
                try {
                    Toast.makeText(ctx, "", Toast.LENGTH_LONG);
                } catch (Exception e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            final String eventName = jsonObject.getString("event");
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("Confirmation");
            builder.setIcon(R.drawable.ic_launcher);
            builder.setMessage("Participate in event " + eventName + "?");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    try {
                        SharedPreferences sp = ctx.getSharedPreferences(StaticConstants.EVENT_PARTICIPATE_PREF, MODE_PRIVATE);
                        sp.edit().putString("selection", "yes").commit();
                        int sleepInterval = jsonObject.getInt("waitBetweenTweets");
                        int randomGapBetweenWaits = jsonObject.getInt("gapVariation");
                        SharedPreferences sharedPreferences = ctx.getSharedPreferences(StaticConstants.PRODUCER_JSON_PREF_NAME,
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        final JSONArray tweetArray = jsonObject.getJSONArray("tweets");

                        for (int i = 0; i < tweetArray.length(); i++) {
                            String tweet = tweetArray.getString(i);
                            String interval = String.valueOf((sleepInterval + (int) (Math.random() * randomGapBetweenWaits)) * 1000);
                            editor.putString(tweet, interval).commit();
                        }
                        alertShowing = false;
                        new SendEventConfirmationResponse().execute("yes",eventName);
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } finally {
                        alertShowing = false;
                    }
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
//                    SharedPreferences sp=ctx.getSharedPreferences(StaticConstants.EVENT_PARTICIPATE_PREF,MODE_PRIVATE);
//                    sp.edit().putString("selection","no").commit();
//
//                    alertShowing=false;
//                    dialog.dismiss();
                    showDialog(mailTwitterJSON, "Reconfirming", "Please Confirm-Participate in event?");
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.setCancelable(false);
            alertShowing = true;
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
            } catch (Exception e) {

            }
            alert.show();
        } catch (Exception e) {
            Log.e("Exception", "In dialogActivity", e);
            alertShowing = false;
        }
    }

    public void showDialog(final String mailTwitterJSON, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

        builder.setTitle(title);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setMessage(message);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                try {
                    JSONObject jsonObject=new JSONObject(mailTwitterJSON);
                    final String eventName = jsonObject.getString("event");

                    SharedPreferences sp = ctx.getSharedPreferences(StaticConstants.EVENT_PARTICIPATE_PREF, MODE_PRIVATE);
                    sp.edit().putString("selection", "no").commit();
                    alertShowing = false;
                    new SendEventConfirmationResponse().execute("no",eventName);
                    dialog.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } finally {
                }
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(mailTwitterJSON);
                    try {
                        final String eventName = jsonObject.getString("event");
                        SharedPreferences sp = ctx.getSharedPreferences(StaticConstants.EVENT_PARTICIPATE_PREF, MODE_PRIVATE);
                        sp.edit().putString("selection", "yes").commit();
                        int sleepInterval = jsonObject.getInt("waitBetweenTweets");
                        int randomGapBetweenWaits = jsonObject.getInt("gapVariation");
                        SharedPreferences sharedPreferences = ctx.getSharedPreferences(StaticConstants.PRODUCER_JSON_PREF_NAME,
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        final JSONArray tweetArray = jsonObject.getJSONArray("tweets");

                        for (int i = 0; i < tweetArray.length(); i++) {
                            String tweet = tweetArray.getString(i);
                            String interval = String.valueOf((sleepInterval + (int) (Math.random() * randomGapBetweenWaits)) * 1000);
                            editor.putString(tweet, interval).commit();
                        }
                        alertShowing = false;
                        new SendEventConfirmationResponse().execute("yes",eventName);
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } finally {
                        alertShowing = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.show();
    }

    class SendEventConfirmationResponse extends AsyncTask<String, String, String> {
        ProgressDialog mProgress;

        @Override
        protected void onPreExecute() {
            mProgress = new ProgressDialog(DialogActivity.this);
            mProgress.setTitle("Processing Response..");
            mProgress.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String confirmResult = strings[0];
                String eventName = strings[1];
                SharedPreferences sharedPreferences = ctx.getSharedPreferences(StaticConstants.APP_PREF, MODE_PRIVATE);
                String gmailUsername = sharedPreferences.getString(StaticConstants.USERNAME_PREF_KEY, "");
                String gmailPassword = sharedPreferences.getString(StaticConstants.PASSWORD_PREF_KEY, "");

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("event", eventName);
                jsonObject.put("response", confirmResult);
                jsonObject.put("email",gmailUsername );
                jsonObject.put("timestamp",System.currentTimeMillis());
                if (StaticConstants.sendEmail(gmailUsername, gmailPassword, gmailUsername,
                        StaticConstants.EVENT_PARTICIPATE_RESPONSE_EMAIL, "EventParticipationResponseJSON",
                        jsonObject.toString())) {
                    return "true";
                }
            } catch (Exception e) {

            } finally {

            }
            return "false";  //To change body of implemented methods use File | Settings | File Templates.
        }
        @Override
        protected void onPostExecute(String s){
            try {
                if(s.equalsIgnoreCase("true")){
                    try {
                        Toast.makeText(ctx, "Response Processed Successfully!", Toast.LENGTH_LONG);
                    } catch (Exception e1) {
                        Log.e("Exc","Exc",e1);
                    }
                }else{
                    try {
                        Toast.makeText(ctx, "Error Processing Response!", Toast.LENGTH_LONG);
                    } catch (Exception e1) {
                        Log.e("Exc","Exc",e1);
                    }
                }
                mProgress.dismiss();
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } finally {
                finish();
            }
        }
    }
}