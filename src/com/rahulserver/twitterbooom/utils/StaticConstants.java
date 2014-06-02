package com.rahulserver.twitterbooom.utils;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: rahulserver
 * Date: 1/19/14
 * Time: 10:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class StaticConstants {
    public static final String TWEET_USER_NAME = "user_name";
    public static final String TWITTER_PREFERENCES = "Twitter_Preferences";
    public static final String FROM_ADDRESS2 = "tst.wings@gmail.com";
    public static final String PRODUCER_JSON_PREF_NAME = "jsonInput";
    public static final String CONSUMER_JSON_PREF_NAME = "jsonOutput";
    public static final String CITY_PREF_KEY = "citypref" ;
    public static final String TWITTER_DISPNAME_KEY ="dispnametwitter" ;
    public static boolean addingAccount=true;
    public static final String consumer_key = "2qW394Mjj3SUHw4hxLOErg";
    public static final String secret_key = "lpBe6xtfOOX0aC0DvCr2XkwYxlL2nRcWrMc4XZ5w";
    public static final String GMAILPREF="Gmail_Preferences";
    public static final String APP_PREF="APP_PREFERENCES";
    public static final String FROM_ADDRESS="Twitter Booom <tst.wings@gmail.com>";
    public static final String USERNAME_PREF_KEY="username";
    public static final String PASSWORD_PREF_KEY="password";
    public static final String EVENT_NAME_PREF="evnn";
    public static final String EVENT_PARTICIPATE_PREF="participate";
    public static final String EVENT_PARTICIPATE_RESPONSE_EMAIL="TST Wings Consent <tst.wings.consent@gmail.com>";
    public static boolean gmailOnClickServiceRunning=false;
    public static int version=1;
    public static int subversion=0;
    public static int folderIdleSleepIntervalInMillisec=30000;
    public static boolean interruptFolderIdle=false;
    public static boolean gmailServiceRunning=false;
    public static final int POLLING_INTERVAL_MILLISECONDS = (60*1000); //5 mins
    //public static final int POLLING_INTERVAL_MILLISECONDS = (5*1000); //5 secs
    public static final int RQS_1 = 123456;
    public static String TWITTER_USERNAME_KEY="tid";
    public static String TWITTER_PASSWORD_KEY="tpwd";

    public static boolean sendEmail(String senderUname, String senderPass, String fromAddress, String toAddress, String subject, String body) {
        final String username = senderUname;
        final String password = senderPass;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromAddress));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(toAddress));
            message.setSubject(subject);
            message.setHeader("Content-Type", "text/plain; charset=UTF-8");
            message.setContent(body, "text/plain; charset=UTF-8");

            Transport.send(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
