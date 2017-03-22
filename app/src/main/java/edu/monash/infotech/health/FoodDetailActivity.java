package edu.monash.infotech.health;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.Calendar;

import edu.monash.infotech.health.basic.ShareApplication;
import edu.monash.infotech.health.bing.BingImageSearch;
import edu.monash.infotech.health.bing.BingWebSearch;
import edu.monash.infotech.health.fatsecret.platform.FatSecretAPI;
import edu.monash.infotech.health.network.HTTPURLConnection;

public class FoodDetailActivity extends AppCompatActivity {

    private ImageView foodImageView;
    private ImageView foodImageView1;
    private TextView foodDescription;
    private TextView FoodNameTextView;
    private TextView UrlTextView;
    private TextView PerTextView;
    private TextView CalorieTextView;
    private TextView FatTextView;
    private TextView CarbsTextView;
    private TextView ProteinTextView;
    private Button TakeLookButton;
    private LinearLayout FoodDetailLL;
    private TextView foodDetailTextView;
    private EditText ServingEditText;

    private String foodName = "";

    private String calorieFromWS = "";
    private String fatFromWS = "";
    private String servingFromWS = "";


    private String USERNAME = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        USERNAME = ((ShareApplication)getApplication()).getValue();
        //System.out.println(USERNAME);


        foodImageView = (ImageView)findViewById(R.id.foodImageView);
        foodImageView1 = (ImageView)findViewById(R.id.foodImageView1);
        foodDescription = (TextView)findViewById(R.id.foodDescription);
        FoodNameTextView = (TextView)findViewById(R.id.FoodNameTextView);
        UrlTextView = (TextView)findViewById(R.id.urlTextView);
        PerTextView = (TextView)findViewById(R.id.PerTextView);
        CalorieTextView = (TextView)findViewById(R.id.CalorieTextView);
        FatTextView = (TextView)findViewById(R.id.FatTextView);
        CarbsTextView = (TextView)findViewById(R.id.CarbsTextView);
        ProteinTextView = (TextView)findViewById(R.id.ProteinTextView);
        TakeLookButton = (Button)findViewById(R.id.TakeLookButton);
        FoodDetailLL = (LinearLayout)findViewById(R.id.FoodDetailLL);
        foodDetailTextView = (TextView)findViewById(R.id.foodDetailTextView);
        ServingEditText = (EditText)findViewById(R.id.ServingEditText);

        foodName = getIntent().getStringExtra("foodName");
        FoodNameTextView.setText(foodName);

