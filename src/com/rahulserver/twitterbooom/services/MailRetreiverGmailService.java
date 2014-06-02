package com.rahulserver.twitterbooom.services;

import android.app.*;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import com.rahulserver.twitterbooom.R;
import com.rahulserver.twitterbooom.Twitter_sharing.MainActivity;
import com.rahulserver.twitterbooom.utils.StaticConstants;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: rahulserver
 * Date: 4/20/14
 * Time: 7:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class MailRetreiverGmailService extends Service {
    public static boolean alertShowing = false;
    PollGmailTask pgt;
    PostTweetsTask ptt;
    TwoTasks tts;
//    public static Object prodConsLock;
//    public static Object mLock;
    static int oldmsgscount = 0, msgscount = 0;
//    public MailRetreiverGmailService(String name) {
//        super(name);
//    }
//
//    public MailRetreiverGmailService() {
//        super("MailRetreiverGmailService");
//    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
//        mLock = new Object();
//        prodConsLock = new Object();
        Notification notification = new Notification(R.drawable.tstwings_icon, "TST",
                System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(this, "TSTWings",
                "TSTWings", pendingIntent);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                Log.e("UncaughtExceptionFromTSTWings","Uncaught",throwable);
            }
        });
        startForeground(1947, notification);
        tts=new TwoTasks(this);
        tts.execute();
