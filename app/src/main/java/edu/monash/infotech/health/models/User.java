package edu.monash.infotech.health.models;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by Administrator on 4/12/2016.
 */
public class User implements Parcelable{
    public static final String TABLE_NAME = "user";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_REGISTERDATE = "registerDateTEXT";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String CREATE_STATEMENT =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL, " +
                    COLUMN_REGISTERDATE + " TEXT NOT NULL, " +
                    COLUMN_LATITUDE + " DOUBLE NOT NULL, " +
                    COLUMN_LONGITUDE + " DOUBLE NOT NULL" +
                    ")";

    private long id;
    private String name;
    private String password;
    private String registerDate;
    private double latitude;
    private double longitude;

    //Constructors
    public User(long id, String name, String password, String registerDate, double latitude, double longitude){
        this.id = id;
        this.name = name;
        this.password = password;
        this.registerDate = registerDate;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public User(String name, String password, String registerDate, double latitude, double longitude){
        this.name = name;
        this.password = password;
        this.registerDate = registerDate;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public User(Parcel in){
        this.id = in.readLong();
        this.name = in.readString();
        this.password = in.readString();
        this.registerDate = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    //get and set methods
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRegisterDate() {
        return registerDate;
    }
    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }
    public double getLatitude(){
        return latitude;
    }
    public void setLatitude(double latitude){
        this.latitude = latitude;
    }
    public double getLongitude(){
        return longitude;
    }
    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    //static method to create Parcelable object(required)
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public User createFromParcel(Parcel in){
            return new User(in);
        }
        public User[] newArray(int size){
            return new User[size];
        }
    };

    @Override
    public int describeContents(){
        return 0;
    }
    //method to write values to parcel in a specific order
    @Override
    public void writeToParcel(Parcel parcel, int i){
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeString(password);
        parcel.writeString(registerDate);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }

}
