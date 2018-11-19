package com.example.student.piano;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    boolean mBound = false;
    BluetoothThread mService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void plotter(View v){
        Intent i = new Intent(this, Plotter.class);
        startActivity(i);
    }

    public void manual(View v){
        Intent i = new Intent(this, Manual.class);
        startActivity(i);
    }

    public void settings(View v){
        Intent i = new Intent(this, Settings.class);
        startActivity(i);
    }
}
