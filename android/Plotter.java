package com.example.student.piano;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

public class Plotter extends AppCompatActivity {
    TextView data;
    Handler handler;
    private BluetoothSocket socket;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public BluetoothThread bluetoothThread;
    StringBuilder sb = new StringBuilder();
    Utils utils;
    BluetoothDevice device;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plotter);
        data = findViewById(R.id.data);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        device = bluetoothAdapter.getRemoteDevice("00:06:66:7D:80:1A");
        utils = new Utils(this);

        try {
            socket = connect(device);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.connect();
            utils.makeToast("Connected to device" + device.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 1:
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);
                        sb.append(strIncom);
                        int endOfLineIndex = sb.indexOf("/");
                        if (endOfLineIndex > 0) {
                            String data = sb.substring(0, endOfLineIndex);
                            sb.delete(0, sb.length());
                            if(data.startsWith("B")){
                                setText(data.replace(":", "/").substring(1, data.length()), false);
                            } else if(data.contains("Done")){
                                setText(data.substring(1, data.length()), true);
                                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    v.vibrate(VibrationEffect.createOneShot(1000,VibrationEffect.DEFAULT_AMPLITUDE));
                                } else {
                                    v.vibrate(1000);
                                }
                            }
                        }
                        break;
                }
            }
        };
        bluetoothThread = new BluetoothThread(socket, handler);
        bluetoothThread.start();
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void setText(final String text, final boolean color) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(color) {
                    data.setTextColor(Color.GREEN);
                }
                data.setText(text);
            }
        });
    }

    public void reconnect(View v){
        try {
            socket = connect(device);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.connect();
            utils.makeToast("Connected to device" + device.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        bluetoothThread = new BluetoothThread(socket,handler);
        bluetoothThread.start();
    }

    public void disconnect(View v){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