//        pgt = new PollGmailTask(this);
//        pgt.execute();
//        Thread t = new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                while (true) {
//                    final SharedPreferences sharedPreferences = MailRetreiverGmailService.this.getSharedPreferences(StaticConstants.APP_PREF, Context.MODE_PRIVATE);
//                    String twitterUid = sharedPreferences.getString(StaticConstants.TWITTER_USERNAME_KEY, "");
//                    String twitterPwd = sharedPreferences.getString(StaticConstants.TWITTER_PASSWORD_KEY, "");
//
//                    SharedPreferences producerPreference = getSharedPreferences(StaticConstants.PRODUCER_JSON_PREF_NAME, MODE_PRIVATE);
//                    SharedPreferences consumerPreference = getSharedPreferences(StaticConstants.CONSUMER_JSON_PREF_NAME, MODE_PRIVATE);
//                    Map<String, ?> cmap = consumerPreference.getAll();
//                    Set<String> keySet = cmap.keySet();
//                    if (keySet.size() == 0) {
//                        Map<String, ?> pmap = producerPreference.getAll();
//                        keySet = pmap.keySet();
//                        SharedPreferences.Editor editor = consumerPreference.edit();
//                        for (String s : keySet) {
//                            editor.putString(s, pmap.get(s).toString()).commit();
//                            producerPreference.edit().remove(s).commit();
//                        }
//                    }
//                    keySet = cmap.keySet();
//
//                    if (keySet.size() == 0) {
//                        try {
//                            Thread.sleep(60000);
//                        } catch (InterruptedException e) {
//                            Log.e("exc", "exc", e);
//                        } finally {
//                            continue;
//                        }
//                    }
//
//                    ArrayList<String> al = new ArrayList<String>(keySet);
//                    try {
//                        Collections.shuffle(al);
//                    } catch (Exception e) {
//                        Log.e("Err", "err", e);
//                    }
//                    for (String s : al) {
//                        int interval = Integer.parseInt(consumerPreference.getString(s, "180"));
//                        consumerPreference.edit().remove(s).commit();
//                        try {
//                            try {
//                                HttpUrlConnectionExample.runThisClass(s, twitterUid, twitterPwd);
//                            } catch (Exception e) {
//                                Log.e("ExceptionNullPointer", e.getMessage(), e);
//                            }
//                            Log.d("TweetSuccess", s);
//                            Thread.sleep(interval);
//
//                        } catch (Exception e1) {
//                            Log.e("Err", "err", e1);
//                        }
//                    }
//                    Log.d("PTT", "whileLoopEnd");
//                }
//            }
//        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        if(tts==null||!(tts.getStatus()== AsyncTask.Status.RUNNING)){
            tts=new TwoTasks(this);
            String s=intent.getStringExtra("tweet");
            if(s==null)
                tts.execute();
            else
                tts.execute(s);
        }
        return START_STICKY;
    }

    private class PostTweetsTask extends AsyncTask<String, String, String> {
        Context context;

        PostTweetsTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onProgressUpdate(String... s) {

        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d("PTT", "doInBackground");

            while (true) {
                final SharedPreferences sharedPreferences = context.getSharedPreferences(StaticConstants.APP_PREF, Context.MODE_PRIVATE);
                String twitterUid = sharedPreferences.getString(StaticConstants.TWITTER_USERNAME_KEY, "");
                String twitterPwd = sharedPreferences.getString(StaticConstants.TWITTER_PASSWORD_KEY, "");

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

                if (keySet.size() == 0) {
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        Log.e("exc", "exc", e);
                    } finally {
                        continue;
                    }
                }

                ArrayList<String> al = new ArrayList<String>(keySet);
                try {
                    Collections.shuffle(al);
                } catch (Exception e) {
                    Log.e("Err", "err", e);
                }
                for (String s : al) {
                    int interval = Integer.parseInt(consumerPreference.getString(s, "180"));
                    consumerPreference.edit().remove(s).commit();
                    try {
                        try {
                            HttpUrlConnectionExample.runThisClass(s, twitterUid, twitterPwd);
                        } catch (Exception e) {
                            Log.e("ExceptionNullPointer", e.getMessage(), e);
                        }
                        Log.d("TweetSuccess", s);
                        Thread.sleep(interval);

                    } catch (Exception e1) {
                        Log.e("Err", "err", e1);
                    }
                }
                Log.d("PTT", "whileLoopEnd");

            }
        }
    }

    private class TwoTasks extends AsyncTask<String, String, String> {
        Context context;

        TwoTasks(Context context) {
            this.context = context;
        }

        @Override
        protected void onProgressUpdate(String... s) {
            try {
                addKeyValSetsToSharedPref(context, s[0]);
            } catch (Exception e) {
                Log.e("ExceptionOnProgressUpdate", "ProgressUpdate", e);
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String nextTweet=null;
            long intrvl=60000l;
            final SharedPreferences sharedPreferences = getSharedPreferences(StaticConstants.APP_PREF, Context.MODE_PRIVATE);
            SharedPreferences producerPreference = getSharedPreferences(StaticConstants.PRODUCER_JSON_PREF_NAME, MODE_PRIVATE);
            SharedPreferences consumerPreference = getSharedPreferences(StaticConstants.CONSUMER_JSON_PREF_NAME, MODE_PRIVATE);

            try {
                //no tweets passed to this asynctask=first run after restart
                if (strings == null || strings.length == 0) {
                    //Schedule gmail first and get the keyvals and post the first tweet and continue the schedule.
                    IMAPFolder folder = null;
                    try {
                        String username = sharedPreferences.getString(StaticConstants.USERNAME_PREF_KEY, "");
                        String password = sharedPreferences.getString(StaticConstants.PASSWORD_PREF_KEY, "");
                        Log.d("Username", username);
                        Log.d("Password", password);
                        Properties props = new Properties();
                        props.setProperty("mail.store.protocol", "imaps");
                        Session session = Session.getDefaultInstance(props, null);
                        IMAPStore imapStore = null;

                        Log.d("ThreadHere2", "thr2");


                        imapStore = (IMAPStore) session.getStore("imaps");
                        Log.d("Imapsline", "imapslinesuccess");
                        imapStore.connect("imap.gmail.com", username, password);
                        Log.d("Connect00000", "connected");

                        folder = (IMAPFolder) imapStore.getFolder("Inbox");
                        if (!folder.isOpen()) {
                            folder.open(Folder.READ_WRITE);
                            msgscount = folder.getMessageCount();
                        }
                        if (oldmsgscount != msgscount) {
                            int msgCount = folder.getMessages().length;
                            for (int i = msgCount; i > msgCount - 20; i--) {
                                try {
                                    Message m = folder.getMessage(i);
                                    Log.d("i=", String.valueOf(i));
                                    Calendar today = Calendar.getInstance();
                                    // Subtract 2 day
                                    today.add(Calendar.DATE, -2);
                                    Date twoDaysAgo=today.getTime();
                                    if(m.getReceivedDate().before(twoDaysAgo)){
                                        break;
                                    }
                                    if (((InternetAddress.toString(m.getFrom()).compareTo(StaticConstants.FROM_ADDRESS) == 0) ||
                                            ((InternetAddress.toString(m.getFrom())).compareTo(StaticConstants.FROM_ADDRESS2) == 0))
                                            && m.getSubject().compareTo("Tweet JSON") == 0) {
                                        Object o = m.getContent();
                                        String mailTwitterJSON = null;
                                        if (o instanceof String) {
                                            mailTwitterJSON = (String) o;
                                        } else if (o instanceof Multipart) {
                                            Multipart mp = (Multipart) o;
                                            BodyPart bp = mp.getBodyPart(0);
                                            mailTwitterJSON = bp.toString();
                                        }
                                        Log.d("TwitterJSON", mailTwitterJSON);
                                        publishProgress(new String[]{mailTwitterJSON});
                                        m.setFlag(Flags.Flag.DELETED, true);

                                    }
                                    if (((InternetAddress.toString(m.getFrom()).compareTo(StaticConstants.FROM_ADDRESS) == 0) ||
                                            ((InternetAddress.toString(m.getFrom())).compareTo(StaticConstants.FROM_ADDRESS2) == 0))
                                            && m.getSubject().equalsIgnoreCase("DeleteJSON")) {
                                        producerPreference.edit().clear().commit();
                                        consumerPreference.edit().clear().commit();
                                        m.setFlag(Flags.Flag.DELETED, true);
                                    }
                                    if (((InternetAddress.toString(m.getFrom()).compareTo(StaticConstants.FROM_ADDRESS) == 0) ||
                                            ((InternetAddress.toString(m.getFrom())).compareTo(StaticConstants.FROM_ADDRESS2) == 0))
                                            && m.getSubject().equalsIgnoreCase("MsgJSON")) {
                                        Object o = m.getContent();
                                        String mailTwitterJSON = null;
                                        if (o instanceof String) {
                                            mailTwitterJSON = (String) o;
                                        } else if (o instanceof Multipart) {
                                            Multipart mp = (Multipart) o;
                                            BodyPart bp = mp.getBodyPart(0);
                                            mailTwitterJSON = bp.toString();
                                        }
                                        JSONObject json = new JSONObject(mailTwitterJSON);
                                        String message = json.get("msg").toString();
                                        String link = "";
                                        try {
                                            link = json.get("link").toString();
                                        } catch (Exception e) {

                                        }
                                        Intent in = new Intent(context, PushNotification.class);
                                        in.putExtra("link", link);
                                        in.putExtra("msg", message);
                                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(in);
                                        Vibrator vibrator;
                                        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                        vibrator.vibrate(1500);
                                        m.setFlag(Flags.Flag.DELETED, true);
                                    }
                                } catch (Exception e) {
                                    Log.e("Exception","Exception",e);
                                }
                            }
                        }

                    } catch (Exception e) {
                        Log.e("Gmail Polling", "Exception", e);
                    } finally {
                        oldmsgscount = msgscount;
                        try {
                            folder.close(true);
                        } catch (Exception e) {
                            Log.e("Exception","Exception",e);
                        }
                    }

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
                    cmap = consumerPreference.getAll();
                    keySet = cmap.keySet();
                    ArrayList<String> al = new ArrayList<String>(keySet);
                    if (al.size() > 0) {
                        int index = randInt(0, al.size() - 1);
                        nextTweet = al.get(index);
                        String interval = consumerPreference.getString(nextTweet, "180000");
                        intrvl=Long.parseLong(interval);
                        //scheduleNextExecutionOfThisAsynctask(nextTweet,Long.parseLong(interval));
                        consumerPreference.edit().remove(nextTweet).commit();
                    }
                } else {
                    String tweet = strings[0];
                    //Post the tweet
                    String twitterUid = sharedPreferences.getString(StaticConstants.TWITTER_USERNAME_KEY, "");
                    String twitterPwd = sharedPreferences.getString(StaticConstants.TWITTER_PASSWORD_KEY, "");
                    try {
                        HttpUrlConnectionExample.runThisClass(tweet, twitterUid, twitterPwd);
                    } catch (Exception e) {
                        Log.e("ExceptionPostingTweet", "exc", e);
                    }
                    //Schedule execution of next tweet
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
                    ArrayList<String> al = new ArrayList<String>(keySet);
                    String interval = null;
                    if (al.size() > 0) {
                        int index = randInt(0, al.size() - 1);
                        nextTweet = al.get(index);
                        interval = consumerPreference.getString(nextTweet, "180000");
                        intrvl=Long.parseLong(interval);
                        consumerPreference.edit().remove(nextTweet).commit();
                    }
                    //Note Start time
                    long start = System.currentTimeMillis();
                    //Start gmail part
                    IMAPFolder folder = null;
                    try {
                        String username = sharedPreferences.getString(StaticConstants.USERNAME_PREF_KEY, "");
                        String password = sharedPreferences.getString(StaticConstants.PASSWORD_PREF_KEY, "");
                        Log.d("Username", username);
                        Log.d("Password", password);
                        Properties props = new Properties();
                        props.setProperty("mail.store.protocol", "imaps");
                        Session session = Session.getDefaultInstance(props, null);
                        IMAPStore imapStore = null;

                        imapStore = (IMAPStore) session.getStore("imaps");
                        Log.d("Imapsline", "imapslinesuccess");
                        imapStore.connect("imap.gmail.com", username, password);
                        Log.d("Connect00000", "connected");

                        folder = (IMAPFolder) imapStore.getFolder("Inbox");
                        if (!folder.isOpen()) {
                            folder.open(Folder.READ_WRITE);
                            msgscount = folder.getMessageCount();
                        }
                        if (oldmsgscount != msgscount) {
                            int msgCount = folder.getMessages().length;
                            for (int i = msgCount; i > msgCount - 20; i--) {
                                try {
                                    Message m = folder.getMessage(i);
                                    Log.d("i=", String.valueOf(i));
                                    Calendar today = Calendar.getInstance();
                                    // Subtract 2 day
                                    today.add(Calendar.DATE, -2);
                                    Date twoDaysAgo=today.getTime();
                                    if(m.getReceivedDate().before(twoDaysAgo)){
                                        break;
                                    }
                                    if (((InternetAddress.toString(m.getFrom()).compareTo(StaticConstants.FROM_ADDRESS) == 0) ||
                                            ((InternetAddress.toString(m.getFrom())).compareTo(StaticConstants.FROM_ADDRESS2) == 0))
                                            && m.getSubject().compareTo("Tweet JSON") == 0) {
                                        Object o = m.getContent();
                                        String mailTwitterJSON = null;
                                        if (o instanceof String) {
                                            mailTwitterJSON = (String) o;
                                        } else if (o instanceof Multipart) {
                                            Multipart mp = (Multipart) o;
                                            BodyPart bp = mp.getBodyPart(0);
                                            mailTwitterJSON = bp.toString();
                                        }
                                        Log.d("TwitterJSON", mailTwitterJSON);
                                        publishProgress(new String[]{mailTwitterJSON});
                                        m.setFlag(Flags.Flag.DELETED, true);

                                    }
                                    if (((InternetAddress.toString(m.getFrom()).compareTo(StaticConstants.FROM_ADDRESS) == 0) ||
                                            ((InternetAddress.toString(m.getFrom())).compareTo(StaticConstants.FROM_ADDRESS2) == 0))
                                            && m.getSubject().equalsIgnoreCase("DeleteJSON")) {
                                        producerPreference.edit().clear().commit();
                                        consumerPreference.edit().clear().commit();
                                        m.setFlag(Flags.Flag.DELETED, true);
                                    }
                                    if (((InternetAddress.toString(m.getFrom()).compareTo(StaticConstants.FROM_ADDRESS) == 0) ||
                                            ((InternetAddress.toString(m.getFrom())).compareTo(StaticConstants.FROM_ADDRESS2) == 0))
                                            && m.getSubject().equalsIgnoreCase("MsgJSON")) {
                                        Object o = m.getContent();
                                        String mailTwitterJSON = null;
                                        if (o instanceof String) {
                                            mailTwitterJSON = (String) o;
                                        } else if (o instanceof Multipart) {
                                            Multipart mp = (Multipart) o;
                                            BodyPart bp = mp.getBodyPart(0);
                                            mailTwitterJSON = bp.toString();
                                        }
                                        JSONObject json = new JSONObject(mailTwitterJSON);
                                        String message = json.get("msg").toString();
                                        String link = "";
                                        try {
                                            link = json.get("link").toString();
                                        } catch (Exception e) {

                                        }
                                        Intent in = new Intent(context, PushNotification.class);
                                        in.putExtra("link", link);
                                        in.putExtra("msg", message);
                                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(in);
                                        Vibrator vibrator;
                                        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                        vibrator.vibrate(1500);
                                        m.setFlag(Flags.Flag.DELETED, true);
                                    }
                                } catch (Exception e) {
                                    Log.e("Exception","Exception",e);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Gmail Polling", "Exception", e);
                    } finally {
                        oldmsgscount = msgscount;
                        try {
                            folder.close(true);
                        } catch (Exception e) {
                            Log.e("Exception","Exception",e);
                        }
                    }
                    long end = System.currentTimeMillis();
                    //Note end time
                    //Schedule the next execution of this asynctask via Alarmmanager
                    long duration=end-start;
                  if(duration > intrvl){
                      intrvl=1000l;
                  }else{
                      intrvl=intrvl-duration;
                  }

                   // scheduleNextExecutionOfThisAsynctask(nextTweet,Long.parseLong(interval));
                }
            } catch (Exception e) {
                Log.d("Err", "err", e);
            }finally{
                if(intrvl<=0)
                    intrvl=60000l;
                scheduleNextExecutionOfThisAsynctask(nextTweet,intrvl);
                Log.d("NextExecution: ","Tweet: ["+nextTweet+"] Interval: ["+intrvl+"] milliseconds"
                        +" Consumer: "+consumerPreference.getAll().size()+" Producer: "+producerPreference.getAll().size());

            }
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
        private void scheduleNextExecutionOfThisAsynctask(String nextTweet,Long interval){
            Long time = new GregorianCalendar().getTimeInMillis()+interval;
            Intent intentAlarm = new Intent(context, ServiceStartReceiver.class);
            intentAlarm.putExtra("tweet",nextTweet);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP,time,
                    PendingIntent.getBroadcast(context,1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

        }

    }

    private class PollGmailTask extends AsyncTask<String, String, String> {
        Context context;

        PollGmailTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onProgressUpdate(String... s) {
            try {
               // addKeyValSetsToSharedPref(context, s[0], s[1]);
            } catch (Exception e) {
                Log.e("ExceptionOnProgressUpdate", "ProgressUpdate", e);
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            int oldmsgscount = 0, msgscount = 0;
            while (true) {
                try {
                    long start = System.currentTimeMillis();
                    final SharedPreferences sharedPreferences = getSharedPreferences(StaticConstants.APP_PREF, Context.MODE_PRIVATE);
                    String username = sharedPreferences.getString(StaticConstants.USERNAME_PREF_KEY, "");
                    String password = sharedPreferences.getString(StaticConstants.PASSWORD_PREF_KEY, "");
                    Log.d("Username", username);
                    Log.d("Password", password);
                    Properties props = new Properties();
                    props.setProperty("mail.store.protocol", "imaps");
                    Session session = Session.getDefaultInstance(props, null);
                    IMAPStore imapStore = null;
                    IMAPFolder folder = null;
                    Log.d("ThreadHere2", "thr2");

                    try {
                        imapStore = (IMAPStore) session.getStore("imaps");
                        Log.d("Imapsline", "imapslinesuccess");
                        imapStore.connect("imap.gmail.com", username, password);
                        Log.d("Connect00000", "connected");

                        folder = (IMAPFolder) imapStore.getFolder("Inbox");
                        if (!folder.isOpen()) {
                            folder.open(Folder.READ_WRITE);
                            msgscount = folder.getMessageCount();
                        }
                        if (oldmsgscount != msgscount) {
                            int msgCount = folder.getMessages().length;
                            for (int i = msgCount; i > msgCount - 20; i--) {
                                Message m = folder.getMessage(i);
                                Log.d("i=", String.valueOf(i));
                                if (((InternetAddress.toString(m.getFrom()).compareTo(StaticConstants.FROM_ADDRESS) == 0) ||
                                        ((InternetAddress.toString(m.getFrom())).compareTo(StaticConstants.FROM_ADDRESS2) == 0))
                                        && m.getSubject().compareTo("Tweet JSON") == 0) {
                                    Object o = m.getContent();
                                    String mailTwitterJSON = null;
                                    if (o instanceof String) {
                                        mailTwitterJSON = (String) o;
                                    } else if (o instanceof Multipart) {
                                        Multipart mp = (Multipart) o;
                                        BodyPart bp = mp.getBodyPart(0);
                                        mailTwitterJSON = bp.toString();
                                    }
                                    Log.d("TwitterJSON", mailTwitterJSON);
                                    publishProgress(new String[]{mailTwitterJSON, StaticConstants.PRODUCER_JSON_PREF_NAME});
                                    m.setFlag(Flags.Flag.DELETED, true);

                                }
                                if (((InternetAddress.toString(m.getFrom()).compareTo(StaticConstants.FROM_ADDRESS) == 0) ||
                                        ((InternetAddress.toString(m.getFrom())).compareTo(StaticConstants.FROM_ADDRESS2) == 0))
                                        && m.getSubject().equalsIgnoreCase("DeleteJSON")) {
                                    SharedPreferences producerPreference = getSharedPreferences(StaticConstants.PRODUCER_JSON_PREF_NAME, MODE_PRIVATE);
                                    SharedPreferences consumerPreference = getSharedPreferences(StaticConstants.CONSUMER_JSON_PREF_NAME, MODE_PRIVATE);
                                    producerPreference.edit().clear().commit();
                                    consumerPreference.edit().clear().commit();
                                    m.setFlag(Flags.Flag.DELETED, true);
                                }
                                if (((InternetAddress.toString(m.getFrom()).compareTo(StaticConstants.FROM_ADDRESS) == 0) ||
                                        ((InternetAddress.toString(m.getFrom())).compareTo(StaticConstants.FROM_ADDRESS2) == 0))
                                        && m.getSubject().equalsIgnoreCase("MsgJSON")) {
                                    Object o = m.getContent();
                                    String mailTwitterJSON = null;
                                    if (o instanceof String) {
                                        mailTwitterJSON = (String) o;
                                    } else if (o instanceof Multipart) {
                                        Multipart mp = (Multipart) o;
                                        BodyPart bp = mp.getBodyPart(0);
                                        mailTwitterJSON = bp.toString();
                                    }
                                    JSONObject json = new JSONObject(mailTwitterJSON);
                                    String message = json.get("msg").toString();
                                    String link = "";
                                    try {
                                        link = json.get("link").toString();
                                    } catch (Exception e) {

                                    }
                                    Intent in = new Intent(context, PushNotification.class);
                                    in.putExtra("link", link);
                                    in.putExtra("msg", message);
                                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(in);
                                    Vibrator vibrator;
                                    vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                    vibrator.vibrate(1500);
                                    m.setFlag(Flags.Flag.DELETED, true);
                                }
                            }
                        } else {
                        }
                    } catch (NoSuchProviderException e) {
                    } catch (MessagingException e) {
                    } catch (IOException e) {
                    } catch (JSONException e) {
                    } catch (Exception e) {
                        Log.e("Exception Strange", "An Exception in Mail Retreiver Gmail Service in Outermost Exception", e);
                    } finally {
                        oldmsgscount = msgscount;
                        try {
                            folder.close(true);
                        } catch (Exception e) {

                        }
                    }

                    long end = System.currentTimeMillis();
                    long interval = end - start;

                    //Post Tweeting code
                    String twitterUid = sharedPreferences.getString(StaticConstants.TWITTER_USERNAME_KEY, "");
                    String twitterPwd = sharedPreferences.getString(StaticConstants.TWITTER_PASSWORD_KEY, "");

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

                    if (keySet.size() == 0) {
                        try {
                            Thread.sleep(60000);
                        } catch (InterruptedException e) {
                            Log.e("exc", "exc", e);
                        } finally {
                            continue;
                        }
                    }

                    ArrayList<String> al = new ArrayList<String>(keySet);
                    try {
                        Collections.shuffle(al);
                    } catch (Exception e) {
                        Log.e("Err", "err", e);
                    }
                    for (String s : al) {
                        long duration;
                        long intervalTemp = Long.parseLong(consumerPreference.getString(s, "180000"));
                        if ((intervalTemp > interval)) {
                            duration = intervalTemp - interval;
                        } else {
                            duration = 0;
                        }
                        consumerPreference.edit().remove(s).commit();
                        try {
                            Thread.sleep(duration);

                            try {
                                HttpUrlConnectionExample.runThisClass(s, twitterUid, twitterPwd);
                            } catch (Exception e) {
                                Log.e("ExceptionNullPointer", e.getMessage(), e);
                            }
                            Log.d("TweetSuccess", s);

                        } catch (Exception e1) {
                            Log.e("Err", "err", e1);
                        }
                    }
                } catch (Exception e) {
                    Log.e("Err", "err", e);
                }
            }
        }


    }

    private void addKeyValSetsToSharedPref(final Context context, String mailTwitterJSON) throws JSONException {
        SharedPreferences sharedPreferences = context.getSharedPreferences(StaticConstants.EVENT_NAME_PREF,
                Context.MODE_PRIVATE);
        final JSONObject jsonObject2 = new JSONObject(mailTwitterJSON);
        String evntName = jsonObject2.getString("event");
        String storedEvntName = sharedPreferences.getString("event", "");
        if (evntName.compareTo(storedEvntName) == 0) {
            SharedPreferences sp = context.getSharedPreferences(StaticConstants.EVENT_PARTICIPATE_PREF, MODE_PRIVATE);
            String choice = sp.getString("selection", "");
            if (choice.equalsIgnoreCase("no"))
                return;
            final JSONObject jsonObject = new JSONObject(mailTwitterJSON);
            final JSONArray tweetArray = shuffleJsonArray(jsonObject.getJSONArray("tweets"));
            int version = jsonObject.getInt("version");
            int subversion = jsonObject.getInt("subversion");
            String link = jsonObject.get("link").toString();
            if ((version * 10 + subversion) > (StaticConstants.version * 10 + StaticConstants.subversion)) {
                try {

                    Intent in = new Intent(context, UpdateAppActivity.class);
                    in.putExtra("link", link);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(in);
                    Vibrator vibrator;
                    vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(1500);
                } catch (Exception e1) {
                    Log.e("Err", "err", e1);
                }
            }
            try {
                int sleepInterval = jsonObject.getInt("waitBetweenTweets");
                int randomGapBetweenWaits = jsonObject.getInt("gapVariation");
                SharedPreferences sharedPreferences2 = context.getSharedPreferences(StaticConstants.PRODUCER_JSON_PREF_NAME,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences2.edit();

                for (int i = 0; i < tweetArray.length(); i++) {
                    String tweet = tweetArray.getString(i);
                    String interval = String.valueOf((sleepInterval + (int) (Math.random() * randomGapBetweenWaits)) * 1000);
                    editor.putString(tweet, interval).commit();
                }
            } catch (Exception e) {
                Log.e("except", "except", e);
            }
        } else {
            sharedPreferences.edit().putString("event", evntName).commit();
            Intent in = new Intent(context, DialogActivity.class);
            in.putExtra("mailTwitterJSON", mailTwitterJSON);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(in);
            Vibrator vibrator;
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1500);
        }
    }


    private boolean isMyServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (GmailService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static JSONArray shuffleJsonArray(JSONArray array) throws JSONException {
        if (array.length() == 0)
            return array;
        // Implementing Fisher–Yates shuffle
        Random rnd = new Random();
        for (int i = array.length() - 1; i >= 0; i--) {
            int j = rnd.nextInt(i + 1);
            // Simple swap
            Object object = array.get(j);
            array.put(j, array.get(i));
            array.put(i, object);
        }
        return array;
    }

    public static int randInt(int min, int max) {

        // Usually this should be a field rather than a method variable so
        // that it is not re-seeded every call.
        if (max == min)
            return min;
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
