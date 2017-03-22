package edu.monash.infotech.health;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import edu.monash.infotech.health.basic.BasicConfig;
import edu.monash.infotech.health.basic.ShareApplication;
import edu.monash.infotech.health.network.HTTPURLConnection;

public class CaloriegoalActivity extends AppCompatActivity {
    private NumberPicker numberPicker1;
    private NumberPicker numberPicker2;
    private NumberPicker numberPicker3;
    private NumberPicker numberPicker4;
    private ProgressBar CalorieGoal_ProgressBar;
    private ImageButton backToHome_ImageButton;

    String serverIP = new BasicConfig().getServerIP();
    String serverPORT = new BasicConfig().getSERVER_PORT();
    private HTTPURLConnection conn;
    private String path = "http://" + serverIP + ":" + serverPORT + "/HealthServer/webresources/com.entity.health.userreport/findByUserNameAndDate";
    private String USERNAME = "";
    private String DATE = "";

    private String calorieGoal = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caloriegoal);

        CalorieGoal_ProgressBar = (ProgressBar)findViewById(R.id.CalorieGoal_ProgressBar);
        backToHome_ImageButton = (ImageButton)findViewById(R.id.Back_Button);
        numberPicker1 = (NumberPicker)findViewById(R.id.numberPicker1);
        numberPicker2 = (NumberPicker)findViewById(R.id.numberPicker2);
        numberPicker3 = (NumberPicker)findViewById(R.id.numberPicker3);
        numberPicker4 = (NumberPicker)findViewById(R.id.numberPicker4);
        numberPicker1.setMaxValue(9);
        numberPicker1.setMinValue(0);
        numberPicker2.setMaxValue(9);
        numberPicker2.setMinValue(0);
        numberPicker3.setMaxValue(9);
        numberPicker3.setMinValue(0);
        numberPicker4.setMaxValue(9);
        numberPicker4.setMinValue(0);

        USERNAME = ((ShareApplication)getApplication()).getValue();
        //System.out.println("USERNAME: "+USERNAME);
        Calendar calendar = Calendar.getInstance();
        DATE = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DATE);
        wrapPath(USERNAME, DATE);
        conn = new HTTPURLConnection();
        new ConnectWebService().execute();


    }

    public void SetOnClick(View view){
        //pop up a dialog to confirm
        new AlertDialog.Builder(this)
                .setTitle("Save changes?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String username=USERNAME;
                        String date=DATE;
                        String goal=getChangedGoal();
                        CalorieGoal_ProgressBar.setProgress(Integer.valueOf(goal));
                        new UpdateCalorieGoal().execute(username, date, goal);

                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public String wrapPath(String... params){
        for(int i=0; i<params.length; i++){
            path = path + "/" + params[i];
        }
        System.out.println(path);
        return path;
    }

    private class ConnectWebService extends AsyncTask<Void, Void, Void> {
        String response = "";
        @Override
        protected Void doInBackground(Void... arg0){
            response = conn.GetFromWebServer(path);
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
                    calorieGoal = jsonArray.getJSONObject(0).getString("calorieGoal");
                    int calorieGoalInt = Integer.valueOf(calorieGoal.split("\\.")[0]);
                    CalorieGoal_ProgressBar.setProgress(calorieGoalInt);
                    int d1, d2, d3, d4 = 0;
                    System.out.println("jiojoijoihoihoi"+calorieGoalInt);
                    if(calorieGoalInt>=0 && calorieGoalInt<=9999){
                        d4 = calorieGoalInt%10;
                        calorieGoalInt = calorieGoalInt/10;
                        d3 = calorieGoalInt%10;
                        calorieGoalInt = calorieGoalInt/10;
                        d2 = calorieGoalInt%10;
                        calorieGoalInt = calorieGoalInt/10;
                        d1 = calorieGoalInt%10;
                        numberPicker1.setValue(d1);
                        numberPicker2.setValue(d2);
                        numberPicker3.setValue(d3);
                        numberPicker4.setValue(d4);
                    }
                    else{
                        numberPicker1.setValue(0);
                        numberPicker2.setValue(0);
                        numberPicker3.setValue(0);
                        numberPicker4.setValue(0);
                    }


                } catch (JSONException e){
                    e.printStackTrace();
                }
            }

        }
    }


    private class UpdateCalorieGoal extends AsyncTask<String, Void, Void> {
        String response = "";
        String path_update = "http://" + serverIP + ":" + serverPORT + "/HealthServer/webresources/com.entity.health.user/updateCalorieGoalByUserNameAndDate";
        private HTTPURLConnection conn_update;

        @Override
        protected Void doInBackground(String... params){
            conn_update = new HTTPURLConnection();
            for(int i=0; i<params.length; i++){
                path_update = path_update + "/" + params[i];
            }
            System.out.println(path_update);
            response = conn_update.PostToWebServer(path_update);
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
                    int status = Integer.valueOf(jsonArray.getString(1));
                    if(status == 0){//update success
                        Toast.makeText(getApplicationContext(), "Save successfully!", Toast.LENGTH_SHORT).show();
                    }
                    if(status == 1){//update fail
                        Toast.makeText(getApplicationContext(), "Fail to save changes.Please check your network!", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e){
                    e.printStackTrace();
                }
            }

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        new ConnectWebService().execute();
    }

    public String getChangedGoal(){
        String goal = "0000";
        String d1 = String.valueOf(numberPicker1.getValue());
        String d2 = String.valueOf(numberPicker2.getValue());
        String d3 = String.valueOf(numberPicker3.getValue());
        String d4 = String.valueOf(numberPicker4.getValue());
        goal = d1 + d2 + d3 + d4;
        System.out.println(goal);
        return goal;
    }

    public void BackOnClick(View view){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
