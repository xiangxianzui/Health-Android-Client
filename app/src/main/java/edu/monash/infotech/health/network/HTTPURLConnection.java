package edu.monash.infotech.health.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 4/14/2016.
 */
public class HTTPURLConnection {
    String response = "";
    URL url;
    HttpURLConnection conn;


    public String PostToWebServer(String path){
        StringBuffer sb = new StringBuffer();
        try{
            url = new URL(path);
            URLConnection urlConnection = null;
            urlConnection = (URLConnection)url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"utf-8"));
            String line = null;
            while((line = reader.readLine()) != null){
                sb.append(line);
            }
//            conn = (HttpURLConnection)url.openConnection();
           // conn.setReadTimeout(15000);
//            conn.setDoOutput(true);
//            conn.setRequestMethod("GET");
//            conn.setConnectTimeout(5000);

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            //conn.disconnect();
        }
        return sb.toString();
    }

    public String GetFromWebServer(String path){
        try{
            url = new URL(path);
            conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                //Log.d("Output",br.toString());
                while ((line = br.readLine()) != null) {
                    response += line;
                    Log.d("output lines", line);
                }
            } else{
                response = "";
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        System.out.println("from get method: "+response);
        return response;
    }



}
