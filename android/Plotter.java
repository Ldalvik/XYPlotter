import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plotter);
        data = findViewById(R.id.data);
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
                            //if(data.startsWith("B")){
                                setText(data);//.replace(":", "/"));
                            //}
                        }
                        break;
                }
            }
        };
        bluetoothThread = new BluetoothThread(socket,handler);
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

    private void setText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                data.setText(text);
            }
        });
    }
}
