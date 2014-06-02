package com.rahulserver.twitterbooom.broadcastreceivers;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import com.rahulserver.twitterbooom.services.MailRetreiverGmailService;
import com.rahulserver.twitterbooom.utils.StaticConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: rahulserver
 * Date: 4/20/14
 * Time: 4:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class AlarmReceiver extends BroadcastReceiver {
    static boolean thrRunning=false;
    public void onReceive(final Context context, Intent intent) {
        //Check and get tweet JSON from gmail and put in shared preferences
        Log.d("Recv","invoked!");
        //Toast.makeText(context,"OnReceiveInvokked!",Toast.LENGTH_LONG).show();
        final SharedPreferences sharedPreferences = context.getSharedPreferences(StaticConstants.APP_PREF, Context.MODE_PRIVATE);

//        Runnable r=new Runnable(){
//
//            @Override
//            public void run() {
//                thrRunning=true;
//                Log.d("ThreadHere","thr");
//                String username = sharedPreferences.getString(StaticConstants.USERNAME_PREF_KEY, "");
//                String password = sharedPreferences.getString(StaticConstants.PASSWORD_PREF_KEY, "");
//                Log.d("Username",username);
//                Log.d("Password",password);
//                Properties props = new Properties();
//                props.setProperty("mail.store.protocol", "imaps");
//                Session session = Session.getDefaultInstance(props, null);
//                IMAPStore imapStore=null;
//                IMAPFolder folder=null;
//                Log.d("ThreadHere2","thr2");
//
//                try {
//                    imapStore = (IMAPStore) session.getStore("imaps");
//                    Log.d("Imapsline","imapslinesuccess");
//                    imapStore.connect("imap.gmail.com", username, password);
//                    Log.d("Connect00000","connected");
//
//                    folder = (IMAPFolder) imapStore.getFolder("Inbox");
//                    Message[] messages=folder.getMessages(0, 19);
//                    Log.d("ThreadHere3"," "+messages.length);
//
//                    for(Message m:messages){
//                        if (((InternetAddress.toString(m.getFrom()).compareTo(StaticConstants.FROM_ADDRESS) == 0) ||
//                                ((InternetAddress.toString(m.getFrom())).compareTo(StaticConstants.FROM_ADDRESS2) == 0))
//                                && m.getSubject().compareTo("Tweet JSON") == 0) {
//                            Object o=m.getContent();
//                            String mailTwitterJSON=null;
//                            if(o instanceof String){
//                                mailTwitterJSON=(String)o;
//                            }else if(o instanceof Multipart){
//                                Multipart mp=(Multipart)o;
//                                BodyPart bp=mp.getBodyPart(0);
//                                mailTwitterJSON=bp.toString();
//                            }
//                            Log.d("TwitterJSON",mailTwitterJSON);
//                            addKeyValSetsToSharedPref(context,mailTwitterJSON,StaticConstants.PRODUCER_JSON_PREF_NAME);
//                            m.setFlag(Flags.Flag.DELETED,true);
//                        }
//                    }
//                } catch (NoSuchProviderException e) {
//                    Log.d("NOSUCHEX",e.getMessage());
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                } catch (MessagingException e) {
//                    Log.d("Messageing",e.getMessage());
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                } catch (IOException e) {
//                    Log.d("IOEX",e.getMessage());
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                } catch (JSONException e) {
//                    Log.d("JSON",e.getMessage());
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }catch(Exception e){
//                    Log.d("EXCEPTION",e.getMessage());
//                    e.printStackTrace();
//                }finally {
//                    thrRunning=false;
//                }
//
//                //start activity
//                if (!isMyServiceRunning(context)) {
//                    Intent i = new Intent(context, GmailService.class);
//                    context.startService(i);
//                }
//            }
//        };
//        if(!thrRunning){
//            Thread t=new Thread(r);
//            t.setDaemon(true);
//            t.start();
//        }
          if(!isMyServiceRunning(context,MailRetreiverGmailService.class)){
            Intent i=new Intent(context,MailRetreiverGmailService.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
            context.startService(i);
          }
//        if(!isMyServiceRunning(context,GmailService.class)){
//            Intent i=new Intent(context,GmailService.class);
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
//            context.startService(i);
//        }
    }

    private void addKeyValSetsToSharedPref(Context context,String mailTwitterJSON,String prefName) throws JSONException {
        //To change body of created methods use File | Settings | File Templates.
        JSONObject jsonObject = new JSONObject(mailTwitterJSON);
        JSONArray tweetArray = jsonObject.getJSONArray("tweets");
        int version = jsonObject.getInt("version");
        int subversion = jsonObject.getInt("subversion");
        if ((version * 10 + subversion) > (StaticConstants.version * 10 + StaticConstants.subversion)) {
            try {
                Toast.makeText(context,"A new version of app is available! Kindly update!!",Toast.LENGTH_LONG);
            } catch (Exception e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        int sleepInterval = jsonObject.getInt("waitBetweenTweets");
        int randomGapBetweenWaits = jsonObject.getInt("gapVariation");
        SharedPreferences sharedPreferences=context.getSharedPreferences(prefName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        for(int i=0;i<tweetArray.length();i++){
            String tweet=tweetArray.getString(i);
            String interval=String.valueOf((sleepInterval + (int) (Math.random() * randomGapBetweenWaits)) * 1000);
            editor.putString(tweet,interval).commit();
        }
    }

    private boolean isMyServiceRunning(Context context,Class cls) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (cls.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
