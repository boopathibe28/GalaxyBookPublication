package com.galaxybookpublication.api;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;


public class MyApplication extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

       // Fresco.initialize(this);

    }




    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }



}
