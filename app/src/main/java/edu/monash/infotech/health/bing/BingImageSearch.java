package edu.monash.infotech.health.bing;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Administrator on 4/20/2016.
 */
public class BingImageSearch {
    String response = "";
    URL url;
    HttpURLConnection conn;
    String basePath = "https://bingapis.azure-api.net/api/v5/images/search";
    String paramsPath = "&count=5&offset=0&mkt=en-us&safeSearch=Moderate";
    String path = "";
 //   String path = "https://bingapis.azure-api.net/api/v5/images/search?q=Man%20United&count=1&offset=0&mkt=en-us&safeSearch=Moderate";
    String thumbnailUrl = "";
    HttpURLConnection conn1;
    URL url1;

    String thumbnailUrl1 = "";
    HttpURLConnection conn2;
    URL url2;

    Bitmap bitmap[] = null;

    //this method get the requested image url first, and then get the bitmap of that image url;
    public Bitmap[] getImage(String name){

        bitmap = new Bitmap[2];

        path = basePath + "?q=" + name.replaceAll(" ", "%20") + paramsPath;

//fistly get thumbnailUrl through Bing Image API.
        try{
            url = new URL(path);
            conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestProperty("Ocp-Apim-Subscription-Key", "0b73ce4968794795ac8c0cebf369c348");

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

            if(!response.equals("") && !response.equals("[]")){
                JSONObject jsonObject = null;
                jsonObject = new JSONObject(response);
                thumbnailUrl = jsonObject.getJSONArray("value").getJSONObject(0).getString("thumbnailUrl");
                thumbnailUrl1 = jsonObject.getJSONArray("value").getJSONObject(1).getString("thumbnailUrl");
                //System.out.println("thumbnailUrl: " + thumbnailUrl);
            }

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

//get the bitmap through thumbnailUrl which has been gotten from the first part
        if(!thumbnailUrl.equals("")){
            try{
                url1 = new URL(thumbnailUrl);
                conn1 = (HttpURLConnection)url1.openConnection();
                conn1.setReadTimeout(15000);
                conn1.setConnectTimeout(15000);
                if(conn1.getResponseCode() == HttpURLConnection.HTTP_OK){
                    InputStream inputStream = conn1.getInputStream();
                    bitmap[0] = BitmapFactory.decodeStream(inputStream);
                }
                else{
                    bitmap[0] = null;
                }
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                conn1.disconnect();
            }
        }

        if(!thumbnailUrl1.equals("")){
            try{
                url2 = new URL(thumbnailUrl1);
                conn2 = (HttpURLConnection)url2.openConnection();
                conn2.setReadTimeout(15000);
                conn2.setConnectTimeout(15000);
                if(conn2.getResponseCode() == HttpURLConnection.HTTP_OK){
                    InputStream inputStream = conn2.getInputStream();
                    bitmap[1] = BitmapFactory.decodeStream(inputStream);
                }
                else{
                    bitmap[1] = null;
                }
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                conn2.disconnect();
            }
        }

        return bitmap;
    }

}

