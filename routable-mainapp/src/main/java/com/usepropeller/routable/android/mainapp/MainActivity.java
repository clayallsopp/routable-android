package com.usepropeller.routable.android.mainapp;

import android.app.Activity;
import android.os.Bundle;
import com.jayway.maven.plugins.android.generation2.samples.libraryprojects.mainapp.R;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}
