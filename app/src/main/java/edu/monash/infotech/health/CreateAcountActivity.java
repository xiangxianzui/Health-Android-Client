package edu.monash.infotech.health;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;

import edu.monash.infotech.health.basic.BasicConfig;
import edu.monash.infotech.health.basic.ShareApplication;
import edu.monash.infotech.health.models.User;
import edu.monash.infotech.health.network.HTTPURLConnection;

public class CreateAcountActivity extends AppCompatActivity {

    private ImageButton Back_Button;
    private EditText Username_EditText;
    private EditText Password_EditText;
    private Spinner BornYear_Spinner;
    private RadioGroup Gender_RadioGroup;
    private RadioButton Male_RadioButton;
    private RadioButton Female_RadioButton;
    private EditText Height_EditText;
    private EditText Weight_EditText;
    private Spinner ActivityLevel_Spinner;
    private EditText StepPerMile_EditText;

    private DatabaseHelper dbHelper;

    String serverIP = new BasicConfig().getServerIP();
    String serverPORT = new BasicConfig().getSERVER_PORT();
    private HTTPURLConnection conn;
    //private String path = "http://172.16.120.42:8080/HealthServer/webresources/com.entity.health.user/addUser";
    private String path = "http://" + serverIP + ":" + serverPORT + "/HealthServer/webresources/com.entity.health.user/addUser";
    private String USERNAME = "";
    private String currentDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acount);

        Back_Button = (ImageButton)findViewById(R.id.Back_Button);
        Username_EditText = (EditText)findViewById(R.id.Username_EditText);
        Password_EditText = (EditText)findViewById(R.id.Password_EditText);
        BornYear_Spinner = (Spinner)findViewById(R.id.BornYear_Spinner);
        Gender_RadioGroup = (RadioGroup)findViewById(R.id.Gender_RadioGroup);
        Male_RadioButton = (RadioButton)findViewById(R.id.Male_RadioButton);
        Female_RadioButton = (RadioButton)findViewById(R.id.Female_RadioButton);
        Height_EditText = (EditText)findViewById(R.id.Height_EditText);
        Weight_EditText = (EditText)findViewById(R.id.Weight_EditText);
        ActivityLevel_Spinner = (Spinner)findViewById(R.id.ActivityLevl_Spinner);
        StepPerMile_EditText = (EditText)findViewById(R.id.StepPerMile_EditText);

        Calendar calendar = Calendar.getInstance();
        currentDate = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DATE);

        //populate data to BornYear_Spinner
        String[] BornYearArray = new String[60];
        for(int i=0; i<60; i++){
            int maxYear = 2000;
            BornYearArray[i] = String.valueOf(maxYear - i);
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, BornYearArray);
        BornYear_Spinner.setAdapter(adapter);
        BornYear_Spinner.setSelection(17);

        //populate data to ActivityLevel_Spinner
        String[] ActivityLevelArray = new String[]{
            "Little/no exercise (sedentary)",
                "Lightly active (exercise/sports 1-3 days/week)",
                "Moderately active (exercise/sports 3-5 days/week)",
                "Very active (hard exercise/sports 6-7 days/wk)",
                "Extra active (very hard exercise/sports or training)"
        };
        ActivityLevel_Spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ActivityLevelArray));


    }

    public void CreateOnClick(View view){
        //check the input fields first
        if(checkInputFields()){
            String name = Username_EditText.getText().toString();
            String password = Password_EditText.getText().toString();
            int bornYear = Integer.valueOf(BornYear_Spinner.getSelectedItem().toString());
            Calendar cal = Calendar.getInstance();
            int age = cal.get(Calendar.YEAR) - bornYear;
            double height = Double.valueOf(Height_EditText.getText().toString());
            double weight = Double.valueOf(Weight_EditText.getText().toString());
            RadioButton checkedRadioButton = (RadioButton)findViewById(Gender_RadioGroup.getCheckedRadioButtonId());
            String genderStr = null;
            int gender = 0;
            if(checkedRadioButton != null){
                genderStr = checkedRadioButton.getText().toString();
            }
            if(genderStr.equals("Male")){
                gender = 0;
            }
            if(genderStr.equals("Female")){
                gender = 1;
            }
            int activityLevel = ActivityLevel_Spinner.getSelectedItemPosition() + 1;
            double stepPerMile = Double.valueOf(StepPerMile_EditText.getText().toString());

            if(!checkIfUserExists(name)){
                dbHelper = new DatabaseHelper(getApplicationContext());

                String passwordEncoded;
                try{
                    passwordEncoded = md5Encode(password);
                } catch (Exception e){
                    passwordEncoded = password;
                }

                Calendar calendar = Calendar.getInstance();
                String registerDate = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DATE);
                double latitude = 31.2740690000;
                double lontitude = 120.7537590000;
//            Location location = getLocation(this);
//            if(location != null){
//                latitude = location.getLatitude();
//                longitude = location.getLongitude();
//                System.out.println(latitude);
//                System.out.println(longitude);
//            }

                User u = new User(name, passwordEncoded, registerDate, latitude, lontitude);
                dbHelper.addUser(u);
                USERNAME = name;
                wrapPath(name, String.valueOf(age), String.valueOf(height), String.valueOf(weight), String.valueOf(gender), String.valueOf(activityLevel), String.valueOf(stepPerMile));
                conn = new HTTPURLConnection();
                new ConnectWebService().execute();
                //System.out.println("path:" + path);
                new addUserreport().execute(name.replaceAll(" ", "%20"), currentDate);
                ((ShareApplication)getApplication()).setValue(name);

            }
        }

    }

    public boolean checkInputFields(){
        String name = Username_EditText.getText().toString();
        String password = Password_EditText.getText().toString();
        RadioButton checkedRadioButton = (RadioButton)findViewById(Gender_RadioGroup.getCheckedRadioButtonId());
        String gender;
        if(checkedRadioButton != null){
            gender = checkedRadioButton.getText().toString();
        }
        String height = Height_EditText.getText().toString();
        String weight = Weight_EditText.getText().toString();
        String stepPerMile = StepPerMile_EditText.getText().toString();
//        System.out.println(gender);
        if(name.equals("")){
            Username_EditText.setError("Name should not be blank");
            return false;
        }
        if(password.equals("")){
            Password_EditText.setError("Password should not be blank");
            return false;
        }
        if(!Male_RadioButton.isChecked() && !Female_RadioButton.isChecked()){
            Male_RadioButton.setError("Select me!(*_*)");
            Female_RadioButton.setError("Select me!(*_*)");
            return false;
        }
        if(height.equals("")){
            Height_EditText.setError("Height should not be blank");
            return false;
        }
        if(weight.equals("")){
            Weight_EditText.setError("Weight should not be blank");
            return false;
        }
        if(stepPerMile.equals("")){
            StepPerMile_EditText.setError("Step/Mile should not be blank");
            return false;
        }
        if(Double.valueOf(height)<=0){
            Height_EditText.setError("Height should not be greater than 0");
            return false;
        }
        if(Double.valueOf(weight)<=0){
            Weight_EditText.setError("Weight should be greater than 0");
            return false;
        }
        if(Double.valueOf(stepPerMile)<=0){
            StepPerMile_EditText.setError("Step/Mile should be greater than 0");
            return false;
        }



        return true;
    }

    public boolean checkIfUserExists(String username){
        //check the database to see if the username already exsists
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        ArrayList<User> users = dbHelper.getAllUsers();
        for(User u : users){
            if(u.getName().equals(username)){
                Toast.makeText(this, "User already exists!", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

    public void BackOnClick(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @SuppressWarnings("static-access")
    private Location getLocation(Context context){
        //You do not instantiate this class directly;
        //instead, retrieve it through:
        //Context.getSystemService(Context.LOCATION_SERVICE).
        LocationManager locationManager = (LocationManager)context.getSystemService(context.LOCATION_SERVICE);
        //get the support of GPS
        Location location;
        try{
            location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            if(location == null){
                //get the support of NETWORK
                location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
            }
            return location;
        } catch (SecurityException e){

        }

        return null;
    }

//Encode the password using MD5
    public String md5Encode(String password) throws Exception{
        MessageDigest md5 = null;
        try{
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }

        byte[] byteArray = password.getBytes("UTF-8");
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        System.out.println("password: " + hexValue.toString());
        return hexValue.toString();

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
            //response = conn.GetFromWebServer(path);
            response = conn.PostToWebServer(path);
            System.out.println("server response: " + response);
            //System.out.println("IP: " + new GetIpAddress().getIP());
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(!response.equals("")){
                JSONArray jsonArray = null;
                try{
                    jsonArray = new JSONArray(response);
                    int status = Integer.valueOf(jsonArray.getString(1));
                    if(status == 0){//add user success
                        Toast.makeText(getApplicationContext(), "Create account successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.putExtra("username", USERNAME);
                        startActivity(intent);
                    }
                    if(status == 1){//add user fail
                        Toast.makeText(getApplicationContext(), "Account already exists!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }

        }
    }

    private class addUserreport extends AsyncTask<String, Void, Void> {
        String response = "";
        String path1 = "http://172.16.120.42:8080/HealthServer/webresources/com.entity.health.userreport/addUserreport";
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
                    int status = Integer.valueOf(jsonArray.getString(1));
                    if(status == 0){//add userreport success
                        System.out.println("status 0");
                    }
                    if(status == 1){//add userreport fail
                        System.out.println("status 1");
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }

        }
    }

}
