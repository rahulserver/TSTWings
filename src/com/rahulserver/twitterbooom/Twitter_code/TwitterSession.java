package com.rahulserver.twitterbooom.Twitter_code;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.rahulserver.twitterbooom.utils.StaticConstants;
import twitter4j.auth.AccessToken;

public class TwitterSession {
    private final SharedPreferences sharedPref;
    private final Editor editor;

    public TwitterSession(Context context) {
        sharedPref = context.getSharedPreferences(StaticConstants.APP_PREF,Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public void storeAccessToken(AccessToken accessToken, String username) {
//        editor.putString(StaticConstants.TWEET_AUTH_KEY, accessToken.getToken());
//        editor.putString(StaticConstants.TWEET_AUTH_SECRET_KEY, accessToken.getTokenSecret());
//        editor.putString(StaticConstants.TWEET_USER_NAME, username);

        editor.commit();
    }

    public void resetAccessToken() {
//        editor.putString(StaticConstants.TWEET_AUTH_KEY, null);
//        editor.putString(StaticConstants.TWEET_AUTH_SECRET_KEY, null);
//        editor.putString(StaticConstants.TWEET_USER_NAME, null);

        editor.commit();
    }

    public String getUsername() {
        return sharedPref.getString(StaticConstants.TWEET_USER_NAME, "");
    }

    public AccessToken getAccessToken() {
        String token = sharedPref.getString("", null);
        String tokenSecret = sharedPref.getString("", null);

        if (token != null && tokenSecret != null)
            return new AccessToken(token, tokenSecret);
        else
            return null;
    }
}