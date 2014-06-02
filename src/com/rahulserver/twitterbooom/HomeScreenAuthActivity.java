package com.rahulserver.twitterbooom;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import com.rahulserver.twitterbooom.Twitter_code.Twitt_Sharing;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: rahulserver
 * Date: 1/19/14
 * Time: 9:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class HomeScreenAuthActivity extends Activity {
    private Button addAccountBtn;
    private ListView accountsListView;
    private Button gotoTweetPageButton;

    //
    public final String consumer_key = "2qW394Mjj3SUHw4hxLOErg";
    public final String secret_key = "lpBe6xtfOOX0aC0DvCr2XkwYxlL2nRcWrMc4XZ5w";
    File casted_image;

    String string_img_url = null, string_msg = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            //initializing the views
            setContentView(R.layout.main2);
            addAccountBtn=(Button)findViewById(R.id.addAccountButton);
            accountsListView=(ListView)findViewById(R.id.accountsListView);
            gotoTweetPageButton=(Button)findViewById(R.id.gotoTweetPageButton);
            //adding the listeners
            addAccountBtn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Twitt_Sharing twitt = new Twitt_Sharing(HomeScreenAuthActivity.this,
                            consumer_key, secret_key);
                    string_img_url = "http://3.bp.blogspot.com/_Y8u09A7q7DU/S-o0pf4EqwI/AAAAAAAAFHI/PdRKv8iaq70/s1600/id-do-anything-logo.jpg";
                    string_msg = "http://chintankhetiya.wordpress.com/";
                    // here we have web url image so we have to make it as file to
                    // upload
                    //String_to_File(string_img_url);
                    // Now share both message & image to sharing activity
                    twitt.shareToTwitter(string_msg, casted_image);

                    return false;
                }
            });
        }catch(Exception e){

        }

    }
}