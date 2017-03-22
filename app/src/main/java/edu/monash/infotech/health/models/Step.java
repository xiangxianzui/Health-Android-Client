package edu.monash.infotech.health.models;

/**
 * Created by Administrator on 4/18/2016.
 */
public class Step {
    public static final String TABLE_NAME = "step";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERID = "userId";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_STEPSNUM = "stepsNum";
    public static final String CREATE_STATEMENT =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    COLUMN_USERID + " INTEGER NOT NULL, " +
                    COLUMN_DATE + " TEXT NOT NULL, " +
                    COLUMN_TIME + " TEXT NOT NULL, " +
                    COLUMN_STEPSNUM + " DOUBLE NOT NULL, " +
                    "FOREIGN KEY(" + COLUMN_USERID +
                    ") REFERENCES user(id)" +
                    ")";


    private long id;
    private long userId;
    private String date;
    private String time;
    private double stepsNum;

    //Constructors
    public Step(long id, long userId, String date, String time, double stepsNum){
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.time = time;
        this.stepsNum = stepsNum;
    }
    public Step(long userId, String date, String time, double stepsNum){
        this.userId = userId;
        this.date = date;
        this.time = time;
        this.stepsNum = stepsNum;
    }

    //get and set methods
    public long getId() {
        return id;
    }
    public void setId(long id){
        this.id = id;
    }
    public long getUserId(){
        return userId;
    }
    public void setUserId(long userId){
        this.userId = userId;
    }
    public String getDate(){
        return date;
    }
    public void setDate(String date){
        this.date = date;
    }
    public String getTime(){
        return time;
    }
    public void setTime(String time){
        this.time = time;
    }
    public double getStepsNum(){
        return stepsNum;
    }
    public void setStepsNum(double stepsNum){
        this.stepsNum = stepsNum;
    }

}
