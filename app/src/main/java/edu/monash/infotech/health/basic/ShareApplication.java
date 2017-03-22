package edu.monash.infotech.health.basic;

import android.app.Application;

/**
 * Created by Administrator on 4/15/2016.
 */
public class ShareApplication extends Application{
    private String value;
    private boolean status = true;

    public String getValue(){
        return value;
    }

    public void setValue(String value){
        this.value = value;
    }

    public boolean getStatus(){
        return status;
    }

    public void setStatus(boolean status){
        this.status = status;
    }
}
