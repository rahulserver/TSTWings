package com.rahulserver.twitterbooom.services;

import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpUrlConnectionExample {

    private List<String> cookies;
    private HttpsURLConnection conn;

    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36";

    public static void main(String[] args) throws Exception {

//        String url = "https://accounts.google.com/ServiceLoginAuth";
//        String gmail = "https://mail.google.com/mail/";
        String url = "https://mobile.twitter.com/session/new";
        String url2 = "https://mobile.twitter.com/session";
        String url3 = "https://mobile.twitter.com/compose/tweet";


        HttpUrlConnectionExample http = new HttpUrlConnectionExample();

        // make sure cookies is turn on
        CookieHandler.setDefault(new CookieManager());

        // 1. Send a "GET" request, so that you can extract the form's data.
        String page = http.GetPageContent(url);
        Map<String,String> postParams = http.getFormParams(page, "", "");

        // 2. Construct above post's content and then send a POST request for
        // authentication
        http.sendPost(url2, postParams);

        // 3. success then go to gmail.
        String result = http.GetPageContent(url3);
        System.out.println(result);
    }
    public static void runThisClass(String tweet,String username,String password) throws Exception{
        String url = "https://mobile.twitter.com/session/new";
        String url2 = "https://mobile.twitter.com/session";
        String url3 = "https://mobile.twitter.com/compose/tweet";
        String url4="https://mobile.twitter.com/";

        DefaultHttpClient httpclient = new DefaultHttpClient();
        CookieStore cookieStore = new BasicCookieStore();
        HttpContext httpContext = new BasicHttpContext();
        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = httpclient.execute(httpget,httpContext);
        HttpEntity entity = response.getEntity();

        String formCode = EntityUtils.toString(entity);
        Map<String,String> formParams =getFormParams(formCode, username, password);



        //HttpUrlConnectionExample http = new HttpUrlConnectionExample();

        // make sure cookies is turn on
        CookieHandler.setDefault(new CookieManager());

        // 1. Send a "GET" request, so that you can extract the form's data.
        //String page = http.GetPageContent(url);
        httpclient.setCookieStore(cookieStore);
        List<Cookie> cookies = httpclient.getCookieStore().getCookies();


        HttpPost httpost = new HttpPost(url2);

        List <NameValuePair> nvps = new ArrayList<NameValuePair>();
        for(String s:formParams.keySet()){
            nvps.add(new BasicNameValuePair(s, formParams.get(s)));
        }

        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

        response = httpclient.execute(httpost,httpContext);
        entity = response.getEntity();
        formCode = EntityUtils.toString(entity);

        httpget=new HttpGet(url3);
        response=httpclient.execute(httpget,httpContext);
        entity=response.getEntity();
        formCode = EntityUtils.toString(entity);
        formParams =getFormParams(formCode, "", "");
        nvps = new ArrayList <NameValuePair>();
        for(String s:formParams.keySet()){
            nvps.add(new BasicNameValuePair(s, formParams.get(s)));
        }
        nvps.add(new BasicNameValuePair("tweet[text]", tweet));

        httpost = new HttpPost(url4);

        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

        response = httpclient.execute(httpost,httpContext);
        entity = response.getEntity();
        formCode = EntityUtils.toString(entity);

        Log.d("FormCode", formCode);
    }
    private void sendPost(String url, Map<String,String> postMap) throws Exception{
        HttpClient httpClient=new DefaultHttpClient();
        HttpPost httpRequest=new HttpPost(url);
        List<NameValuePair>pairs=new ArrayList<NameValuePair>();
        for(String k:postMap.keySet()){
            pairs.add(new BasicNameValuePair(k,postMap.get(k)));
        }
        httpRequest.setEntity(new UrlEncodedFormEntity(pairs));
        CookieStore cookieStore = new BasicCookieStore();
        for (String cookie : this.cookies) {
            String k=cookie.split(";", 1)[0].split("=")[0];
            String v=cookie.split(";", 1)[0].split("=")[1];
            Cookie cookie1=new BasicClientCookie(k,v);
            cookieStore.addCookie(cookie1);
        }
        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

        HttpResponse httpResponse=httpClient.execute(httpRequest,localContext);
        int n=httpResponse.getStatusLine().getStatusCode();
        Log.d("StatusCode", String.valueOf(n));
    }
    private String GetPageContent(String url) throws Exception {

        URL obj = new URL(url);
        conn = (HttpsURLConnection) obj.openConnection();

        // default is GET
        conn.setRequestMethod("GET");

        conn.setUseCaches(false);

        // act like a browser
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        if (cookies != null) {
            for (String cookie : this.cookies) {
                conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
            }
        }
        int responseCode = conn.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in =
                new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Get the response cookies
        setCookies(conn.getHeaderFields().get("set-cookie"));

        return response.toString();

    }

    public static Map<String,String> getFormParams(String html, String username, String password)
            throws UnsupportedEncodingException {

        System.out.println("Extracting form's data...");

        Document doc = Jsoup.parse(html);

        // Google form id
        Element loginform = doc.getElementsByTag("form").get(0);
        Elements inputElements = loginform.getElementsByTag("input");
        List<String> paramList = new ArrayList<String>();
        Map<String,String>map=new HashMap<String, String>();
        for (Element inputElement : inputElements) {
            String key = inputElement.attr("name");
            String value = inputElement.attr("value");

            if (key.equals("username"))
                value = username;
            else if (key.equals("password"))
                value = password;

            map.put(key,value);
        }

        // build parameters list

        return map;
    }

    public List<String> getCookies() {
        return cookies;
    }

    public void setCookies(List<String> cookies) {
        this.cookies = cookies;
    }

}