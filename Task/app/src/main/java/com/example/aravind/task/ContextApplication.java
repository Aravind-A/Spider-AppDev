package com.example.aravind.task;

import android.app.Application;
import android.content.Context;

/**
 * Created by aravind on 4/4/16.
 */
public class ContextApplication extends Application {
    private static ContextApplication instance;

    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;
    }
    public static ContextApplication getInstance(){
        return instance;
    }
    public static Context getAppContext(){
        return instance.getApplicationContext();
    }
}
