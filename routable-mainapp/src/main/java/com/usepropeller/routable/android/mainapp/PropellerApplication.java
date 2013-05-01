package com.usepropeller.routable.android.mainapp;

import android.app.Application;
import com.usepropeller.routable.Router;

public class PropellerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Set the global context
        Router.sharedRouter().setContext(getApplicationContext());
        // Symbol-esque params are passed as intent extras to the activities
        Router.sharedRouter().map("users/:id", MainActivity.class);
        Router.sharedRouter().map("users/new/:name/:zip", MainActivity.class);
    }
}
