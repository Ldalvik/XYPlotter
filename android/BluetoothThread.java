package com.example.student.piano;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothThread extends Thread {
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private Handler handler;

    public BluetoothThread(BluetoothSocket socket, Handler handler) {
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.handler = handler;
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        byte[] buffer = new byte[256];
        int bytes;
        while (true) {
            try {
                bytes = mmInStream.read(buffer);
                handler.obtainMessage(1, bytes, -1, buffer).sendToTarget();
            } catch (IOException e) {
                break;
            }
        }
    }

    public void write(String message) {
        byte[] msgBuffer = message.getBytes();
        try {
            mmOutStream.write(msgBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Handler handle(final String delimiter, final BluetoothClass bluetoothClass) {
        final StringBuilder sb = new StringBuilder();
        Handler h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 1:
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);
                        sb.append(strIncom);
                        int endOfLineIndex = sb.indexOf(delimiter);
                        if (endOfLineIndex > 0) {
                            String sbprint = sb.substring(0, endOfLineIndex);
                            sb.delete(0, sb.length());
                            bluetoothClass.onReceived(sbprint);

                        }
                        break;
                }
            }
        };
        return h;
    }

    interface BluetoothClass {
        void onReceived(String data);
    }


}
