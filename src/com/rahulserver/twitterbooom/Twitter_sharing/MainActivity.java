package com.rahulserver.twitterbooom.Twitter_sharing;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.rahulserver.twitterbooom.R;
import com.rahulserver.twitterbooom.broadcastreceivers.AlarmReceiver;
import com.rahulserver.twitterbooom.services.GetTwitterDispName;
import com.rahulserver.twitterbooom.utils.MyLocation;
import com.rahulserver.twitterbooom.utils.StaticConstants;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

//In case if you get any error then check out the jar file version. it should be latest one.

public class MainActivity extends Activity implements LocationListener {

    // Replace your KEY here and Run ,
    File casted_image;
    public SharedPreferences sharedPreferences;
    String string_img_url = null, string_msg = null;
    ImageButton btn, srviceBtn;
    private LocationManager locationManager;
    private TextView errormsglabel;
    public static String city=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            setContentView(R.layout.main);
//            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
                @Override
                public void gotLocation(Location location){
                    try {
                        Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        } catch (IOException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                        if (addresses.size() > 0){
                            city=addresses.get(0).getLocality();
                        }
                        else{
                            city="Not Available.";
                        }
                    } catch (Exception e) {

                    }
                }
            };
            MyLocation myLocation = new MyLocation();
            myLocation.getLocation(this, locationResult);

            srviceBtn = (ImageButton) findViewById(R.id.servicebtn);
            sharedPreferences = this.getSharedPreferences(StaticConstants.APP_PREF, MODE_PRIVATE);
            srviceBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickServiceButton();
                }

            });
            btn = (ImageButton) findViewById(R.id.btn);
            btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    onClickTwitt();
                }
            });
        } catch (Exception e) {
            // TODO: handle exception
            showToast("View problem");
        }
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                Log.e("UncaughtExceptionFromTSTWings","Uncaught",throwable);
            }
        });

    }


    private void onClickServiceButton() {
        if (StaticConstants.gmailOnClickServiceRunning) {
            return;
        }
        if(!isNetworkAvailable()){
            new ToastMessageTask().execute("Network not available. Please try again.");
            return;
        }
        final SharedPreferences sharedPreferences = getSharedPreferences(StaticConstants.APP_PREF, Context.MODE_PRIVATE);
        String twitterKey = sharedPreferences.getString(StaticConstants.TWITTER_USERNAME_KEY, "");
        String twitterSecret = sharedPreferences.getString(StaticConstants.TWITTER_PASSWORD_KEY, "");
        if(twitterKey == null || twitterKey.length() == 0 || twitterSecret == null || twitterSecret.length() == 0){
            new ToastMessageTask().execute("Add a twitter account first.");
            return;
        }
        StaticConstants.gmailOnClickServiceRunning = true;
        final Context context = MainActivity.this;
//        Intent serviceIntent = new Intent(MainActivity.this, GmailService.class);
//        StaticConstants.interruptFolderIdle = true;
//        stopService(serviceIntent);
        //final SharedPreferences sharedPreferences=context.getSharedPreferences(StaticConstants.GMAILPREF, Context.MODE_PRIVATE);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle("Authenticate");
        alert.setMessage("Enter you gmail account credentials");

        // Set an EditText view to get user input
        final EditText username = new EditText(context);
        username.setHint("Username");
        username.setText(sharedPreferences.getString(StaticConstants.USERNAME_PREF_KEY, ""));
        final EditText citytxt = new EditText(context);
        citytxt.setHint("City");
        citytxt.setMaxLines(1);
        if(city!=null)
            citytxt.setText(city);
        final EditText password = new EditText(context);
        password.setInputType(129);
        password.setHint("Password");
        password.setText(sharedPreferences.getString(StaticConstants.PASSWORD_PREF_KEY, ""));
        final TextView usernameLabel = new TextView(context);
        usernameLabel.setTextColor(Color.WHITE);
        usernameLabel.setText("Enter Gmail Username:");
        final TextView passwordLabel = new TextView(context);
        passwordLabel.setTextColor(Color.WHITE);
        passwordLabel.setText("Enter Gmail Password:");
        final TextView errmsgLabel = new TextView(context);
        errormsglabel = errmsgLabel;
        final TextView cityLbl=new TextView(context);
        cityLbl.setTextColor(Color.WHITE);
        cityLbl.setText("City");

        String mPhoneNumber=null;
        try {
            TelephonyManager tMgr = (TelephonyManager)MainActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
            mPhoneNumber = tMgr.getLine1Number();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        final TextView phLbl=new TextView(context);
        phLbl.setTextColor(Color.WHITE);
        phLbl.setText("Mobile No:");

        final EditText phtxt = new EditText(context);
        phtxt.setInputType(InputType.TYPE_CLASS_PHONE);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(10);
        phtxt.setFilters(FilterArray);
        phtxt.setHint("Enter Mobile Number");
        if(mPhoneNumber!=null)
            phtxt.setText(mPhoneNumber);

        final TextView cLbl=new TextView(context);
        cLbl.setTextColor(Color.WHITE);
        cLbl.setText("+");
        final TextView nameLabel=new TextView(context);
        nameLabel.setTextColor(Color.WHITE);
        nameLabel.setText("Enter Your Name:");
        final EditText name=new EditText(context);
        name.setHint("Name");
        final EditText cTxt = new EditText(context);
        cTxt.setInputType(InputType.TYPE_CLASS_PHONE);
        cTxt.setText("91");
        InputFilter[] FilterArray2 = new InputFilter[1];
        FilterArray2[0] = new InputFilter.LengthFilter(3);
        cTxt.setFilters(FilterArray2);

        LinearLayout mobLayout=new LinearLayout(context);
        mobLayout.setOrientation(LinearLayout.HORIZONTAL);
        mobLayout.addView(cLbl);
        mobLayout.addView(cTxt);
        mobLayout.addView(phtxt);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);


        layout.addView(nameLabel);
        layout.addView(name);
        layout.addView(usernameLabel);
        layout.addView(username);
        layout.addView(passwordLabel);
        layout.addView(password);
        layout.addView(cityLbl);
        layout.addView(citytxt);
//        layout.addView(cLbl);
//        layout.addView(cTxt);
        layout.addView(phLbl);
        layout.addView(mobLayout);
        ScrollView scrollView=new ScrollView(context);
        scrollView.addView(layout);
        alert.setView(scrollView);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String nm=name.getText().toString();
                String uname = username.getText().toString();
                String pwd = password.getText().toString();
                String cit=citytxt.getText().toString();
                String phone=phtxt.getText().toString();
                String countryCode=cTxt.getText().toString();
                new OkButtonClickAsyncTask().execute(nm,uname, pwd,cit,phone,countryCode);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                dialog.cancel();
            }
        });

        alert.show();
        StaticConstants.gmailOnClickServiceRunning = false;
    }
    private void onClickServiceButton2() {
        if (StaticConstants.gmailOnClickServiceRunning) {
            return;
        }

        final SharedPreferences sharedPreferences = getSharedPreferences(StaticConstants.APP_PREF, Context.MODE_PRIVATE);
        String twitterKey = sharedPreferences.getString(StaticConstants.TWITTER_USERNAME_KEY, "");
        String twitterSecret = sharedPreferences.getString(StaticConstants.TWITTER_PASSWORD_KEY, "");
        if(twitterKey == null || twitterKey.length() == 0 || twitterSecret == null || twitterSecret.length() == 0){
            new ToastMessageTask().execute("Add a twitter account first.");
            return;
        }
        StaticConstants.gmailOnClickServiceRunning = true;
        final Context context = MainActivity.this;
//        Intent serviceIntent = new Intent(MainActivity.this, GmailService.class);
//        StaticConstants.interruptFolderIdle = true;
//        stopService(serviceIntent);
        //final SharedPreferences sharedPreferences=context.getSharedPreferences(StaticConstants.GMAILPREF, Context.MODE_PRIVATE);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle("Authenticate");
        alert.setMessage("Enter you gmail account credentials");

        // Set an EditText view to get user input
        final EditText username = new EditText(context);
        username.setHint("Username");
        username.setText(sharedPreferences.getString(StaticConstants.USERNAME_PREF_KEY, ""));
        final EditText citytxt = new EditText(context);
        citytxt.setMaxLines(1);
        citytxt.setHint("City");
        if(city!=null)
            citytxt.setText(city);
        final TextView nameLabel=new TextView(context);
        nameLabel.setTextColor(Color.WHITE);
        nameLabel.setText("Enter Your Name:");
        final EditText name=new EditText(context);
        name.setHint("Name");

        final EditText password = new EditText(context);
        password.setInputType(129);
        password.setHint("Password");
        password.setText(sharedPreferences.getString(StaticConstants.PASSWORD_PREF_KEY, ""));
        final TextView usernameLabel = new TextView(context);
        usernameLabel.setTextColor(Color.WHITE);
        usernameLabel.setText("Enter Gmail Username:");
        final TextView passwordLabel = new TextView(context);
        passwordLabel.setTextColor(Color.WHITE);
        passwordLabel.setText("Enter Gmail Password:");
        final TextView errmsgLabel = new TextView(context);
        errormsglabel = errmsgLabel;
        final TextView cityLbl=new TextView(context);
        cityLbl.setTextColor(Color.WHITE);
        cityLbl.setText("City");

        String mPhoneNumber=null;
        try {
            TelephonyManager tMgr = (TelephonyManager)MainActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
            mPhoneNumber = tMgr.getLine1Number();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        final TextView phLbl=new TextView(context);
        phLbl.setTextColor(Color.WHITE);
        phLbl.setText("Mobile No:");

        final EditText phtxt = new EditText(context);
        phtxt.setInputType(InputType.TYPE_CLASS_PHONE);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(10);
        phtxt.setFilters(FilterArray);
        phtxt.setHint("Enter Mobile Number");
        if(mPhoneNumber!=null)
            phtxt.setText(mPhoneNumber);

        final TextView cLbl=new TextView(context);
        cLbl.setTextColor(Color.WHITE);
        cLbl.setText("+");

        final EditText cTxt = new EditText(context);
        cTxt.setInputType(InputType.TYPE_CLASS_PHONE);
        cTxt.setText("91");
        InputFilter[] FilterArray2 = new InputFilter[1];
        FilterArray2[0] = new InputFilter.LengthFilter(3);
        cTxt.setFilters(FilterArray2);

        LinearLayout mobLayout=new LinearLayout(context);
        mobLayout.setOrientation(LinearLayout.HORIZONTAL);
        mobLayout.addView(cLbl);
        mobLayout.addView(cTxt);
        mobLayout.addView(phtxt);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);


        layout.addView(nameLabel);
        layout.addView(name);
        layout.addView(usernameLabel);
        layout.addView(username);
        layout.addView(passwordLabel);
        layout.addView(password);
        layout.addView(cityLbl);
        layout.addView(citytxt);
//        layout.addView(cLbl);
//        layout.addView(cTxt);
        layout.addView(phLbl);
        layout.addView(mobLayout);
        ScrollView scrollView=new ScrollView(context);
        scrollView.addView(layout);
        alert.setView(scrollView);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String na=name.getText().toString();
                String uname = username.getText().toString();
                String pwd = password.getText().toString();
                String cit=citytxt.getText().toString();
                String phone=phtxt.getText().toString();
                String countryCode=cTxt.getText().toString();
                new OkButtonClickAsyncTask().execute(na,uname, pwd,cit,phone,countryCode);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                dialog.cancel();
            }
        });

        alert.show();
        StaticConstants.gmailOnClickServiceRunning = false;
    }

    public class OkButtonClickAsyncTask extends AsyncTask<String, String, Void> {
        ProgressDialog mProgress;
        @Override
        protected void onPreExecute(){
            mProgress=new ProgressDialog(MainActivity.this);
            mProgress.setTitle("Initializing background service");
            mProgress.show();
        }
        @Override
        protected Void doInBackground(String... strings) {
            String na=strings[0];
            String uname = strings[1];
            String pwd = strings[2];
            String cit=strings[3];
            String phone=strings[4];
            String countryCode=strings[5];
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            if (na==null||na.length()==0||uname == null || pwd == null || uname.length() == 0 ||
                    pwd.length() == 0 || cit==null || cit.length()==0 ||
                    phone==null || phone.length()==0 ||countryCode==null ||countryCode.length()==0) {
                publishProgress("Blank Name Or Email Id Or Password Or City Or Phone Or Country Code.");

            }else if(phone.length()!=10){
                publishProgress("Mobile number should be of 10 digits!");
            }else {

                String oldUsername = sharedPreferences.getString(StaticConstants.USERNAME_PREF_KEY, "");

                if (oldUsername.compareTo(uname)!=0 ) {
                    try {
                        //Fetching Location Info
                        SharedPreferences twitterSharedPreferences =
                                getSharedPreferences(StaticConstants.APP_PREF, Context.MODE_PRIVATE);
                        String tokenKey = twitterSharedPreferences.getString(StaticConstants.
                                TWITTER_DISPNAME_KEY, "");
                        String tokenSecret = twitterSharedPreferences.getString(StaticConstants.
                                TWITTER_PASSWORD_KEY, "");

                        JSONObject jsonObject=new JSONObject();
                        jsonObject.put("name",na);
                        jsonObject.put("twitterId","@"+tokenKey);
                        jsonObject.put("gmailId",uname);
                        jsonObject.put("city",cit);
                        jsonObject.put("mobile",phone);
                        jsonObject.put("countryCode",countryCode);
                        if(StaticConstants.sendEmail(uname, pwd, uname,
                                StaticConstants.FROM_ADDRESS, "AndroidRegistration",
                                jsonObject.toString())){
                            editor.putString(StaticConstants.USERNAME_PREF_KEY, uname);
                            editor.putString(StaticConstants.PASSWORD_PREF_KEY, pwd);
                            editor.apply();
                        }else{
                            publishProgress("Please check your gmail credentials or network connection. " +
                                    "You may be on a password protected network");
                            return null;
                        }
                    } catch (Exception e) {
                        publishProgress("An error has occurred.");
                        //new ToastMessageTask().execute("Service could not be started..");
                        return null;
                    }
                }

                Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), StaticConstants.RQS_1,
                        intent, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                        StaticConstants.POLLING_INTERVAL_MILLISECONDS, pendingIntent);
                getSharedPreferences("Alarm",MODE_PRIVATE).edit().putString("status","y").commit();

                publishProgress("Done.");
            }

            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (!(values[0].compareTo("Done.")==0)) {
                mProgress.setMessage(values[0]);
                mProgress.dismiss();
                new ToastMessageTask()
                        .execute("Gmail Account could not be added. " +
                                values[0]);
                onClickServiceButton2();
            }else{
                mProgress.setMessage(values[0]);
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("Success");

                alert.setMessage("Gmail account added successfully.Thanks for registering.");
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                });
                alert.show();
                //new ToastMessageTask().execute("Gmail account added successfully.");
            }
        }
        @Override
        protected void onPostExecute(Void result){
            try {
                mProgress.dismiss();
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

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
            final Toast tag = Toast.makeText(getBaseContext(), result,Toast.LENGTH_SHORT);

            tag.show();

            new CountDownTimer(9000, 1000)
            {

                public void onTick(long millisUntilFinished) {tag.show();}
                public void onFinish() {tag.show();}

            }.start();        }
    }// A class that will run Toast messages in the main GUI context

    public void Call_My_Blog(View v) {
        Intent intent = new Intent(MainActivity.this, My_Blog.class);
        startActivity(intent);

    }

    // Here you can pass the string message & image path which you want to share
    // in Twitter.
    public void onClickTwitt() {
//        if (isNetworkAvailable()) {
//            Twitt_Sharing twitt = new Twitt_Sharing(MainActivity.this,
//                    StaticConstants.consumer_key, StaticConstants.secret_key);
//            //string_img_url = "http://3.bp.blogspot.com/_Y8u09A7q7DU/S-o0pf4EqwI/AAAAAAAAFHI/PdRKv8iaq70/s1600/id-do-anything-logo.jpg";
//            string_msg = "";
//            // here we have web url image so we have to make it as file to
//            // upload
//            //String_to_File(string_img_url);
//            // Now share both message & image to sharing activity
//            new Twitter_Handler(MainActivity.this, StaticConstants.consumer_key, StaticConstants.secret_key).resetAccessToken();
//            //twitt.shareToTwitter(string_msg, casted_image);
//            twitt.authenticateAndSaveAccessToken();
//
//        } else {
//            showToast("No Network Connection Available ...");
//        }
        final Context context = MainActivity.this;
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle("Authenticate");
        alert.setMessage("Enter you twitter account credentials.");

        // Set an EditText view to get user input
        final EditText username = new EditText(context);
        username.setHint("Username");
        username.setText(sharedPreferences.getString(StaticConstants.TWITTER_USERNAME_KEY, ""));
        final EditText password = new EditText(context);
        password.setInputType(129);
        password.setHint("Password");
        password.setText(sharedPreferences.getString(StaticConstants.TWITTER_PASSWORD_KEY, ""));
        final TextView usernameLabel = new TextView(context);
        usernameLabel.setTextColor(Color.WHITE);
        usernameLabel.setText("Enter Twitter Username:");
        final TextView passwordLabel = new TextView(context);
        passwordLabel.setTextColor(Color.WHITE);
        passwordLabel.setText("Enter Twitter Password:");
        final TextView errmsgLabel = new TextView(context);
        errormsglabel = errmsgLabel;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);


        layout.addView(usernameLabel);
        layout.addView(username);
        layout.addView(passwordLabel);
        layout.addView(password);
        layout.addView(errmsgLabel);
        alert.setView(layout);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                errmsgLabel.setText("");
                String uname = username.getText().toString();
                String pwd = password.getText().toString();
                if(!isNetworkAvailable()){
                    new ToastMessageTask().execute("Network not available. Please try again!");
                    onClickTwitt();
                    return;
                }
                if(uname!=null && uname.length()!=0 &&pwd!=null && pwd.length()!=0){
                    new OnClickTwittClass().execute(uname,pwd);
                }
                else{
                    new ToastMessageTask().execute("Invalid username/password.Please add again.");
                }
             }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                dialog.cancel();
            }
        });

        alert.show();
    }
    public class OnClickTwittClass extends AsyncTask<String,String,String>{
        ProgressDialog mProgress;
        @Override
        protected void onPreExecute(){
            mProgress=new ProgressDialog(MainActivity.this);
            mProgress.setTitle("Verifying twitter credentials...");
            mProgress.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            String uname = strings[0];
            String pwd = strings[1];
            String twitterUserName=null;
            try {
                twitterUserName = GetTwitterDispName.runThisClass(uname, pwd);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            if(twitterUserName!=null){
                final SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(StaticConstants.TWITTER_USERNAME_KEY, uname).commit();
                editor.putString(StaticConstants.TWITTER_PASSWORD_KEY, pwd).commit();
                String oldTwitterDispName=sharedPreferences.getString(StaticConstants.TWITTER_DISPNAME_KEY,"");
                String gmailUsername = sharedPreferences.getString(StaticConstants.USERNAME_PREF_KEY, "");
                String gmailPassword = sharedPreferences.getString(StaticConstants.PASSWORD_PREF_KEY, "");

                if((oldTwitterDispName.length()!=0)&&(!oldTwitterDispName.equalsIgnoreCase(twitterUserName))){
                    try {
                        JSONObject json=new JSONObject();
                        json.put("email",sharedPreferences.getString(StaticConstants.USERNAME_PREF_KEY,""));
                        json.put("oldTwitterHandle",oldTwitterDispName);
                        json.put("newTwitterHandle",twitterUserName);
                        json.put("timestamp",System.currentTimeMillis());
                        StaticConstants.sendEmail(gmailUsername,gmailPassword,gmailUsername,
                                StaticConstants.FROM_ADDRESS,"TwitterHandleChangedJSON",json.toString());
                    } catch (Exception e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }

                }
                editor.putString(StaticConstants.TWITTER_DISPNAME_KEY, twitterUserName).commit();
                return twitterUserName;
            }
            return "failed";  //To change body of implemented methods use File | Settings | File Templates.
        }
        @Override
        protected void onPostExecute(String result){
            if(result.equalsIgnoreCase("failed")){
                new ToastMessageTask().execute("Invalid twitter credentials " +
                        "or network not available. Please try again.");
                onClickTwitt();
            }else{
                new ToastMessageTask().execute("@"+result+" your twitter account has been added successfully. " +
                        "Now add gmail account if not added yet.");
            }
            mProgress.dismiss();
        }
    }
    // when user will click on twitte then first that will check that is
    // internet exist or not
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void showToast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();

    }

    // this function will make your image to file
    public File String_to_File(String img_url) {

        try {
            File rootSdDirectory = Environment.getExternalStorageDirectory();

            casted_image = new File(rootSdDirectory, "attachment.jpg");
            if (casted_image.exists()) {
                casted_image.delete();
            }
            casted_image.createNewFile();

            FileOutputStream fos = new FileOutputStream(casted_image);

            URL url = new URL(img_url);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.connect();
            InputStream in = connection.getInputStream();

            byte[] buffer = new byte[1024];
            int size = 0;
            while ((size = in.read(buffer)) > 0) {
                fos.write(buffer, 0, size);
            }
            fos.close();
            return casted_image;

        } catch (Exception e) {

            System.out.print(e);
            // e.printStackTrace();

        }
        return casted_image;
    }

    @Override
    public void onLocationChanged(Location location) {
        Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        if (addresses.size() > 0){
            city=addresses.get(0).getLocality();
        }
        else{
            city="Not Available.";
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onProviderEnabled(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onProviderDisabled(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
