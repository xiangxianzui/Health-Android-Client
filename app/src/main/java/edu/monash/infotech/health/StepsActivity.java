package edu.monash.infotech.health;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;

import edu.monash.infotech.health.basic.ShareApplication;
import edu.monash.infotech.health.models.Step;
import edu.monash.infotech.health.network.HTTPURLConnection;

public class StepsActivity extends AppCompatActivity {

    //private TextView durationTextView;
    private EditText step1EditText;
    private Button set1Button;
    private LinearLayout LL1;
    private TextView totalStepsTextView;
    private Button doneButton;

    private double TOTAL_STEPS = 0;
    private String LAST_TIME;
    private String USER_NAME;

    private ArrayList<String> recordArrayList;
    private ListView recordListView;

    private DatabaseHelper dbHelper;

    private boolean screenStatus = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        step1EditText = (EditText)findViewById(R.id.step1EditText);
        set1Button = (Button)findViewById(R.id.set1Button);
        LL1 = (LinearLayout)findViewById(R.id.LL1);
        totalStepsTextView = (TextView)findViewById(R.id.totalStepsTextView);
        recordListView = (ListView)findViewById(R.id.stepRecordListView);
        doneButton = (Button)findViewById(R.id.doneButton);

        dbHelper = new DatabaseHelper(getApplicationContext());
        USER_NAME = ((ShareApplication)getApplication()).getValue();
        TOTAL_STEPS = getTotalSteps(USER_NAME);


        recordArrayList = new ArrayList<String>();
        recordArrayList = getStepRecords(USER_NAME);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, recordArrayList);
        recordListView.setAdapter(arrayAdapter);

        screenStatus = ((ShareApplication)getApplication()).getStatus();
        if(screenStatus == false){
            set1Button.setVisibility(View.INVISIBLE);
            step1EditText.setVisibility(View.INVISIBLE);
            doneButton.setVisibility(View.INVISIBLE);
        }

        getCurrentTime();


    }

    public void set1OnClick(View view){
        if(step1EditText.getText().toString().equals("")){
            step1EditText.setError("Enter your steps first!");
        }
        else{
            updateTotalSteps(step1EditText);
            insertLocalStep(USER_NAME, getCurrentDate(), getCurrentTime(), Double.valueOf(step1EditText.getText().toString()));
            recordArrayList.add(getCurrentTime() + "\t\t\t" + step1EditText.getText().toString() + ".0");
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recordArrayList);
            recordListView.setAdapter(arrayAdapter);
            step1EditText.setText("");
        }
    }

    public double getTotalSteps(String userName){
        double totalSteps = 0;
        ArrayList<Step> records;
        records =  dbHelper.getAllSteps();
        long thisId = dbHelper.getUserIdByName(userName);
        String thisDate = getCurrentDate();
        if(!records.isEmpty()){
            for(int i=0; i<records.size(); i++){
                Step s = records.get(i);
                if(s.getUserId() == thisId && s.getDate().equals(thisDate)){
                    totalSteps = totalSteps + s.getStepsNum();
                }
            }
        }
        totalStepsTextView.setText("Total Steps: " + totalSteps);
        return totalSteps;
    }

    public void updateTotalSteps(EditText thisStepEditText){
        double thisSteps = Double.valueOf(thisStepEditText.getText().toString());
        TOTAL_STEPS = TOTAL_STEPS + thisSteps;
        totalStepsTextView.setText("Total Steps: " + String.valueOf(TOTAL_STEPS));
    }

    public String getCurrentTime(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        //System.out.println(hour + " " + minute + " " + second);
        String currentTime = String.valueOf(hour) + ":" + String.valueOf(minute) + ":" + String.valueOf(second);
        LAST_TIME = currentTime;
        return currentTime;
    }

    public String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DATE);
        String currentDate = year + "-" + month + "-" + day;
        return currentDate;
    }

    public void insertLocalStep(String userName, String date, String time, double stepsNum){
        //update step num in local database
        long userId = dbHelper.getUserIdByName(userName);
        System.out.println(userId);
        if(userId != 0){
            Step s = new Step(userId, date, time, stepsNum);
            dbHelper.addStep(s);
        }
    }

    public ArrayList<String> getStepRecords(String userName){
        //get step records in
        ArrayList<Step> records;
        records =  dbHelper.getAllSteps();
        long thisId = dbHelper.getUserIdByName(userName);
        String thisDate = getCurrentDate();
        ArrayList<String> thisRecords = new ArrayList<String>();
        if(!records.isEmpty()){
            for(int i=0; i<records.size(); i++){
                Step s = records.get(i);
                if(s.getUserId() == thisId && s.getDate().equals(thisDate)){
                    thisRecords.add(s.getTime() + "\t\t\t"+ s.getStepsNum());
                }
            }
        }
        return thisRecords;
    }

    public void doneOnClick(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning"); //设置标题
        builder.setMessage("Once you click on 'Yes', you can't edit steps totay!"); //设置内容
        builder.setIcon(R.drawable.chat_exclamation);//设置图标，图片id即可
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //关闭dialog
                set1Button.setVisibility(View.INVISIBLE);
                step1EditText.setVisibility(View.INVISIBLE);
                doneButton.setVisibility(View.INVISIBLE);
                //update total step to web service
                String username = USER_NAME;
                String date = getCurrentDate();
                String totalSteps = String.valueOf(TOTAL_STEPS);
                new updateStepsToWS().execute(username.replaceAll(" ", "%20"), date, totalSteps);

                new updateCalorieBurnedToWS().execute(username.replaceAll(" ", "%20"), date);

                ((ShareApplication)getApplication()).setStatus(false);//set the step screen to uneditable

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }

    private class updateStepsToWS extends AsyncTask<String, Void, Void> {
        String response = "";
        String path1 = "http://172.16.120.42:8080/HealthServer/webresources/com.entity.health.user/updateStepsByUserNameAndDate";
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
                        Toast.makeText(getApplicationContext(), "Update sucess!", Toast.LENGTH_SHORT).show();
                    }
                    if(status == 1){
                        Toast.makeText(getApplicationContext(), "Update failed! Check your network maybe...", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e){
                    e.printStackTrace();
                }
            }

        }
    }

    private class updateCalorieBurnedToWS extends AsyncTask<String, Void, Void> {
        String response = "";
        String path1 = "http://172.16.120.42:8080/HealthServer/webresources/com.entity.health.user/updateCalBurnedByUserNameAndDate";
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
                        Toast.makeText(getApplicationContext(), "Update sucess!", Toast.LENGTH_SHORT).show();
                    }
                    if(status == 1){
                        Toast.makeText(getApplicationContext(), "Update failed! Check your network maybe...", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e){
                    e.printStackTrace();
                }
            }

        }
    }

}
