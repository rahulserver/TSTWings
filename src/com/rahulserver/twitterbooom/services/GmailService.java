package com.rahulserver.twitterbooom.services;

import android.app.AlertDialog;
import android.app.IntentService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;
import com.rahulserver.twitterbooom.utils.StaticConstants;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: rahulserver
 * Date: 1/26/14
 * Time: 4:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class GmailService extends IntentService implements
        TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    public SharedPreferences sharedPreferences;
    IMAPStore imapStore = null;
    IMAPFolder folder = null;

    public GmailService() {
        super("GmailService");
    }

    public GmailService(String name) {
        super(name);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //To change body of implemented methods use File | Settings | File Templates.
//        Twitter twitter = new TwitterFactory().getInstance();
//        twitter.setOAuthConsumer(StaticConstants.consumer_key, StaticConstants.secret_key);
//        SharedPreferences twitterSharedPreferences = GmailService.this.
//                getSharedPreferences(StaticConstants.APP_PREF, Context.MODE_PRIVATE);
//        String tokenKey = twitterSharedPreferences.getString(StaticConstants.
//                TWEET_AUTH_KEY, "");
//        String tokenSecret = twitterSharedPreferences.getString(StaticConstants.
//                TWEET_AUTH_SECRET_KEY, "");
//        twitter.setOAuthAccessToken(new AccessToken(tokenKey, tokenSecret));

        try {
            if (tts != null)
                tts = new TextToSpeech(this, this);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        final SharedPreferences sharedPreferences = GmailService.this.getSharedPreferences(StaticConstants.APP_PREF, Context.MODE_PRIVATE);
        String twitterUid = sharedPreferences.getString(StaticConstants.TWITTER_USERNAME_KEY, "");
        String twitterPwd = sharedPreferences.getString(StaticConstants.TWITTER_PASSWORD_KEY, "");

        StaticConstants.gmailServiceRunning = true;
        if (twitterUid == null || twitterUid.length() == 0 || twitterPwd == null || twitterPwd.length() == 0) {
            new ToastMessageTask().execute("Add a twitter account first!");
            try {
                speakOut("Please add a twitter account!");
            } catch (Exception ex) {
                ex.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return;
        }
//        Toast.makeText(getApplicationContext(), "Service started!!", Toast.LENGTH_LONG).show();
        SharedPreferences producerPreference = getSharedPreferences(StaticConstants.PRODUCER_JSON_PREF_NAME, MODE_PRIVATE);
        SharedPreferences consumerPreference = getSharedPreferences(StaticConstants.CONSUMER_JSON_PREF_NAME, MODE_PRIVATE);
        Map<String, ?> cmap = consumerPreference.getAll();
        Set<String> keySet = cmap.keySet();
        if (keySet.size() == 0) {
            Map<String, ?> pmap = producerPreference.getAll();
            keySet = pmap.keySet();
            SharedPreferences.Editor editor = consumerPreference.edit();
            for (String s : keySet) {
                editor.putString(s, pmap.get(s).toString()).commit();
                producerPreference.edit().remove(s).commit();
            }
        }
        keySet = cmap.keySet();
        ArrayList<String> al=new ArrayList<String>(keySet);
        try {
            Collections.shuffle(al);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        for (String s : al) {
            int interval = Integer.parseInt(consumerPreference.getString(s, "180"));
            consumerPreference.edit().remove(s).commit();
            try{
                try {
                    HttpUrlConnectionExample.runThisClass(s,twitterUid,twitterPwd);
                } catch (Exception e) {
                    Log.e("ExceptionNullPointer",e.getMessage(),e);
                }
                Log.d("TweetSuccess",s);
                Thread.sleep(interval);

            } catch (Exception e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

    }

    // A class that will run Toast messages in the main GUI context
    public class ToastMessageTask extends AsyncTask<String, String, String> {
        String toastMessage;

        @Override
        protected String doInBackground(String... params) {
            toastMessage = params[0];
            return toastMessage;
        }

        protected void OnProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        // This is executed in the context of the main GUI thread
        protected void onPostExecute(String result) {
            Toast toast = Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT);
            toast.show();
        }
    }// A class that will run Toast messages in the main GUI context

    public class AlertMessageTask extends AsyncTask<String, String, String> {
        String alertMessage;

        @Override
        protected String doInBackground(String... params) {
            alertMessage = params[0];
            return alertMessage;
        }

        protected void OnProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        // This is executed in the context of the main GUI thread
        protected void onPostExecute(String result) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getApplicationContext());
            alert.setTitle("Alert!");
            alert.setMessage(result);
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            alert.show();
        }
    }

    public static String getText(Part p) throws
            MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            String s = (String) p.getContent();
            boolean textIsHtml = p.isMimeType("text/html");
            return s;
        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart) p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text = getText(bp);
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null)
                        return s;
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }

        return null;
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                try {
                    speakOut("Service Started!!");
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }


        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    private void speakOut(String text) throws Exception {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
    private class POSTAsyncTask extends AsyncTask<String,Void,Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                HttpUrlConnectionExample.runThisClass(strings[0],strings[1],strings[2]);
                return true;
            } catch (Exception e) {
                Log.e("ExceptionNullPointer",e.getMessage(),e);
                return false;
            }
        }

    }
}
