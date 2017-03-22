package edu.monash.infotech.health.bing;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 4/21/2016.
 */
public class BingWebSearch {
    String response = "";
    URL url;
    HttpURLConnection conn;
    String basePath = "https://bingapis.azure-api.net/api/v5/search";
    String paramsPath = "&count=5&offset=0&mkt=en-us&safeSearch=Moderate";
    String path = "";
    String description = "";

    public String getDescription(String name){
        path = basePath + "?q=" + name.replaceAll(" ", "%20") + paramsPath;

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
                description = jsonObject.getJSONObject("webPages").getJSONArray("value").getJSONObject(0).getString("snippet");
                if(description.contains("...") && !description.startsWith("...")){
                    description = description.split("\\.")[0];
                }
                System.out.println(description);
            }

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return description;
    }
}
