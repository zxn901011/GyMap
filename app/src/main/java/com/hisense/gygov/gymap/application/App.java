package com.hisense.gygov.gymap.application;

import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;

import com.baidu.mapapi.SDKInitializer;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
    }
}
