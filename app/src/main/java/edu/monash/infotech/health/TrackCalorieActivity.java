package edu.monash.infotech.health;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import edu.monash.infotech.health.basic.ShareApplication;
import edu.monash.infotech.health.network.HTTPURLConnection;

public class TrackCalorieActivity extends AppCompatActivity {

    private TextView TodayTextView;
    private TextView myCorieGoalTextView;
    private TextView ConsumedNumTextView;
    private TextView BurnedNumTextView;
    private TextView NetNumTextView;
    private ProgressBar RemainingProgressBar;
    private TextView RemaingTextView;
    private Spinner orderBySpinner;

    private ListView FoodTodayListView;
    private ArrayList<String> FoodTodayArrayList;
    private ArrayAdapter<String> FoodTodayArrayAdapter;

    private String USERNAME = "";
    private String DATE = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_calorie);

        USERNAME = ((ShareApplication)getApplication()).getValue();
        DATE = getCurrentDate();

        TodayTextView = (TextView)findViewById(R.id.TodayTextView);
        myCorieGoalTextView = (TextView)findViewById(R.id.myCorieGoalTextView);
        ConsumedNumTextView = (TextView)findViewById(R.id.ConsumedNumTextView);
        BurnedNumTextView = (TextView)findViewById(R.id.BurnedNumTextView);
        NetNumTextView = (TextView)findViewById(R.id.NetNumTextView);
        RemainingProgressBar = (ProgressBar)findViewById(R.id.RemainingProgressBar);
        RemaingTextView = (TextView)findViewById(R.id.RemaingTextView);
        orderBySpinner = (Spinner)findViewById(R.id.orderBySpinner);
        String[] orderByType = new String[]{
            "Order By Time", "Order By Food Name"
        };
        orderBySpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, orderByType));


        FoodTodayListView = (ListView)findViewById(R.id.FoodTodayListView);
        FoodTodayArrayList = new ArrayList<String>();
        FoodTodayArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, FoodTodayArrayList);
        FoodTodayListView.setAdapter(FoodTodayArrayAdapter);

        orderBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("select: " + position);
                if (position == 0) {
                    new getFoodEatenTodayOrderByTimeFromWS().execute(USERNAME.replaceAll(" ", "%20"), DATE);
                }
                if (position == 1) {
                    new getFoodEatenTodayOrderByFoodNameFromWS().execute(USERNAME.replaceAll(" ", "%20"), DATE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TodayTextView.setText("Today (" + DATE + ")");

        new getReportFromWS().execute(USERNAME.replaceAll(" ", "%20"), DATE);


    }

    public String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        String today = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
        return today;
    }

    private class getReportFromWS extends AsyncTask<String, Void, Void> {
        String response = "";
        String path1 = "http://172.16.120.42:8080/HealthServer/webresources/com.entity.health.userreport/findByUserNameAndDate";
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
                    String calConsumed = jsonArray.getJSONObject(0).getString("calConsumed");
                    String calBurned = jsonArray.getJSONObject(0).getString("calBurned");
                    String steps = jsonArray.getJSONObject(0).getString("steps");
                    String calorieGoal = jsonArray.getJSONObject(0).getString("calorieGoal");
                   // System.out.println(calConsumed + ", " + calBurned + ", " + steps + ", " + calorieGoal + ", " + remaining);
                    myCorieGoalTextView.setText(calorieGoal + " cal");
                    ConsumedNumTextView.setText(calConsumed);
                    BurnedNumTextView.setText("-" + calBurned);
                    double net = Double.valueOf(calConsumed) - Double.valueOf(calBurned);
                    NetNumTextView.setText(String.valueOf(net));
                    int calorieGoal_int = Integer.valueOf(calorieGoal.split("\\.")[0]);
                    RemainingProgressBar.setMax(calorieGoal_int);
                    double remaining_double = Double.valueOf(calorieGoal) - net;
                    int remaining_int = (int)remaining_double;
                    if(net >= 0){
                        RemainingProgressBar.setProgress(calorieGoal_int-remaining_int);
                    }
                    else{
                        RemainingProgressBar.setProgress(calorieGoal_int);
                    }

                    RemaingTextView.setText(remaining_int +" Remaining Today");

                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            else{
                //user does not have a report on this date
                Toast.makeText(getApplicationContext(), "No record found on this date! Select another date!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class getFoodEatenTodayOrderByTimeFromWS extends AsyncTask<String, Void, Void> {
        String response = "";
        String path1 = "http://172.16.120.42:8080/HealthServer/webresources/com.entity.health.userfood/findByUserNameAndDateOrderByTime";
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
                    int foodNum = jsonArray.length();
                    System.out.println(foodNum);
                    FoodTodayArrayList.clear();
                    for(int i=0; i<foodNum; i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String time = jsonObject.getString("time").split("T")[1].split("\\+")[0];
                        String foodname = jsonObject.getJSONObject("food").getString("name");
                        String amount = jsonObject.getString("amount");
                        double amount_double = Double.valueOf(amount);
                        double serving_double = Double.valueOf(jsonObject.getJSONObject("food").getString("serving"));
                        String calorie = jsonObject.getJSONObject("food").getString("calorie");
                        double calorieEaten_double = Double.valueOf(calorie) * amount_double / serving_double;
                        String calorieEaten = String.valueOf(calorieEaten_double);
                        FoodTodayArrayList.add(time + "  " + foodname + "  " + calorieEaten);
                    }
                    FoodTodayListView.setAdapter(FoodTodayArrayAdapter);

                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            else{
                //user does not have a report on this date
                Toast.makeText(getApplicationContext(), "You haven't eated any food today!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class getFoodEatenTodayOrderByFoodNameFromWS extends AsyncTask<String, Void, Void> {
        String response = "";
        String path1 = "http://172.16.120.42:8080/HealthServer/webresources/com.entity.health.userfood/findByUserNameAndDateOrderByFoodName";
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
                    int foodNum = jsonArray.length();
                    System.out.println(foodNum);
                    FoodTodayArrayList.clear();
                    for(int i=0; i<foodNum; i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String time = jsonObject.getString("time").split("T")[1].split("\\+")[0];
                        String foodname = jsonObject.getJSONObject("food").getString("name");
                        String amount = jsonObject.getString("amount");
                        double amount_double = Double.valueOf(amount);
                        double serving_double = Double.valueOf(jsonObject.getJSONObject("food").getString("serving"));
                        String calorie = jsonObject.getJSONObject("food").getString("calorie");
                        double calorieEaten_double = Double.valueOf(calorie) * amount_double / serving_double;
                        String calorieEaten = String.valueOf(calorieEaten_double);
                        FoodTodayArrayList.add(time + "  " + foodname + "  " + calorieEaten);
                    }
                    FoodTodayListView.setAdapter(FoodTodayArrayAdapter);

                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            else{
                //user does not have a report on this date
                Toast.makeText(getApplicationContext(), "You haven't eated any food today!", Toast.LENGTH_SHORT).show();
            }

        }
    }

}
