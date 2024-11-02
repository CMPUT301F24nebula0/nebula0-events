package com.example.pickme_nebula0;

import android.app.Application;

public class PickMeApplication extends Application {
    private static PickMeApplication instance;

    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;
    }

    public static PickMeApplication getInstance(){
        return instance;
    }
}
