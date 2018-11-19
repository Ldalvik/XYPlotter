package com.example.student.piano;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

public class Settings extends AppCompatActivity {
    Handler handler;
    private BluetoothSocket socket;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public BluetoothThread bluetoothThread;
    Utils utils;
    boolean isConnected;
    BluetoothDevice device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice("00:06:66:7D:80:1A");
        utils = new Utils(this);
        try {
            socket = connect(device);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.connect();
            utils.makeToast("Connected to device" + device.getName());
            isConnected = true;
        } catch (IOException e) {
            e.printStackTrace();
            isConnected = false;
        }
        bluetoothThread = new BluetoothThread(socket, handler);
        bluetoothThread.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private BluetoothSocket connect(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, MY_UUID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    public void xDelay(View v){
        EditText xDelay = findViewById(R.id.xDelay);
        bluetoothThread.write(SettingsInfo.command("X_DELAY", Utils.getValue(xDelay)));
    }

    public void yDelay(View v){
        EditText yDelay = findViewById(R.id.yDelay);
        bluetoothThread.write(SettingsInfo.command("Y_DELAY", Utils.getValue(yDelay)));

    }

    public void tapDelay(View v){
        EditText tapDelay = findViewById(R.id.tapDelay);
        bluetoothThread.write(SettingsInfo.command("TAP_DELAY", Utils.getValue(tapDelay)));

    }

    public void tapLoopHigh(View v){
        EditText tapLoopHigh = findViewById(R.id.tapLoopHigh);
        bluetoothThread.write(SettingsInfo.command("TAP_LOOP_HIGH", Utils.getValue(tapLoopHigh)));
    }

    public void tapLoopLow(View v){
        EditText tapLoopLow = findViewById(R.id.tapLoopLow);
        bluetoothThread.write(SettingsInfo.command("TAP_LOOP_LOW", Utils.getValue(tapLoopLow)));
    }

    public void reverseTap(View v){
        bluetoothThread.write(SettingsInfo.command("REVERSE_TAP", "0"));
    }

    public void connect(){
        try {
            socket = connect(device);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.connect();
            utils.makeToast("Connected to device" + device.getName());
            isConnected = true;
        } catch (IOException e) {
            e.printStackTrace();
            isConnected = false;
        }
        bluetoothThread = new BluetoothThread(socket, BluetoothThread.handle("/", new BluetoothThread.BluetoothClass() {
            @Override
            public void onReceived(String data) {
                utils.makeToast(data);
            }
        }));
        bluetoothThread.start();
    }

    public void disconnect(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
