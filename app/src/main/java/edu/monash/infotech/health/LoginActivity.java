package edu.monash.infotech.health;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import edu.monash.infotech.health.basic.ShareApplication;
import edu.monash.infotech.health.bing.BingImageSearch;
import edu.monash.infotech.health.fatsecret.platform.FatSecretAPI;
import edu.monash.infotech.health.models.User;
import edu.monash.infotech.health.network.GetIpAddress;
import edu.monash.infotech.health.network.HTTPURLConnection;

public class LoginActivity extends AppCompatActivity {
    private EditText Username_EditText;
    private EditText Password_EditText;
    private Button SignIn_Button;
    private Button SignUp_Button;
    private ProgressBar progressBar;

    int authenticateFailedCount = 0;

    //private String path = "http://172.16.120.42:8080/HealthServer/webresources/com.entity.health.user/addUser";

    private String currentDate = "";

    //private String[] dummyUser = {"hunter:wh0606", "wanghao:123456"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Username_EditText = (EditText)findViewById(R.id.Username_EditText);
        Password_EditText = (EditText)findViewById(R.id.Password_EditText);
        SignIn_Button = (Button)findViewById(R.id.SignIn_Button);
        SignUp_Button = (Button)findViewById(R.id.SignUp_Button);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        Calendar calendar = Calendar.getInstance();
        currentDate = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DATE);



        //new testBing().execute();
        //new testFatSecret().execute();

        //Initialize HTTPURLConnection class object
//        conn = new HTTPURLConnection();
//        new ConnectWebService().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void SignInOnClick(View view){
        String username = Username_EditText.getText().toString();
        String password = Password_EditText.getText().toString();
        if(username.equals("")){
            Username_EditText.setError("Username should not be blank!");
            return;
        }
        if(password.equals("")){
            Password_EditText.setError("Password should not be blank!");
            return;
        }
        if(authenticateUser(username, password)){
            System.out.println("right");
            showProgressBar(true);
            new addUserreport().execute(username, currentDate);
            ((ShareApplication)getApplication()).setValue(username);
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);

            //log in success,then jump to personal page
        }
        else{
            authenticateFailedCount++;
            if(authenticateFailedCount<3){
                Toast.makeText(this, "Username/Password is wrong!", Toast.LENGTH_SHORT).show();
            } else{
                //animate the sign up button
                SignUp_Button.animate().rotation(360).setDuration(800);
            }

        }
    }
    public boolean authenticateUser(String username, String password){
        //check the database to see if username and password are correct
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        ArrayList<User> users = dbHelper.getAllUsers();
        String passwordEncoded;
        try {
            passwordEncoded = md5Encode(password);
        } catch (Exception e){
            passwordEncoded = password;
        }
        for(User u : users){
            if(u.getName().equals(username) && u.getPassword().equals(passwordEncoded)){
                return true;
            }
        }
        return false;
    }

    public void SignUpOnClick(View view){
        //System.out.println("sign up");
        Intent intent = new Intent(this, CreateAcountActivity.class);
        startActivity(intent);
    }

    public void showProgressBar(final boolean show){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);

            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }
        else{
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
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
        System.out.println(hexValue.toString());
        return hexValue.toString();
    }


    private class testFatSecret extends AsyncTask<Void, Void, Void>{
        JSONObject foods = null;
        @Override
        protected Void doInBackground(Void... arg0){
            FatSecretAPI api = new FatSecretAPI("1cdc19f5589940798ec8f643be0ab6f4", "f01d121e378b426d90f4024e527ddb72");
            try{
                foods = api.getFoodItems("Burgers");
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
                    System.out.println("Description: " + wholeEntry.getString("food_description"));
                    System.out.println("Name: " + wholeEntry.getString("food_name"));
                    System.out.println("Type: " + wholeEntry.getString("food_type"));
                    System.out.println("Url: " + wholeEntry.getString("food_url"));
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