        new testFatSecret().execute();
        new testBing().execute();
        new testBingWeb().execute();



    }


    private class testBing extends AsyncTask<Void, Void, Void>{
        Bitmap[] bitmap;
        @Override
        protected Void doInBackground(Void... arg0){
            bitmap = new BingImageSearch().getImage(foodName);
            //System.out.println("server response: " + response);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(bitmap != null){
                if(bitmap[0] != null){
                    foodImageView.setImageBitmap(bitmap[0]);
                }
                if(bitmap[1] != null){
                    foodImageView1.setImageBitmap(bitmap[1]);
                }
            }
        }
    }

    private class testBingWeb extends AsyncTask<Void, Void, Void>{
        String description = "";
        @Override
        protected Void doInBackground(Void... arg0){
            description = new BingWebSearch().getDescription(foodName);
            //System.out.println("server response: " + response);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(description != null){
                foodDescription.setText(description);
            }
        }
    }

    private class testFatSecret extends AsyncTask<Void, Void, Void> {
        JSONObject foods = null;
        @Override
        protected Void doInBackground(Void... arg0){
            FatSecretAPI api = new FatSecretAPI("1cdc19f5589940798ec8f643be0ab6f4", "f01d121e378b426d90f4024e527ddb72");
            try{
                foods = api.getFoodItems(foodName);
            } catch (Exception e){
                e.printStackTrace();
            }
            //System.out.println("server response: " + response);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(foods != null){
                try{
                    JSONObject wholeEntry = (JSONObject)foods.getJSONObject("result").getJSONObject("foods").getJSONArray("food").get(0);
                    String foodUrl = wholeEntry.getString("food_url");
                    String description = wholeEntry.getString("food_description");
                    String per = description.split(" - ")[0];
                    String nutritions = description.split(" - ")[1];
                    String calories = nutritions.split("\\|")[0];
                    String fat = nutritions.split("\\|")[1];
                    String carbs = nutritions.split("\\|")[2];
                    String protein = nutritions.split("\\|")[3];
//                    System.out.println("Description: " + wholeEntry.getString("food_description"));
//                    System.out.println("Name: " + wholeEntry.getString("food_name"));
//                    System.out.println("Type: " + wholeEntry.getString("food_type"));
//                    System.out.println("Url: " + wholeEntry.getString("food_url"));
                    UrlTextView.setText(foodUrl);
                    PerTextView.setText(per);
                    CalorieTextView.setText(calories);
                    FatTextView.setText(fat);
                    CarbsTextView.setText(carbs);
                    ProteinTextView.setText(protein);

                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Network Unavailable", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class getFoodInfoFromWS extends AsyncTask<String, Void, Void> {
        String response = "";
        String path1 = "http://172.16.120.42:8080/HealthServer/webresources/com.entity.health.food/findByName";
        HTTPURLConnection conn;

        @Override
        protected Void doInBackground(String... params){
            conn = new HTTPURLConnection();
            for(int i=0; i<params.length; i++){
                path1 = path1 + "/" + params[i];
            }
            System.out.println(path1);
            response = conn.GetFromWebServer(path1);
            System.out.println("server response1: " + response);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(!response.equals("") && !response.equals("[]")){
                JSONArray jsonArray = null;
                try{
                    jsonArray = new JSONArray(response);
                    calorieFromWS = jsonArray.getJSONObject(0).getString("calorie");
                    fatFromWS = jsonArray.getJSONObject(0).getString("fat");
                    servingFromWS = jsonArray.getJSONObject(0).getString("serving");
                    String foodDetailFromWS = "Food Details: " + "Serving " + servingFromWS + ", Calorie " + calorieFromWS + "cal, Fat " + fatFromWS + "g";
                    foodDetailTextView.setText(foodDetailFromWS);
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }

        }
    }

    private class addUserfoodToWS extends AsyncTask<String, Void, Void> {
        String response = "";
        String path1 = "http://172.16.120.42:8080/HealthServer/webresources/com.entity.health.userfood/addUserfoodByName";
        HTTPURLConnection conn;

        @Override
        protected Void doInBackground(String... params){
            conn = new HTTPURLConnection();
            for(int i=0; i<params.length; i++){
                path1 = path1 + "/" + params[i];
            }
            System.out.println(path1);
            response = conn.GetFromWebServer(path1);
            System.out.println("server response: " + response);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(!response.equals("") && !response.equals("[]")){
                JSONArray jsonArray = null;
                try{
                    jsonArray = new JSONArray(response);
                    int status = Integer.valueOf(jsonArray.get(1).toString());
                    if(status == 0){
                        Toast.makeText(getApplicationContext(), "Eat sucess!", Toast.LENGTH_SHORT).show();
                    }
                    if(status == 1){
                        Toast.makeText(getApplicationContext(), "Failure! Check your network maybe...", Toast.LENGTH_SHORT).show();
                    }
                    if(status == 2){
                        Toast.makeText(getApplicationContext(), "You have already consumed this food today. Select another one!", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e){
                    e.printStackTrace();
                }
            }

        }
    }

    private class updateCalConsumedToWS extends AsyncTask<String, Void, Void> {
        String response = "";
        String path1 = "http://172.16.120.42:8080/HealthServer/webresources/com.entity.health.user/updateCalConsumedByUserNameAndDate";
        HTTPURLConnection conn;

        @Override
        protected Void doInBackground(String... params){
            conn = new HTTPURLConnection();
            for(int i=0; i<params.length; i++){
                path1 = path1 + "/" + params[i];
            }
            System.out.println(path1);
            response = conn.GetFromWebServer(path1);
            System.out.println("server response: " + response);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(!response.equals("") && !response.equals("[]")){
                JSONArray jsonArray = null;
                try{
                    jsonArray = new JSONArray(response);
                    int status = Integer.valueOf(jsonArray.get(1).toString());
                    if(status == 0){
                        System.out.println("calConsumed updated success");
                    }
                    if(status == 1){
                        System.out.println("calConsumed updated failure");
                    }

                } catch (JSONException e){
                    e.printStackTrace();
                }
            }

        }
    }



    public void EatOnClick(View view){
        String serving = ServingEditText.getText().toString();
        if(serving.equals("")){
            ServingEditText.setError("Should not be blank!");
        }
        else{
            double serving_double = Double.valueOf(serving);
            double servingFromWS_double = Double.valueOf(servingFromWS);
            double calorieFromWS_double = Double.valueOf(calorieFromWS);
            final double calorieEaten_double = (serving_double/servingFromWS_double) * calorieFromWS_double;
            String calorieReminder = "Eat this food to consume " + calorieEaten_double + " cal?";
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Reminder");
            builder.setMessage(calorieReminder);
            builder.setIcon(R.drawable.smiley_winking);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    String username = USERNAME;
                    String foodname = foodName;
                    String date = getCurrentDate();
                    String time = getCurrentTime();
                    //System.out.println(date);
                    String amount = ServingEditText.getText().toString();
                    new addUserfoodToWS().execute(username.replaceAll(" ", "%20"), foodname.replaceAll(" ", "%20"), date, time, amount);

                    new updateCalConsumedToWS().execute(USERNAME.replaceAll(" ", "%20"), date);

                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create().show();
        }
    }

    public void TakeLookOnClick(View view){
        TakeLookButton.setVisibility(View.GONE);
        FoodDetailLL.setVisibility(View.VISIBLE);
        new getFoodInfoFromWS().execute(foodName.replaceAll(" ", "%20"));
    }

    public String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DATE);
        String currentDate = year + "-" + month + "-" + day;
        return currentDate;
    }

    public String getCurrentTime(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        String currentTime = String.valueOf(hour) + ":" + String.valueOf(minute) + ":" + String.valueOf(second);
        return currentTime;
    }



}
