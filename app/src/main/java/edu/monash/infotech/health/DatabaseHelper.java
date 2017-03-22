package edu.monash.infotech.health;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import edu.monash.infotech.health.models.Step;
import edu.monash.infotech.health.models.User;

/**
 * Created by Administrator on 4/12/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    //set database properties
    public static final String DATABASE_NAME = "HealthDB";
    public static final int DATABASE_VERSION = 1;

    //Constructor
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(User.CREATE_STATEMENT);
        db.execSQL(Step.CREATE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + User.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Step.TABLE_NAME);
    }

    //methods for table user
    public void addUser(User u){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(User.COLUMN_NAME, u.getName());
        values.put(User.COLUMN_PASSWORD, u.getPassword());
        values.put(User.COLUMN_REGISTERDATE, u.getRegisterDate());
        values.put(User.COLUMN_LATITUDE, u.getLatitude());
        values.put(User.COLUMN_LONGITUDE, u.getLongitude());

        db.insert(User.TABLE_NAME, null, values);
        db.close();
    }
    public ArrayList<User> getAllUsers(){
        ArrayList<User> users = new ArrayList<User>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + User.TABLE_NAME, null);

        // Add monster to ArrayList for each row result
        if(cursor.moveToFirst()){
            do{
                User u = new User(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
                        cursor.getDouble(4), cursor.getDouble(5));
                users.add(u);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return users;
    }
    public boolean isUserExist(String name){
        ArrayList<User> users = getAllUsers();
        for(User u : users){
            if(u.getName().equals(name)){
                return true;
            }
        }
        return false;
    }
    public boolean authenticateUser(String name, String password){
        ArrayList<User> users = getAllUsers();
        for(User u : users){
            if(u.getName().equals(name) && u.getPassword().equals(password)){
                return true;
            }
        }
        return false;
    }
    public String getUserNameById(long id){
        String userName = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String[] arg = {String.valueOf(id)};
        Cursor cursor = db.rawQuery("SELECT * FROM " + User.TABLE_NAME + " WHERE " + User.COLUMN_ID + " =?", arg);
        if(cursor.moveToFirst()){
            do{
                userName = cursor.getString(1);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userName;
    }
    public long getUserIdByName(String name){
        long id = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + User.TABLE_NAME + " WHERE " + User.COLUMN_NAME + " =?", new String[]{name});
        if(cursor.moveToFirst()){
            do{
                id = cursor.getLong(0);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return id;
    }

    //methods for table step
    public void addStep(Step s){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Step.COLUMN_USERID, s.getUserId());
        values.put(Step.COLUMN_DATE, s.getDate());
        values.put(Step.COLUMN_TIME, s.getTime());
        values.put(Step.COLUMN_STEPSNUM, s.getStepsNum());

        db.insert(Step.TABLE_NAME, null, values);
        db.close();
    }
    public ArrayList<Step> getAllSteps(){
        ArrayList<Step> steps = new ArrayList<Step>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Step.TABLE_NAME, null);

        if(cursor.moveToFirst()){
            do{
                Step s = new Step(cursor.getLong(0), cursor.getLong(1), cursor.getString(2), cursor.getString(3), cursor.getDouble(4));
                steps.add(s);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return steps;
    }







}
